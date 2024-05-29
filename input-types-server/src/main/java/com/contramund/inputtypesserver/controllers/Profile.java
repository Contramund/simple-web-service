package com.contramund.inputtypesserver.controllers;

import com.contramund.inputtypesserver.storage.DataBase;
import com.contramund.inputtypesserver.storage.Person;
import com.contramund.inputtypesserver.storage.Storage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.UUID;

import static com.contramund.inputtypesserver.storage.Person.defaultPerson;

@Controller
public class Profile {
    @Autowired
    private DataBase db;


    @GetMapping("/profile/{id}/view")
    @ResponseBody
    ModelAndView showProfile (
            @PathVariable String id
    ) {
        Storage st = db.persistentStorage;

        Person person = id.equals("test") ? defaultPerson :  st.getPerson(UUID.fromString(id));

        ModelAndView modelAndView = new ModelAndView("ProfilePage");
        modelAndView.addObject("profile", person);

        return modelAndView;
    }

    @GetMapping(value = "/profile/{id}/avatar", produces = {MediaType.IMAGE_JPEG_VALUE, MediaType.IMAGE_PNG_VALUE})
    @ResponseBody
    byte[] sendProfileAvatar (
            @PathVariable String id
    ) {
        Storage st = db.persistentStorage;

        Person person = id.equals("test") ? defaultPerson : st.getPerson(UUID.fromString(id));

        if (person.avatar().isEmpty()) {
            byte[] buf;
            try {
                buf = Files.readAllBytes(Paths.get("src/main/resources/static/defaultAvatar.png"));
            } catch (Exception e) {
                System.out.println("Cannot open file: " + e.getMessage());
                buf = new byte[]{};
            }
            return buf;
        } else {
            return person.avatar().get();
        }
    }
}
