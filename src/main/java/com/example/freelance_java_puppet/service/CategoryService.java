package com.example.freelance_java_puppet.service;

import com.example.freelance_java_puppet.entity.Category;
import com.example.freelance_java_puppet.entity.History;
import com.example.freelance_java_puppet.repository.CategoryRepository;
import com.example.freelance_java_puppet.repository.HistoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private HistoryRepository historyRepository;

    public Category addCategory(Category category){
        return categoryRepository.save(category);
    }

    public String assignCategoryToHistory(int categoryId, int historyId){
        Category category = categoryRepository.findById(categoryId).orElse(null);
        History history = historyRepository.findById(historyId).orElse(null);

        category.getHistories().add(history);
        history.getCategories().add(category);

        categoryRepository.save(category);
        historyRepository.save(history);

        return "category " +category.getName() + ", successful assigned to " + history.getName();
    }

    public List<History> getHistoriesByCategory(int categoryId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new RuntimeException("Category not found!"));

        return category.getHistories();
    }

}
