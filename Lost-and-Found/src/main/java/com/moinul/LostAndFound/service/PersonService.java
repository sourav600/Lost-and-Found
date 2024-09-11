package com.moinul.LostAndFound.service;

import com.moinul.LostAndFound.exception.ResourceNotFoundException;
import com.moinul.LostAndFound.model.Person;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface PersonService {
    Person createPerson(Person person, Long userId) throws ResourceNotFoundException;
    String uploadPhoto(Long id, MultipartFile file, Long userId) throws IOException;
    Person getPersonById(Long personId) throws ResourceNotFoundException;
    List<Person> getAllPerson() throws ResourceNotFoundException;
    Person updatePerson(Long personId, Person updateData, Long userId) throws ResourceNotFoundException;
    void deletePerson(Long personId, Long userId) throws Exception;
    String searchPersons(String base64image) throws IOException;
}
