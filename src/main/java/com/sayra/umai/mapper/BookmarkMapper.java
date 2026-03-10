package com.sayra.umai.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.sayra.umai.model.entity.work.Bookmark;
import com.sayra.umai.model.response.BookmarkResponse;

@Mapper(componentModel = "spring")
public interface BookmarkMapper {

    @Mapping(source = "work.id", target = "workId")
    @Mapping(source = "work.title", target = "workTitle")
    @Mapping(source = "work.coverUrl", target = "workImageUrl")
    @Mapping(source = "chapter.id", target = "chapterId")
    @Mapping(source = "chapter.chapterTitle", target = "chapterTitle")
    BookmarkResponse toBookmarkResponse(Bookmark bookmark);

}
