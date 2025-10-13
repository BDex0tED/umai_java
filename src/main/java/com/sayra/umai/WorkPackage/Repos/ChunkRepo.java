package com.sayra.umai.WorkPackage.Repos;

import com.sayra.umai.WorkPackage.Entities.Chunk;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChunkRepo extends CrudRepository<Chunk, Long> {
}
