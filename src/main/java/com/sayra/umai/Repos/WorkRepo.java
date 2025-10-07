package com.sayra.umai.Repos;

import com.sayra.umai.Entities.Work;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WorkRepo extends JpaRepository<Work, Long> {

    List<Work> findAllBy();
}
