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
        log.info("Creando categoría con nombre: {}", request.getName());

        if (categoryRepository.existsByNameIgnoreCase(request.getName())) {
            throw new CatalogException("Ya existe una categoría con el nombre: " + request.getName(), HttpStatus.CONFLICT);
        }

        Category category = categoryMapper.toEntity(request);
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

        if (!category.getName().equalsIgnoreCase(request.getName())
                && categoryRepository.existsByNameIgnoreCase(request.getName())) {
            throw new CatalogException("Ya existe una categoría con el nombre: " + request.getName(), HttpStatus.CONFLICT);
        }

        category.setName(request.getName());
        category.setDescription(request.getDescription());

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
}
