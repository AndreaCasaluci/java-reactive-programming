package com.andrea.reactive.entity;

import com.andrea.reactive.constants.SatelliteConstants;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.ZonedDateTime;
import java.util.UUID;

@Getter
@Setter
@ToString
@NoArgsConstructor
@Builder
@AllArgsConstructor
@Table(name = SatelliteConstants.TABLE_NAME)
public class Satellite {

    @Id
    private Integer id;

    private UUID guid;

    private String name;

    private ZonedDateTime date;

    private String line1;

    private String line2;

}
