package com.andrea.reactive.controller;

import com.andrea.reactive.dto.TleDto;
import com.andrea.reactive.dto.request.CreateSatelliteRequest;
import com.andrea.reactive.dto.request.UpdateSatelliteRequest;
import com.andrea.reactive.dto.response.externalApi.ExternalSatelliteApiResponse;
import com.andrea.reactive.dto.response.externalApi.FetchSatelliteResponse;
import com.andrea.reactive.service.HttpService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.client.WebClient;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import reactor.core.publisher.Mono;

import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static reactor.core.publisher.Mono.when;

@ExtendWith({SpringExtension.class, MockitoExtension.class})
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

    @MockBean
    private HttpService httpService;

    @MockBean
    private WebClient webClient;


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

    @AfterEach
    void tearDown() {
        databaseClient.sql("DROP TABLE IF EXISTS satellite").then().block();
    }

    @Test
    void should_respond_ok_on_getSatelliteById() {
        webTestClient.get().uri("/v1/satellite/{guid}", "c4e63c53-c0cf-4b61-b6f8-8f0b739e1a8d")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.name").isEqualTo("Satellite 1");
    }

    @Test
    void should_respond_ok_on_getList() {
        webTestClient.get().uri("/v1/satellite/list?page=0&size=10&name=Satellite")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.totalElements").isEqualTo(2)
                .jsonPath("$.content[0].name").isEqualTo("Satellite 1")
                .jsonPath("$.content[1].name").isEqualTo("Satellite 2");
    }

    @Test
    void should_respond_created_on_createSatellite() {
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
    void should_respond_ok_on_updateSatellite() {
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
    void should_respond_no_content_and_not_found_on_deleteSatellite() {
        UUID guid = UUID.fromString("c4e63c53-c0cf-4b61-b6f8-8f0b739e1a8d");

        webTestClient.delete().uri("/v1/satellite/{guid}", guid)
                .exchange()
                .expectStatus().isNoContent();

        webTestClient.get().uri("/v1/satellite/{guid}", guid)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void should_respond_created_on_fetchSatellites() {

        ExternalSatelliteApiResponse mockExternalApiResponse = new ExternalSatelliteApiResponse();
        mockExternalApiResponse.setTotalItems(2);
        mockExternalApiResponse.setMember(Arrays.asList(
                new TleDto(25544, "ISS (ZARYA)", OffsetDateTime.parse("2024-08-18T18:27:44+00:00"),
                        "1 25544U 98067A   24231.76926683  .00091167  00000+0  15647-2 0  9997",
                        "2 25544  51.6408   5.2900 0004971 223.3800 278.3149 15.50389962468258"),
                new TleDto(40075, "AISSAT 2", OffsetDateTime.parse("2023-12-28T11:59:02+00:00"),
                        "1 40075U 14037G   23362.49933056  .00003465  00000+0  40707-3 0  9994",
                        "2 40075  98.3401 268.4723 0004780 335.0232  25.0749 14.85601820512563")
        ));

        Mockito.when(httpService.getMany(any(), eq(ExternalSatelliteApiResponse.class), any())).thenReturn(Mono.just(mockExternalApiResponse));

        webTestClient.post().uri("/v1/satellite/fetch?size=2&chunk-size=2")
                .exchange()
                .expectStatus().isCreated()
                .expectBody(FetchSatelliteResponse.class)
                .consumeWith(response -> {
                    FetchSatelliteResponse responseBody = response.getResponseBody();
                    assert responseBody.getNewCount() == 2;
                    assert responseBody.getUpdatedCount() == 0;
                });
    }

}

