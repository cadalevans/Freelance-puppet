package com.example.freelance_java_puppet.controller;

import com.example.freelance_java_puppet.DTO.HistoryDTO;
import com.example.freelance_java_puppet.entity.Category;
import com.example.freelance_java_puppet.entity.History;
import com.example.freelance_java_puppet.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/category")
@CrossOrigin("**")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    // 1️⃣ Add a new category
    @PostMapping("/add")
    public ResponseEntity<Category> addCategory(@RequestBody Category category) {
        Category savedCategory = categoryService.addCategory(category);
        return ResponseEntity.ok(savedCategory);
    }

    // 2️⃣ Assign category to history
    @PostMapping("/assign")
    public ResponseEntity<String> assignCategoryToHistory(@RequestParam int categoryId, @RequestParam int historyId) {
        String response = categoryService.assignCategoryToHistory(categoryId, historyId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{categoryId}/histories")
    public ResponseEntity<List<HistoryDTO>> getHistoriesByCategory(@PathVariable int categoryId) {
        List<HistoryDTO> histories = categoryService.getHistoriesByCategory(categoryId);
        return ResponseEntity.ok(histories);
    }

}
