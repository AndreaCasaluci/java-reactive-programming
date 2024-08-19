package com.andrea.reactive.controller;

import com.andrea.reactive.dto.request.CreateSatelliteRequest;
import com.andrea.reactive.dto.request.UpdateSatelliteRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;

import java.time.OffsetDateTime;
import java.util.UUID;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class SatelliteControllerTest {

    @Container
    public static PostgreSQLContainer<?> postgresContainer = new PostgreSQLContainer<>("postgres:15")
            .withDatabaseName("satellites_test")
            .withUsername("poc")
            .withPassword("password");

    @Autowired
    private DatabaseClient databaseClient;

    @Autowired
    private R2dbcEntityTemplate r2dbcEntityTemplate;

    @LocalServerPort
    private int port;

    private WebTestClient webTestClient;

    @BeforeEach
    void setUp() {

        webTestClient = WebTestClient.bindToServer().baseUrl("http://localhost:"+port).build();

        databaseClient.sql("DROP TABLE IF EXISTS satellite").then().block();

        String createSchema =
                "CREATE TABLE satellite (" +
                "id SERIAL PRIMARY KEY, " +
                "guid UUID NOT NULL DEFAULT gen_random_uuid(), " +
                "ext_id INT UNIQUE, " +
                "name VARCHAR(255), " +
                "date TIMESTAMP WITH TIME ZONE, " +
                "line1 VARCHAR(255), " +
                "line2 VARCHAR(255), " +
                "created_at TIMESTAMP WITH TIME ZONE DEFAULT now(), " +
                "updated_at TIMESTAMP WITH TIME ZONE DEFAULT now())";


        String insertData = "INSERT INTO satellite (guid, name, date, line1, line2) " +
                "VALUES ('c4e63c53-c0cf-4b61-b6f8-8f0b739e1a8d', 'Satellite 1', now(), 'Line 1A', 'Line 2A'), " +
                "('a9b52c4a-3b42-4d6e-9c43-f742e9a7f9b1', 'Satellite 2', now(), 'Line 1B', 'Line 2B')";

        databaseClient.sql(createSchema).then().block();
        databaseClient.sql(insertData).then().block();
    }

    @Test
    void testGetSatelliteById() {
        webTestClient.get().uri("/v1/satellite/{guid}", "c4e63c53-c0cf-4b61-b6f8-8f0b739e1a8d")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.name").isEqualTo("Satellite 1");
    }

    @Test
    void testGetList() {
        webTestClient.get().uri("/v1/satellite/list?page=0&size=10&name=Satellite")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.totalElements").isEqualTo(2)
                .jsonPath("$.content[0].name").isEqualTo("Satellite 1")
                .jsonPath("$.content[1].name").isEqualTo("Satellite 2");
    }

    @Test
    void testCreateSatellite() {
        CreateSatelliteRequest request = new CreateSatelliteRequest();
        request.setName("New Satellite");
        request.setLine1("Line 1C");
        request.setLine2("Line 2C");
        request.setDate(OffsetDateTime.now());

        webTestClient.post().uri("/v1/satellite")
                .bodyValue(request)
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.name").isEqualTo("New Satellite");
    }

    @Test
    void testUpdateSatellite() {
        UUID guid = UUID.fromString("c4e63c53-c0cf-4b61-b6f8-8f0b739e1a8d");

        UpdateSatelliteRequest request = new UpdateSatelliteRequest();
        request.setName("Updated Satellite");
        request.setDate(OffsetDateTime.now());
        request.setLine1("Line 1D");
        request.setLine2("Line 2D");

        webTestClient.put().uri("/v1/satellite/{guid}", guid)
                .bodyValue(request)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.name").isEqualTo("Updated Satellite");
    }

    @Test
    void testDeleteSatellite() {
        UUID guid = UUID.fromString("c4e63c53-c0cf-4b61-b6f8-8f0b739e1a8d");

        webTestClient.delete().uri("/v1/satellite/{guid}", guid)
                .exchange()
                .expectStatus().isNoContent();

        webTestClient.get().uri("/v1/satellite/{guid}", guid)
                .exchange()
                .expectStatus().isNotFound();
    }
}

