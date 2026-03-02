package com.sayra.umai.service.impl;

import com.sayra.umai.model.entity.user.UserEntity;
import com.sayra.umai.repo.UserEntityRepo;
import com.sayra.umai.model.request.BookmarkRequest;
import com.sayra.umai.model.entity.work.Bookmark;
import com.sayra.umai.model.entity.work.Chapter;
import com.sayra.umai.model.entity.work.Work;
import com.sayra.umai.repo.BookmarkRepo;
import com.sayra.umai.repo.ChapterRepo;
import com.sayra.umai.repo.WorkRepo;
import com.sayra.umai.repo_service.BookmarkDataService;
import com.sayra.umai.service.BookmarkService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.file.attribute.UserPrincipalNotFoundException;
import java.security.Principal;

import java.util.*;

@Service
public class BookmarkServiceImpl implements BookmarkService {
    private final UserEntityRepo userRepo;
    private final WorkRepo workRepo;
    private final ChapterRepo chapterRepo;
    private final BookmarkDataService bookmarkDataService;
    public BookmarkServiceImpl(BookmarkDataService bookmarkDataService,
                               UserEntityRepo userRepo,
                               WorkRepo workRepo,
                               ChapterRepo chapterRepo) {
        this.bookmarkDataService = bookmarkDataService;
        this.userRepo = userRepo;
        this.workRepo = workRepo;
        this.chapterRepo = chapterRepo;
    }

    @Transactional
    @Override
    public BookmarkRequest createBookmark(BookmarkRequest bookmarkRequest, Principal principal) throws UserPrincipalNotFoundException {
        UserEntity user = userRepo.findByUsername(principal.getName()).orElseThrow(()->new UserPrincipalNotFoundException("User with id" + bookmarkRequest.getUserId() + " not found"));
        Work work = workRepo.findById(bookmarkRequest.getWorkId()).orElseThrow(()->new IllegalArgumentException("Work with id: "+ bookmarkRequest.getWorkId()+" not found"));
        Chapter chapter = chapterRepo.findById(bookmarkRequest.getChapterId()).orElseThrow(()->new IllegalArgumentException("Chapter with id: "+ bookmarkRequest.getChapterId()+" not found"));

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

        bookmarkDataService.save(bookmark);

        return bookmarkRequest;
    }

    @Transactional(readOnly = true)
    @Override
    public List<BookmarkRequest> getAllBookmarks(Principal principal) throws UserPrincipalNotFoundException {
        UserEntity user = userRepo.findByUsername(principal.getName()).orElseThrow(()->new UserPrincipalNotFoundException("User with id" + principal.getName() + " not found"));
        List<Bookmark> bookmarks = bookmarkDataService.findAllByUserOrThrow(user);
        List<BookmarkRequest> bookmarkRequests = new ArrayList<>();
        for(Bookmark bookmark : bookmarks){
            BookmarkRequest bookmarkRequest = getBookmarkInDTO(bookmark);
            bookmarkRequests.add(bookmarkRequest);
        }
        return bookmarkRequests;
    }

    @Transactional(readOnly = true)
    @Override
    public BookmarkRequest getBookmark(Long id, Principal principal) throws UserPrincipalNotFoundException {
        UserEntity user = userRepo.findByUsername(principal.getName()).orElseThrow(()->new UserPrincipalNotFoundException("User not found"));

        Bookmark bookmark = bookmarkDataService.findByIdAndUserOrThrow(id, user);

        return getBookmarkInDTO(bookmark);

    }

    @Transactional
    @Override
    public void deleteBookmark(Long id, Principal principal) throws UserPrincipalNotFoundException {
        UserEntity user = userRepo.findByUsername(principal.getName()).orElseThrow(()->new UserPrincipalNotFoundException("User not found"));
        Bookmark bookmark = bookmarkDataService.findByIdAndUserOrThrow(id, user);
        bookmarkDataService.delete(bookmark);
    }

    @Transactional
    @Override
    public void deleteAllBookmarks(Principal principal) throws UserPrincipalNotFoundException {
        UserEntity user = userRepo.findByUsername(principal.getName()).orElseThrow(()->new UserPrincipalNotFoundException("User not found"));
        bookmarkDataService.deleteAllByUserOrThrow(user);
    }

    private static BookmarkRequest getBookmarkInDTO(Bookmark bookmark) {
        BookmarkRequest bookmarkRequest = new BookmarkRequest();
        bookmarkRequest.setWorkId(bookmark.getWork().getId());
        bookmarkRequest.setChapterId(bookmark.getChapter().getId());
        bookmarkRequest.setChunkId(bookmark.getChunkId());
        bookmarkRequest.setUserNote(bookmark.getUserNote());
        bookmarkRequest.setWorkNote(bookmark.getWorkNote());
        bookmarkRequest.setStartOffset(bookmark.getStartOffset());
        bookmarkRequest.setEndOffset(bookmark.getEndOffset());
        return bookmarkRequest;
    }

}
