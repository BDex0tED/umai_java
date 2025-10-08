package com.sayra.umai.Services;

import com.sayra.umai.DTO.ChapterOutDTO;
import com.sayra.umai.DTO.ChunkOutDTO;
import com.sayra.umai.DTO.WorkOutDTO;
import com.sayra.umai.Entities.*;
import com.sayra.umai.Other.ChunkType;
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
import java.util.*;

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

    public ResponseEntity<WorkOutDTO> findById(Long id){
        Optional<Work> optionalWork = workRepo.findById(id);
        if(optionalWork.isEmpty()){
            return ResponseEntity.notFound().build();
        }
        Work work = optionalWork.get();
        List<ChapterOutDTO> chapterOutDTOS = new ArrayList<>();
        for(Chapter chapter : work.getChapters()){
            List<ChunkOutDTO> chunkOutDTOS = new ArrayList<>();
            for(Chunk chunk : chapter.getChunks()){
                ChunkOutDTO chunkOutDTO = new ChunkOutDTO(chunk.getId(), chunk.getChunkNumber(), chunk.getType(), chunk.getText());
                chunkOutDTOS.add(chunkOutDTO);}
            ChapterOutDTO chapterOutDTO = new ChapterOutDTO(chapter.getChapterNumber(), chapter.getChapterTitle(),chunkOutDTOS);
            chapterOutDTOS.add(chapterOutDTO);
        }
        WorkOutDTO workOutDTO = new WorkOutDTO(work.getId(), work.getTitle(), work.getDescription(),work.getAuthor(),work.getGenres(),chapterOutDTOS);
        return  ResponseEntity.ok(workOutDTO);
    }

    public List<Work> getAllWorks(){
        return workRepo.findAllBy();
    }

    @Transactional
    public Work uploadWork(MultipartFile pdfFile,
                           String title,
                           Long authorId,
                           Set<Long> genresId,
                           String description,
                           MultipartFile coverImage //null pokachto
    ) throws IOException {

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
                chunk.setText(chunkText);
                //needed to be redone if img is here
                //it needs to get the type from pdfservice, it will provide
                chunk.setType(ChunkType.html);
                chunk.setChapter(chapter);
                chunks.add(chunk);
            }
            chapter.setChunks(chunks);
            chapters.add(chapter);
        }

        work.setChapters(chapters);

        Work saved = workRepo.save(work);

        return saved;
    }




}
