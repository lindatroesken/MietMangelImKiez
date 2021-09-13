package de.lindatroesken.backend.controller;

import de.lindatroesken.backend.api.Credentials;
import de.lindatroesken.backend.api.Mangel;
import de.lindatroesken.backend.config.JwtConfig;
import de.lindatroesken.backend.model.MangelEntity;
import de.lindatroesken.backend.model.UserEntity;
import de.lindatroesken.backend.repo.MangelRepository;
import de.lindatroesken.backend.repo.UserRepository;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.hibernate.type.ZonedDateTimeType;
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

import java.time.Duration;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.*;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;


@SpringBootTest(
        properties = "spring.profiles.active:h2",
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
class MangelControllerTest {

    @LocalServerPort
    private int port;

    private String getUrl(){
        return "http://localhost:"+port+"/mangel";
    }

    private final ZonedDateTime DATE = ZonedDateTime.now();
    private final String USERNAME = "testuser";

    @Autowired
    TestRestTemplate restTemplate;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MangelRepository mangelRepository;

    @Autowired
    private JwtConfig jwtConfig;

    @BeforeEach
    public void initializeDB(){
        UserEntity user1 = UserEntity.builder()
                .username("testuser")
                .password("$2a$10$3k.oydYW4Tvi2kpz7twi2.J.bXQWudC4.jEeYz7eScdedmNrg7chG")
                .role("user")
                .build();
        userRepository.save(user1);


        MangelEntity mangel1 = MangelEntity.builder()
                .description("Aufzug geht nicht")
                .dateNoticed(DATE)
                .userEntity(user1)
                .build();

        mangelRepository.save(mangel1);

    }

    @AfterEach
    public void clearDB() {
        userRepository.deleteAll();
    }

    @Test
    @DisplayName("GET all mangel with valid credentials should return own mangel list")
    void testFindAllByUser() {
        //GIVEN
        String username = "testuser";
        String url = getUrl() + "/" + username;
        HttpEntity<Credentials> httpEntity = new HttpEntity<>(authorizedHeader(username, "user"));

        //WHEN
        ParameterizedTypeReference<List<Mangel>> responseType = new ParameterizedTypeReference<>() {};
        ResponseEntity<List<Mangel>> response= restTemplate.exchange(url, HttpMethod.GET, httpEntity, responseType);

        //THEN
        assertThat(response.getStatusCode(), is(HttpStatus.OK));
        assertThat(response.getBody().size(), is(1));
        assertThat(response.getBody().get(0).getDescription(), is("Aufzug geht nicht"));
        assertThat(response.getBody().get(0).getDateNoticed(), is(DATE.toString()));

    }

    @Test
    void testCreateNewMangel() {
        //GIVEN
        String username = "testuser";
        String url = getUrl() + "/" + username;
        Mangel newMangel = Mangel.builder()
                .description("Heizung")
                .dateNoticed(DATE.toString())
                .build();
        HttpEntity<Mangel> httpEntity = new HttpEntity(newMangel, authorizedHeader(username, "user"));

        //WHEN
        ResponseEntity<Mangel> response = restTemplate.exchange(url, HttpMethod.POST, httpEntity, Mangel.class);

        //THEN
        assertThat(response.getStatusCode(), is(HttpStatus.OK));
        assertThat(response.getBody().getDescription(), is("Heizung"));

    }

    private HttpHeaders authorizedHeader(String username, String role){
        Map<String,Object> claims = new HashMap<>();
        claims.put("role", role);
        Instant now = Instant.now();
        Date iat = Date.from(now);
        Date exp = Date.from(now.plus(Duration.ofMinutes(jwtConfig.getExpiresAfterMinutes())));
        String token = Jwts.builder()
                .setClaims(claims)
                .setSubject(username)
                .setIssuedAt(iat)
                .setExpiration(exp)
                .signWith(SignatureAlgorithm.HS256, jwtConfig.getSecret())
                .compact();

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);

        return headers;
    }
}