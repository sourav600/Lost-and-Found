package com.moinul.LostAndFound.service;

import com.moinul.LostAndFound.model.Person;

import java.util.List;

public interface PersonService {
//    Person createPerson(Person person) throws Exception;
    Person createPerson(Person person, Long userId) throws Exception;
    Person getPersonById(Long personId) throws Exception;
    List<Person> getAllPerson() throws Exception;
    Person updatePerson(Long personId, Person updateData, Long userId) throws Exception;
    void deletePerson(Long personId, Long userId) throws Exception;
}
