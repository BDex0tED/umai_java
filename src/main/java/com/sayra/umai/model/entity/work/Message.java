package com.sayra.umai.model.entity.work;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@ToString(onlyExplicitlyIncluded = true)
@AllArgsConstructor
@NoArgsConstructor
@Table(name="messages")
public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ToString.Include
    private Long id;

    @Column(nullable = false, columnDefinition = "TEXT")
    @ToString.Include
    private String content;

    @Column(nullable = false)
    @ToString.Include
    @Enumerated(value = EnumType.STRING)
    private SenderEnum sender;

    @Column(nullable = false)
    @ToString.Include
    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="chat_session_id", nullable = false)
    private ChatSession chatSession;

    @PrePersist
    void onCreate(){
        createdAt = LocalDateTime.now();
    }

}
