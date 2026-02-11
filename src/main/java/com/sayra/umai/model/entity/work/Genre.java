package com.sayra.umai.model.entity.work;

import jakarta.persistence.*;
import lombok.*;

import java.util.*;

@Entity
@Table(name = "genres")
@Getter
@Setter
@ToString(onlyExplicitlyIncluded = true)
@NoArgsConstructor
@AllArgsConstructor
public class Genre {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ToString.Include
    private Long id;

    @Column(nullable = false, unique = true)
    @ToString.Include
    private String name;

    @EqualsAndHashCode.Exclude
    @ManyToMany(mappedBy = "genres", fetch = FetchType.LAZY)
    private Set<Work> works;

}
