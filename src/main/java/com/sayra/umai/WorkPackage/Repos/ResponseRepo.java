package com.sayra.umai.WorkPackage.Repos;

import com.sayra.umai.WorkPackage.Entities.Response;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ResponseRepo  extends CrudRepository<Response, Long> {
}
