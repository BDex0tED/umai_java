package com.sayra.umai.Entities;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class Chunk {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer chunkNumber;

    @Column(nullable = false, columnDefinition = "text")
    private String text;

    @ManyToOne
    @JoinColumn(name="chapter_id",  nullable = false)
    private Chapter chapter;
}
