package com.contramund.inputtypesserver.controllers;

import com.contramund.inputtypesserver.storage.DataBase;
import com.contramund.inputtypesserver.storage.Person;
import com.contramund.inputtypesserver.storage.Storage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import java.util.Optional;
import java.util.UUID;

import static com.contramund.inputtypesserver.storage.Person.defaultPerson;

@Controller
public class ProfileEdit {
    @Autowired
    private DataBase db;

    private Person getPerson(DataBase db, String id) {
        Person ans;

        ans = db.editStorage.getPerson(UUID.fromString(id));
        if (ans != null) return ans;

        ans = db.persistentStorage.getPerson(UUID.fromString(id));
        if (ans != null) db.editStorage.emplacePerson(ans);

        return ans;
    }

    @GetMapping("/profile/{id}/edit")
    @ResponseBody
    ModelAndView showEdit (
            @PathVariable String id
    ) {
        Person person = id.equals("test") ? defaultPerson :  getPerson(db, id);

        ModelAndView modelAndView = new ModelAndView("ProfileEditPage");
        modelAndView.addObject("profile", person);

        return modelAndView;
    }


    @PostMapping(value = "/profile/{id}/edit/sync", consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
    @ResponseStatus ( HttpStatus.OK )
    void SyncEdition(
            @PathVariable String id,
            @RequestParam(value = "fname", required = false) String fname,
            @RequestParam(value = "sname", required = false) String sname,
            @RequestParam(value = "lname", required = false) String lname,
            @RequestParam(value = "sex", required = false) String sex,
            @RequestParam(value = "hidden-data", required = false) String hidden,
            @RequestParam(value = "birthday", required = false) String birthday,
            @RequestParam(value = "email", required = false) String email,
            @RequestParam(value = "telephone", required = false) String phone,
            @RequestParam(value = "mkn-master", required = false) String mknMaster,
            @RequestParam(value = "paper-count", required = false) String paperCount,
            @RequestParam(value = "math-net-link", required = false) String mathNetLink,
            @RequestParam(value = "degree", required = false) String degree,
            @RequestParam(value = "avatar-color", required = false) String avatarColor,
            @RequestParam(value = "description", required = false) String description,
            @RequestParam(value = "password", required = false) String password,
            @RequestParam(value = "avatar", required = false) MultipartFile document
    ) {
        Storage st = db.editStorage;

        System.out.println("Got:");
        if (fname != null) System.out.println("\tfname => " + fname);
        if (sname != null) System.out.println("\tsname => " + sname);
        if (lname != null) System.out.println("\tlname => " + lname);
        if (sex != null) System.out.println("\tsex => " + sex);
        if (hidden != null) System.out.println("\thidden => " + hidden);
        if (birthday != null) System.out.println("\tbirthday => " + birthday);
        if (email != null) System.out.println("\temail => " + email);
        if (phone != null) System.out.println("\tphone => " + phone);
        if (mknMaster != null) System.out.println("\tisMnkMaster => " + mknMaster);
        if (paperCount != null) System.out.println("\tpaperCount => " + paperCount);
        if (mathNetLink != null) System.out.println("\tmathNetLink => " + mathNetLink);
        if (degree != null) System.out.println("\tdegree => " + degree);
        if (avatarColor != null) System.out.println("\tavatarColor => " + avatarColor);
        if (description != null) System.out.println("\tdescription => " + description);
        if (password != null) System.out.println("\tpassword => " + password);
        System.out.println("\n");

        Optional<byte[]> avatar;
        try {
            if(document == null || document.isEmpty()) {
                avatar = Optional.empty();
            } else {
                avatar = Optional.of(document.getBytes());
            }
        } catch (Exception e) {
            System.out.println("Decoding error: " + e.getMessage());
            avatar = Optional.empty();
        }

        Person newPerson = st.getPerson(UUID.fromString(id))
                .copyWithUpdate(
                    fname, sname, lname, sex, hidden, birthday,
                    email, phone, mknMaster, paperCount, mathNetLink,
                    degree, avatarColor, description, avatar);
        st.emplacePerson(newPerson);
    }

    @GetMapping(value = "/profile/{id}/edit/submit")
    @ResponseStatus ( HttpStatus.OK )
    void submitEdition( @PathVariable String id ) {
        Person newPerson = db.editStorage.getPerson(UUID.fromString(id));

        if( newPerson != null) {
            db.persistentStorage.emplacePerson(newPerson);
            db.editStorage.deletePerson(UUID.fromString(id));
            System.out.println("Submitted: " + id);
        } else {
            System.out.println("Corrupted submission: " + id);
        }
    }

    @GetMapping(value = "/profile/{id}/edit/abort")
    @ResponseStatus ( HttpStatus.OK )
    void abortEdition( @PathVariable String id ) {
        db.editStorage.deletePerson(UUID.fromString(id));
        System.out.println("Aborted: " + id);
    }
}
