package com.back.zapateria.service;

import com.back.zapateria.model.Category;
import com.back.zapateria.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    public List<Category> getAll() { return categoryRepository.findAll(); }

    public Category create(Category c) { return categoryRepository.save(c); }

    public Optional<Category> getById(Long id) { return categoryRepository.findById(id); }

    public void delete(Long id) { categoryRepository.deleteById(id); }
}
