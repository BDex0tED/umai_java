package com.sayra.umai.WorkPackage.DTO;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ChatSessionDTO {
    private Long id;
    private String title;
    private String userName;
}
