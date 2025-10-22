package com.sayra.umai.WorkPackage.Entities;

import com.sayra.umai.UserPackage.Entities.UserEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "chat_sessions")
public class ChatSession {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    @OneToMany(mappedBy = "chatSession", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("created_at ASC")
    private List<Message> messages;

    @ManyToOne
    @JoinColumn(name="user_id", nullable = false)
    private UserEntity user;

}
