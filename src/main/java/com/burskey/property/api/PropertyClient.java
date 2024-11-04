package com.burskey.property.api;

import com.burskey.property.domain.Property;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.DefaultUriBuilderFactory;

public class PropertyClient {




    private RestClient getByNameAndCategoryClient;
    private RestClient saveClient;
    private RestClient getByIDClient;



    public static PropertyClient Builder() {
        return new PropertyClient();
    }


    public PropertyClient withSave(String aURI) {
        RestClient restClient = RestClient.create(aURI);
        this.saveClient = restClient;
        return this;
    }

    public PropertyClient withGetByID(String aURI) {

        DefaultUriBuilderFactory factory = new DefaultUriBuilderFactory(aURI);

        RestClient restClient = RestClient.builder().uriBuilderFactory(factory).build();
        this.getByIDClient = restClient;

        return this;
    }


    public PropertyClient withGetByCategoryAndName(String aURI) {

        DefaultUriBuilderFactory factory = new DefaultUriBuilderFactory(aURI);

        RestClient restClient = RestClient.builder().uriBuilderFactory(factory).build();
        this.getByNameAndCategoryClient = restClient;



        return this;
    }



    public ResponseEntity save(Property property) {


        ResponseEntity<String> response = this.saveClient.post()
                .contentType(MediaType.APPLICATION_JSON)
                .body(property)
                .retrieve()
                .toEntity(String.class);

        if (response.getStatusCode() == HttpStatus.OK) {
            // Success
            String body = response.getBody();
            if (body != null) {
                property.setId(body);
            }
        } else {
            // Handle error
        }


        return response;
    }

    public ResponseEntity findByID(String id) {

        ResponseEntity<String> response = this.getByIDClient
                .get()
                .uri("{id}", id)
                .retrieve()
                .toEntity(String.class);

        return response;
    }

    public ResponseEntity findByCategoryAndName(String category, String name) {

        ResponseEntity<String> response = this.getByNameAndCategoryClient
                .get()
                .uri("?category={category}&name={name}", category, name)
                .retrieve()
                .toEntity(String.class);

        return response;
    }


    public ResponseEntity findByCategory(String category) {
        ResponseEntity<String> response = this.getByNameAndCategoryClient
                .get()
                .uri("?category={category}", category)
                .retrieve()
                .toEntity(String.class);

        return response;
    }
}
