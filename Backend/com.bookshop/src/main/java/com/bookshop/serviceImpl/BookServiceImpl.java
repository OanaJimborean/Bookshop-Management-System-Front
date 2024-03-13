package com.bookshop.serviceImpl;

import com.bookshop.JWT.JwtFilter;
import com.bookshop.constents.BookshopConstants;
import com.bookshop.dao.BookDAO;
import com.bookshop.model.Book;
import com.bookshop.model.Category;
import com.bookshop.service.BookService;
import com.bookshop.utils.BookshopUtility;
import com.bookshop.wrapper.BookWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class BookServiceImpl implements BookService {

    @Autowired
    BookDAO bookDAO;

    @Autowired
    JwtFilter jwtFilter;

    @Override
    public ResponseEntity<String> addNewProduct(Map<String, String> requestMap) {
        try{
            if(jwtFilter.isAdmin()){
                if(validateBookMap(requestMap, false)){
                    bookDAO.save(getBookFromMap(requestMap, false));
                    return BookshopUtility.getResponseEntity("Book Added Successfully", HttpStatus.OK);
                }
                return BookshopUtility.getResponseEntity(BookshopConstants.INVALID_DATA, HttpStatus.BAD_REQUEST);
            }else
                return BookshopUtility.getResponseEntity(BookshopConstants.UNAUTHORIZED_ACCESS, HttpStatus.UNAUTHORIZED);
        }catch(Exception ex){
            ex.printStackTrace();
        }
        return BookshopUtility.getResponseEntity(BookshopConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }



    private boolean validateBookMap(Map<String, String> requestMap, boolean validateId) {
        if(requestMap.containsKey("title") && requestMap.containsKey("author")){
            if(requestMap.containsKey("id") && validateId){
                return true;
            }
            else return !validateId;
        }
        return false;
    }

    private Book getBookFromMap(Map<String, String> requestMap, boolean isAdd) {
        Category category = new Category();
        category.setId(Integer.parseInt(requestMap.get("categoryId")));

        Book book = new Book();
        if(isAdd){
            book.setId(Integer.parseInt(requestMap.get("id")));
        }else{
            book.setStatus("true");
        }

        book.setCategory(category);
        book.setTitle(requestMap.get("title"));
        book.setAuthor(requestMap.get("author"));
        book.setDescription(requestMap.get("description"));
        book.setPrice(Integer.parseInt(requestMap.get("price")));
        return book;
    }

    @Override
    public ResponseEntity<List<BookWrapper>> getAllBook() {
        try{
            return new ResponseEntity<>(bookDAO.getAllBook(), HttpStatus.OK);
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return new ResponseEntity<>(new ArrayList<>(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<String> updateBook(Map<String, String> requestMap) {
        try{
            if(jwtFilter.isAdmin()){
                if(validateBookMap(requestMap, true)){
                    Optional<Book> optional = bookDAO.findById(Integer.parseInt(requestMap.get("id")));
                    if(!optional.isEmpty()){
                        Book book = getBookFromMap(requestMap, true);
                        book.setStatus(optional.get().getStatus());
                        bookDAO.save(book);
                        return BookshopUtility.getResponseEntity("Book Updated Successfully", HttpStatus.OK);
                    }else{
                        return BookshopUtility.getResponseEntity("Book id does not exist", HttpStatus.OK);
                    }
                }else{
                    return BookshopUtility.getResponseEntity(BookshopConstants.INVALID_DATA, HttpStatus.BAD_REQUEST);
                }
            }else{
                return BookshopUtility.getResponseEntity(BookshopConstants.UNAUTHORIZED_ACCESS, HttpStatus.UNAUTHORIZED);
            }
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return BookshopUtility.getResponseEntity(BookshopConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<String> deleteBook(Integer id) {
        try{
            if(jwtFilter.isAdmin()){
               Optional optional =  bookDAO.findById(id);
               if(!optional.isEmpty()){
                   bookDAO.deleteById(id);
                   return BookshopUtility.getResponseEntity("Book Deleted Successfully", HttpStatus.OK);
               }else{
                   return BookshopUtility.getResponseEntity("Book id does not exist", HttpStatus.OK);
               }
            }else{
                return BookshopUtility.getResponseEntity(BookshopConstants.UNAUTHORIZED_ACCESS, HttpStatus.UNAUTHORIZED);
            }
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return BookshopUtility.getResponseEntity(BookshopConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<String> updateStatus(Map<String, String> requestMap) {
        try{
            if(jwtFilter.isAdmin()){
                Optional optional = bookDAO.findById(Integer.parseInt(requestMap.get("id")));
                if(!optional.isEmpty()){
                    bookDAO.updateBookStatus(requestMap.get("status"), Integer.parseInt(requestMap.get("id")));
                    return BookshopUtility.getResponseEntity("Book Status Updated Successfully", HttpStatus.OK);
                }else{
                    BookshopUtility.getResponseEntity("Book id does not exist", HttpStatus.OK);
                }
            }else{
                return BookshopUtility.getResponseEntity(BookshopConstants.UNAUTHORIZED_ACCESS, HttpStatus.UNAUTHORIZED);
            }
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return BookshopUtility.getResponseEntity(BookshopConstants.SOMETHING_WENT_WRONG, HttpStatus.UNAUTHORIZED);
    }

    @Override
    public ResponseEntity<List<BookWrapper>> getByCategory(Integer id) {
        try{
            return new ResponseEntity<>(bookDAO.getBookByCategory(id), HttpStatus.OK);
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return new ResponseEntity<>(new ArrayList<>(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<BookWrapper> getBookById(Integer id) {
        try{
            return new ResponseEntity<>(bookDAO.getBookById(id), HttpStatus.OK);
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return new ResponseEntity<>(new BookWrapper(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
