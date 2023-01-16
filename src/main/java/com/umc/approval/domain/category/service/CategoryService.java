package com.umc.approval.domain.category.service;

import com.umc.approval.domain.category.entity.Category;
import com.umc.approval.domain.category.entity.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CategoryService {
    private final CategoryRepository categoryRepository;
    public Optional<Category> getCategory(Integer category){
        return categoryRepository.findById(Long.valueOf(category));
    }
}
