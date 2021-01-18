package com.example.Server.Rest.Controllers;


import ModelsRestApi.RestUser;
import com.example.Server.DataBase.DataBaseLink;
import com.example.Server.Rest.Jwt.JwtTokenUtils;
import com.example.Server.Rest.Jwt.JwtUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;

@RestController
@RequestMapping("rest-user")
public class RestUserController {
    private DataBaseLink dataBaseLink;
    private JwtTokenUtils jwtTokenUtils;
    private JwtUserDetailsService jwtUserDetailsService;

    public RestUserController() throws SQLException {
        this.dataBaseLink = new DataBaseLink();
        this.jwtTokenUtils = new JwtTokenUtils();
        this.jwtUserDetailsService = new JwtUserDetailsService();
    }


    /**
     *
     * @param username
     * @param password
     * @param userIp
     * @return
     */
    @PostMapping("authenticate")
    public RestUser login(@RequestParam(value="username") String username,
                          @RequestParam(value="password") String password,
                          @RequestParam(value="userIp") String userIp) {
        RestUser restUser = new RestUser(username, password, userIp);
        try {
            if (isPasswordValid(restUser.getUsername(), restUser.getPassword())) {
                    dataBaseLink.updateUser(restUser.getUsername(), restUser.getUserIp());
                final UserDetails userDetails = jwtUserDetailsService.loadUserByUsername(username);
                restUser.setToken(jwtTokenUtils.generateToken(userDetails));
            }
            else {
                restUser.setToken(restUser.getUsername() + "_notLogged");
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            restUser.setToken("NOT_LOGGED");
        }

        return restUser;
    }

    /**
     * Verifies if a password is valid or not
     * @param userName
     * @param password
     * @return true if the password is corrected, false if it is not
     * @throws SQLException
     */
    private boolean isPasswordValid(String userName, String password) throws SQLException {
        return dataBaseLink.logInOperation(userName, password);
    }
}
