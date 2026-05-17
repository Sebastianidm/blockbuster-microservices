package com.blockbuster.catalog.mapper;

import com.blockbuster.catalog.model.dto.CategoryRequestDTO;
import com.blockbuster.catalog.model.dto.CategoryResponseDTO;
import com.blockbuster.catalog.model.entity.Category;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CategoryMapperTest {

    private final CategoryMapper mapper = new CategoryMapper();

    @Test
    void shouldMapRequestDtoToEntity() {
        CategoryRequestDTO requestDTO = new CategoryRequestDTO();
        requestDTO.setName("Drama");
        requestDTO.setDescription("Películas dramáticas");

        Category category = mapper.toEntity(requestDTO);

        assertThat(category).isNotNull();
        assertThat(category.getName()).isEqualTo("Drama");
        assertThat(category.getDescription()).isEqualTo("Películas dramáticas");
    }

    @Test
    void shouldMapEntityToResponseDto() {
        Category category = Category.builder()
                .id(5L)
                .name("Comedy")
                .description("Películas de comedia")
                .build();

        CategoryResponseDTO responseDTO = mapper.toResponseDTO(category);

        assertThat(responseDTO).isNotNull();
        assertThat(responseDTO.getId()).isEqualTo(5L);
        assertThat(responseDTO.getName()).isEqualTo("Comedy");
        assertThat(responseDTO.getDescription()).isEqualTo("Películas de comedia");
    }

    @Test
    void shouldReturnNullWhenRequestDtoIsNull() {
        assertThat(mapper.toEntity(null)).isNull();
    }

    @Test
    void shouldReturnNullWhenEntityIsNull() {
        assertThat(mapper.toResponseDTO(null)).isNull();
    }
}
