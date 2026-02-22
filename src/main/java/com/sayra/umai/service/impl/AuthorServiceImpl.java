package com.sayra.umai.service.impl;

import com.sayra.umai.exception.UserAlreadyExistsException;
import com.sayra.umai.mapper.AuthorMapper;
import com.sayra.umai.model.dto.AuthorDTO;
import com.sayra.umai.model.entity.work.Work;
import com.sayra.umai.model.request.AuthorRequest;
import com.sayra.umai.model.entity.work.Author;
import com.sayra.umai.repo_service.AuthorDataService;
import com.sayra.umai.repo_service.UserEntityDataService;
import com.sayra.umai.repo_service.WorkDataService;
import com.sayra.umai.service.AuthorService;
import com.sayra.umai.service.DropboxService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthorServiceImpl implements AuthorService {
    private final AuthorDataService authorDataService;
    private final WorkDataService workDataService;
    private final AuthorMapper authorMapper;
    private final UserEntityDataService userEntityDataService;
    private final DropboxService dropboxService;


  @Transactional(readOnly = true)
  @Override
  public List<AuthorDTO> getAllAuthors() {
    return authorMapper.toAuthorDTO(authorDataService.findAllWithWorks());
  }

  @Override
  @Transactional(readOnly = true)
  public AuthorDTO getAuthorById(Long id) throws EntityNotFoundException {
      if(id == null || id <= 0){
          throw new IllegalArgumentException("Invalid id");
      }
      Author author = authorDataService.findByIdOrThrow(id);
      return authorMapper.toAuthorDTO(author);
  }

  @Transactional
  @Override
  public AuthorDTO save(AuthorRequest authorRequest) {
        if(authorRequest.getName() == null || authorRequest.getName().equals("")){
            throw new IllegalArgumentException("Author name is required");
        }
        if(authorDataService.existsByName(authorRequest.getName())){
            throw new IllegalArgumentException("Author with name: "+ authorRequest.getName()+" already exists");
        }
        Author author = new Author();
        author.setName(authorRequest.getName());
        author.setBio(authorRequest.getBio());
        author.setWiki(authorRequest.getWiki());
        author.setDate(authorRequest.getDateOfBirth());

        try{
            String uniqueName = UUID.randomUUID() + "_" + authorRequest.getPhoto().getOriginalFilename();
            String dropboxPath = "/authors/" + uniqueName;

            String authorUrl = dropboxService.uploadFile(authorRequest.getPhoto(),dropboxPath);

            author.setPhotoUrl(authorUrl);
            author.setPhotoDropboxPath(dropboxPath);
        } catch (IllegalArgumentException e) {
            log.warn("Invalid author photo provided: {}", e.getMessage());
            throw e;
        } catch(Exception e){
            log.warn("Author photo upload failed: {}", e.getMessage());
        }

        List<Work> authorWorks = workDataService.findAllById(authorRequest.getWorkIds());
        if(!authorWorks.isEmpty()){
            author.setWorks(authorWorks);
        }
        authorDataService.save(author);
      return authorMapper.toAuthorDTO(author);
  }
  @Transactional
  public void createKyrgyzNationalAuthor() {
        String name = "Кыргыз эл чыгармачылыгы";

        if(userEntityDataService.existsByUsernameOrThrow(name)){
            log.info("Kyrgyz National Author already exists");
            return;
        }
        Author author = new Author();
        author.setName(name);
        author.setBio(null);
        author.setDate(null);
        author.setWiki(null);
        author.setPhotoUrl(null);
        author.setPhotoDropboxPath(null);
        authorDataService.save(author);

        log.info("Created Kyrgyz National Author");
    }


}
