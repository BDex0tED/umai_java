package com.sayra.umai.WorkPackage.Services;

import com.sayra.umai.WorkPackage.DTO.*;
import com.sayra.umai.WorkPackage.Entities.*;
import com.sayra.umai.WorkPackage.Other.ChunkType;
import com.sayra.umai.WorkPackage.Other.WorkStatus;
import com.sayra.umai.WorkPackage.DTO.*;
import com.sayra.umai.WorkPackage.Entities.*;
import com.sayra.umai.WorkPackage.Repos.AuthorRepo;
import com.sayra.umai.WorkPackage.Repos.ChapterRepo;
import com.sayra.umai.WorkPackage.Repos.GenreRepo;
import com.sayra.umai.WorkPackage.Repos.WorkRepo;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

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

    public List<AllWorksDTO> getAllWorks(){
        List<Work> works = workRepo.findAllWithGenresAndAuthor();

        return works.stream()
                .sorted(Comparator.comparing(Work::getTitle, Comparator.nullsLast(String::compareToIgnoreCase))
                        .thenComparing(Work::getId))
                .map(work->{
                    List<GenreDTO> genreDTOS = work.getGenres().stream()
                            .sorted(Comparator.comparing(Genre::getName, Comparator.nullsLast(String::compareToIgnoreCase))
                                    .thenComparing(Genre::getId))
                            .map(genre-> new GenreDTO(genre.getId(), genre.getName()))
                            .collect(Collectors.toCollection(ArrayList::new));
                    AllWorksDTO allWorksDTO = new AllWorksDTO();
                    allWorksDTO.setId(work.getId());
                    allWorksDTO.setTitle(work.getTitle());
                    allWorksDTO.setDescription(work.getDescription());
                    if(work.getAuthor() != null){
                        allWorksDTO.setAuthorName(work.getAuthor().getName());
                    }else{
                        allWorksDTO.setAuthorName("Unknown author");
                    }
                    allWorksDTO.setGenres(genreDTOS);
                    return allWorksDTO;
                })
                .collect(Collectors.toCollection(ArrayList::new));
    }

    public WorkOutDTO findById(Long id) throws EntityNotFoundException {
        Optional<Work> optionalWork = workRepo.findByIdWithFullContent(id);
        if(optionalWork.isEmpty()){
            throw new EntityNotFoundException("Work with id " + id + " not found");
        }
        Work work = optionalWork.get();

        List<Chapter> sortedChapters = new ArrayList<>(work.getChapters());
        sortedChapters.sort(Comparator.comparingInt(Chapter::getChapterNumber));

        Set<ChapterOutDTO> chapterOutDTOS = new LinkedHashSet<>();
        for(Chapter chapter : sortedChapters){
            List<Chunk> sortedChunks = new ArrayList<>(chapter.getChunks());
            sortedChunks.sort(Comparator.comparingInt(Chunk::getChunkNumber));

            Set<ChunkOutDTO> chunkOutDTOS = new LinkedHashSet<>();
            for(Chunk chunk : sortedChunks){
                ChunkOutDTO chunkOutDTO = new ChunkOutDTO(chunk.getId(), chunk.getChunkNumber(), chunk.getType(), chunk.getText());
                chunkOutDTOS.add(chunkOutDTO);
            }
            ChapterOutDTO chapterOutDTO = new ChapterOutDTO(chapter.getChapterNumber(), chapter.getChapterTitle(),chunkOutDTOS);
            chapterOutDTOS.add(chapterOutDTO);
        }
        AuthorSummaryDTO authorDTO = null;
        if (work.getAuthor() != null) {
            authorDTO = new AuthorSummaryDTO(work.getAuthor().getId(), work.getAuthor().getName());
        }

        Set<GenreDTO> genreDTOS = work.getGenres() == null ? Collections.emptySet()
                : work.getGenres().stream()
                .sorted(Comparator.comparing(Genre::getName, Comparator.nullsLast(String::compareToIgnoreCase))
                        .thenComparing(Genre::getId))
                .map(g -> new GenreDTO(g.getId(), g.getName()))
                .collect(Collectors.toCollection(LinkedHashSet::new));

        List<WorkBriefDTO> otherWorks = Collections.emptyList();
        if (work.getAuthor() != null) {
            List<Work> sameAuthorWorks = workRepo.findAllByAuthor_Id(work.getAuthor().getId());
            otherWorks = sameAuthorWorks.stream()
                    .filter(w -> !Objects.equals(w.getId(), work.getId()))
                    .sorted(Comparator.comparing(Work::getTitle, Comparator.nullsLast(String::compareToIgnoreCase))
                            .thenComparing(Work::getId))
                    .limit(10)
                    .map(w -> new WorkBriefDTO(w.getId(), w.getTitle(), null))
                    .toList();
        }

        return new WorkOutDTO(
                work.getId(),
                work.getTitle(),
                work.getDescription(),
                authorDTO,
                genreDTOS,
                chapterOutDTOS,
                work.getCoverUrl(),
                otherWorks
        );
    }

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

        List<Long> ids = workRepo.searchWorkIdsWithFTS(
                query, authorId, genresArray, hasGenres, statusStr, createdFrom, createdTo, limit, offset
        );

        if (ids.isEmpty()) return List.of();

        Set<Work> works = workRepo.findAllWithGenresAndAuthorByIds(ids);

        Map<Long, Integer> order = new HashMap<>();
        for (int i = 0; i < ids.size(); i++) order.put(ids.get(i), i);

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

            File cleanedPdf = pdfService.savePdf(pdfFile);

            List<PdfService.ChapterData> chaptersData = pdfService.extractChapters(cleanedPdf);

            Author author = authorRepo.findById(authorId).orElseThrow(()-> new IllegalArgumentException("Author with id: " + authorId + " not found"));
            Set<Genre> genres = new HashSet<>();
            if(genresId != null && !genresId.isEmpty()){
                for(Long genreId : genresId){
                    Genre genre = genreRepo.findById(genreId).orElseThrow(()-> new IllegalArgumentException("Genre with id: "+ genreId+" not found"));
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

            Set<Chapter> chapters = new HashSet<>();
            for (PdfService.ChapterData chData : chaptersData) {
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

            return workRepo.save(work);
        } catch(IllegalArgumentException e){
            throw new IllegalArgumentException(e.getMessage());
        } catch(RuntimeException e){
            throw new RuntimeException(e.getMessage());
        } catch(Exception e){
            throw new RuntimeException(e.getMessage());
        }
    }
}
