package com.sayra.umai.model.entity.work;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name="authors")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString(onlyExplicitlyIncluded = true)
public class Author {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ToString.Include
    private Long id;

    @Column(name = "photo_url")
    private String photoUrl;

    @Column(name = "photo_dropbox_path")
    private String photoDropboxPath;

    @Column(nullable = false)
    @ToString.Include
    private String name;

    @Column()
    private String date;

    @Column()
    private String wiki;

    @Column(columnDefinition = "text")
    private String bio;

    @OneToMany(mappedBy = "author", cascade = CascadeType.ALL,
            orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Work> works;

}
