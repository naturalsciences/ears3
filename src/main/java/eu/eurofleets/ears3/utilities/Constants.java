package eu.eurofleets.ears3.utilities;

import org.springframework.http.MediaType;

import java.nio.charset.StandardCharsets;

public final class Constants {

    public static final String APPLICATION_XML_UTF8_VALUE = "application/xml;charset=UTF-8";

    public static final MediaType APPLICATION_XML_UTF8 =
            MediaType.parseMediaType(APPLICATION_XML_UTF8_VALUE);

    public static final String APPLICATION_JSON_UTF8_VALUE = "application/json;charset=UTF-8";

    public static final MediaType APPLICATION_JSON_UTF8 =
            MediaType.parseMediaType(APPLICATION_JSON_UTF8_VALUE);
}
