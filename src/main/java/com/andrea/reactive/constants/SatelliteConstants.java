package com.andrea.reactive.constants;

import lombok.experimental.UtilityClass;

@UtilityClass
public class SatelliteConstants {

    public static final String TABLE_NAME = "satellite";

    public static final String SATELLITE_BASE_PATH = "/v1/satellite";

    public static final String FETCH_ENDPOINT = "/fetch";

    public static final String GET_LIST_ENDPOINT = "/list";

    public static final String FETCH_SIZE_PARAM_NAME = "size";
    public static final String FETCH_CHUNK_SIZE_PARAM_NAME = "chunk-size";
    public static final String FETCH_DEFAULT_SIZE_VALUE = "10";
    public static final int FETCH_MIN_CHUNK_SIZE_VALUE = 1;
    public static final int FETCH_MAX_CHUNK_SIZE_VALUE = 100;

    public static final String EXTERNAL_API_PAGE_PARAMETER_NAME = "page";
    public static final int EXTERNAL_API_DEFAULT_PAGE_VALUE = 1;
    public static final int EXTERNAL_API_MAX_PAGE_SIZE = 100;
    public static final String EXTERNAL_API_PAGE_SIZE_PARAMETER_NAME = "page-size";

    public static final String NAME_PROPERTY = "name";
    public static final String DATE_PROPERTY = "date";
}
