package com.sayra.umai.WorkPackage.Controllers;

import com.sayra.umai.WorkPackage.DTO.*;
import com.sayra.umai.WorkPackage.DTO.AllWorksDTO;
import com.sayra.umai.WorkPackage.DTO.WorkOutDTO;
import com.sayra.umai.WorkPackage.Entities.Work;
import com.sayra.umai.WorkPackage.Other.WorkStatus;
import com.sayra.umai.WorkPackage.Services.WorkService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Set;
import java.util.List;

@RestController
@RequestMapping("/umai")
//@CrossOrigin(origins = {"http://localhost:5173"})
public class UmaiController {
    private final WorkService workService;
    public UmaiController(WorkService workService) {
        this.workService = workService;
    }
    @GetMapping("/home")
    public ResponseEntity<String> home(){
        return ResponseEntity.ok("Yokoso");
    }

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> uploadWork(
            @RequestParam("file") MultipartFile file,
            @RequestParam("title") String title,
            @RequestParam("authorId") Long authorId,
            @RequestParam(value = "genreIds", required = false) Set<Long> genresId,
            @RequestParam(value = "description", required = false) String description,
            @RequestParam(value = "cover", required = false) MultipartFile cover
    ) {
        try {
            Work saved = workService.uploadWork(file, title, authorId, genresId, description, cover);
            return ResponseEntity.status(HttpStatus.CREATED).body(saved.getId());
        } catch(IllegalArgumentException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        } catch(RuntimeException e){
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("error", e.getMessage()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

//    @GetMapping("/works/{work_id}/pages/{page_num}")
//    public ResponseEntity<PagesDTO> getPageOfWork(@PathVariable Long work_id, @PathVariable Integer page_num){
//        return workService.getPageOfWork(work_id, page_num);
//    }
//
    @GetMapping("/works")
    public ResponseEntity<List<AllWorksDTO>> getAllWorks(){
        return ResponseEntity.ok(workService.getAllWorks());
    }

    @GetMapping("/work/{id}")
    public ResponseEntity<WorkOutDTO> getWorkById(@PathVariable Long id){
        return ResponseEntity.ok(workService.findById(id));
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
        List<AllWorksDTO> result = workService.searchWorks(q, authorId, genreIds, status, createdFrom, createdTo, page, size);
        return ResponseEntity.ok(result);
    }



}
