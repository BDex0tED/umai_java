package com.sayra.umai.model.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class OtherWorksByAuthorResponse {
    private Long id;
    private String title;
    private String coverUrl;
}
