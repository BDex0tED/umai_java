package com.sayra.umai.Repos;

import com.sayra.umai.Entities.Response;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ResponseRepo  extends CrudRepository<Response, Long> {
}
