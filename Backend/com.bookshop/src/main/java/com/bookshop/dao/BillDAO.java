package com.bookshop.dao;

import com.bookshop.model.Bill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BillDAO extends JpaRepository<Bill, Integer> {

    List<Bill> getAllBills();
    List<Bill> getBillByUsername(@Param("username") String username);
}
