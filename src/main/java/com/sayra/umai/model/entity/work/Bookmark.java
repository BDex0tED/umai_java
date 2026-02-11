package com.sayra.umai.model.entity.work;

import com.sayra.umai.model.entity.user.UserEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Table(
        name = "bookmarks",
        indexes = {
                @Index(name = "idx_bookmarks_user", columnList = "user_id"),
                @Index(name = "idx_bookmarks_work", columnList = "work_id"),
                @Index(name = "idx_bookmarks_chapter", columnList = "chapter_id"),
                @Index(name = "idx_bookmarks_user_work", columnList = "user_id, work_id")
        }
)
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString(onlyExplicitlyIncluded = true)
public class Bookmark {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ToString.Include
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="user_id", nullable = false)
    private UserEntity user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="work_id", nullable = false)
    private Work work;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="chapter_id", nullable = false)
    private Chapter chapter;

    @Column(name="chunk_id", nullable = false)
    @ToString.Include
    private Long chunkId;

    @Column(name="user_note", columnDefinition = "TEXT")
    private String userNote;

    @Column(name="work_note", columnDefinition = "TEXT")
    private String workNote;

    @Column(name="start_offset", nullable = false)
    @ToString.Include
    private Integer startOffset;
    @Column(name="end_offset", nullable = false)
    @ToString.Include
    private Integer endOffset;

    @Column(name="created_at", nullable = false,updatable = false)
    @ToString.Include
    private Instant created_at;

    @PrePersist
    void onCreate() {
        validateOffsets();
        this.created_at = Instant.now();
    }

    public void validateOffsets(){
        if(chunkId == null || chunkId < 0){
            throw new IllegalArgumentException("chunkId must be greater than 0");
        }
        if (startOffset == null || endOffset == null || startOffset < 0 || endOffset <= startOffset) {
            throw new IllegalArgumentException("Invalid diapason of offsets");
        }
    }

    @ToString.Include(name = "workId")
    public Long getWorkIdForLog() {
        return work != null ? work.getId() : null;
    }
}
