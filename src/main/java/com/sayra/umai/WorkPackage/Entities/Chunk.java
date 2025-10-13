package com.sayra.umai.WorkPackage.Entities;

import com.sayra.umai.WorkPackage.Other.ChunkType;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Data
public class Chunk {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer chunkNumber;

    @Column(nullable = false)
    private ChunkType type;

    @Column(nullable = false, columnDefinition = "text")
    private String text;

    @EqualsAndHashCode.Exclude
    @ManyToOne
    @JoinColumn(name="chapter_id",  nullable = false)
    private Chapter chapter;
}

