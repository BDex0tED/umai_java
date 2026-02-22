package com.sayra.umai.model.entity.work;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.sayra.umai.model.dto.WorkStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.*;

@Entity
@Getter
@Setter
@ToString(onlyExplicitlyIncluded = true)
@Table(name="works", indexes = {
        @Index(columnList = "author", name = "author_idx")
})
public class Work {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ToString.Include
    private Long id;

    @Column(nullable = false)
    @ToString.Include
    private String title;

//    @ManyToOne
//    @JoinColumn(name = "author_id", nullable = false)
//    private Author author; потом можно будет сделать словарь и выбирать в админке
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="author", nullable = false)
    private Author author;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name="work_genres",
            joinColumns = @JoinColumn(name = "work_id"),
            inverseJoinColumns = @JoinColumn(name = "genre_id"))
    private Set<Genre> genres;

    @Column(columnDefinition = "text")
    private String description;

    private String filepath;

    @Column(name = "cover_url")
    private String coverUrl;

    @Column(name = "cover_dropbox_path")
    private String coverDropboxPath;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private WorkStatus status = WorkStatus.PENDING;

    @Column(columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    @ToString.Include
    private LocalDateTime created_at = LocalDateTime.now();

    @OneToMany(mappedBy = "work", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonManagedReference
    @OrderBy("chapterNumber ASC")
    private List<Chapter> chapters;

    public void setFilePath(String filepath) {
        this.filepath = filepath;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Work)) return false;
        Work work = (Work) o;
        return id != null && id.equals(work.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
