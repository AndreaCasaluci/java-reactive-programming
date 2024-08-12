package com.andrea.reactive.mapper;

import com.andrea.reactive.dto.SatelliteDto;
import com.andrea.reactive.entity.Satellite;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface SatelliteMapper {

    SatelliteMapper INSTANCE = Mappers.getMapper(SatelliteMapper.class);

    SatelliteDto satelliteToSatelliteDto(Satellite satellite);

}
