package com.moinul.LostAndFound.service;

import com.moinul.LostAndFound.exception.ResourceNotFoundException;
import com.moinul.LostAndFound.model.Person;
import com.moinul.LostAndFound.model.PersonStatus;
import com.moinul.LostAndFound.repository.PersonRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.json.JSONObject;

import static com.moinul.LostAndFound.constant.PHOTO_DIRECTORY;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;


@Service
@AllArgsConstructor
public class PersonServiceImpl implements PersonService{
    private final PersonRepository repository;

    @Autowired
    private RestTemplate restTemplate;

    @Override
    public Person createPerson(Person person, Long userId)  throws ResourceNotFoundException {
        person.setCreatedUserId(userId);
        person.setStatus(PersonStatus.MISSING);
        return repository.save(person);
    }

    @Override
    public  String uploadPhoto(Long id, MultipartFile file, Long userId) throws  IOException{

        Person person = getPersonById(id);
        if (!person.getCreatedUserId().equals(userId)) {
            throw new ResourceNotFoundException("You can't update another person's post!");
        }

        String base64Image = Base64.getEncoder().encodeToString(file.getBytes());
        String photoUrl = photoFunction.apply(id.toString(), base64Image);

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

    @Override
    public List<String> searchPersons(MultipartFile[] base64Image) throws IOException {

        List<String> galleryBase64 = convertToBase64List(base64Image);

        String url =  "https://sg.opencv.fr/compare";
        String apiKey = "8DpB_hYNzM1NjAxYzAtM2VmMi00YThjLThiMjctYTFkYTllZTgwYjc4";
        String searchMode = "ACCURATE";

        HttpHeaders headers = new HttpHeaders();
        headers.set("x-api-key", apiKey);
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("accept", "application/json");

        List<Person> personList = getAllPerson();
        List<String> scoreList = new ArrayList<>();

        for(Person person: personList){
            String dbImage = person.getPhoto();

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("gallery", galleryBase64);
            requestBody.put("probe", galleryBase64);
            requestBody.put("search_mode", searchMode);

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);

            if(response.getStatusCode() == HttpStatus.OK){
                String responseBody = response.getBody();
                JSONObject jsonResponse = new JSONObject(responseBody);
                double score = jsonResponse.getDouble("score");
                scoreList.add("Person ID: " + person.getId() + " - Score: " + score);
                //return  response.getBody();
            }else{
                throw new IOException("Failed to search! " + response.getStatusCode());
            }
        }
        return scoreList;

    }

    private List<String> convertToBase64List(MultipartFile[] images) throws IOException {
        return Arrays.stream(images)
                .map(image -> {
                    try {
                        return Base64.getEncoder().encodeToString(image.getBytes());
                    } catch (IOException e) {
                        throw new RuntimeException("Failed to encode image", e);
                    }
                })
                .collect(Collectors.toList());
    }

    private final Function<String, String> fileExtension = fileName -> Optional.of(fileName).filter(name -> name.contains(".")).
            map(name -> "." + name.substring(fileName.lastIndexOf(".") + 1)).orElse(".png");


    private final BiFunction<String, String, String> photoFunction = (id, base64Image) -> {
        try {
            byte[] imageBytes = Base64.getDecoder().decode(base64Image);

            String fileName = id + fileExtension.apply(id); // You can choose the appropriate file extension

            Path fileStorageLocation = Paths.get(PHOTO_DIRECTORY).toAbsolutePath().normalize();

            if (!Files.exists(fileStorageLocation)) {
                Files.createDirectories(fileStorageLocation);
            }
            Path targetLocation = fileStorageLocation.resolve(fileName);
            Files.write(targetLocation, imageBytes);

            return ServletUriComponentsBuilder
                    .fromCurrentContextPath()
                    .path("/api/person/image/" + fileName).toUriString();
        } catch (Exception exception) {
            throw new RuntimeException("Unable to save image", exception);
        }
    };



}
