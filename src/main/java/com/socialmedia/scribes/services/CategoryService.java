package com.socialmedia.scribes.services;

import com.socialmedia.scribes.entities.Category;
import com.socialmedia.scribes.repositories.CategoryRepository;
import org.bson.types.ObjectId;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;
    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }
    public Category getCategoryById(ObjectId categoryId) {
        Optional<Category> categoryExists= categoryRepository.findById(categoryId);
        Category category = categoryExists.orElseThrow(()-> new UsernameNotFoundException(String.format("Category not found")));
        return category;
    }
    public Category createCategory(Category category) {
        return categoryRepository.save(category);
    }
    public void deleteCategory(ObjectId categoryId) {
        categoryRepository.deleteById(categoryId);
    }
}
