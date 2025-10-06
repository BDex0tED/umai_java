package com.sayra.umai.Repos;

import com.sayra.umai.Entities.Chunk;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChunkRepo extends CrudRepository<Chunk, Long> {
}
