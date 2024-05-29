package com.contramund.inputtypesserver.controllers;

import com.contramund.inputtypesserver.storage.DataBase;
import com.contramund.inputtypesserver.storage.Person;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;
import java.util.stream.Collectors;

@CrossOrigin(maxAge = 3600)
@Controller
public class ChatInit {
    @Autowired
    private DataBase db;

    @CrossOrigin("http://localhost:3000")
    @RequestMapping(value = "/chat-startup", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String chatStartup(
            @RequestParam("uuid") String rawUserUUID
    ) {
        UUID userUUID = UUID.fromString(rawUserUUID);
        Person usr = db.persistentStorage.getPerson(userUUID);

        String messages = db.chatHistory.stream().map( msg ->
            "{\"senderName\":\"" + msg.senderName() + "\",\"msgType\":\""+msg.msgType()+"\",\"payload\":\"" + msg.payload() + "\",\"date\":\""+ msg.date() + "\"}"
        ).collect(Collectors.joining(","));

        return "{\"userName\":\"" + usr.fname() + " " + usr.lname() + "\",\"history\":[" + messages + "]}";
    }

}
