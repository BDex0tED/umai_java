package com.sayra.umai.controller;


import com.sayra.umai.model.dto.AllWorksDTO;
import com.sayra.umai.model.request.UploadWorkRequest;
import com.sayra.umai.model.response.WorkResponse;
import com.sayra.umai.model.entity.work.Work;
import com.sayra.umai.model.dto.WorkStatus;
import com.sayra.umai.service.WorkService;
import com.sayra.umai.service.impl.WorkServiceImpl;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/works")
@RequiredArgsConstructor
@Validated
public class WorkController  {

    private final WorkService workService;

    @GetMapping("/home")
    public ResponseEntity<String> home(){
        return ResponseEntity.ok("Yokoso");
    }

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Long> uploadWork(
            @ModelAttribute @RequestBody UploadWorkRequest uploadWorkRequest,
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "cover", required = false) MultipartFile cover
    ) throws IOException, Exception {

        Work saved = workService.uploadWork(uploadWorkRequest, file, cover);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved.getId());
    }

    @GetMapping
    public ResponseEntity<Page<AllWorksDTO>> getAllWorks(@PageableDefault(size = 20, direction = Sort.Direction.ASC)Pageable pageable){
        return ResponseEntity.ok(workService.getAllWorks(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<WorkResponse> getWorkById(@PathVariable @Positive Long id){
        return ResponseEntity.ok(workService.findById(id));
    }

    @GetMapping("/search")
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
        List<AllWorksDTO> result = workService.searchWorks(q, authorId, genreIds, status, createdFrom, createdTo, page, size);
        return ResponseEntity.ok(result);
    }
}