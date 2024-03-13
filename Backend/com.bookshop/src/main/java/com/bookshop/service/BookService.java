package com.bookshop.service;

import com.bookshop.wrapper.BookWrapper;
import org.springframework.data.repository.query.Param;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;
import java.util.Map;

public interface BookService {

    ResponseEntity<String> addNewProduct(Map<String, String> requestMap);
    ResponseEntity<List<BookWrapper>>getAllBook();
    ResponseEntity<String> updateBook(Map<String, String> requestMap);
    ResponseEntity<String> deleteBook(Integer id);
    ResponseEntity<String> updateStatus(Map<String, String> requestMap);
    ResponseEntity<List<BookWrapper>>getByCategory(Integer id);
    ResponseEntity<BookWrapper> getBookById(Integer id);

}
