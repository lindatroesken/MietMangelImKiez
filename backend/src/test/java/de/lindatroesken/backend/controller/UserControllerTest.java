package de.lindatroesken.backend.controller;


import de.lindatroesken.backend.api.User;
import de.lindatroesken.backend.model.UserEntity;
import de.lindatroesken.backend.repo.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import java.util.LinkedList;


import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@SpringBootTest(
        properties = "spring.profiles.active:h2",
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
public class UserControllerTest {

    @LocalServerPort
    private int port;

    private String getUrl(){
        return "http://localhost:"+port+"/user";
    }

    @Autowired
    TestRestTemplate restTemplate;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    public void initializeDB(){

        UserEntity user1 = UserEntity.builder()
                .username("linda")
                .password("$2a$10$3k.oydYW4Tvi2kpz7twi2.J.bXQWudC4.jEeYz7eScdedmNrg7chG")
                .role("user")
                .build();
        userRepository.save(user1);

        UserEntity user2 = UserEntity.builder()
                .username("admin")
                .password("$2a$10$3k.oydYW4Tvi2kpz7twi2.J.bXQWudC4.jEeYz7eScdedmNrg7chG")
                .role("admin")
                .build();
        userRepository.save(user2);

    }

    @AfterEach
    public void clearDB() {
        userRepository.deleteAll();
    }

    @Test
    @DisplayName("GET should return a list of all users in database (2 users)")
    public void getListOfUsersShouldReturnTwoUsersForAdmin(){
        //GIVEN

        // credentials to be done in next step
//        Credentials credentials = Credentials.builder()
//                .username("admin")
//                .password("1234")
//                .build();

        //WHEN
        ParameterizedTypeReference<LinkedList<User>> responseType = new ParameterizedTypeReference<>() {};
        ResponseEntity<LinkedList<User>> response = restTemplate.exchange(getUrl(), HttpMethod.GET, null, responseType);


        //THEN
        assertThat(response.getStatusCode(), is(HttpStatus.OK));
        assertThat(response.getBody(),is(notNullValue()));
        assertThat(response.getBody().size(), is(2));
    }

    @Test
    @DisplayName("GET /{username} should return a user from database")
    public void testGetUserByUserNameReturnsUser(){
        //GIVEN
        String url = getUrl() + "/linda";
        // WHEN
        ResponseEntity<User> response = restTemplate.exchange(url,HttpMethod.GET,null, User.class);
        // THEN
        assertThat(response.getStatusCode(), is(HttpStatus.OK));
        assertThat(response.getBody(),is(notNullValue()));
        assertThat(response.getBody().getUsername(),is("linda"));
    }

    private HttpHeaders getHttpHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }


}