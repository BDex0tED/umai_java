package com.sayra.umai.Entities;

import jakarta.persistence.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data
public class Chapter {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String chapterTitle;
    @Column(nullable = false)
    private Integer chapterNumber;

    @ManyToOne
    @JoinColumn(name="work_id", nullable = false)
    private Work work;

    @OneToMany(mappedBy = "chapter",  cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Chunk> chunks = new ArrayList<>();


}
