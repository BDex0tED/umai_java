package com.sayra.umai.service;

import com.sayra.umai.model.request.BookmarkRequest;
import com.sayra.umai.model.response.BookmarkResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.nio.file.attribute.UserPrincipalNotFoundException;


public interface BookmarkService {
    BookmarkResponse createBookmark(BookmarkRequest bookmarkRequest);
    Page<BookmarkResponse> getAllBookmarks(Pageable pageable);
    BookmarkResponse getBookmark(Long id);
    void deleteBookmark(Long id);
    void deleteAllBookmarks();
}
