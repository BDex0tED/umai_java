package com.sayra.umai.WorkPackage.Repos;

import com.sayra.umai.UserPackage.Entities.UserEntity;
import com.sayra.umai.WorkPackage.Entities.Bookmark;
import com.sayra.umai.WorkPackage.Entities.Chapter;
import com.sayra.umai.WorkPackage.Entities.Work;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookmarkRepo extends JpaRepository<Bookmark, Long> {
    List<Bookmark> findAllByUserAndWork(UserEntity user, Work work);
    List<Bookmark> findAllByUser(UserEntity user);
    List<Bookmark> findAllByWork(Work work);
    List<Bookmark> findAllByChapter(Chapter chapter);


}
