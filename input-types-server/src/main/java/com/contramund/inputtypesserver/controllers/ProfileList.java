package com.contramund.inputtypesserver.controllers;

import com.contramund.inputtypesserver.storage.DataBase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class ProfileList {
    @Autowired
    private DataBase db;

    @GetMapping("/ProfileListPage.html")
    @ResponseBody
    ModelAndView showProfile () {
        ModelAndView modelAndView = new ModelAndView("ProfileListPage");
        modelAndView.addObject("profileList", db.persistentStorage.getAllPersons());
        modelAndView.addObject("draftList", db.editStorage.getAllPersons());
        modelAndView.addObject("pictureList", db.id_to_chat_img.keySet().stream().toList());

        return modelAndView;
    }
}
