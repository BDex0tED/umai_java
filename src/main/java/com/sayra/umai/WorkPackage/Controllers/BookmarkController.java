package com.sayra.umai.WorkPackage.Controllers;

import com.sayra.umai.WorkPackage.DTO.BookmarkInDTO;
import com.sayra.umai.WorkPackage.Services.BookmarkService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.file.attribute.UserPrincipalNotFoundException;
import java.security.Principal;

@RestController
@RequestMapping("/api/bookmarks")
public class BookmarkController {
    private final BookmarkService bookmarkService;
    public BookmarkController(BookmarkService bookmarkService) {
        this.bookmarkService = bookmarkService;
    }
    @PostMapping("/create")
    public ResponseEntity<BookmarkInDTO> createBookmark(@RequestBody BookmarkInDTO bookmarkInDTO, Principal principal) throws UserPrincipalNotFoundException {
        return ResponseEntity.ok(bookmarkService.createBookmark(bookmarkInDTO, principal));
    }

    @GetMapping("/get-all")
    public ResponseEntity<Iterable<BookmarkInDTO>> getAllBookmarks(Principal principal) throws UserPrincipalNotFoundException, IllegalArgumentException {
        return ResponseEntity.ok(bookmarkService.getAllBookmarks(principal));
    }

    @GetMapping("/get/{id}")
    public ResponseEntity<BookmarkInDTO> getBookmark(@PathVariable Long id, Principal principal) throws UserPrincipalNotFoundException, IllegalArgumentException {
        return ResponseEntity.ok(bookmarkService.getBookmark(id, principal));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteBookmark(@PathVariable Long id, Principal principal) throws UserPrincipalNotFoundException, IllegalArgumentException {
        bookmarkService.deleteBookmark(id, principal);
        return ResponseEntity.ok("Bookmark deleted successfully");
    }

    @DeleteMapping("/delete-all")
    public ResponseEntity<String> deleteAllBookmarks(Principal principal) throws UserPrincipalNotFoundException, IllegalArgumentException {
        bookmarkService.deleteAllBookmarks(principal);
        return ResponseEntity.ok("All bookmarks deleted successfully");
    }



}
