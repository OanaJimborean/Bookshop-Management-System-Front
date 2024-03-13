package com.bookshop.serviceImpl;

import com.bookshop.dao.BillDAO;
import com.bookshop.dao.BookDAO;
import com.bookshop.dao.CategoryDAO;
import com.bookshop.service.DashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class DashboardServiceImpl implements DashboardService {

    @Autowired
    CategoryDAO categoryDAO;

    @Autowired
    BookDAO bookDAO;

    @Autowired
    BillDAO billDAO;

    @Override
    public ResponseEntity<Map<String, Object>> getDetails() {
        Map<String, Object> map = new HashMap<>();
        map.put("category", categoryDAO.count());
        map.put("book", bookDAO.count());
        map.put("bill", billDAO.count());
        return new ResponseEntity<>(map, HttpStatus.OK);
    }
}