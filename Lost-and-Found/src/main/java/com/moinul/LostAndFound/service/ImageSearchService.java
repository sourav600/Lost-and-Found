package com.moinul.LostAndFound.service;

import com.moinul.LostAndFound.exception.ResourceNotFoundException;
import com.moinul.LostAndFound.model.Person;
import com.moinul.LostAndFound.repository.PersonRepository;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ImageSearchService {
    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private PersonRepository personRepository;

    public List<Person> searchByImage(MultipartFile image) throws IOException {
        // Convert image to Base64
        String base64Image = encodeToBase64(image);

        // Prepare JSON payload
        String jsonPayload = buildJsonPayload(base64Image);

        // Set headers
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-API-Key", "8DpB_hYNzM1NjAxYzAtM2VmMi00YThjLThiMjctYTFkYTllZTgwYjc4");
        headers.setContentType(MediaType.APPLICATION_JSON);

        // Create request entity
        HttpEntity<String> requestEntity = new HttpEntity<>(jsonPayload, headers);

        // Send request to OpenCV API
        String url = "https://sg.opencv.fr/compare";
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, requestEntity, String.class);

        // Handle response
        if (response.getStatusCode() == HttpStatus.OK) {
            String opencvResponse = response.getBody();
            // Compare this result with features stored in the database
            List<Person> matchedPersons = matchWithDatabase(opencvResponse);
            return matchedPersons;
        } else {
            // Handle error response
            throw new RuntimeException("Failed to get response from OpenCV API: " + response.getStatusCode());
        }
    }

    private String encodeToBase64(MultipartFile image) throws IOException {
        byte[] bytes = image.getBytes();
        return Base64.encodeBase64String(bytes);
    }

    private String buildJsonPayload(String base64Image) {
        // Construct JSON payload
        return String.format(
                "{\"gallery\": [\"%s\"], \"probe\": [\"%s\"], \"search_mode\": \"FAST\"}",
                base64Image, base64Image
        );
    }

    private List<Person> matchWithDatabase(String opencvResponse) {
        // Implement logic to compare response features with the stored images
        List<Person> persons = personRepository.findAll();

        return persons.stream()
                .filter(person -> compareImages(opencvResponse, person.getPhoto()))
                .collect(Collectors.toList());
//        return persons.stream()
//                .filter(person -> {
//                    try {
//                        // Fetch and encode image from path
//                        String base64ImageFromDb = encodeImageFromPath(person.getPhoto());
//                        return compareImages(opencvResponse, base64ImageFromDb);
//                    } catch (IOException e) {
//                        // Handle IO exceptions, e.g., log or rethrow
//                        throw new RuntimeException("Error processing image: " + e.getMessage(), e);
//                    }
//                })
//                .collect(Collectors.toList());
    }

    private String encodeImageFromPath(String imagePath) throws IOException {
        byte[] bytes;
        if (imagePath.startsWith("http://") || imagePath.startsWith("https://")) {
            // Read image from URL
            bytes = readImageFromUrl(imagePath);
        } else {
            // Read image from file path
            bytes = Files.readAllBytes(Paths.get(imagePath));
        }
        return Base64.encodeBase64String(bytes);
    }

    private byte[] readImageFromUrl(String imageUrl) throws IOException {
        URL url = new URL(imageUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        try (InputStream inputStream = connection.getInputStream()) {
            return inputStream.readAllBytes();
        }
    }

    private boolean compareImages(String apiImage, String dbImage) {
        // Implement actual comparison logic here
        return apiImage.equals(dbImage);
    }
//    public List<Person> searchByImage(MultipartFile image) throws IOException {
//        // Convert image to Base64
//        String base64Image = encodeToBase64(image);
//
//        // Prepare JSON payload
//        String jsonPayload = buildJsonPayload(base64Image);
//
//        // Set headers
//        HttpHeaders headers = new HttpHeaders();
//        headers.set("X-API-Key", "8DpB_hYNzM1NjAxYzAtM2VmMi00YThjLThiMjctYTFkYTllZTgwYjc4");
//        headers.setContentType(MediaType.APPLICATION_JSON);
//
//        // Create request entity
//        HttpEntity<String> requestEntity = new HttpEntity<>(jsonPayload, headers);
//
//        // Send request to OpenCV API
//        String url = "https://sg.opencv.fr/compare";
//        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, requestEntity, String.class);
//
//        // Handle response
//        if (response.getStatusCode() == HttpStatus.OK) {
//            String opencvResponse = response.getBody();
//            // Compare this result with features stored in the database
//            List<Person> matchedPersons = matchWithDatabase(opencvResponse);
//            return matchedPersons;
//        } else {
//            // Handle error response
//            throw new RuntimeException("Failed to get response from OpenCV API: " + response.getStatusCode());
//        }
//    }
//
//    private String encodeToBase64(MultipartFile image) throws IOException {
//        byte[] bytes = image.getBytes();
//        return Base64.encodeBase64String(bytes);
//    }
//
//    private String buildJsonPayload(String base64Image) {
//        // Construct JSON payload
//        return String.format(
//                "{\"gallery\": [\"%s\"], \"probe\": [\"%s\"], \"search_mode\": \"FAST\"}",
//                base64Image, base64Image
//        );
//    }
//
//    private List<Person> matchWithDatabase(String opencvResponse) {
//        // Implement logic to compare response features with the stored images
//        List<Person> persons = personRepository.findAll();
//
//        // Example pseudo-logic for comparison
//        return persons.stream()
//                .filter(person -> compareImages(opencvResponse, encodeToBase64(person.getPhoto())))
//                .collect(Collectors.toList());
//    }
//
//    private boolean compareImages(String apiImage, String dbImage) {
//        // Implement actual comparison logic here
//        return apiImage.equals(dbImage);
//    }

//    public List<Person> searchByImage(MultipartFile image) throws IOException {
//        // Convert image to Base64 or ByteArray for sending
//        HttpHeaders headers = new HttpHeaders();
//        headers.set("X-API-Key", "8DpB_hYNzM1NjAxYzAtM2VmMi00YThjLThiMjctYTFkYTllZTgwYjc4");
//        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
//
//        // Prepare request
//        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
//        body.add("image", new ByteArrayResource(image.getBytes()));
//
//        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);
//
//        // Send request to OpenCV API
//        String url = "https://sg.opencv.fr/compare";
//        ResponseEntity<String> response = restTemplate.postForEntity(url, requestEntity, String.class);
//
//        // Parse the response to get features or matching result
//        String opencvResponse = response.getBody();
//
//        // Compare this result with features stored in the database
//        List<Person> matchedPersons = matchWithDatabase(opencvResponse);
//
//        return matchedPersons;
//    }
//
//    private List<Person> matchWithDatabase(String opencvResponse) {
//        // Implement logic to compare response features with the stored images
//        List<Person> persons = personRepository.findAll();
//
//        // Example pseudo-logic for comparison
//        return persons.stream()
//                .filter(person -> compareImages(opencvResponse, person.getPhoto()))
//                .collect(Collectors.toList());
//    }
//
//    private boolean compareImages(String apiphotoPath, String DBphotoPath) {
//        // Implement actual comparison logic here
//        return apiphotoPath.equals(DBphotoPath);
//    }
}
