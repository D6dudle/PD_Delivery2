package com.example.Server.Controllers;


import Data.AuthenticationRequestData;
import Data.AuthenticationResponseData;
import ModelsRestApi.RestUser;
import com.example.Server.Data.ClientData;
import com.example.Server.DataBase.DataBaseLink;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.sql.SQLException;

@RestController
@RequestMapping("rest-user")
public class RestUserController {
    private DataBaseLink dataBaseLink;

    @Autowired
    public RestUserController(DataBaseLink dataBaseLink){
        this.dataBaseLink = dataBaseLink;
    }

    /*

    public RestUserController(){
        this.dataBaseLink = new DataBaseLink();
    }

     */

    @PostMapping("login")
    public boolean login(@RequestBody RestUser restUser) {
        try {
            if (isPasswordValid(restUser.getUsername(), restUser.getPassword())) {
                    dataBaseLink.updateUser(restUser.getUsername(), restUser.getUserIp());
                return true;
            }
            else {
                return false;
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            return false;
        }

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
