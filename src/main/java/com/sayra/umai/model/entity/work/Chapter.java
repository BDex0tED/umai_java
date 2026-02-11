package com.sayra.umai.model.entity.work;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@ToString(onlyExplicitlyIncluded = true)
@AllArgsConstructor
@NoArgsConstructor
public class Chapter {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ToString.Include
    private Long id;

    @Column(nullable = false)
    @ToString.Include
    private String chapterTitle;
    @Column(nullable = false)
    @ToString.Include
    private Integer chapterNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="work_id", nullable = false)
    @JsonBackReference
    private Work work;

    @OneToMany(mappedBy = "chapter",  cascade = CascadeType.ALL,
            orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<Chunk> chunks = new HashSet<>();


}
