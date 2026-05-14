package com.sayra.umai.service.impl;

import com.sayra.umai.config.exception.ResourceAlreadyExists;
import com.sayra.umai.exception.ResourceNotFoundException;
import com.sayra.umai.mapper.AuthorMapper;
import com.sayra.umai.model.dto.AuthorDTO;
import com.sayra.umai.model.entity.work.Work;
import com.sayra.umai.model.request.AuthorRequest;
import com.sayra.umai.model.entity.work.Author;
import com.sayra.umai.repo.AuthorRepo;
import com.sayra.umai.repo_service.UserEntityDataService;
import com.sayra.umai.repo_service.WorkDataService;
import com.sayra.umai.service.AuthorService;
import com.sayra.umai.service.CloudinaryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthorServiceImpl implements AuthorService {
    private final AuthorRepo authorRepo;
    private final WorkDataService workDataService;
    private final AuthorMapper authorMapper;
    private final UserEntityDataService userEntityDataService;
    private final CloudinaryService cloudinaryService;


  @Transactional(readOnly = true)
  @Override
  public Page<AuthorDTO> getAllAuthors(Pageable pageable) {
    Page<Author> authors = authorRepo.findAll(pageable);
    return authors.map(authorMapper::toAuthorDTO);

  }

  @Override
  @Transactional(readOnly = true)
  public AuthorDTO getAuthorById(Long id) {
      Author author = authorRepo.findById(id).orElseThrow(()->new ResourceNotFoundException("Author with id: " +id + " not found"));
      return authorMapper.toAuthorDTO(author);
  }

  @Transactional
  @Override
  public AuthorDTO save(AuthorRequest authorRequest) {
        if(authorRepo.existsByName(authorRequest.getName())){
            throw new ResourceAlreadyExists("Author with name: "+ authorRequest.getName()+" already exists");
        }
        Author author = new Author();
        author.setName(authorRequest.getName());
        author.setBio(authorRequest.getBio());
        author.setWiki(authorRequest.getWiki());
        author.setDate(authorRequest.getDateOfBirth());

        try{
            String authorUrl = cloudinaryService.uploadFile(authorRequest.getPhoto(), "authors");

            author.setPhotoUrl(authorUrl);
            author.setPhotoPublicId(extractPublicId(authorUrl));
        }  catch(Exception e){
            log.warn("Author photo upload failed: {}", e.getMessage());
        }

        List<Work> authorWorks = workDataService.findAllById(authorRequest.getWorkIds());
        if(!authorWorks.isEmpty()){
            author.setWorks(authorWorks);
        }
        authorRepo.save(author);
      return authorMapper.toAuthorDTO(author);
  }
    @Transactional
    public void createKyrgyzNationalAuthor() {

        createAuthor(
                "Кыргыз эл чыгармачылыгы",
                "Kyrgyz National Folklore",
                null
        );


        createAuthor(
                "Жусуп Баласагын",
                "Жусуп Баласагын Хасс Хажиб — Карахан доорундагы акын, ойчул жана жазуучу.",
                "1016–1075"
        );

        createAuthor(
                "Токтогул Сатылганов",
                "Токтогул Сатылганов — кыргыздын залкар акыны, комузчу жана төкмө акын.",
                "1864–1933"
        );

        log.info("Created Kyrgyz authors");
    }

    private void createAuthor(String name, String bio, String date) {
        if (userEntityDataService.existsByUsernameOrThrow(name)) {
            log.warn("Author with name {} already exists", name);
            return;
        }

        Author author = new Author();
        author.setName(name);
        author.setBio(bio);
        author.setDate(date);
        author.setWiki(null);
        author.setPhotoUrl(null);
        author.setPhotoPublicId(null);

        authorRepo.save(author);
    }

    private String extractPublicId(String url) {
        if (url == null) return null;
        int dotIndex = url.lastIndexOf('.');
        String withoutExt = dotIndex > 0 ? url.substring(0, dotIndex) : url;
        int uploadIdx = withoutExt.indexOf("/upload/");
        if (uploadIdx < 0) return withoutExt;
        String afterUpload = withoutExt.substring(uploadIdx + "/upload/".length());
        if (afterUpload.startsWith("v") && afterUpload.contains("/")) {
            afterUpload = afterUpload.substring(afterUpload.indexOf('/') + 1);
        }
        return afterUpload;
    }
}
