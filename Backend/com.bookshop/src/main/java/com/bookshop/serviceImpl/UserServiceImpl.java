package com.bookshop.serviceImpl;

import com.bookshop.JWT.CustomerUserDetailsServ;
import com.bookshop.JWT.JwtFilter;
import com.bookshop.JWT.JwtUtil;
import com.bookshop.constents.BookshopConstants;
import com.bookshop.dao.UserDAO;
import com.bookshop.model.User;
import com.bookshop.service.UserService;
import com.bookshop.utils.BookshopUtility;
import com.bookshop.utils.EmailUtils;
import com.bookshop.wrapper.UserWrapper;
import com.google.common.base.Strings;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.*;

@Slf4j
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    UserDAO userDAO;

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    CustomerUserDetailsServ customerUserDetailsServ;
    @Autowired
    JwtUtil jwtUtil;
    @Autowired
    JwtFilter jwtFilter;

    @Override
    public ResponseEntity<String> signUp(Map<String, String> requestMap) {
        final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);
        log.info("Inside signup {}", requestMap);
        try {
            if (validateSignUpMap(requestMap)) {
                User user = userDAO.findByEmailId(requestMap.get("email"));
                if (Objects.isNull(user)) {
                    userDAO.save(getUserFromMap(requestMap));
                    return BookshopUtility.getResponseEntity("Successfully Registered", HttpStatus.OK);
                } else {
                    return BookshopUtility.getResponseEntity("Email already exists", HttpStatus.BAD_REQUEST);
                }
            } else {
                return BookshopUtility.getResponseEntity(BookshopConstants.INVALID_DATA, HttpStatus.BAD_REQUEST);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return BookshopUtility.getResponseEntity(BookshopConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }


    private boolean validateSignUpMap(Map<String, String> requestMap) {
        if (requestMap.containsKey("username") && requestMap.containsKey("password") && requestMap.containsKey("email")) {
            return true;
        }
        return false;

    }

    private User getUserFromMap(Map<String, String> requestMap) {
        User user = new User();
        user.setUsername(requestMap.get("username"));
        user.setPassword(requestMap.get("password"));
        user.setEmail(requestMap.get("email"));
        user.setStatus("false");
        user.setRole("user");

        return user;

    }

    @Override
    public ResponseEntity<String> login(Map<String, String> requestMap) {
        log.info("Inside login");
        try {
            Authentication auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(requestMap.get("email"), requestMap.get("password"))
            );
            if (auth.isAuthenticated()) {
                if (customerUserDetailsServ.getUserDetail().getStatus().equalsIgnoreCase("true")) {
                    return new ResponseEntity<String>("{\"token\":\"" +
                            jwtUtil.generateToken(customerUserDetailsServ.getUserDetail().getEmail(),
                                    customerUserDetailsServ.getUserDetail().getRole()) + "\"}",
                            HttpStatus.OK);
                } else {
                    return new ResponseEntity<String>("{\"message\":\"" + "Wait for admin approval." + "\"}", HttpStatus.BAD_REQUEST);
                }
            }
        } catch (Exception ex) {
            log.error("{}", ex);
        }
        return new ResponseEntity<String>("{\"message\":\"" + "Bad credentials." + "\"}",
                HttpStatus.BAD_REQUEST);
    }

    @Override
    public ResponseEntity<List<UserWrapper>> getAllUser() {
        try {
            if (jwtFilter.isAdmin()) {
                return new ResponseEntity<>(userDAO.getAllUser(), HttpStatus.OK);
            } else {
                return new ResponseEntity<>(new ArrayList<>(), HttpStatus.UNAUTHORIZED);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return new ResponseEntity<>(new ArrayList<>(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<String> update(Map<String, String> requestMap) {
        try {
            if (jwtFilter.isAdmin()) {
                Optional<User> optional = userDAO.findById(Integer.parseInt(requestMap.get("id")));
                if(!optional.isEmpty()){
                    userDAO.updateStatus(requestMap.get("status"), Integer.parseInt(requestMap.get("id")));
                    //sendMailToAllAdmin(requestMap.get("status"), optional.get().getEmail(), userDAO.getAllAdmin());
                    return BookshopUtility.getResponseEntity("User Status Updated Successfully", HttpStatus.OK);
                }else{
                    return BookshopUtility.getResponseEntity("User id doesn't exist", HttpStatus.OK);
                }
            } else {
                return BookshopUtility.getResponseEntity(BookshopConstants.UNAUTHORIZED_ACCESS, HttpStatus.UNAUTHORIZED);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return BookshopUtility.getResponseEntity(BookshopConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }


    /*private void sendMailToAllAdmin(String status, String user, List<String> allAdmin) {
        allAdmin.remove(jwtFilter.getCurrentUser());
        if(status!=null && status.equalsIgnoreCase("true")){
            emailUtils.sendSimpleMessage(jwtFilter.getCurrentUser(),"Account Approved", "USER:- "+ user +" \n is approved by \nADMIN:-"+jwtFilter.getCurrentUser(), allAdmin);
        }else {
            emailUtils.sendSimpleMessage(jwtFilter.getCurrentUser(),"Account Disabled", "USER:- "+ user +" \n is disabled by \nADMIN:-"+jwtFilter.getCurrentUser(), allAdmin);
        }
    }*/
    @Override
    public ResponseEntity<String> checkToken() {
        return BookshopUtility.getResponseEntity("true", HttpStatus.OK);
    }

    @Override
    public ResponseEntity<String> changePassword(Map<String, String> requestMap) {
        try{
            User userObj = userDAO.findByEmail(jwtFilter.getCurrentUser());
            if(!userObj.equals(null)){
                if(userObj.getPassword().equals(requestMap.get("oldPassword"))){
                    userObj.setPassword(requestMap.get("newPassword"));
                    userDAO.save(userObj);
                    return BookshopUtility.getResponseEntity("Password Updated Successfully", HttpStatus.OK);
                }
                return BookshopUtility.getResponseEntity("Incorrect Old Password", HttpStatus.BAD_REQUEST);
            }return BookshopUtility.getResponseEntity(BookshopConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
        }catch(Exception ex){
            ex.printStackTrace();
        }
        return BookshopUtility.getResponseEntity(BookshopConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<String> forgotPassword(Map<String, String> requestMap) {
        try{
            User user = userDAO.findByEmail(requestMap.get("email"));
            if(!Objects.isNull(user) && !Strings.isNullOrEmpty(user.getEmail())){
                return BookshopUtility.getResponseEntity("Check your mail for credentials", HttpStatus.OK);
            }
        }catch(Exception ex){
            ex.printStackTrace();
        }
        return BookshopUtility.getResponseEntity(BookshopConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<String> deleteUser(Integer id) {
        try{
            if(jwtFilter.isAdmin()){
                Optional optional =  userDAO.findById(id);
                if(!optional.isEmpty()){
                    userDAO.deleteById(id);
                    return BookshopUtility.getResponseEntity("User Deleted Successfully", HttpStatus.OK);
                }else{
                    return BookshopUtility.getResponseEntity("User id does not exist", HttpStatus.OK);
                }
            }else{
                return BookshopUtility.getResponseEntity(BookshopConstants.UNAUTHORIZED_ACCESS, HttpStatus.UNAUTHORIZED);
            }
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return BookshopUtility.getResponseEntity(BookshopConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }


}
