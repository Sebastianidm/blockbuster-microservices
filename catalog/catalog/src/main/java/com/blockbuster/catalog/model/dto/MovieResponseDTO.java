package com.blockbuster.catalog.model.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MovieResponseDTO {

    private Long id;
    private String title;
    private Long categoryId;
    private String categoryName;
    private Integer releaseYear;
    private Integer stock;
    private Boolean available;
}
