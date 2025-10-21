package com.sayra.umai.WorkPackage.Services;

import com.sayra.umai.UserPackage.Entities.UserEntity;
import com.sayra.umai.UserPackage.Repo.UserEntityRepo;
import com.sayra.umai.WorkPackage.DTO.BookmarkInDTO;
import com.sayra.umai.WorkPackage.Entities.Bookmark;
import com.sayra.umai.WorkPackage.Entities.Chapter;
import com.sayra.umai.WorkPackage.Entities.Work;
import com.sayra.umai.WorkPackage.Repos.BookmarkRepo;
import com.sayra.umai.WorkPackage.Repos.ChapterRepo;
import com.sayra.umai.WorkPackage.Repos.WorkRepo;
import org.springframework.stereotype.Service;

import java.nio.file.attribute.UserPrincipalNotFoundException;
import java.security.Principal;

import java.util.*;

@Service
public class BookmarkService {
    private final UserEntityRepo userRepo;
    private final WorkRepo workRepo;
    private final ChapterRepo chapterRepo;
    private final BookmarkRepo bookmarkRepo;
    public BookmarkService(BookmarkRepo bookmarkRepo, UserEntityRepo userRepo, WorkRepo workRepo, ChapterRepo chapterRepo) {
        this.bookmarkRepo = bookmarkRepo;
        this.userRepo = userRepo;
        this.workRepo = workRepo;
        this.chapterRepo = chapterRepo;
    }

    public BookmarkInDTO createBookmark(BookmarkInDTO bookmarkInDTO, Principal principal) throws UserPrincipalNotFoundException {
        UserEntity user = userRepo.findByUsername(principal.getName()).orElseThrow(()->new UserPrincipalNotFoundException("User with id" + bookmarkInDTO.getUserId() + " not found"));
        Work work = workRepo.findById(bookmarkInDTO.getWorkId()).orElseThrow(()->new IllegalArgumentException("Work with id: "+bookmarkInDTO.getWorkId()+" not found"));
        Chapter chapter = chapterRepo.findById(bookmarkInDTO.getChapterId()).orElseThrow(()->new IllegalArgumentException("Chapter with id: "+bookmarkInDTO.getChapterId()+" not found"));

        Bookmark bookmark = new Bookmark();
        bookmark.setWork(work);
        bookmark.setUser(user);
        bookmark.setChapter(chapter);
        bookmark.setChunkId(bookmarkInDTO.getChunkId());
        bookmark.setUserNote(bookmarkInDTO.getUserNote());
        bookmark.setWorkNote(bookmarkInDTO.getWorkNote());
        bookmark.setStartOffset(bookmarkInDTO.getStartOffset());
        bookmark.setEndOffset(bookmarkInDTO.getEndOffset());

        bookmark.validateOffsets();

        bookmarkRepo.save(bookmark);

        return bookmarkInDTO;
    }

    public List<BookmarkInDTO> getAllBookmarks(Principal principal) throws UserPrincipalNotFoundException {
        UserEntity user = userRepo.findByUsername(principal.getName()).orElseThrow(()->new UserPrincipalNotFoundException("User with id" + principal.getName() + " not found"));
        List<Bookmark> bookmarks = bookmarkRepo.findAllByUser(user);
        List<BookmarkInDTO> bookmarkInDTOS = new ArrayList<>();
        for(Bookmark bookmark : bookmarks){
            BookmarkInDTO bookmarkInDTO = getBookmarkInDTO(bookmark);
            bookmarkInDTOS.add(bookmarkInDTO);
        }
        return bookmarkInDTOS;
    }

    private static BookmarkInDTO getBookmarkInDTO(Bookmark bookmark) {
        BookmarkInDTO bookmarkInDTO = new BookmarkInDTO();
        bookmarkInDTO.setWorkId(bookmark.getWork().getId());
        bookmarkInDTO.setChapterId(bookmark.getChapter().getId());
        bookmarkInDTO.setChunkId(bookmark.getChunkId());
        bookmarkInDTO.setUserNote(bookmark.getUserNote());
        bookmarkInDTO.setWorkNote(bookmark.getWorkNote());
        bookmarkInDTO.setStartOffset(bookmark.getStartOffset());
        bookmarkInDTO.setEndOffset(bookmark.getEndOffset());
        return bookmarkInDTO;
    }

}
