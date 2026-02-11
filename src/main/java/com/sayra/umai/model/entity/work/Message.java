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
    private String sender;

    @Column(nullable = false, updatable = false)
    @ToString.Include
    private LocalDateTime createdAt = LocalDateTime.now();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="chat_session_id", nullable = false)
    private ChatSession chatSession;

}
