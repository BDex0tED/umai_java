package com.sayra.umai.WorkPackage.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class WorkBriefDTO {
    private Long id;
    private String title;
    private String coverUrl;
}
