package com.sayra.umai.Repos;

import com.sayra.umai.Entities.Chapter;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChapterRepo extends CrudRepository<Chapter, Long > {
}
