package com.blockbuster.catalog.service;

import com.blockbuster.catalog.exception.CatalogException;
import com.blockbuster.catalog.mapper.CategoryMapper;
import com.blockbuster.catalog.model.dto.CategoryRequestDTO;
import com.blockbuster.catalog.model.dto.CategoryResponseDTO;
import com.blockbuster.catalog.model.entity.Category;
import com.blockbuster.catalog.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    @Override
    @Transactional
    public CategoryResponseDTO createCategory(CategoryRequestDTO request) {
        String normalizedName = normalizeRequiredText(request.getName());
        String normalizedDescription = normalizeOptionalText(request.getDescription());
        log.info("Creando categoría con nombre: {}", normalizedName);

        if (categoryRepository.existsByNameIgnoreCase(normalizedName)) {
            throw new CatalogException("Ya existe una categoría con el nombre: " + normalizedName, HttpStatus.CONFLICT);
        }

        Category category = categoryMapper.toEntity(request);
        category.setName(normalizedName);
        category.setDescription(normalizedDescription);
        Category savedCategory = categoryRepository.save(category);

        return categoryMapper.toResponseDTO(savedCategory);
    }

    @Override
    public List<CategoryResponseDTO> getAllCategories() {
        return categoryRepository.findAll().stream()
                .map(categoryMapper::toResponseDTO)
                .toList();
    }

    @Override
    public CategoryResponseDTO getCategoryById(Long id) {
        return categoryMapper.toResponseDTO(getCategoryEntityById(id));
    }

    @Override
    @Transactional
    public CategoryResponseDTO updateCategory(Long id, CategoryRequestDTO request) {
        Category category = getCategoryEntityById(id);
        String normalizedName = normalizeRequiredText(request.getName());
        String normalizedDescription = normalizeOptionalText(request.getDescription());

        if (!category.getName().equalsIgnoreCase(normalizedName)
                && categoryRepository.existsByNameIgnoreCase(normalizedName)) {
            throw new CatalogException("Ya existe una categoría con el nombre: " + normalizedName, HttpStatus.CONFLICT);
        }

        category.setName(normalizedName);
        category.setDescription(normalizedDescription);

        return categoryMapper.toResponseDTO(categoryRepository.save(category));
    }

    @Override
    @Transactional
    public void deleteCategory(Long id) {
        Category category = getCategoryEntityById(id);
        categoryRepository.delete(category);
    }

    private Category getCategoryEntityById(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new CatalogException("Categoría no encontrada con ID: " + id, HttpStatus.NOT_FOUND));
    }

    private String normalizeRequiredText(String value) {
        return value == null ? null : value.trim();
    }

    private String normalizeOptionalText(String value) {
        if (value == null) {
            return null;
        }

        String trimmedValue = value.trim();
        return trimmedValue.isEmpty() ? null : trimmedValue;
    }
}
