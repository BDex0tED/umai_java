package com.sayra.umai.service.impl;

import com.sayra.umai.exception.ResourceNotFoundException;
import com.sayra.umai.mapper.BookmarkMapper;
import com.sayra.umai.model.entity.user.UserEntity;
import com.sayra.umai.model.response.BookmarkResponse;
import com.sayra.umai.repo.UserEntityRepo;
import com.sayra.umai.model.request.BookmarkRequest;
import com.sayra.umai.model.entity.work.Bookmark;
import com.sayra.umai.model.entity.work.Chapter;
import com.sayra.umai.model.entity.work.Work;
import com.sayra.umai.repo.BookmarkRepo;
import com.sayra.umai.repo.ChapterRepo;
import com.sayra.umai.repo.WorkRepo;
import com.sayra.umai.service.BookmarkService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor

public class BookmarkServiceImpl implements BookmarkService {
    private final UserEntityRepo userRepo;
    private final WorkRepo workRepo;
    private final ChapterRepo chapterRepo;
    private final UserService userService;
    private final BookmarkRepo bookmarkRepo;
    private final BookmarkMapper bookmarkMapper;

    @Transactional
    @Override
    public BookmarkResponse createBookmark(BookmarkRequest bookmarkRequest) {
        UserEntity user = userService.getCurrentUser();
        Work work = workRepo.findById(bookmarkRequest.getWorkId()).orElseThrow(()->new ResourceNotFoundException("Work with id: "+ bookmarkRequest.getWorkId()+" not found"));
        Chapter chapter = chapterRepo.findById(bookmarkRequest.getChapterId()).orElseThrow(()->new ResourceNotFoundException("Chapter with id: "+ bookmarkRequest.getChapterId()+" not found"));

        Bookmark bookmark = new Bookmark();
        bookmark.setWork(work);
        bookmark.setUser(user);
        bookmark.setChapter(chapter);
        bookmark.setChunkId(bookmarkRequest.getChunkId());
        bookmark.setUserNote(bookmarkRequest.getUserNote());
        bookmark.setWorkNote(bookmarkRequest.getWorkNote());
        bookmark.setStartOffset(bookmarkRequest.getStartOffset());
        bookmark.setEndOffset(bookmarkRequest.getEndOffset());

        bookmark.validateOffsets();

        bookmarkRepo.save(bookmark);

        return bookmarkMapper.toBookmarkResponse(bookmark);
    }

    @Transactional(readOnly = true)
    @Override
    public Page<BookmarkResponse> getAllBookmarks(Pageable pageable)  {
        UserEntity user = userService.getCurrentUser();
        Page<Bookmark> bookmarks = bookmarkRepo.findAllByUser(user, pageable);

        return bookmarks.map(bookmarkMapper::toBookmarkResponse);
    }

    @Transactional(readOnly = true)
    @Override
    public BookmarkResponse getBookmark(Long id) {
        UserEntity user = userService.getCurrentUser();

        Bookmark bookmark = bookmarkRepo.findByIdAndUser(id, user).orElseThrow
                (()->new ResourceNotFoundException("Bookmark with id: " + id + " not found"));

        return bookmarkMapper.toBookmarkResponse(bookmark);

    }

    @Transactional
    @Override
    public void deleteBookmark(Long id){
        UserEntity user = userService.getCurrentUser();
        Bookmark bookmark = bookmarkRepo.findByIdAndUser(id, user).orElseThrow
                (()->new ResourceNotFoundException("Bookmark with id: " + id + " not found"));
        bookmarkRepo.delete(bookmark);
    }

    @Transactional
    @Override
    public void deleteAllBookmarks() {
        UserEntity user = userService.getCurrentUser();
        bookmarkRepo.deleteAllByUser(user);
    }


}
