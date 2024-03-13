package com.bookshop.rest;

import com.bookshop.wrapper.UserWrapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RequestMapping(path = "/user")

public interface UserRest {
    @PostMapping(path = "/signup")
    public ResponseEntity<String> signUp(@RequestBody(required = true) Map<String, String> requestMap);

    @PostMapping(path = "/login")
    public ResponseEntity<String> login(@RequestBody(required = true) Map<String, String> requestMap);

    @GetMapping(path = "/get")
    public ResponseEntity<List<UserWrapper>>getAllUser();

    @PostMapping(path = "/update")
    public ResponseEntity<String> update(@RequestBody(required = true) Map<String, String> requestMap);
    @GetMapping(path = "/checkToken")
    ResponseEntity<String>checkToken();

    @PostMapping(path = "/changePassword")
    ResponseEntity<String> changePassword(@RequestBody Map<String, String> requestMAp);

    @PostMapping(path = "/forgotPassword")
    ResponseEntity<String> forgotPassword(@RequestBody Map<String, String> requestMap);

    @PostMapping(path = "/delete/{id}")
    ResponseEntity<String> deleteUser(@PathVariable Integer id);




}
