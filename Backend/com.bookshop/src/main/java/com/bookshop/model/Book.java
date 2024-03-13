package com.bookshop.model;

import lombok.Data;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.io.Serializable;

@NamedQuery(name = "Book.getAllBook", query = "select new com.bookshop.wrapper.BookWrapper(b.id, b.title, b.author, b.description, b.price, b.status, b.category.id, b.category.name) from Book b ")
@NamedQuery(name = "Book.updateBookStatus", query = "update Book b set b.status=:status where b.id=:id")
@NamedQuery(name = "Book.getBookByCategory", query = "select new com.bookshop.wrapper.BookWrapper(b.id, b.title, b.author) from Book b where b.category.id=:id and b.status='true'")
@NamedQuery(name = "Book.getBookById", query = "select new com.bookshop.wrapper.BookWrapper(b.id, b.title, b.author, b.description, b.price) from Book b where b.id=:id")


@Data
@Entity
@DynamicInsert
@DynamicUpdate
@Table(name = "book")
public class Book implements Serializable {
    public static final Long serialVersionUid = 123456L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "title")
    private String title;

    @Column(name = "author")
    private String author;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_fk", nullable = false)
    private Category category;

    @Column(name = "description")
    private String description;

    @Column(name = "price")
    private Integer price;

    @Column(name = "status")
    private String status;

}
