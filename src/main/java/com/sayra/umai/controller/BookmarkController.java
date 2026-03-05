package com.sayra.umai.controller;

import com.sayra.umai.model.request.BookmarkRequest;
import com.sayra.umai.model.response.BookmarkResponse;
import com.sayra.umai.service.BookmarkService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.nio.file.attribute.UserPrincipalNotFoundException;

@RestController
@RequestMapping("/api/bookmarks")
@RequiredArgsConstructor
@Validated
public class BookmarkController {
    private final BookmarkService bookmarkService;

    @PostMapping
    public ResponseEntity<BookmarkResponse> createBookmark(@RequestBody @Valid BookmarkRequest bookmarkRequest) {
        return ResponseEntity.ok(bookmarkService.createBookmark(bookmarkRequest));
    }

    @GetMapping
    public ResponseEntity<Page<BookmarkResponse>> getAllBookmarks(@PageableDefault(size = 25, direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(bookmarkService.getAllBookmarks(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<BookmarkResponse> getBookmark(@PathVariable @Positive Long id){
        return ResponseEntity.ok(bookmarkService.getBookmark(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBookmark(@PathVariable @Positive Long id){
        bookmarkService.deleteBookmark(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteAllBookmarks() {
        bookmarkService.deleteAllBookmarks();
        return ResponseEntity.noContent().build();
    }



}
