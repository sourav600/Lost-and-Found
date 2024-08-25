package com.moinul.LostAndFound.service;

import com.moinul.LostAndFound.exception.ResourceNotFoundException;
import com.moinul.LostAndFound.model.Person;
import com.moinul.LostAndFound.model.PersonStatus;
import com.moinul.LostAndFound.repository.PersonRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class PersonServiceImpl implements PersonService{
    private final PersonRepository repository;

    @Override
    public Person createPerson(Person person, Long userId) {
        person.setCreatedUserId(userId);
        person.setStatus(PersonStatus.MISSING);
        return repository.save(person);
    }

    @Override
    public Person getPersonById(Long personId) {
        return repository.findById(personId)
                .orElseThrow(() -> new ResourceNotFoundException("Person not found with this Id: "+personId));
    }
    @Override
    public List<Person> getAllPerson() {
        List<Person> allPerson = repository.findAll();
        return allPerson.stream().map((person) -> (person))
                .collect(Collectors.toList());
    }

    @Override
    public Person updatePerson(Long personId, Person updateData, Long userId) {

        Person person = getPersonById(personId);
        if (!person.getCreatedUserId().equals(userId)) {
            throw new ResourceNotFoundException("You can't update another person's post!");
        }

        if(updateData.getFullName()!=null)
            person.setFullName(updateData.getFullName());
        if(updateData.getAge()!=null)
            person.setAge(updateData.getAge());
        if(updateData.getGender()!=null)
            person.setGender(updateData.getGender());
        if(updateData.getContact()!=null)
            person.setContact(updateData.getContact());
        if(updateData.getAddress()!=null)
            person.setAddress(updateData.getAddress());
        if(updateData.getDescription()!=null)
            person.setDescription(updateData.getDescription());
        if(updateData.getHeight()!=null)
            person.setHeight(updateData.getHeight());
        if(updateData.getWeight()!=null)
            person.setWeight(updateData.getWeight());
        if(updateData.getHairColor()!=null)
            person.setHairColor(updateData.getHairColor());
        if(updateData.getEyeColor()!=null)
            person.setEyeColor(updateData.getEyeColor());
        if(updateData.getMissingDate()!=null)
            person.setMissingDate(updateData.getMissingDate());
        if(updateData.getPhoto()!=null)
            person.setPhoto(updateData.getPhoto());
        if(updateData.getStatus()!=null)
            person.setStatus(updateData.getStatus());

        return repository.save(person);
    }

    @Override
    public void deletePerson(Long personId, Long userId) {
        Person person = getPersonById(personId);
        if (!person.getCreatedUserId().equals(userId)) {
            throw new ResourceNotFoundException("You can't delete another person's post!");
        }
        repository.deleteById(personId);
    }
}
