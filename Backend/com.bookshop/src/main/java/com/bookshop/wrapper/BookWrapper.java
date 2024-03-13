package com.bookshop.wrapper;

import lombok.Data;
import org.hibernate.annotations.DynamicUpdate;

@Data
public class BookWrapper {
    Integer id;
    String title;
    String author;
    String description;
    Integer price;
    String status;
    Integer categoryId;
    String categoryName;
    public BookWrapper(){

    }

    public BookWrapper(Integer id, String title, String author, String description, Integer price, String status, Integer categoryId, String categoryName) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.description = description;
        this.price = price;
        this.status = status;
        this.categoryId = categoryId;
        this.categoryName = categoryName;
    }

    public BookWrapper(Integer id, String title, String author) {
        this.id = id;
        this.title = title;
        this.author = author;
    }

    public BookWrapper(Integer id, String title, String author, String description, Integer price) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.description = description;
        this.price = price;
    }
}
