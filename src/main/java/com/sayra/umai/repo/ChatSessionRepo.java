package com.sayra.umai.repo;

import com.sayra.umai.model.entity.user.UserEntity;
import com.sayra.umai.model.entity.work.ChatSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface ChatSessionRepo extends JpaRepository<ChatSession, Long> {
    List<ChatSession> findAllByUser(UserEntity user);

    Optional<ChatSession> findByUserUsernameAndId(String username, Long id);

    Optional<ChatSession> findByIdAndUser(Long id, UserEntity user);

    UserEntity user(UserEntity user);
}
