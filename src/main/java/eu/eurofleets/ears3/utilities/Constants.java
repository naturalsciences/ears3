package eu.eurofleets.ears3.utilities;

import org.springframework.http.MediaType;
import java.nio.charset.StandardCharsets;

public final class Constants {

    public static final MediaType APPLICATION_XML_UTF8 =
            new MediaType("application", "xml", StandardCharsets.UTF_8);

    public static final MediaType APPLICATION_JSON_UTF8 =
            new MediaType("application", "json", StandardCharsets.UTF_8);
}
