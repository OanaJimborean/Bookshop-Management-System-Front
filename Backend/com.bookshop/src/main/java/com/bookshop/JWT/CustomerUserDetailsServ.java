package com.bookshop.JWT;

import com.bookshop.dao.UserDAO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Objects;

@Slf4j
@Service
public class CustomerUserDetailsServ implements UserDetailsService {
    @Autowired
    UserDAO userDAO;

    private com.bookshop.model.User userDetail;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException{
        log.info("Inside loadByUsername {}", email);
        userDetail = userDAO.findByEmailId(email);
        if(!Objects.isNull(userDetail))
            return new User(userDetail.getEmail(), userDetail.getPassword(), new ArrayList<>());
        else
            throw  new UsernameNotFoundException("User not found");

    }

    public com.bookshop.model.User getUserDetail(){
        com.bookshop.model.User user = userDetail;
        user.setPassword(null);
        return user;
        //return userDetail;
    }
}
