package com.sayra.umai.Services;

import com.sayra.umai.DTO.PageOutDTO;
import com.sayra.umai.Entities.*;
import com.sayra.umai.Other.WorkStatus;
import com.sayra.umai.Repos.AuthorRepo;
import com.sayra.umai.Repos.ChapterRepo;
import com.sayra.umai.Repos.GenreRepo;
import com.sayra.umai.Repos.WorkRepo;
import jakarta.transaction.Transactional;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class WorkService {
    private final PdfService pdfService;
    private final WorkRepo workRepo;
    private final ChapterRepo chapterRepo;
    private final AuthorRepo authorRepo;
    private final GenreRepo genreRepo;
    public WorkService(WorkRepo workRepo, PdfService pdfService,  ChapterRepo chapterRepo, AuthorRepo authorRepo, GenreRepo genreRepo) {
        this.workRepo = workRepo;
        this.pdfService = pdfService;
        this.chapterRepo = chapterRepo;
        this.authorRepo = authorRepo;
        this.genreRepo = genreRepo;
    }

    public Work findById(Long id){
        return workRepo.findById(id).orElse(null);
    }

    @Transactional
    public Work uploadWork(MultipartFile pdfFile,
                           String title,
                           String author,
                           String genre,
                           String description,
                           MultipartFile coverImage // если не используешь, можно передавать null
    ) throws IOException {

        // 1) Сохранить временный/чистый PDF на диск
        File cleanedPdf = pdfService.savePdf(pdfFile);

        // 2) Вызвать парсер, получить список глав с уже разбитыми чанками <= 500KB
        List<PdfService.ChapterData> chaptersData = pdfService.extractChapters(cleanedPdf);

        // 3) Создать Work и заполнить поля
        Work work = new Work();
        work.setTitle(title);
        work.setAuthor(author);
        work.setDescription(description);
        work.setFilePath(cleanedPdf.getAbsolutePath());
        // Если у тебя в сущности есть поле genre (пока его нет — можно добавить),
        // то раскомментируй следующую строку:
        // work.setGenre(genre);
        work.setGenre(genre);
        work.setStatus(WorkStatus.PENDING);

        // 4) Собираем Chapter -> Chunk объекты согласно chaptersData
        List<Chapter> chapters = new ArrayList<>();
        for (PdfService.ChapterData chData : chaptersData) {
            Chapter chapter = new Chapter();
            chapter.setChapterNumber(chData.chapterNumber());
            chapter.setChapterTitle(chData.title());
            chapter.setWork(work);

            List<Chunk> chunks = new ArrayList<>();
            int chunkNum = 1;
            for (String chunkText : chData.chunks()) {
                Chunk chunk = new Chunk();
                chunk.setChunkNumber(chunkNum++);
                // Оборачиваем в HTML параграфы — фронт ожидает html в "data"
                // (если хочешь, можно оставить plain text)
                chunk.setText(chunkText);
                chunk.setChapter(chapter);
                chunks.add(chunk);
            }
            chapter.setChunks(chunks);
            chapters.add(chapter);
        }

        work.setChapters(chapters);

        // 5) Сохранить cascade: Work -> Chapters -> Chunks
        Work saved = workRepo.save(work);

        // Возвращаем сохранённую сущность
        return saved;
    }

    // Небольшая util-функция — простая экранизация
    private String escapeHtml(String s) {
        if (s == null) return "";
        return s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
    }


//    public Work uploadWork(MultipartFile pdfFile,
//                           String title,
//                           String authorName,
//                           String genreName,
//                           String description,
//                           MultipartFile coverImage) throws IOException {
//
////        Author author = authorRepo.findByName(authorName)
////                .orElseGet(() -> authorRepo.save(new Author(authorName)));
//
////        Genre genre = genreRepo.findByName(genreName)
////                .orElseGet(() -> genreRepo.save(new Genre(genreName)));
//
//
//        // 5️⃣ Парсинг PDF и деление на главы/чанки
//        List<TranslationChunk> chunks = PdfParser.parsePdfToChunks(pdfFile.getBytes());
//
//        // 6️⃣ Создание книги
//        Work book = new Work();
//        book.setTitle(title);
//        book.setAuthor(author);
//        book.setGenre(genre);
//        book.setDescription(description);
//        book.setCoverUrl(coverUrl);
//        book.setPdfUrl(pdfUrl);
//        book.setStatus(BookStatus.PENDING);
//
//        // 7️⃣ Сохранение книги
//        book = bookRepository.save(book);
//
//        // 8️⃣ Сохранение перевода (всегда кыргызский оригинал)
//        for (int i = 0; i < chunks.size(); i++) {
//            Translation translation = new Translation();
//            translation.setBook(book);
//            translation.setChapterTitle(chunks.get(i).getChapterTitle());
//            translation.setLanguage("ky");
//            translation.setContent(chunks.get(i).getJsonContent());
//            translationRepository.save(translation);
//        }
//
//        return book;
//    }

    public ResponseEntity<List<Chapter>> getChaptersOfWork(Long workId) {
        Work work = workRepo.findById(workId).orElse(null);
        if(work == null) return ResponseEntity.notFound().build();

        List<Chapter> chapters = work.getChapters();
        if(chapters.isEmpty()) return ResponseEntity.notFound().build();

        return ResponseEntity.ok(chapters);
    }




}
