package com.bookshop.dao;

import com.bookshop.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CategoryDAO extends JpaRepository<Category, Integer> {
    List<Category> getAllCategory();
}
