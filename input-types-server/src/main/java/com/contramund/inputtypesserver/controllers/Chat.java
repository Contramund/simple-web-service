package com.contramund.inputtypesserver.controllers;

import com.contramund.inputtypesserver.primitives.ChatMessage;
import com.contramund.inputtypesserver.storage.DataBase;
import jakarta.servlet.MultipartConfigElement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@CrossOrigin(maxAge = 3600)
@Controller
public class Chat {
    @Autowired
    DataBase db;

    @CrossOrigin("http://localhost:3000")
    @MessageMapping("/message")
    @SendTo("/chat")
    public ChatMessage receiveMessage(@Payload ChatMessage msg) {
        db.chatHistory.add(msg);

        System.out.println("Got msg: " + msg);
        return msg;
    }

    @Bean
    public MultipartConfigElement multipartConfigElement() {
        return new MultipartConfigElement("");
    }

    @PostMapping(value = "/chat-images/save", consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
    @ResponseBody
    public String saveImage (
        @RequestParam("img") MultipartFile document
    ) {
        byte[] avatar;
        try {
            if(document.isEmpty()) {
                throw new RuntimeException("Expected image, got noting");
            } else {
                avatar = document.getBytes();
            }
        } catch (Exception e) {
            System.out.println("Decoding error: " + e.getMessage());
            throw new RuntimeException("Error loading image");
        }

        UUID newUUID = UUID.randomUUID();

        System.out.println("Created new image under uuid=" + newUUID);
        System.out.println("new image: " + avatar.length);

        db.id_to_chat_img.put(newUUID, avatar);

        return "{\"uuid\":\"" + newUUID + "\"}";
    }

    @GetMapping(value = "/chat-images/load/{id}", produces = {MediaType.IMAGE_JPEG_VALUE, MediaType.IMAGE_PNG_VALUE})
    @ResponseBody
    byte[] loadImage (
        @PathVariable("id") String id
    ) {
        return db.id_to_chat_img.get(UUID.fromString(id));
    }
}
