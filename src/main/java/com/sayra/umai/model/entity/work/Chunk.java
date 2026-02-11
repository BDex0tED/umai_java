package com.sayra.umai.model.entity.work;

import com.sayra.umai.model.dto.ChunkType;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
public class Chunk {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer chunkNumber;

    @Column(nullable = false)
    private ChunkType type;

    @Column(nullable = false, columnDefinition = "text")
    private String text;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="chapter_id",  nullable = false)
    private Chapter chapter;
}

