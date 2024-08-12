package com.andrea.reactive.constants;

import lombok.experimental.UtilityClass;

@UtilityClass
public class SatelliteConstants {

    public static final String TABLE_NAME = "satellite";

    public static final String SATELLITE_BASE_PATH = "/v1/satellite";

    public static final String FETCH_SIZE_PARAM_NAME = "size";
    public static final String FETCH_DEFAULT_SIZE_VALUE = "10";
    public static final int FETCH_MIN_SIZE_VALUE = 1;
    public static final int FETCH_MAX_SIZE_VALUE = 1;
}
