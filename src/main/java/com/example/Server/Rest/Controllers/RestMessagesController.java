package com.example.Server.Rest.Controllers;


import com.example.Server.Data.MessageData;
import com.example.Server.DataBase.DataBaseLink;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("rest-messages")
public class RestMessagesController {
    private DataBaseLink dataBaseLink;

    public RestMessagesController() throws SQLException {
        this.dataBaseLink = new DataBaseLink();
    }

    @GetMapping("get-last-n")
    public List<MessageData> getLastNMessages(@RequestParam(value = "n") Integer n) throws SQLException {

        return dataBaseLink.getLastNMessages(n);
    }

    @PostMapping("deploy-message")
    public void deployMessage(@RequestParam(value = "message") String message, @RequestParam(value = "username") String username) throws SQLException {
        dataBaseLink.saveRestMessage(new MessageData(message,
                username,
                null,
                "global",
                LocalDateTime.now(),
                false,
                false));

    }

}
