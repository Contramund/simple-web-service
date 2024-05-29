package com.contramund.inputtypesserver.controllers;

import com.contramund.inputtypesserver.storage.DataBase;
import com.contramund.inputtypesserver.storage.Storage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.view.RedirectView;

import javax.xml.crypto.Data;
import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

@Controller
public class Registration {
    @Autowired
    private DataBase db;

    @PostMapping(value = "/registration", consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
    RedirectView register(
            @RequestParam("fname") String fname,
            @RequestParam("sname") String sname,
            @RequestParam("lname") String lname,
            @RequestParam("sex") String sex,
            @RequestParam("hidden-data") String hidden,
            @RequestParam("birthday") String birthday,
            @RequestParam("email") String email,
            @RequestParam("telephone") String phone,
            @RequestParam(value = "mkn-master", required = false) String mknMaster,
            @RequestParam("paper-count") String paperCount,
            @RequestParam("math-net-link") String mathNetLink,
            @RequestParam("degree") String degree,
            @RequestParam("avatar-color") String avatarColor,
            @RequestParam("description") String description,
            @RequestParam("password") String password,
            @RequestParam("avatar") MultipartFile document
    ) {
        Storage st = db.persistentStorage;

        Optional<byte[]> avatar;
        try {
            if(document.isEmpty()) {
                avatar = Optional.empty();
            } else {
                avatar = Optional.of(document.getBytes());
            }
        } catch (Exception e) {
            System.out.println("Decoding error: " + e.getMessage());
            avatar = Optional.empty();
        }

        UUID newPersonId = st.createPerson(
                fname,
                sname,
                lname,
                sex,
                hidden,
                birthday.isEmpty() ? Optional.empty() : Optional.of(birthday),
                email,
                phone.isEmpty() ? Optional.empty() : Optional.of(phone),
                mknMaster != null,
                Integer.valueOf(paperCount),
                mathNetLink,
                degree,
                avatarColor,
                description,
                password,
                avatar
        );

        db.login_to_uuid.put(email, newPersonId);
        if (!phone.isEmpty()) db.login_to_uuid.put(phone, newPersonId);

        return new RedirectView("profile/" + newPersonId.toString() + "/view");
    }
}
