package com.andrea.reactive.mapper.config;

import com.andrea.reactive.mapper.SatelliteMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MapperConfig {

    @Bean
    public SatelliteMapper satelliteMapper() { return SatelliteMapper.INSTANCE; }

}
