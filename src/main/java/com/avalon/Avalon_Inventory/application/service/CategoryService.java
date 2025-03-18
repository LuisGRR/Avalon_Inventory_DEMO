package com.avalon.Avalon_Inventory.application.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.avalon.Avalon_Inventory.application.dto.CategoryRequestDTO;
import com.avalon.Avalon_Inventory.application.dto.CategoryResponseDTO;
import com.avalon.Avalon_Inventory.domain.mapper.CategoryMapper;
import com.avalon.Avalon_Inventory.domain.model.Category;
import com.avalon.Avalon_Inventory.domain.repository.CategoryRepository;

@Service
public class CategoryService {
    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private CategoryMapper categoryMapper;

    public Page<CategoryResponseDTO> getAllCategorys(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);

        Page<Category> categorysPage = categoryRepository.findAll(pageable);

        List<CategoryResponseDTO> categorys = categorysPage.getContent().stream()
                .map(categoryMapper::categoryToCategoryResponseDTO)
                .collect(Collectors.toList());

        return new PageImpl<>(categorys,pageable,categorysPage.getTotalElements());
    }

    public CategoryResponseDTO getCategoryById(Long id) {
        return categoryRepository.findById(id).map(categoryMapper::categoryToCategoryResponseDTO).orElse(null);
    }

    public CategoryResponseDTO createCategory(CategoryRequestDTO categoryRequestDTO) {
        Category category = categoryMapper.categoryRequestDTOToCategory(categoryRequestDTO);
        Category saveCategory = categoryRepository.save(category);
        return categoryMapper.categoryToCategoryResponseDTO(saveCategory);
    }

    public CategoryResponseDTO updateCategory(Long id, CategoryRequestDTO categoryRequestDTO) {
        return categoryRepository.findById(id).map(existingCategory -> {
            categoryMapper.updateCategorytFromDto(categoryRequestDTO, existingCategory);
            Category updateCategory = categoryRepository.save(existingCategory);
            return categoryMapper.categoryToCategoryResponseDTO(updateCategory);
        }).orElse(null);
    }

    public boolean deleteCategory(Long id) {
        if (categoryRepository.existsById(id)) {
            categoryRepository.deleteById(id);
            return true;
        }
        return false;
    }

}
