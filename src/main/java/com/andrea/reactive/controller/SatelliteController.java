package com.andrea.reactive.controller;

import com.andrea.reactive.constants.SatelliteConstants;
import com.andrea.reactive.dto.SatelliteDto;
import com.andrea.reactive.dto.request.CreateSatelliteRequest;
import com.andrea.reactive.dto.request.UpdateSatelliteRequest;
import com.andrea.reactive.dto.response.GenericPagedResponse;
import com.andrea.reactive.dto.response.externalApi.FetchSatelliteResponse;
import com.andrea.reactive.exception.ValidationException;
import com.andrea.reactive.service.SatelliteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import javax.swing.*;
import java.util.*;

@RestController
@RequestMapping(SatelliteConstants.SATELLITE_BASE_PATH)
@Slf4j
@Tag(name = "Satellites", description = "Operations related to satellites")
public class SatelliteController {

    private final SatelliteService satelliteService;

    public SatelliteController(SatelliteService satelliteService) {
        this.satelliteService = satelliteService;
    }

    @PostMapping(SatelliteConstants.FETCH_ENDPOINT)
    @Operation(
            summary = "Fetch and update satellites from external source",
            description = "Fetches satellites from an external source and updates the local repository.",
            parameters = {
                    @Parameter(name = "size", description = "Number of satellites to fetch", required = true),
                    @Parameter(name = "chunkSize", description = "Size of chunks for processing", required = false)
            }
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Satellites fetched and updated successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = FetchSatelliteResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid chunk-size parameter",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "503", description = "Unable to reach external service",
                    content = @Content(mediaType = "application/json"))
    })

    public Mono<ResponseEntity<FetchSatelliteResponse>> fetchSatellitesFromExternalSource(
            @RequestParam(name=SatelliteConstants.FETCH_SIZE_PARAM_NAME, defaultValue = SatelliteConstants.FETCH_DEFAULT_SIZE_VALUE) int size,
            @RequestParam(name=SatelliteConstants.FETCH_CHUNK_SIZE_PARAM_NAME) Integer chunkSize
    ) {

        log.info("Start method - fetchSatellitesFromExternalSource - size: {} - chunkSize: {}", size, chunkSize);

        if(!Objects.isNull(chunkSize) && (chunkSize < SatelliteConstants.FETCH_MIN_CHUNK_SIZE_VALUE || chunkSize > SatelliteConstants.FETCH_MAX_CHUNK_SIZE_VALUE))
            return Mono.error(new ValidationException("Invalid chunk-size parameter. It must be between "+SatelliteConstants.FETCH_MIN_CHUNK_SIZE_VALUE +" and "+SatelliteConstants.FETCH_MAX_CHUNK_SIZE_VALUE +"."));

        return satelliteService.fetchAndUpdateSatellites(size, Optional.of(chunkSize))
                .doOnSuccess(response -> log.info("End method - fetchSatellitesFromExternalSource - size: {} - chunkSize: {}", size, chunkSize))
                .map(response -> ResponseEntity.status(HttpStatus.CREATED).body(response));
    }

    @GetMapping("/{guid}")
    @Operation(
            summary = "Get satellite by GUID",
            description = "Fetches the details of a satellite using its GUID.",
            parameters = {
                    @Parameter(name = "guid", description = "Unique identifier of the satellite", required = true, example = "123e4567-e89b-12d3-a456-426614174000")
            }
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Satellite details retrieved successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = SatelliteDto.class))),
            @ApiResponse(responseCode = "404", description = "Satellite not found",
                    content = @Content(mediaType = "application/json"))
    })
    public Mono<ResponseEntity<SatelliteDto>> getSatelliteById(@PathVariable UUID guid) {

        log.info("Start method - getSatelliteById - guid: {}", guid);

        return satelliteService.getSatelliteByGuid(guid)
                .doOnSuccess(response -> log.info("End method - getSatelliteById - guid: {}", guid))
                .map(response -> ResponseEntity.ok(response));
    }

    @PostMapping
    @Operation(
            summary = "Create a new satellite",
            description = "Creates a new satellite record in the system based on the provided details."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Satellite created successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = SatelliteDto.class)
                    )),
            @ApiResponse(responseCode = "400", description = "Invalid request data",
                    content = @Content(
                            mediaType = "application/json"
                    ))
    })
    public Mono<ResponseEntity<SatelliteDto>> createSatellite(@Valid @RequestBody CreateSatelliteRequest request) {

        log.info("Start method - createSatellite");

        return satelliteService.createSatellite(request)
                .doOnSuccess(response -> log.info("End method - createSatellite"))
                .map(response -> ResponseEntity.status(HttpStatus.CREATED).body(response));
    }

    @PutMapping("/{guid}")
    @Operation(
            summary = "Update an existing satellite",
            description = "Updates the details of an existing satellite identified by the GUID.",
            parameters = @Parameter(name = "guid", description = "Unique identifier of the satellite to be updated", required = true)
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Satellite updated successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = SatelliteDto.class)
                    )),
            @ApiResponse(responseCode = "400", description = "Invalid request data",
                    content = @Content(
                            mediaType = "application/json"
                    )),
            @ApiResponse(responseCode = "404", description = "Satellite not found",
                    content = @Content(
                            mediaType = "application/json"
                    ))
    })
    public Mono<ResponseEntity<SatelliteDto>> updateSatellite(
            @PathVariable UUID guid,
            @Valid @RequestBody UpdateSatelliteRequest request) {

        log.info("Start method - updateSatellite - guid: {}", guid);

        return satelliteService.updateSatellite(guid, request)
                .doOnSuccess(response -> log.info("End method - updateSatellite - guid: {}", guid))
                .map(response -> ResponseEntity.ok(response));
    }

    @DeleteMapping("/{guid}")
    @Operation(
            summary = "Delete a satellite by GUID",
            description = "Deletes a satellite identified by the provided GUID. If the satellite is found and successfully deleted, a 204 No Content response is returned.",
            parameters = @Parameter(name = "guid", description = "Unique identifier of the satellite to be deleted", required = true)
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Satellite deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Satellite not found",
                    content = @Content(
                            mediaType = "application/json"
                    ))
    })
    public Mono<ResponseEntity<Void>> deleteSatelliteByGuid(@PathVariable UUID guid) {

        log.info("Start method - deleteSatelliteByGuid - guid: {}", guid);

        return satelliteService.deleteSatellite(guid)
                .doOnSuccess(response -> log.info("End method - deleteSatelliteByGuid - guid: {}", guid))
                .then(Mono.just(ResponseEntity.status(HttpStatus.NO_CONTENT).build()));
    }

    @GetMapping(SatelliteConstants.GET_LIST_ENDPOINT)
    @Operation(
            summary = "Get a list of satellites",
            description = "Retrieves a paginated list of satellites based on the provided filters and sorting options.",
            parameters = {
                    @Parameter(name = "page", in = ParameterIn.QUERY, description = "Page number for pagination", required = true, example = "1"),
                    @Parameter(name = "size", in = ParameterIn.QUERY, description = "Number of items per page", required = true, example = "10"),
                    @Parameter(name = "name", in = ParameterIn.QUERY, description = "Filter by satellite name", required = false, example = "Hubble"),
                    @Parameter(name = "nameOrder", in = ParameterIn.QUERY, description = "Sort order for name", required = false, example = "ASC", schema = @Schema(type = "string", allowableValues = {"ASC", "DESC"})),
                    @Parameter(name = "dateOrder", in = ParameterIn.QUERY, description = "Sort order for date", required = false, example = "DESC", schema = @Schema(type = "string", allowableValues = {"ASC", "DESC"}))
            }
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved the list of satellites",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = GenericPagedResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid parameters supplied",
                    content = @Content(mediaType = "application/json"))
    })
    public Mono<ResponseEntity<GenericPagedResponse>> getList(
            @RequestParam("page") int page,
            @RequestParam("size") int size,
            @RequestParam(required = false, defaultValue = "") String name,
            @RequestParam(required = false) SortOrder nameOrder,
            @RequestParam(required = false) SortOrder dateOrder
    ) {

        log.info("Start method - getList - page: {} - size: {} - name: {} - nameOrder: {} - dateOrder: {}", page, size, name, nameOrder, dateOrder);

        return satelliteService.getSatellites(name, page, size, nameOrder, dateOrder)
                .doOnSuccess(response ->  log.info("Start method - getList - page: {} - size: {} - name: {} - nameOrder: {} - dateOrder: {}", page, size, name, nameOrder, dateOrder))
                .map(response -> ResponseEntity.ok(response));

    }

}
