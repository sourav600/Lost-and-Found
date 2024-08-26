package com.moinul.LostAndFound.service;

import com.moinul.LostAndFound.exception.ResourceNotFoundException;
import com.moinul.LostAndFound.model.Person;
import com.moinul.LostAndFound.model.PersonStatus;
import com.moinul.LostAndFound.repository.PersonRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.moinul.LostAndFound.constant.PHOTO_DIRECTORY;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;


@Service
@AllArgsConstructor
public class PersonServiceImpl implements PersonService{

    private final PersonRepository repository;

    @Override
    public Person createPerson(Person person, Long userId)  throws ResourceNotFoundException {
        person.setCreatedUserId(userId);
        person.setStatus(PersonStatus.MISSING);
        return repository.save(person);
    }

    @Override
    public  String uploadPhoto(Long id, MultipartFile file, Long userId) throws ResourceNotFoundException{

        Person person = getPersonById(id);
        if (!person.getCreatedUserId().equals(userId)) {
            throw new ResourceNotFoundException("You can't update another person's post!");
        }

        String photoUrl = photoFunction.apply(id.toString(), file);
        person.setPhoto(photoUrl);
        repository.save(person);
        return photoUrl;
    }


    @Override
    public Person getPersonById(Long personId) throws ResourceNotFoundException{
        return repository.findById(personId)
                .orElseThrow(() -> new ResourceNotFoundException("Person not found with this Id: "+personId));
    }
    @Override
    public List<Person> getAllPerson() throws ResourceNotFoundException {
        List<Person> allPerson = repository.findAll();
        return allPerson.stream().map((person) -> (person))
                .collect(Collectors.toList());
    }

    @Override
    public Person updatePerson(Long personId, Person updateData, Long userId) throws ResourceNotFoundException{

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
        if(updateData.getStatus()!=null)
            person.setStatus(updateData.getStatus());

        return repository.save(person);
    }

    @Override
    public void deletePerson(Long personId, Long userId) throws ResourceNotFoundException {
        Person person = getPersonById(personId);
        if (!person.getCreatedUserId().equals(userId)) {
            throw new ResourceNotFoundException("You can't delete another person's post!");
        }
        repository.deleteById(personId);
    }

    private final Function<String, String> fileExtension = fileName -> Optional.of(fileName).filter(name -> name.contains(".")).
            map(name -> "." + name.substring(fileName.lastIndexOf(".") + 1)).orElse(".png");

    private  final BiFunction<String, MultipartFile, String> photoFunction= (id, image) -> {
        String fileName = id + "_" + image.getOriginalFilename();
        try {
            Path fileStroageLocation = Paths.get(PHOTO_DIRECTORY).toAbsolutePath().normalize();
            if(!Files.exists(fileStroageLocation)) { Files.createDirectories(fileStroageLocation); }
            Files.copy(image.getInputStream(), fileStroageLocation.resolve(fileName), REPLACE_EXISTING);
            return ServletUriComponentsBuilder
                    .fromCurrentContextPath()
                    .path("/api/person/image/" + fileName ).toUriString();
        }catch (Exception exception){
            throw new RuntimeException("Unable to save image");
        }
    };

}
