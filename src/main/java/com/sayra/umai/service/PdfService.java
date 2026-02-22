package com.sayra.umai.service;

import com.sayra.umai.model.entity.work.Work;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Set;

public interface PdfService {
  Work uploadWork(
    MultipartFile pdfFile,
    String title,
    Long authorId,
    Set<Long> genresId,
    String description,
    MultipartFile coverImage
  ) throws IOException;
}
