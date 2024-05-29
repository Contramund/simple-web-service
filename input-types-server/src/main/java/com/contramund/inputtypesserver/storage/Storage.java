package com.contramund.inputtypesserver.storage;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class Storage {
    private final HashMap<UUID, Person> idToPerson = new HashMap<>() {{
        put(Person.defaultPerson.personId(), Person.defaultPerson);
    }};

    public Person getPerson(UUID id) {
        return idToPerson.getOrDefault(id, null);
    }

    public void emplacePerson(Person p) {
        idToPerson.put(p.personId(), p);
    }

    public void deletePerson(UUID id) { idToPerson.remove(id); }

    public List<Person> getAllPersons() {
        return idToPerson.values().stream().toList();
    }

    public UUID createPerson(
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
        UUID newPersonsId = UUID.randomUUID();
        idToPerson.put(
            newPersonsId,
            new Person(newPersonsId, fname, sname, lname, sex, hidden, birthday, email, telephone,
                isMknMaster, paperCount, mathNetLink, degree, avatarColor, description, password, avatar)
        );

        return newPersonsId;
    }

}
