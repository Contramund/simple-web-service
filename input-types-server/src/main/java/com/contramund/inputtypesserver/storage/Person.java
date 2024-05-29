package com.contramund.inputtypesserver.storage;

import java.util.Optional;
import java.util.UUID;

public record Person (
    UUID personId,
    String fname,
    String sname,
    String lname,
    String sex,
    String hidden,
    Optional<String> birthday,
    String email,
    Optional<String> telephone,
    Boolean isMknMaster,
    Integer paperCount,
    String mathNetLink,
    String degree,
    String avatarColor,
    String description,
    String password,
    Optional<byte[]> avatar
) {
    public final static Person defaultPerson = new Person(
            UUID.randomUUID(),
            "Fname",
            "Sname",
            "Lname",
            "male",
            "some_hidden_data",
            Optional.empty(),
            "example@example.com",
            Optional.of("800-555-3535"),
            true,
            9,
            "https://some.link.net",
            "degree-none",
            "#51e895",
            "There is some sample description:\nLove hiking, playing banjo and solving math problems!\nOpen to work with a new students :3\n\nEnjoy your life:)",
            "password",
            Optional.empty()
    );

    public Person copyWithUpdate(
        String new_fname,
        String new_sname,
        String new_lname,
        String new_sex,
        String new_hidden,
        String new_birthday,
        String new_email,
        String new_phone,
        String new_isMknMaster,
        String new_paperCount,
        String new_mathNetLink,
        String new_degree,
        String new_avatarColor,
        String new_description,
        Optional<byte[]> new_avatar
    ) {
        return new Person(
                this.personId,
                new_fname != null ? new_fname : this.fname,
                new_sname != null ? new_sname : this.sname,
                new_lname != null ? new_lname : this.lname,
                new_sex != null ? new_sex : this.sex,
                new_hidden != null ? new_hidden : this.hidden,
                new_birthday != null ? Optional.of(new_birthday) : this.birthday,
                new_email != null ? new_email : this.email,
                new_phone != null ? Optional.of(new_phone) : this.telephone,
                new_isMknMaster != null ? new_isMknMaster.equals("true") : this.isMknMaster,
                new_paperCount != null ? Integer.valueOf(new_paperCount) : this.paperCount,
                new_mathNetLink != null ? new_mathNetLink : this.mathNetLink,
                new_degree != null ? new_degree : this.degree,
                new_avatarColor != null ? new_avatarColor : this.avatarColor,
                new_description != null ? new_description : this.description,
                password,
                new_avatar.isPresent() ? new_avatar : this.avatar
        );
    }
};

