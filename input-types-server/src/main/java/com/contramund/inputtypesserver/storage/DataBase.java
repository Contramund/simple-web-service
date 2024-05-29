package com.contramund.inputtypesserver.storage;

import com.contramund.inputtypesserver.primitives.ChatMessage;
import com.contramund.inputtypesserver.primitives.ChatMessageType;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

@Component
@Scope(scopeName = "singleton")
public class DataBase {
    public Storage persistentStorage = new Storage();
    public Storage editStorage = new Storage();
    public HashMap<String, UUID> login_to_uuid = new HashMap<>(){{
        put(Person.defaultPerson.email(), Person.defaultPerson.personId());
    }};
    public ArrayList<ChatMessage> chatHistory = new ArrayList<>(List.of(
        new ChatMessage("Random Turtle", ChatMessageType.TextMsg, "Hello, dude!", "long ago"),
        new ChatMessage("Concrete Bobr", ChatMessageType.TextMsg, "Nice to meet you!", "long ago")
    ));
    public HashMap<UUID, byte[]> id_to_chat_img = new HashMap<>();
}
