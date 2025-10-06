package com.sayra.umai.Repos;

import com.sayra.umai.Entities.Work;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WorkRepo extends CrudRepository<Work, Long> {
    List<Work> findAllBy();

    Optional<Work> findById(long id);
}
