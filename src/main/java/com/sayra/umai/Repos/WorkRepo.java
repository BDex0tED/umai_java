package com.sayra.umai.Repos;

import com.sayra.umai.Entities.Work;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface WorkRepo extends JpaRepository<Work, Long> {

    @Query("SELECT DISTINCT w FROM Work w LEFT JOIN FETCH w.genres g LEFT JOIN FETCH w.author a")
    Set<Work> findAllWithGenresAndAuthor();

    @Query("SELECT w FROM Work w " +
            "LEFT JOIN FETCH w.chapters c " +
            "LEFT JOIN FETCH c.chunks " +
            "WHERE w.id = :id")
    Optional<Work> findByIdWithFullContent(Long id);
// ... existing code ...
    // Для блока "Еще от автора"
    List<Work> findAllByAuthor_Id(Long authorId);
}
