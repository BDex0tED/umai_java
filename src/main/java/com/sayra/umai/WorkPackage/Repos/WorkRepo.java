package com.sayra.umai.WorkPackage.Repos;

import com.sayra.umai.WorkPackage.Entities.Work;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface WorkRepo extends JpaRepository<Work, Long> {

    @Query("SELECT DISTINCT w FROM Work w LEFT JOIN FETCH w.genres g LEFT JOIN FETCH w.author a")
    List<Work> findAllWithGenresAndAuthor();

    @Query("SELECT w FROM Work w " +
            "LEFT JOIN FETCH w.chapters c " +
            "LEFT JOIN FETCH c.chunks " +
            "WHERE w.id = :id")
    Optional<Work> findByIdWithFullContent(@Param("id") Long id);

    @Query("SELECT DISTINCT w FROM Work w LEFT JOIN FETCH w.genres g LEFT JOIN FETCH w.author a WHERE w.id IN :ids")
    Set<Work> findAllWithGenresAndAuthorByIds(@Param("ids") Collection<Long> ids);

    // FTS только по названию, описанию, автору и жанрам.
    @Query(value = """
            WITH data AS (
              SELECT 
                w.id,
                to_tsvector('simple', 
                  unaccent(coalesce(w.title,'')) || ' ' ||          -- 1. Название
                  unaccent(coalesce(w.description,'')) || ' ' ||    -- 2. Описание
                  unaccent(coalesce(max(a.name),'')) || ' ' ||      -- 3. Автор (агрегирован)
                  unaccent(coalesce(string_agg(DISTINCT g.name, ' '), '')) -- Жанры
                ) AS doc
              FROM works w
              LEFT JOIN authors a ON a.id = w.author
              LEFT JOIN work_genres wg ON wg.work_id = w.id
              LEFT JOIN genres g ON g.id = wg.genre_id
              WHERE (CAST(:status AS varchar) IS NULL OR w.status = CAST(:status AS varchar)) 
                AND (CAST(:authorId AS bigint) IS NULL OR w.author = CAST(:authorId AS bigint))
                AND (
                      :hasGenres = false 
                      OR EXISTS (
                           SELECT 1 
                           FROM work_genres wg2 
                           WHERE wg2.work_id = w.id AND wg2.genre_id = ANY(CAST(:genreIds AS bigint[]))
                      )
                )
                -- Используем COALESCE: если :createdFrom/To NULL, используем w.created_at (по сути, не фильтруем)
                AND w.created_at >= COALESCE(CAST(:createdFrom AS timestamp), w.created_at)
                AND w.created_at <= COALESCE(CAST(:createdTo AS timestamp), w.created_at)
              GROUP BY w.id
            )
            SELECT d.id
            FROM data d
            WHERE (
                -- Используем COALESCE для запроса FTS, чтобы избежать проблемы NULL-параметра
                COALESCE(CAST(:query AS text), '') = '' OR 
                d.doc @@ websearch_to_tsquery('simple', unaccent(CAST(:query AS text)))
            )
            ORDER BY d.id
            LIMIT :limit OFFSET :offset
            """,
            nativeQuery = true)
    List<Long> searchWorkIdsWithFTS(
            @Param("query") String query,
            @Param("authorId") Long authorId,
            @Param("genreIds") long[] genreIds,
            @Param("hasGenres") boolean hasGenres,
            @Param("status") String status,
            @Param("createdFrom") LocalDateTime createdFrom,
            @Param("createdTo") LocalDateTime createdTo,
            @Param("limit") int limit,
            @Param("offset") int offset
    );

    // Счетчик (Count) для пагинации
    @Query(value = """
            WITH data AS (
              SELECT 
                w.id,
                to_tsvector('simple',
                  unaccent(coalesce(w.title,'')) || ' ' || 
                  unaccent(coalesce(w.description,'')) || ' ' ||
                  unaccent(coalesce(max(a.name),'')) || ' ' ||      -- Автор (агрегирован)
                  unaccent(coalesce(string_agg(DISTINCT g.name, ' '), ''))
                ) AS doc
              FROM works w
              LEFT JOIN authors a ON a.id = w.author
              LEFT JOIN work_genres wg ON wg.work_id = w.id
              LEFT JOIN genres g ON g.id = wg.genre_id
              WHERE (CAST(:status AS varchar) IS NULL OR w.status = CAST(:status AS varchar)) 
                AND (CAST(:authorId AS bigint) IS NULL OR w.author = CAST(:authorId AS bigint))
                AND (
                      :hasGenres = false 
                      OR EXISTS (
                           SELECT 1 
                           FROM work_genres wg2 
                           WHERE wg2.work_id = w.id AND wg2.genre_id = ANY(CAST(:genreIds AS bigint[]))
                      )
                )
                AND w.created_at >= COALESCE(CAST(:createdFrom AS timestamp), w.created_at)
                AND w.created_at <= COALESCE(CAST(:createdTo AS timestamp), w.created_at)
              GROUP BY w.id
            )
            SELECT count(1)
            FROM data d
            WHERE (
                COALESCE(CAST(:query AS text), '') = '' OR 
                d.doc @@ websearch_to_tsquery('simple', unaccent(CAST(:query AS text)))
            )
            """,
            nativeQuery = true)
    long countSearchWithFTS(
            @Param("query") String query,
            @Param("authorId") Long authorId,
            @Param("genreIds") long[] genreIds,
            @Param("hasGenres") boolean hasGenres,
            @Param("status") String status,
            @Param("createdFrom") LocalDateTime createdFrom,
            @Param("createdTo") LocalDateTime createdTo
    );

    List<Work> findAllByAuthor_Id(Long authorId);
}
