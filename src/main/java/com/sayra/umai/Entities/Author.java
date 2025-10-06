package com.sayra.umai.Entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.net.URL;
import java.util.List;

@Entity
@Table(name="authors")
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Author {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String photo;

    @Column(nullable = false)
    private String name;
    @Column(nullable = false)
    private String date;
    @Column()
    private URL wiki;

    @Column(columnDefinition = "text")
    private String bio;

    @OneToMany(mappedBy = "author", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Work> works;

}
