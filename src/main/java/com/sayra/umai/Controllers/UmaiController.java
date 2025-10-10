package com.sayra.umai.Controllers;

import com.sayra.umai.DTO.*;
import com.sayra.umai.Entities.Work;
import com.sayra.umai.Services.WorkService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;
import java.util.Set;
import java.util.List;

@RestController
@RequestMapping("/umai")
@CrossOrigin(origins = {"http://localhost:5173"})
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
            @RequestParam("author") Long authorId,
            @RequestParam(value = "genre", required = false) Set<Long> genresId,
            @RequestParam(value = "description", required = false) String description,
            @RequestParam(value = "cover", required = false) MultipartFile cover
    ) {
        try {
            Work saved = workService.uploadWork(file, title, authorId, genresId, description, cover);
            return ResponseEntity.ok(Map.of("workId", saved.getId()));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", e.getMessage()));
        }
    }

//    @GetMapping("/works/{work_id}/pages/{page_num}")
//    public ResponseEntity<PagesDTO> getPageOfWork(@PathVariable Long work_id, @PathVariable Integer page_num){
//        return workService.getPageOfWork(work_id, page_num);
//    }
//
    @GetMapping("/works")
    public ResponseEntity<Set<AllWorksDTO>> getAllWorks(){
        return ResponseEntity.ok(workService.getAllWorks());
    }

    @GetMapping("/work/{id}")
    public ResponseEntity<WorkOutDTO> getWorkById(@PathVariable Long id){
        return ResponseEntity.ok(workService.findById(id));
    }


}
