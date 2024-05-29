package com.contramund.inputtypesserver.controllers;

import com.contramund.inputtypesserver.storage.DataBase;
import com.contramund.inputtypesserver.storage.Person;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@CrossOrigin(maxAge = 3600)
@Controller
public class ProfileLogin {
    @Autowired
    private DataBase db;

    @CrossOrigin("http://localhost:3000")
    @RequestMapping(value = "/userCheck", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String userCheck(
            @RequestParam("login") String login,
            @RequestParam("password") String password
    ) {
        UUID userUUID = db.login_to_uuid.get(login);
        if (userUUID == null) {
            return "{\"uuid\":\"\",\"error\":\"Login invalid\"}";
        }
        Person user = db.persistentStorage.getPerson(userUUID);
        if (user == null) {
            return "{\"uuid\":\"\",\"error\":\"Data inconsistency???\"}";
        }

        if (user.password().equals(password)) {
            return "{\"uuid\":\"" + userUUID + "\",\"error\":\"\"}";
        } else {
            return "{\"uuid\":\"\",\"error\":\"Password invalid\"}";
        }
    }
}
