package com.sayra.umai.WorkPackage.Repos;

import com.sayra.umai.WorkPackage.Entities.Chapter;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChapterRepo extends CrudRepository<Chapter, Long > {
}
