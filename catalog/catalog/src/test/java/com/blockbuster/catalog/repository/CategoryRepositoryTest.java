package com.blockbuster.catalog.repository;

import com.blockbuster.catalog.model.entity.Category;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class CategoryRepositoryTest {

    @Autowired
    private CategoryRepository categoryRepository;

    @Test
    void shouldFindCategoryByNameIgnoringCase() {
        Category category = Category.builder()
                .name("Thriller")
                .description("Películas de suspenso")
                .build();

        categoryRepository.save(category);

        assertThat(categoryRepository.findByNameIgnoreCase("thriller"))
                .isPresent()
                .get()
                .extracting(Category::getDescription)
                .isEqualTo("Películas de suspenso");
    }

    @Test
    void shouldCheckIfCategoryExistsIgnoringCase() {
        Category category = Category.builder()
                .name("Animation")
                .description("Películas animadas")
                .build();

        categoryRepository.save(category);

        assertThat(categoryRepository.existsByNameIgnoreCase("ANIMATION")).isTrue();
        assertThat(categoryRepository.existsByNameIgnoreCase("Documentary")).isFalse();
    }
}
