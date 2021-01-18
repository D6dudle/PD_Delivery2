package com.example.Server.Rest.Jwt;

import java.util.ArrayList;

import com.example.Server.DataBase.DataBaseLink;
import lombok.SneakyThrows;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.xml.crypto.Data;

@Service
public class JwtUserDetailsService implements UserDetailsService {

    DataBaseLink dataBaseLink;

    @SneakyThrows
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        dataBaseLink = new DataBaseLink();
        String password = dataBaseLink.getUserPassword(username);
        if (password != null && !password.equals("")) {
            return new User(username, password, new ArrayList<>());
        } else {
            throw new UsernameNotFoundException("User not found with username: " + username);
        }
    }
}