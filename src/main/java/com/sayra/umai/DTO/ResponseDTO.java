package com.sayra.umai.DTO;

import lombok.Data;

@Data
public class ResponseDTO {
    private Long id;
    private String response_User;
    private String response_AI;
    private String created_at;
}
