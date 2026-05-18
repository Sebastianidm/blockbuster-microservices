package com.blockbuster.catalog.controller;

import com.blockbuster.catalog.exception.CatalogException;
import com.blockbuster.catalog.exception.GlobalExceptionHandler;
import com.blockbuster.catalog.model.dto.CategoryResponseDTO;
import com.blockbuster.catalog.security.JwtAuthenticationFilter;
import com.blockbuster.catalog.service.CategoryService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CategoryController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(GlobalExceptionHandler.class)
class CategoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CategoryService categoryService;

    @MockitoBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Test
    void shouldCreateCategorySuccessfully() throws Exception {
        CategoryResponseDTO response = CategoryResponseDTO.builder()
                .id(1L)
                .name("Drama")
                .description("Películas dramáticas")
                .build();

        when(categoryService.createCategory(any())).thenReturn(response);

        mockMvc.perform(post("/api/v1/categories")
                        .contentType("application/json")
                        .content("{\"name\":\"Drama\",\"description\":\"Películas dramáticas\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Drama"));
    }

    @Test
    void shouldReturnValidationErrorWhenCategoryNameIsBlank() throws Exception {
        mockMvc.perform(post("/api/v1/categories")
                        .contentType("application/json")
                        .content("{\"name\":\"\",\"description\":\"Películas dramáticas\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("El nombre de la categoría es obligatorio"))
                .andExpect(jsonPath("$.path").value("/api/v1/categories"));
    }

    @Test
    void shouldGetAllCategoriesSuccessfully() throws Exception {
        when(categoryService.getAllCategories()).thenReturn(List.of(
                CategoryResponseDTO.builder().id(1L).name("Action").description("Películas de acción").build(),
                CategoryResponseDTO.builder().id(2L).name("Comedy").description("Películas de comedia").build()
        ));

        mockMvc.perform(get("/api/v1/categories"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Action"))
                .andExpect(jsonPath("$[1].name").value("Comedy"));
    }

    @Test
    void shouldReturnNotFoundWhenCategoryDoesNotExist() throws Exception {
        when(categoryService.getCategoryById(99L))
                .thenThrow(new CatalogException("Categoría no encontrada con ID: 99", HttpStatus.NOT_FOUND));

        mockMvc.perform(get("/api/v1/categories/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("Categoría no encontrada con ID: 99"));
    }

    @Test
    void shouldDeleteCategorySuccessfully() throws Exception {
        doNothing().when(categoryService).deleteCategory(eq(5L));

        mockMvc.perform(delete("/api/v1/categories/5"))
                .andExpect(status().isNoContent());
    }

    @Test
    void shouldUpdateCategorySuccessfully() throws Exception {
        CategoryResponseDTO response = CategoryResponseDTO.builder()
                .id(3L)
                .name("Thriller")
                .description("Películas de suspenso")
                .build();

        when(categoryService.updateCategory(eq(3L), any())).thenReturn(response);

        mockMvc.perform(put("/api/v1/categories/3")
                        .contentType("application/json")
                        .content("{\"name\":\"Thriller\",\"description\":\"Películas de suspenso\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Thriller"));
    }
}
