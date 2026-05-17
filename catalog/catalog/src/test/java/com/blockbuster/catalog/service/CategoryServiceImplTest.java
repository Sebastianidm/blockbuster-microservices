package com.blockbuster.catalog.service;

import com.blockbuster.catalog.exception.CatalogException;
import com.blockbuster.catalog.mapper.CategoryMapper;
import com.blockbuster.catalog.model.dto.CategoryRequestDTO;
import com.blockbuster.catalog.model.dto.CategoryResponseDTO;
import com.blockbuster.catalog.model.entity.Category;
import com.blockbuster.catalog.repository.CategoryRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CategoryServiceImplTest {

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private CategoryMapper categoryMapper;

    @InjectMocks
    private CategoryServiceImpl categoryService;

    @Test
    void shouldCreateCategorySuccessfully() {
        CategoryRequestDTO request = new CategoryRequestDTO();
        request.setName("  Drama  ");
        request.setDescription("  Películas dramáticas  ");

        Category category = Category.builder()
                .name("  Drama  ")
                .description("  Películas dramáticas  ")
                .build();

        Category savedCategory = Category.builder()
                .id(1L)
                .name("Drama")
                .description("Películas dramáticas")
                .build();

        CategoryResponseDTO response = CategoryResponseDTO.builder()
                .id(1L)
                .name("Drama")
                .description("Películas dramáticas")
                .build();

        when(categoryRepository.existsByNameIgnoreCase("Drama")).thenReturn(false);
        when(categoryMapper.toEntity(request)).thenReturn(category);
        when(categoryRepository.save(any(Category.class))).thenReturn(savedCategory);
        when(categoryMapper.toResponseDTO(savedCategory)).thenReturn(response);

        CategoryResponseDTO result = categoryService.createCategory(request);

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("Drama");
        assertThat(category.getName()).isEqualTo("Drama");
        assertThat(category.getDescription()).isEqualTo("Películas dramáticas");
    }

    @Test
    void shouldThrowExceptionWhenCategoryNameAlreadyExists() {
        CategoryRequestDTO request = new CategoryRequestDTO();
        request.setName("Action");

        when(categoryRepository.existsByNameIgnoreCase("Action")).thenReturn(true);

        assertThatThrownBy(() -> categoryService.createCategory(request))
                .isInstanceOf(CatalogException.class)
                .hasMessage("Ya existe una categoría con el nombre: Action");
    }

    @Test
    void shouldThrowExceptionWhenCategoryNotFound() {
        when(categoryRepository.findById(10L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> categoryService.getCategoryById(10L))
                .isInstanceOf(CatalogException.class)
                .hasMessage("Categoría no encontrada con ID: 10");
    }

    @Test
    void shouldUpdateCategoryWithNormalizedValues() {
        CategoryRequestDTO request = new CategoryRequestDTO();
        request.setName("  Thriller  ");
        request.setDescription("  Películas de suspenso  ");

        Category category = Category.builder()
                .id(4L)
                .name("Drama")
                .description("Películas dramáticas")
                .build();

        Category savedCategory = Category.builder()
                .id(4L)
                .name("Thriller")
                .description("Películas de suspenso")
                .build();

        when(categoryRepository.findById(4L)).thenReturn(Optional.of(category));
        when(categoryRepository.existsByNameIgnoreCase("Thriller")).thenReturn(false);
        when(categoryRepository.save(category)).thenReturn(savedCategory);
        when(categoryMapper.toResponseDTO(savedCategory)).thenReturn(CategoryResponseDTO.builder()
                .id(4L)
                .name("Thriller")
                .description("Películas de suspenso")
                .build());

        CategoryResponseDTO result = categoryService.updateCategory(4L, request);

        assertThat(result.getName()).isEqualTo("Thriller");
        assertThat(category.getName()).isEqualTo("Thriller");
        assertThat(category.getDescription()).isEqualTo("Películas de suspenso");
    }

    @Test
    void shouldThrowExceptionWhenUpdatingCategoryWithExistingName() {
        CategoryRequestDTO request = new CategoryRequestDTO();
        request.setName("Comedy");
        request.setDescription("Películas de comedia");

        Category category = Category.builder()
                .id(8L)
                .name("Action")
                .description("Películas de acción")
                .build();

        when(categoryRepository.findById(8L)).thenReturn(Optional.of(category));
        when(categoryRepository.existsByNameIgnoreCase("Comedy")).thenReturn(true);

        assertThatThrownBy(() -> categoryService.updateCategory(8L, request))
                .isInstanceOf(CatalogException.class)
                .hasMessage("Ya existe una categoría con el nombre: Comedy");
    }
}
