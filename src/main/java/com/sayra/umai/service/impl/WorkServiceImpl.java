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

          // Use the stored path! No more URL parsing needed.
          if (work.getCoverDropboxPath() != null) {
              try {
                  dropboxService.deleteFile(work.getCoverDropboxPath());
              } catch (Exception e) {
                  log.error("Ошибка при удалении обложки из Dropbox: " + e.getMessage());
              }
          }

          work.setCoverUrl(null);
          work.setCoverDropboxPath(null); // Clear the path
          workDataService.saveWork(work);
      }

      @Transactional
      public Work uploadWork(MultipartFile pdfFile, String title, Long authorId, Set<Long> genresId, String description, MultipartFile coverImage) throws IOException {

          File tempPdf = pdfTextService.savePdf(pdfFile);
          List<PdfServiceImpl.ChapterData> chaptersData;

          try {
              chaptersData = pdfTextService.extractChapters(tempPdf);
          } finally {
              if (tempPdf != null && tempPdf.exists()) {
                  tempPdf.delete();
                  log.info("Temporary PDF file deleted successfully.");
              }
          }

          Work work = buildBaseWork(title, authorId, genresId, description, null);

          uploadAndAttachCover(work, coverImage);

          buildAndAttachChapters(work, chaptersData);

          log.info("Successfully uploaded and processed work '{}'", title);
          return workDataService.saveWork(work);
      }

      private Work buildBaseWork(String title, Long authorId, Set<Long> genresId, String description, String filePath) {
          Author author = authorDataService.findByIdOrThrow(authorId);

          Set<Genre> genres = new HashSet<>();
          if (genresId != null && !genresId.isEmpty()) {
              genres = genresId.stream().map(genreDataService::findByIdOrThrow).collect(Collectors.toSet());
          }

          Work work = new Work();
          work.setTitle(title);
          work.setAuthor(author);
          work.setDescription(description);
          work.setFilePath(filePath);
          work.setGenres(genres);
          work.setStatus(WorkStatus.PENDING);

          return work;
      }

      private void uploadAndAttachCover(Work work, MultipartFile coverImage) {
          if (coverImage == null || coverImage.isEmpty()) {
              return;
          }

          try {
              String uniqueFilename = UUID.randomUUID().toString() + "_" + coverImage.getOriginalFilename();
              String dropboxPath = "/covers/" + uniqueFilename;

              String coverUrl = dropboxService.uploadFile(coverImage, dropboxPath);

              work.setCoverUrl(coverUrl);
              work.setCoverDropboxPath(dropboxPath);
          } catch (IllegalArgumentException e) {
              log.warn("Invalid cover image provided: {}", e.getMessage());
              throw e;
          } catch (Exception e) {
              log.warn("Cover upload failed for work '{}', but continuing. Error: {}", work.getTitle(), e.getMessage());
          }
      }

      private void buildAndAttachChapters(Work work, List<PdfServiceImpl.ChapterData> chaptersData) {
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
      }
  }
