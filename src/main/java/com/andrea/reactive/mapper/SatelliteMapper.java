package com.andrea.reactive.mapper;

import com.andrea.reactive.dto.SatelliteDto;
import com.andrea.reactive.dto.request.CreateSatelliteRequest;
import com.andrea.reactive.entity.Satellite;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface SatelliteMapper {

    SatelliteMapper INSTANCE = Mappers.getMapper(SatelliteMapper.class);

    @Mapping(target = "guid", source = "guid")
    SatelliteDto satelliteToSatelliteDto(Satellite satellite);

    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "guid", ignore = true)
    @Mapping(target = "extId", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    SatelliteDto createSatelliteRequestToSatelliteDto(CreateSatelliteRequest request);

    @Mapping(target = "id", ignore = true)
    Satellite satelliteDtoToSatellite(SatelliteDto satelliteDto);
}
