package com.sayra.umai.service;

import com.dropbox.core.DbxException;
import com.sayra.umai.model.dto.AllWorksDTO;
import com.sayra.umai.model.dto.WorkStatus;
import com.sayra.umai.model.entity.work.Work;
import com.sayra.umai.model.request.UploadWorkRequest;
import com.sayra.umai.model.response.WorkResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

public interface WorkService {
    Page<AllWorksDTO> getAllWorks(Pageable pageable);

    WorkResponse findById(Long workId);

    List<AllWorksDTO> searchWorks(String query,
                                  Long authorId,
                                  List<Long> genreIds,
                                  WorkStatus status,
                                  LocalDateTime createdFrom,
                                  LocalDateTime createdTo,
                                  int page,
                                  int size);

    Work uploadWork(UploadWorkRequest uploadWorkRequest, MultipartFile pdfFile, MultipartFile coverImage) throws IOException, DbxException;



}
