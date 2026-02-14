  package com.sayra.umai.service.impl;

  import com.sayra.umai.model.entity.work.*;
  import com.sayra.umai.repo_service.AuthorDataService;
  import com.sayra.umai.repo_service.GenreDataService;
  import com.sayra.umai.repo_service.WorkDataService;
  import com.sayra.umai.model.dto.AllWorksDTO;
  import com.sayra.umai.model.dto.GenreDTO;
  import com.sayra.umai.model.response.*;
  import com.sayra.umai.model.dto.ChunkType;
  import com.sayra.umai.model.dto.WorkStatus;
  import com.sayra.umai.service.*;
  import jakarta.persistence.EntityNotFoundException;
  import lombok.AllArgsConstructor;
  import lombok.NoArgsConstructor;
  import lombok.RequiredArgsConstructor;
  import lombok.extern.slf4j.Slf4j;
  import org.slf4j.Logger;
  import org.slf4j.LoggerFactory;
  import org.springframework.stereotype.Service;
  import org.springframework.transaction.annotation.Transactional;
  import org.springframework.web.multipart.MultipartFile;

  import java.io.File;
  import java.io.IOException;
  import java.time.LocalDateTime;
  import java.util.*;
  import java.util.stream.Collectors;

  @Service
  @RequiredArgsConstructor
  @Slf4j
  public class WorkServiceImpl implements WorkService {
      private final WorkDataService workDataService;
      private final AuthorDataService authorDataService;
      private final GenreDataService genreDataService;

      private final WorkMapper workMapper;
      private final PdfService pdfService;
      private final PdfTextService pdfTextService;
      private final DropboxService dropboxService;

      @Override
      @Transactional(readOnly=true)
      public List<AllWorksDTO> getAllWorks() {
        List<Work> works = workDataService.findAllWithGenresAndAuthor();
        works.sort(Comparator.comparing(Work::getTitle, Comparator.nullsLast(String::compareToIgnoreCase)).thenComparing(Work::getId));
        return workMapper.worksToAllWorksDTOs(works);
      }
    @Override
    @Transactional(readOnly=true)
    public WorkResponse findById(Long id) throws EntityNotFoundException {
        return workMapper.workToWorkResponse(workDataService.findByIdOrThrow(id));
    }

    @Transactional(readOnly=true)
    public List<AllWorksDTO> searchWorks(String query,
                                           Long authorId,
                                           List<Long> genreIds,
                                           WorkStatus status,
                                           LocalDateTime createdFrom,
                                           LocalDateTime createdTo,
                                           int page,
                                           int size) {

          long[] genresArray = (genreIds == null || genreIds.isEmpty())
                  ? new long[0]
                  : genreIds.stream().mapToLong(Long::longValue).toArray();

          boolean hasGenres = genresArray.length > 0;

          int limit = Math.max(1, Math.min(size, 100));
          int offset = Math.max(0, page) * limit;

          String statusStr = status == null ? null : status.name();

          List<Long> ids = workDataService.searchWorkIdsWithFTS(
                  query, authorId, genresArray, hasGenres, statusStr, createdFrom, createdTo, limit, offset
          );

          if (ids.isEmpty()) return List.of();

          List<Work> works = workDataService.findAllWithGenresAndAuthorByIds(ids);

          Map<Long, Integer> order = new HashMap<>();
          for (int i = 0; i < ids.size(); i++) order.put(ids.get(i), i);

          log.info("Found {} works for query '{}'", works.size(), query);

          return works.stream()
                  .sorted(Comparator.comparingInt(w -> order.getOrDefault(w.getId(), Integer.MAX_VALUE)))
                  .map(work -> {
                      List<GenreDTO> gd = work.getGenres().stream()
                              .sorted(Comparator.comparing(Genre::getName, Comparator.nullsLast(String::compareToIgnoreCase))
                                      .thenComparing(Genre::getId))
                              .map(genre -> new GenreDTO(genre.getId(), genre.getName()))
                              .collect(Collectors.toCollection(ArrayList::new));
                      AllWorksDTO dto = new AllWorksDTO();
                      dto.setId(work.getId());
                      dto.setTitle(work.getTitle());
                      dto.setDescription(work.getDescription());
                      dto.setAuthorName(work.getAuthor() != null ? work.getAuthor().getName() : "Unknown author");
                      dto.setGenres(gd);
                      return dto;
                  })
                  .toList();
      }

      @Transactional
      public Work uploadWork(MultipartFile pdfFile,
                             String title,
                             Long authorId,
                             Set<Long> genresId,
                             String description,
                             MultipartFile coverImage
      ) throws IOException {
          try{

              File cleanedPdf = pdfTextService.savePdf(pdfFile);

              List<PdfServiceImpl.ChapterData> chaptersData = pdfTextService.extractChapters(cleanedPdf);

              Author author = authorDataService.findByIdOrThrow(authorId);
              Set<Genre> genres = new HashSet<>();
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
                  } catch (IllegalArgumentException e) {
                      log.warn("Error uploading cover image:{}", e.getMessage());
                      throw e;
                  }catch(Exception e){
                      log.warn("Cover upload failed for work '{}', but continuing. Error: {}", title, e.getMessage());
                  }
              }

              Set<Chapter> chapters = new HashSet<>();
              for (PdfServiceImpl.ChapterData chData : chaptersData) {
                  Chapter chapter = new Chapter();
                  chapter.setChapterNumber(chData.chapterNumber());
                  chapter.setChapterTitle(chData.title());
                  chapter.setWork(work);

                  Set<Chunk> chunks = new HashSet<>();
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

              log.info("Uploaded work '{}'", title);
              return workDataService.saveWork(work);
          } catch(IllegalArgumentException e){
              log.warn("Invalid arguments: {}, {}, {}", title, authorId, genresId, e);
              throw e;
          } catch(RuntimeException e){
              log.error("Error uploading work '{}': ", title, e);
              throw new RuntimeException(e.getMessage());
          } catch(Exception e){
              log.error("Error uploading work: ", e);
              throw new RuntimeException(e.getMessage());
          }
      }

      /**
       * Загружает обложку для существующего произведения
       * @param workId ID произведения
       * @param coverImage файл обложки
       * @return URL загруженной обложки
       * @throws EntityNotFoundException если произведение не найдено
       */
      @Transactional
      public String uploadCover(Long workId, MultipartFile coverImage) throws EntityNotFoundException {
          Work work = workDataService.findByIdOrThrow(workId);

          if (coverImage == null || coverImage.isEmpty()) {
              log.error("Cover image is required");
              throw new IllegalArgumentException("Cover image is required");
          }

          try {
              String coverUrl = dropboxService.uploadFile(coverImage, "covers");
              work.setCoverUrl(coverUrl);
              workDataService.saveWork(work);
              return coverUrl;
          } catch (Exception e) {
              log.warn("Error uploading cover image for work '{}': {}", work.getTitle(), e.getMessage());
              throw new RuntimeException("Ошибка при загрузке обложки в Dropbox: " + e.getMessage());
          }
      }

      /**
       * Удаляет обложку произведения
       * @param workId ID произведения
       * @throws EntityNotFoundException если произведение не найдено
       */
      @Transactional
      public void deleteCover(Long workId) throws EntityNotFoundException {
          Work work = workDataService.findByIdOrThrow(workId);

          if (work.getCoverUrl() != null && !work.getCoverUrl().isEmpty()) {
              try {
                  // Извлекаем путь к файлу из URL для удаления из Dropbox
                  String filePath = extractFilePathFromUrl(work.getCoverUrl());
                  if (filePath != null) {
                      dropboxService.deleteFile(filePath);
                  }
              } catch (Exception e) {
                  // Логируем ошибку, но не прерываем выполнение
                  System.err.println("Ошибка при удалении обложки из Dropbox: " + e.getMessage());
              }
          }

          work.setCoverUrl(null);
          workDataService.saveWork(work);
      }

      /**
       * Извлекает путь к файлу из Dropbox URL
       * @param url URL файла в Dropbox
       * @return путь к файлу или null если не удалось извлечь
       */
      private String extractFilePathFromUrl(String url) {
          try {
              // URL выглядит как: https://www.dropbox.com/s/.../filename?raw=1
              // Нужно извлечь путь к файлу
              if (url.contains("dropbox.com")) {
                  // Простое извлечение - в реальном проекте может потребоваться более сложная логика
                  return null; // Пока возвращаем null, так как для удаления нужен точный путь
              }
          } catch (Exception e) {
              System.err.println("Ошибка при извлечении пути из URL: " + e.getMessage());
          }
          return null;
      }
  }
