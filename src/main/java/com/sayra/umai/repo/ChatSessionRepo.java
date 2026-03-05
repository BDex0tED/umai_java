package com.sayra.umai.repo;

import com.sayra.umai.model.entity.user.UserEntity;
import com.sayra.umai.model.entity.work.ChatSession;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface ChatSessionRepo extends JpaRepository<ChatSession, Long> {
    Page<ChatSession> findAllByUser(UserEntity user, Pageable pageable);
    Optional<ChatSession> findByUserUsernameAndId(String username, Long id);

    Optional<ChatSession> findByIdAndUser(Long id, UserEntity user);

}
