package com.sayra.umai.repo;

import com.sayra.umai.model.entity.user.UserEntity;
import com.sayra.umai.model.entity.work.Bookmark;
import com.sayra.umai.model.entity.work.Chapter;
import com.sayra.umai.model.entity.work.Work;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookmarkRepo extends JpaRepository<Bookmark, Long> {
    List<Bookmark> findAllByUserAndWork(UserEntity user, Work work);
    List<Bookmark> findAllByChapter(Chapter chapter);
    Optional<Bookmark> findByIdAndUser(Long id, UserEntity user);

    @EntityGraph(attributePaths = {"user", "work", "chapter"})
    Page<Bookmark> findAllByUser(UserEntity user, Pageable pageable);

    @EntityGraph(attributePaths = {"user"})
    List<Bookmark> findAllByWork(Work work);

    @Modifying
    @Query("DELETE FROM Bookmark b WHERE b.user = :user")
    void deleteAllByUser(UserEntity user);

}
