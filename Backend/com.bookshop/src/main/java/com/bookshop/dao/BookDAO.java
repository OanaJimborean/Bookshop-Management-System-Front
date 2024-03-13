package com.bookshop.dao;

import com.bookshop.model.Book;
import com.bookshop.wrapper.BookWrapper;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.repository.query.Param;

import javax.transaction.Transactional;
import java.util.List;

public interface BookDAO extends JpaRepository<Book, Integer> {

    List<BookWrapper> getAllBook();
    @Modifying
    @Transactional
    Integer updateBookStatus(@Param("status") String status, @Param("id") Integer id);

    List<BookWrapper> getBookByCategory(@Param("id") Integer id);
    BookWrapper getBookById(@Param("id") Integer id);
}
