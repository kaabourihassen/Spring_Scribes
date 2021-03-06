package com.socialmedia.scribes.controllers;

import com.socialmedia.scribes.entities.Category;
import com.socialmedia.scribes.services.CategoryService;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin()
@RestController
public class CategoryController {
    @Autowired
    private CategoryService categoryService;

    @GetMapping(path = "/categories")
    public List<Category> getAllCategories(){
        return categoryService.getAllCategories();
    }
    @GetMapping(path ="/category/{categoryId}")
    public Category getCategoryById(@PathVariable ObjectId categoryId){
        return categoryService.getCategoryById(categoryId);
    }
    @PostMapping(path ="/category")
    public Category createCategory(@RequestBody Category category){
        return categoryService.createCategory(category);
    }
    @DeleteMapping(path="/category/{categoryId}")
    public void deletePost(@PathVariable ObjectId categoryId){
        categoryService.deleteCategory(categoryId);
    }
}
