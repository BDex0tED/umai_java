package com.sayra.umai.Services;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDestinationNameTreeNode;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.interactive.action.PDAction;
import org.apache.pdfbox.pdmodel.interactive.action.PDActionGoTo;
import org.apache.pdfbox.pdmodel.interactive.documentnavigation.destination.*;
import org.apache.pdfbox.pdmodel.interactive.documentnavigation.outline.PDDocumentOutline;
import org.apache.pdfbox.pdmodel.interactive.documentnavigation.outline.PDOutlineItem;
import org.apache.pdfbox.pdmodel.interactive.documentnavigation.outline.PDOutlineNode;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.regex.Pattern;
//if there's an image it should be exported =>future task

@Service
public class PdfService {

    private final String type_html = "html";
    private static final int CHUNK_SIZE_BYTES = 500 * 1024; // 500 KB
    // Более гибкий паттерн для fallback деления (строка-начало)
    private static final Pattern CHAPTER_PATTERN = Pattern.compile(
            "(?im)^(?:\\s*)(?:бөлүм|болум|глава|chapter|section|кисими|часть|[0-9]{1,3}|[IVXLCDM]{1,7})\\b[ .–:,-]*"
    );

    private static final List<String> WORDS_TO_REMOVE = Arrays.asList(
            "кыргыз китептери", "китептер", "мисал", "бизнес"
    );
    private static final Pattern LINK_PATTERN = Pattern.compile("(https?://\\S+|www\\.\\S+|\\w+\\.kg)", Pattern.CASE_INSENSITIVE);

    // ---------------- Сохраняем PDF на диск ----------------
    public File savePdf(MultipartFile file) throws IOException {
        File tmpFile = File.createTempFile("upload_", ".pdf");
        file.transferTo(tmpFile);

        File outputFile = new File("uploads", Objects.requireNonNull(file.getOriginalFilename()));
        outputFile.getParentFile().mkdirs();

        try (PDDocument document = Loader.loadPDF(tmpFile)) {
            document.save(outputFile);
        }
        return outputFile;
    }

    // ---------------- Основной метод — извлечение глав ----------------
    public List<ChapterData> extractChapters(File pdfFile) throws IOException {
        try (PDDocument document = Loader.loadPDF(pdfFile)) {
            PDDocumentOutline outline = document.getDocumentCatalog().getDocumentOutline();
            if (outline != null) {
                List<ChapterData> chapters = extractFromOutline(document, outline);
                if (!chapters.isEmpty()) {
                    System.out.println("✅ Использовано встроенное оглавление PDF");
                    return chapters;
                }
            }
        }

        // Если TOC нет
        System.out.println("⚙️ TOC отсутствует, делим по ключевым словам");
        return extractByTextPatterns(pdfFile);
    }

    // ---------------- Парсинг TOC ----------------
    private List<ChapterData> extractFromOutline(PDDocument document, PDDocumentOutline outline) throws IOException {
        List<ChapterData> chapters = new ArrayList<>();
        PDFTextStripper stripper = new PDFTextStripper();

        PDOutlineItem current = outline.getFirstChild();
        int chapterNumber = 1;

        while (current != null) {
            PDPage startPage = resolveDestinationPage(current, document);
            if (startPage != null) {
                int startIndex = document.getPages().indexOf(startPage);

                PDOutlineItem next = current.getNextSibling();
                PDPage nextPage = (next != null) ? resolveDestinationPage(next, document) : null;
                int endIndex;
                if (nextPage != null) {
                    endIndex = document.getPages().indexOf(nextPage) - 1;
                } else {
                    endIndex = document.getNumberOfPages() - 1;
                }
                if (endIndex < startIndex) endIndex = document.getNumberOfPages() - 1;

                stripper.setStartPage(startIndex + 1);
                stripper.setEndPage(endIndex + 1);

                String raw = stripper.getText(document);
                String text = cleanText(raw);
                if (!text.isEmpty()) {
                    List<String> chunks = chunkTextToHtml(text, CHUNK_SIZE_BYTES);
                    String title = Optional.ofNullable(current.getTitle()).orElse("Глава " + chapterNumber);
                    chapters.add(new ChapterData(chapterNumber++, title, type_html, chunks));
                    System.out.printf("Parsed chapter #%d title='%s' pages=%d..%d chars=%d chunks=%d%n",
                            chapterNumber-1, title, startIndex+1, endIndex+1, text.length(), chunks.size());
                }
            } else {
                System.out.println("Warning: outline item without resolvable page: '" + current.getTitle() + "'");
            }
            current = current.getNextSibling();
        }

        return chapters;
    }

    // ---------------- Разрешение destination (страницы) для PDOutlineItem ----------------
    private PDPage resolveDestinationPage(PDOutlineItem item, PDDocument document) {
        if (item == null) return null;
        try {
            PDDestination dest = item.getDestination();
            if (dest instanceof PDPageDestination pageDest) {
                PDPage p = pageDest.getPage();
                if (p != null) return p;
            } else if (item.getAction() instanceof PDActionGoTo goTo) {
                PDDestination ad = goTo.getDestination();
                if (ad instanceof PDPageDestination pageDest2) {
                    PDPage p = pageDest2.getPage();
                    if (p != null) return p;
                } else if (ad instanceof PDNamedDestination named) {
                    PDPage p = lookupNamedDestinationPage(named.getNamedDestination(), document);
                    if (p != null) return p;
                }
            } else if (dest instanceof PDNamedDestination named2) {
                PDPage p = lookupNamedDestinationPage(named2.getNamedDestination(), document);
                if (p != null) return p;
            }
        } catch (Exception ex) {
            System.out.println("Exception while resolving destination: " + ex.getMessage());
        }
        return null;
    }

