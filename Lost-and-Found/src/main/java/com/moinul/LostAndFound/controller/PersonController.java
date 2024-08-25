package com.moinul.LostAndFound.controller;

import com.moinul.LostAndFound.model.Person;
import com.moinul.LostAndFound.model.UserDto;
import com.moinul.LostAndFound.service.PersonService;
import com.moinul.LostAndFound.service.PersonServiceImpl;
import com.moinul.LostAndFound.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/person")
public class PersonController {

    @Autowired
    private PersonService service;

    @Autowired
    private UserService userService;

    @PostMapping
    public ResponseEntity<Person> createPerson(@RequestBody Person person,
                                               @RequestHeader("Authorization") String jwt) throws Exception{
        UserDto user = userService.getUserProfile(jwt);
        Person savedPerson = service.createPerson(person, user.getId());
//        Person savedPerson = service.createPerson(person);
        return new ResponseEntity<>(savedPerson, HttpStatus.CREATED);
    }

    @GetMapping("{id}")
    public ResponseEntity<Person> getPersonById(@PathVariable("id") Long personId) throws Exception{
        Person dto = service.getPersonById(personId);
        return ResponseEntity.ok(dto);
    }

    @GetMapping
    public ResponseEntity<List<Person>> getAllPerson() throws Exception{
        List<Person> allPerson = service.getAllPerson();
        return ResponseEntity.ok(allPerson);
    }

    @PutMapping("{id}")
    public ResponseEntity<Person> updatePerson(@PathVariable("id") Long personId,
                                               @RequestBody Person updateData,
                                               @RequestHeader("Authorization") String jwt) throws Exception{
        UserDto user = userService.getUserProfile(jwt);
        Person person = service.updatePerson(personId, updateData, user.getId());
        return  ResponseEntity.ok(person);
    }

    @DeleteMapping("{id}")
    public ResponseEntity<String> deletePerson(@PathVariable("id") Long personId,
                                               @RequestHeader("Authorization") String jwt) throws Exception{
        UserDto user = userService.getUserProfile(jwt);
        service.deletePerson(personId, user.getId());
        return ResponseEntity.ok("Successfully deleted person with id: "+ personId);
    }
}
