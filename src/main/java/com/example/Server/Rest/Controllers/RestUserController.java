package com.example.Server.Rest.Controllers;


import ModelsRestApi.RestUser;
import com.example.Server.DataBase.DataBaseLink;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;

@RestController
@RequestMapping("rest-user")
public class RestUserController {
    private DataBaseLink dataBaseLink;

    public RestUserController() throws SQLException {
        this.dataBaseLink = new DataBaseLink();
    }

    /**
     *
     * @param username
     * @param password
     * @param userIp
     * @return
     */
    @PostMapping("login")
    public RestUser login(@RequestParam(value="username") String username,
                          @RequestParam(value="password") String password,
                          @RequestParam(value="userIp") String userIp) {
        RestUser restUser = new RestUser(username, password, userIp);
        try {
            if (isPasswordValid(restUser.getUsername(), restUser.getPassword())) {
                    dataBaseLink.updateUser(restUser.getUsername(), restUser.getUserIp());

                restUser.setToken(restUser.getUsername() + "_logged");
            }
            else {
                restUser.setToken(restUser.getUsername() + "_notLogged");
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            restUser.setToken(restUser.getUsername() + "_notLogged");
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
