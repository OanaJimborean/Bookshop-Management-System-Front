package com.bookshop.serviceImpl;

import com.bookshop.JWT.JwtFilter;
import com.bookshop.constents.BookshopConstants;
import com.bookshop.dao.CategoryDAO;
import com.bookshop.model.Category;
import com.bookshop.service.CategoryService;
import com.bookshop.utils.BookshopUtility;
import com.google.common.base.Strings;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Service

public class CategoryServiceImpl implements CategoryService {
    @Autowired
    CategoryDAO categoryDAO;
    @Autowired
    JwtFilter jwtFilter;
    @Override
    public ResponseEntity<String> addNewCategory(Map<String, String> requestMap) {
        try{
            if(jwtFilter.isAdmin()){
                if(validateCategoryMap(requestMap, false)){
                    categoryDAO.save(getCategoryFromMap(requestMap, false));
                    return BookshopUtility.getResponseEntity("Category Added Successfully", HttpStatus.OK);
                }
            }else{
                return BookshopUtility.getResponseEntity(BookshopConstants.UNAUTHORIZED_ACCESS, HttpStatus.UNAUTHORIZED);
            }
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return BookshopUtility.getResponseEntity(BookshopConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }



    private boolean validateCategoryMap(Map<String, String> requestMap, boolean validateId) {

        if(requestMap.containsKey("name")){
            if(requestMap.containsKey("id") && validateId){
                return true;
            }else if(!validateId) {
                return true;
            }
        }
        return false;

    }

    private Category getCategoryFromMap(Map<String, String> requestMap, Boolean isAdd){
        Category category = new Category();
        if(isAdd){
            category.setId(Integer.parseInt(requestMap.get("id")));
        }
        category.setName(requestMap.get("name"));
        return category;
    }

    @Override
    public ResponseEntity<List<Category>> getAllCategory(String filterValue) {
        try{
            if (!Strings.isNullOrEmpty(filterValue) && filterValue.equalsIgnoreCase("true")) {
                log.info("Inside if");
                return new ResponseEntity<List<Category>>(categoryDAO.getAllCategory(), HttpStatus.OK);
            }
            return new ResponseEntity<>(categoryDAO.findAll(), HttpStatus.OK);
        }catch (Exception ex){
            ex.printStackTrace();;
        }
        return new ResponseEntity<List<Category>>(new ArrayList<>(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<String> updateCategory(Map<String, String> requestMap) {
        try{
            if(jwtFilter.isAdmin()){
                if(validateCategoryMap(requestMap, true)){
                    Optional optional = categoryDAO.findById(Integer.parseInt(requestMap.get("id")));
                if(!optional.isEmpty()){
                        categoryDAO.save(getCategoryFromMap(requestMap, true));
                        return BookshopUtility.getResponseEntity("Category Updated Successfully", HttpStatus.OK);
                    }else{
                    return BookshopUtility.getResponseEntity("Category id does not exist", HttpStatus.OK);
                    }
                }
                return BookshopUtility.getResponseEntity(BookshopConstants.INVALID_DATA, HttpStatus.BAD_REQUEST);
            }
            else{
                return BookshopUtility.getResponseEntity(BookshopConstants.UNAUTHORIZED_ACCESS, HttpStatus.UNAUTHORIZED);
            }
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return BookshopUtility.getResponseEntity(BookshopConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
