package com.socialmedia.scribes.controllers;

import com.socialmedia.scribes.entities.Category;
import com.socialmedia.scribes.services.CategoryService;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class CategoryController {

    private CategoryService categoryService;
    @GetMapping(path = "/categories")
    public List<Category> getAllCategories(){
        return categoryService.getAllCategories();
    }
    @GetMapping(path ="/category")
    public Category getCategoryById(@RequestParam ObjectId categoryId){
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
