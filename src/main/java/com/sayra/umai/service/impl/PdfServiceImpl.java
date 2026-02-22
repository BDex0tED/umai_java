package com.sayra.umai.service.impl;

import com.sayra.umai.model.dto.ChunkType;
import com.sayra.umai.model.dto.WorkStatus;
import com.sayra.umai.model.entity.work.*;
import com.sayra.umai.repo_service.AuthorDataService;
import com.sayra.umai.repo_service.GenreDataService;
import com.sayra.umai.repo_service.WorkDataService;
import com.sayra.umai.service.DropboxService;
import com.sayra.umai.service.PdfService;
import com.sayra.umai.service.PdfTextService;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.regex.Pattern;

@Service
public class PdfServiceImpl implements PdfService {

  private final WorkDataService workDataService;
  private final AuthorDataService authorDataService;
  private final GenreDataService genreDataService;
  private final DropboxService dropboxService;
  private final PdfTextService pdfTextService;
  public PdfServiceImpl(WorkDataService workDataService,
                        AuthorDataService authorDataService,
                        GenreDataService genreDataService,
                        DropboxService dropboxService,
                        PdfTextService pdfTextService) {
    this.workDataService = workDataService;
    this.authorDataService = authorDataService;
    this.genreDataService = genreDataService;
    this.dropboxService = dropboxService;
    this.pdfTextService = pdfTextService;
  }

    public static final String type_html = "html";
    public static final int CHUNK_SIZE_BYTES = 50 * 1024; // 500 KB

    // Более гибкий паттерн для fallback деления (по заголовкам)
    public static final Pattern CHAPTER_PATTERN = Pattern.compile(
            "(?im)^(?:\\s*)(?:бөлүм|болум|глава|chapter|section|кисими|часть|[0-9]{1,3}|[IVXLCDM]{1,7})\\b[ .–:,-]*"
    );

    public static final List<String> WORDS_TO_REMOVE = Arrays.asList(
            "кыргыз китептери", "китептер", "мисал", "бизнес"
    );
    public static final Pattern LINK_PATTERN = Pattern.compile("(https?://\\S+|www\\.\\S+|\\w+\\.kg)", Pattern.CASE_INSENSITIVE);


  @Transactional
  public Work uploadWork(MultipartFile pdfFile,
                         String title,
                         Long authorId,
                         List<Long> genresId,
                         String description,
                         MultipartFile coverImage
  ) throws IOException {
    try{

      File cleanedPdf = pdfTextService.savePdf(pdfFile);

      List<PdfServiceImpl.ChapterData> chaptersData = pdfTextService.extractChapters(cleanedPdf);

      Author author = authorDataService.findByIdOrThrow(authorId);
      List<Genre> genres = new ArrayList<>();
      if(genresId != null && !genresId.isEmpty()){
        for(Long genreId : genresId){
          Genre genre = genreDataService.findByIdOrThrow(genreId);
          genres.add(genre);
        }
      }

      Work work = new Work();
      work.setTitle(title);
      work.setAuthor(author);
      work.setDescription(description);
      work.setFilePath(cleanedPdf.getAbsolutePath());
      work.setGenres(genres);
      work.setStatus(WorkStatus.PENDING);

      // Загружаем обложку в Dropbox, если она предоставлена
      if (coverImage != null && !coverImage.isEmpty()) {
        try {
          String coverUrl = dropboxService.uploadFile(coverImage, "covers");
          work.setCoverUrl(coverUrl);
        } catch (Exception e) {
          throw new RuntimeException("Ошибка при загрузке обложки в Dropbox: " + e.getMessage());
        }
      }

      List<Chapter> chapters = new ArrayList<>();
      for (PdfServiceImpl.ChapterData chData : chaptersData) {
        Chapter chapter = new Chapter();
        chapter.setChapterNumber(chData.chapterNumber());
        chapter.setChapterTitle(chData.title());
        chapter.setWork(work);

        List<Chunk> chunks = new ArrayList<>();
        int chunkNum = 1;
        for (String chunkText : chData.chunks()) {
          Chunk chunk = new Chunk();
          chunk.setChunkNumber(chunkNum++);
          chunk.setText(chunkText);
          chunk.setType(ChunkType.html);
          chunk.setChapter(chapter);
          chunks.add(chunk);
        }
        chapter.setChunks(chunks);
        chapters.add(chapter);
      }

      work.setChapters(chapters);

      return workDataService.saveWork(work);
    } catch(IllegalArgumentException e){
      throw new IllegalArgumentException(e.getMessage());
    } catch(RuntimeException e){
      throw new RuntimeException(e.getMessage());
    } catch(Exception e){
      throw new RuntimeException(e.getMessage());
    }
  }

    // ---------------- DTO для главы ----------------
    public record ChapterData(int chapterNumber, String title, String type, List<String> chunks) {}
}
