package com.moinul.LostAndFound.controller;

import com.moinul.LostAndFound.model.Person;
import com.moinul.LostAndFound.model.UserDto;
import com.moinul.LostAndFound.service.ImageSearchService;
import com.moinul.LostAndFound.service.PersonService;
import com.moinul.LostAndFound.service.PersonServiceImpl;
import com.moinul.LostAndFound.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;

import static com.moinul.LostAndFound.constant.PHOTO_DIRECTORY;
import static org.springframework.http.MediaType.IMAGE_JPEG_VALUE;
import static org.springframework.http.MediaType.IMAGE_PNG_VALUE;

@RestController
@RequestMapping("/api/person")
@Tag(name = "Missing Person Management")
public class PersonController {

    @Autowired
    private PersonService service;

    @Autowired
    private UserService userService;

    @Autowired
    private ImageSearchService imageSearchService;


    @Operation(description = "Just fill up the form with required information. It is highly recommend to provide latest info about missing one including Photo. ",
                summary = "Create a Missing person post")
    @PostMapping
    public ResponseEntity<Person> createPerson(@RequestBody Person person,
                                               @RequestHeader("Authorization") String jwt) throws Exception{

        UserDto user = userService.getUserProfile(jwt);
        Person savedPerson = service.createPerson(person, user.getId());

        return new ResponseEntity<>(savedPerson, HttpStatus.CREATED);
    }



    @Operation(summary = "Search a missing person by id")
    @GetMapping("{id}")
    public ResponseEntity<Person> getPersonById(@PathVariable("id") Long personId) throws Exception{
        Person dto = service.getPersonById(personId);
        return ResponseEntity.ok(dto);
    }



    @Operation(summary = "Get all of the Missing person")
    @GetMapping
    public ResponseEntity<List<Person>> getAllPerson() throws Exception{
        List<Person> allPerson = service.getAllPerson();
        return ResponseEntity.ok(allPerson);
    }



    @Operation(summary = "Update Missing person data",
                description = "***Warning*** Only allow the user to update/modify a missing person data who is created this post!")
    @PutMapping("{id}")
    public ResponseEntity<Person> updatePerson(@PathVariable("id") Long personId,
                                               @RequestBody Person updateData,
                                               @RequestHeader("Authorization") String jwt) throws Exception{
        UserDto user = userService.getUserProfile(jwt);
        Person person = service.updatePerson(personId, updateData, user.getId());
        return  ResponseEntity.ok(person);
    }


//    @PutMapping("/photo")
//    public ResponseEntity<String> uploadPhoto(@RequestParam("id") Long id,
//                                              @RequestParam("file")MultipartFile file,
//                                              @RequestHeader("Authorization") String jwt) throws Exception {
//
//        UserDto user = userService.getUserProfile(jwt);
//        String photoURL = service.uploadPhoto(id,file,user.getId());
//        return ResponseEntity.ok().body(photoURL);
//    }

    @Operation(summary = "Upload Missing person photo")
//    @PostMapping("/photo")
    @RequestMapping(
            path = "/photo",
            method = RequestMethod.POST,
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> uploadPhoto(@RequestParam("id") Long id,
                                              @RequestPart("file") MultipartFile file,
                                              @RequestHeader("Authorization") String jwt) throws Exception {

        UserDto user = userService.getUserProfile(jwt);
        String photoURL = service.uploadPhoto(id,file,user.getId());
        return ResponseEntity.ok().body(photoURL);
    }


    @GetMapping(path = "/image/{filename}", produces = {IMAGE_PNG_VALUE, IMAGE_JPEG_VALUE})
    public byte[] getPhoto (@PathVariable("filename") String filename) throws IOException {
        return Files.readAllBytes(Paths.get(PHOTO_DIRECTORY + filename));
    }


    @Operation(summary = "Delete Missing person post",
            description = "***Warning*** Only allow the user to delete a missing person who is created this post!")
    @DeleteMapping("{id}")
    public ResponseEntity<String> deletePerson(@PathVariable("id") Long personId,
                                               @RequestHeader("Authorization") String jwt) throws Exception{
        UserDto user = userService.getUserProfile(jwt);
        service.deletePerson(personId, user.getId());
        return ResponseEntity.ok("Successfully deleted person with id: "+ personId);
    }

//    @PostMapping("/search-by-image")
    @RequestMapping(
        path = "/search-by-image",
        method = RequestMethod.POST,
        consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<List<String>> searchByImage(@RequestPart("image") MultipartFile[] image) {
        try {
            //List<String> base64Image = Base64.getEncoder().encodeToString(image.getBytes());
            List<String> matchedPersons = service.searchPersons(image);
            return ResponseEntity.ok(matchedPersons);
        }
        catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Arrays.asList("Error in processing image: " + e.getMessage()));
        }
    }
}
