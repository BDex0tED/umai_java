package com.sayra.umai.service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.Named;

import com.sayra.umai.model.dto.AllWorksDTO;
import com.sayra.umai.model.dto.GenreDTO;
import com.sayra.umai.model.entity.work.*;
import com.sayra.umai.model.response.AuthorResponse;
import com.sayra.umai.model.response.ChapterResponse;
import com.sayra.umai.model.response.ChunkResponse;
import com.sayra.umai.model.response.WorkResponse;

@Mapper(componentModel = "spring")
public interface WorkMapper {
  GenreDTO genreToGenreDTO(Genre genre);

  List<GenreDTO> genresToGenreDTOs(List<Genre> genres);

  List<GenreDTO> genresSetToGenreDTOSet(List<Genre> genres);

  AuthorResponse authorToAuthorResponse(Author author);

  @Mappings({
      @Mapping(source = "author.name", target = "authorName", defaultValue = "Unknown author"),
      @Mapping(source = "genres", target = "genres"),
      @Mapping(source = "coverUrl", target = "imageUrl")
  })
  AllWorksDTO workToAllWorksDTO(Work work);

  List<AllWorksDTO> worksToAllWorksDTOs(List<Work> works);

  @Mappings({
      @Mapping(source = "id", target = "chunkId"),
      @Mapping(source = "chunkNumber", target = "chunkNumber"),
      @Mapping(source = "type", target = "chunkType"),
      @Mapping(source = "text", target = "text")
  })
  ChunkResponse chunkToChunkResponse(Chunk chunk);

  @Named("sortedChunks")
  default List<ChunkResponse> chunksToChunkResponses(List<Chunk> chunks) {
    if (chunks == null)
      return List.of();
    return chunks.stream()
        .sorted(Comparator.comparing(Chunk::getChunkNumber, Comparator.nullsLast(Comparator.naturalOrder())))
        .map(this::chunkToChunkResponse)
        .collect(Collectors.toList());
  }

  @Mappings({
      @Mapping(source = "chapterNumber", target = "chapterNumber"),
      @Mapping(source = "chapterTitle", target = "chapterTitle"),
      @Mapping(source = "chunks", target = "chunks", qualifiedByName = "sortedChunks")
  })
  ChapterResponse chapterToChapterResponse(Chapter chapter);

  @Named("sortedChapters")
  default List<ChapterResponse> chaptersToChapterResponses(List<Chapter> chapters) {
    if (chapters == null)
      return List.of();
    return chapters.stream()
        .sorted(Comparator.comparing(Chapter::getChapterNumber, Comparator.nullsLast(Comparator.naturalOrder())))
        .map(this::chapterToChapterResponse)
        .collect(Collectors.toList());
  }

  @Mappings({
      @Mapping(source = "id", target = "workId"),
      @Mapping(source = "author", target = "author"),
      @Mapping(source = "genres", target = "genres"),
      @Mapping(source = "chapters", target = "chapters", qualifiedByName = "sortedChapters"),
      @Mapping(target = "otherWorks", ignore = true)
  })
  WorkResponse workToWorkResponse(Work work);
}
