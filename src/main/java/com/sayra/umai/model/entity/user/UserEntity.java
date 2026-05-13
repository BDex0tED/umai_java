package com.sayra.umai.model.entity.user;

import com.sayra.umai.model.entity.work.Bookmark;
import com.sayra.umai.model.entity.work.ChatSession;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "users")
@Getter
@Setter
@ToString(onlyExplicitlyIncluded = true)
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ToString.Include
    private Long id;

    @Column(unique = true, nullable = false)
    @ToString.Include
    private String username;
    @Column
    private String password;
    @ToString.Include
    @Column(nullable = false)
    private String email;

    @Column(name = "google_id", unique = true)
    private String googleId;

    @Enumerated(EnumType.STRING)
    @Column(name = "auth_provider")
    private AuthProvider authProvider;

    @Column(name = "profile_photo_url")
    private String profilePhotoUrl;

    @Column(name = "profile_photo_public_id")
    private String profilePhotoPublicId;


    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "role_id", referencedColumnName = "id"))
    private List<Role> roles = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Bookmark> bookmarks = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ChatSession> chatSessions = new ArrayList<>();

//    Сохраняй для прогресса: user_id, book_id, progress_percent
//храни book_id, user_id, start_offset, end_offset и note_text для закладок и выделений текста(типо закладка на цитату и для запроса у ии)
    public UserEntity(String username, String email, List<Role> roles, AuthProvider authProvider) {
        this.username = username;
        this.email = email;
        this.roles = roles;
        this.authProvider = authProvider;


    }

}
