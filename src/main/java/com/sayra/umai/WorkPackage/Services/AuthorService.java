package com.sayra.umai.WorkPackage.Services;

import com.sayra.umai.WorkPackage.DTO.AuthorDTO;
import com.sayra.umai.WorkPackage.DTO.AuthorInDTO;
import com.sayra.umai.WorkPackage.DTO.WorkInAuthorInDTO;
import com.sayra.umai.WorkPackage.Entities.Author;
import com.sayra.umai.WorkPackage.Repos.AuthorRepo;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.stream.Collectors;

@Service
public class AuthorService {
    private AuthorRepo authorRepo;


    public AuthorService(AuthorRepo authorRepo) {
        this.authorRepo = authorRepo;
    }

    @Transactional(readOnly = true)
    public Set<AuthorDTO> getAllAuthors() {

        Set<Author> authors = authorRepo.findAllWithWorks();

        return authors.stream()
                .map(author -> {

                    Set<WorkInAuthorInDTO> worksDTO = author.getWorks().stream()
                            .map(work -> new WorkInAuthorInDTO(
                                    work.getId(),
                                    work.getTitle(),
                                    work.getDescription()
                            ))
                            .collect(Collectors.toSet());

                    AuthorDTO authorDTO = new AuthorDTO();
                    authorDTO.setId(author.getId());
                    authorDTO.setName(author.getName());
                    authorDTO.setBio(author.getBio());

                    authorDTO.setDateOfBirth(author.getDate());
                    authorDTO.setWiki(author.getWiki());
                    authorDTO.setWorks(worksDTO);

                    return authorDTO;
                })
                .collect(Collectors.toSet());
    }

    public ResponseEntity<Author> save(AuthorInDTO authorInDTO) {
        if(authorInDTO.getName() == null || authorInDTO.getName().equals("")){
            return ResponseEntity.badRequest().build();
        }
        if(authorRepo.findByName(authorInDTO.getName()).isPresent()){
            return ResponseEntity.badRequest().build();
        }
        Author author = new Author();
        author.setName(authorInDTO.getName());
        author.setBio(authorInDTO.getBio());
        author.setWiki(authorInDTO.getWiki());
        author.setDate(authorInDTO.getDateOfBirth());
        author.setPhoto(authorInDTO.getPhoto());
//        author.setPhoto(authorInDTO.getPhoto()); should be in db then the url
//        author.setWorks(); need to be done
        authorRepo.save(author);
        return ResponseEntity.ok(author);


    }

}