    // Попытка найти named destination через именованные цели в каталоге
    private PDPage lookupNamedDestinationPage(String name, PDDocument document) {
        try {
            if (name == null) return null;
            var namesDict = document.getDocumentCatalog().getNames();
            if (namesDict == null) return null;
            PDDestinationNameTreeNode dests = namesDict.getDests();
            if (dests == null) return null;
            Map<String, PDPageDestination> named = dests.getNames();
            if (named == null) return null;
            PDDestination dest = named.get(name);
            if (dest instanceof PDPageDestination pd) {
                return pd.getPage();
            }
        } catch (Exception e) {
        }
        return null;
    }

    // ---------------- Парсинг без TOC ----------------
    private List<ChapterData> extractByTextPatterns(File pdfFile) throws IOException {
        String fullText;
        try (PDDocument document = Loader.loadPDF(pdfFile)) {
            PDFTextStripper stripper = new PDFTextStripper();
            fullText = cleanText(stripper.getText(document));
        }

        var matcher = CHAPTER_PATTERN.matcher(fullText);
        List<Integer> positions = new ArrayList<>();
        List<String> titles = new ArrayList<>();

        while (matcher.find()) {
            positions.add(matcher.start());
            titles.add(matcher.group().trim());
        }

        List<ChapterData> chapters = new ArrayList<>();

        if (positions.isEmpty()) {
            List<String> chunks = chunkTextToHtml(fullText, CHUNK_SIZE_BYTES);
            chapters.add(new ChapterData(1, "Автоматическая глава",type_html, chunks));
            return chapters;
        }

        for (int i = 0; i < positions.size(); i++) {
            int start = positions.get(i);
            int end = (i + 1 < positions.size()) ? positions.get(i + 1) : fullText.length();
            String title = titles.get(i);
            String body = fullText.substring(start, end).trim();

            if (!body.isEmpty()) {
                List<String> chunks = chunkTextToHtml(body, CHUNK_SIZE_BYTES);
                chapters.add(new ChapterData(i + 1, title,type_html, chunks));
                System.out.printf("Fallback chapter #%d title='%s' chars=%d chunks=%d%n",
                        i+1, title, body.length(), chunks.size());
            }
        }

        return chapters;
    }

    // ---------------- Чистка мусора ----------------
    private String cleanText(String text) {
        StringBuilder sb = new StringBuilder();
        for (String line : text.split("\\r?\\n")) {
            String trimmed = line.trim();
            String lower = trimmed.toLowerCase();
            boolean skip = LINK_PATTERN.matcher(trimmed).find()
                    || WORDS_TO_REMOVE.stream().anyMatch(lower::contains)
                    || trimmed.matches("^\\d+$")
                    || trimmed.length() <= 2;
            if (!skip) {
                sb.append(trimmed).append("\n");
            }
        }
        return sb.toString().trim();
    }

    // ---------------- Деление на чанки (без разрезания UTF-8) и оборачивание в HTML ----------------
    private List<String> chunkTextToHtml(String text, int maxBytes) {
        List<String> chunks = new ArrayList<>();
        // Разбиваем по "параграфам" — чаще всего безопасно и красиво
        String[] paras = text.split("\\r?\\n\\s*\\r?\\n");
        StringBuilder current = new StringBuilder();
        int currentBytes = 0;

        for (String p : paras) {
            if (p.isBlank()) continue;
            String paraHtml = "<p>" + escapeHtml(p).replace("\n", "<br/>") + "</p>";
            byte[] paraBytes = paraHtml.getBytes(StandardCharsets.UTF_8);

            if (paraBytes.length > maxBytes) {
                // flush current
                if (current.length() > 0) {
                    chunks.add(current.toString());
                    current.setLength(0);
                    currentBytes = 0;
                }
                int start = 0;
                int len = p.length();
                while (start < len) {
                    int approxChars = Math.max(1, (int) (maxBytes / 2)); // грубая оценка
                    int end = Math.min(len, start + approxChars);
                    String piece = p.substring(start, end);
                    String pieceHtml = "<p>" + escapeHtml(piece) + "</p>";
                    chunks.add(pieceHtml);
                    start = end;
                }
                continue;
            }

            if (currentBytes + paraBytes.length <= maxBytes) {
                current.append(paraHtml).append("\n");
                currentBytes += paraBytes.length;
            } else {
                // flush and start new
                if (current.length() > 0) {
                    chunks.add(current.toString());
                }
                current.setLength(0);
                current.append(paraHtml).append("\n");
                currentBytes = paraBytes.length;
            }
        }

        if (current.length() > 0) chunks.add(current.toString());
        return chunks;
    }

    // ---------------- Экранирование HTML ----------------
    private String escapeHtml(String s) {
        if (s == null) return "";
        return s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
    }

    // ---------------- DTO для главы ----------------
    public record ChapterData(int chapterNumber, String title,String type, List<String> chunks) {}
}
