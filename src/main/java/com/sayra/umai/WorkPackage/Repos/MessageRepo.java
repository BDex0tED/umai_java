package com.sayra.umai.WorkPackage.Repos;

import com.sayra.umai.WorkPackage.Entities.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepo extends JpaRepository<Message, Long> {
//    List<Message> findByChatSessionIdOrderByCreated_atAsc(Long sessionId);

}
