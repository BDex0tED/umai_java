package com.sayra.umai.repo_service;

import com.sayra.umai.model.entity.work.Chapter;
import com.sayra.umai.model.entity.work.Work;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

public interface WorkDataService {
  List<Work> findAllWithGenresAndAuthor();

  Work findByIdWithFullContent(Long id);

  Work findByIdOrThrow(Long id);

  List<Work> findOtherWorksByAuthor(Long authorId, Long excludeWorkId);

  List<Work> findAllById(List<Long> workIds);

//  List<Work> findWorksBySearch(SearchRequest request);

  @Transactional
  Work saveWork(Work work);

  @Transactional
  Work saveWorkAndChapters(Work work, List<Chapter> chapters);

  List<Long> searchWorkIdsWithFTS(@Param("query") String query,
                                  @Param("authorId") Long authorId,
                                  @Param("genreIds") long[] genreIds,
                                  @Param("hasGenres") boolean hasGenres,
                                  @Param("status") String status,
                                  @Param("createdFrom") LocalDateTime createdFrom,
                                  @Param("createdTo") LocalDateTime createdTo,
                                  @Param("limit") int limit,
                                  @Param("offset") int offset);

  List<Work> findAllWithGenresAndAuthorByIds(List<Long> ids);

}
