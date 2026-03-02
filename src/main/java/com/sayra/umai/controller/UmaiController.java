package com.sayra.umai.controller;

import com.sayra.umai.model.dto.AllWorksDTO;
import com.sayra.umai.model.response.WorkResponse;
import com.sayra.umai.model.entity.work.Work;
import com.sayra.umai.model.dto.WorkStatus;
import com.sayra.umai.service.impl.WorkServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/umai")
@RequiredArgsConstructor
public class UmaiController {

    private final WorkServiceImpl workServiceImpl;

    @GetMapping("/home")
    public ResponseEntity<String> home(){
        return ResponseEntity.ok("Yokoso");
    }

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Long> uploadWork(
            @RequestParam("file") MultipartFile file,
            @RequestParam("title") String title,
            @RequestParam("authorId") Long authorId,
            @RequestParam(value = "genreIds", required = false) Set<Long> genresId,
            @RequestParam(value = "description", required = false) String description,
            @RequestParam(value = "cover", required = false) MultipartFile cover
    ) throws IOException {

        Work saved = workServiceImpl.uploadWork(file, title, authorId, genresId, description, cover);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved.getId());
    }

    @GetMapping("/works")
    public ResponseEntity<List<AllWorksDTO>> getAllWorks(){
        return ResponseEntity.ok(workServiceImpl.getAllWorks());
    }

    @GetMapping("/work/{id}")
    public ResponseEntity<WorkResponse> getWorkById(@PathVariable Long id){
        return ResponseEntity.ok(workServiceImpl.findById(id));
    }

    @GetMapping("/works/search")
    public ResponseEntity<List<AllWorksDTO>> search(
            @RequestParam(value = "q", required = false) String q,
            @RequestParam(value = "authorId", required = false) Long authorId,
            @RequestParam(value = "genreIds", required = false) List<Long> genreIds,
            @RequestParam(value = "status", required = false) WorkStatus status,
            @RequestParam(value = "createdFrom", required = false) LocalDateTime createdFrom,
            @RequestParam(value = "createdTo", required = false) LocalDateTime createdTo,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "20") int size
    ){
        List<AllWorksDTO> result = workServiceImpl.searchWorks(q, authorId, genreIds, status, createdFrom, createdTo, page, size);
        return ResponseEntity.ok(result);
    }
}