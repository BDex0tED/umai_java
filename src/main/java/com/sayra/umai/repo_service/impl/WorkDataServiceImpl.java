package com.sayra.umai.repo_service.impl;

import com.sayra.umai.model.entity.work.Chapter;
import com.sayra.umai.model.entity.work.Work;
import com.sayra.umai.repo.WorkRepo;
import com.sayra.umai.repo_service.WorkDataService;
import jakarta.transaction.Transactional;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
public class WorkDataServiceImpl implements WorkDataService {
  private final WorkRepo workRepo;
  public WorkDataServiceImpl(WorkRepo workRepo) {
    this.workRepo = workRepo;

  }

  @Override
  public List<Work> findAllWithGenresAndAuthor() {
    return workRepo.findAllWithGenresAndAuthor();
  }

  @Override
  public Work findByIdWithFullContent(Long id) {
    return workRepo.findByIdWithFullContent(id)
            .orElseThrow(()->new IllegalArgumentException("Work with id " + id + " not found"));
  }

  @Override
  public Work findByIdOrThrow(Long id) {
    return workRepo.findById(id)
            .orElseThrow(()->new IllegalArgumentException("Work with id " + id + " not found"));
  }

//  @Override
//  public Set<Genre> findGenresByIdsOrThrow(Set<Long> ids) {
//    if(ids.isEmpty()){
//      return new HashSet<>();
//    }
//    Set<Genre> genres = genreRepo.findGenreByIds(ids).orElseThrow(()->new EntityNotFoundException("Genres with ids " + ids + " not found"));
//    if(genres.isEmpty()){
//      throw new IllegalArgumentException("Genres with ids " + ids + " not found");
//    }
//    if(genres.size() != ids.size()){
//      throw new IllegalArgumentException("Genres with ids " + ids + " not found");
//    }
//    return genres;
//  }

  @Override
  public List<Work> findOtherWorksByAuthor(Long authorId, Long excludeWorkId) {
    return workRepo.findAllByAuthor_Id(authorId).stream().filter(work -> !work.getId().equals(excludeWorkId)).limit(10).toList();
  }

    @Override
    public List<Work> findAllById(List<Long> workIds) {
      if(workIds == null || workIds.isEmpty()) {
          return new ArrayList<>();
      }
        return workRepo.findAllById(workIds);
    }

    @Override
  @Transactional
  public Work saveWork(Work work) {
    return workRepo.save(work);
  }

  @Override
  @Transactional
  public Work saveWorkAndChapters(Work work, List<Chapter> chapters) {
    work.setChapters(chapters);
    return workRepo.save(work);
  }

  @Override
  public List<Long> searchWorkIdsWithFTS(
    @Param("query") String query,
    @Param("authorId") Long authorId,
    @Param("genreIds") long[] genreIds,
    @Param("hasGenres") boolean hasGenres,
    @Param("status") String status,
    @Param("createdFrom") LocalDateTime createdFrom,
    @Param("createdTo") LocalDateTime createdTo,
    @Param("limit") int limit,
    @Param("offset") int offset
  ){
    return workRepo.searchWorkIdsWithFTS(query, authorId, genreIds, hasGenres, status, createdFrom, createdTo, limit, offset);
  }

  @Override
  public List<Work> findAllWithGenresAndAuthorByIds(List<Long> ids){
    return workRepo.findAllWithGenresAndAuthorByIds(ids);
  }
}
