/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.eurofleets.ears3.utilities;

import eu.eurofleets.ears3.domain.Acquisition;
import eu.eurofleets.ears3.domain.Coordinate;
import eu.eurofleets.ears3.domain.Navigation;
import eu.eurofleets.ears3.domain.Thermosal;
import eu.eurofleets.ears3.domain.Weather;
import org.springframework.http.MediaType;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A class to easily retrieve Objects of a certain acquisition type (nav, met, tss)
 *
 * @author Thomas Vandenberghe
 */
public class DatagramUtilities<A extends Acquisition> {

    public static final int CONNECT_TIMEOUT = 15; //15 seconds

    // Matches EarsObject.toDatagramTime(Instant) on the ears3Nav (producer) side: yyMMdd'T'HHmmss
    private static final DateTimeFormatter DATAGRAM_FIELD_TIMESTAMP_FORMAT = DateTimeFormatter.ofPattern("yyMMdd'T'HHmmss");

    public static Logger log = Logger.getLogger(DatagramUtilities.class.getSimpleName());

    private final Class<A> cls;
    private final URL baseUrl;

    private static Map<Class, String> abbrevs = new HashMap<>();

    // Derived from Navigation's own @DatagramOrder annotations rather than hardcoded, so a future reorder
    // of Navigation's fields doesn't silently break coordinate parsing here.
    private static final int LON_INDEX;
    private static final int LAT_INDEX;

    static {
        abbrevs.put(Navigation.class, "nav");
        abbrevs.put(Thermosal.class, "tss");
        abbrevs.put(Weather.class, "met");

        try {
            LON_INDEX = Navigation.class.getDeclaredField("lon").getAnnotation(DatagramOrder.class).value();
            LAT_INDEX = Navigation.class.getDeclaredField("lat").getAnnotation(DatagramOrder.class).value();
        } catch (NoSuchFieldException e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    public DatagramUtilities(Class<A> cls, String baseUrl) throws MalformedURLException {
        this.cls = cls;
        Logger.getLogger(DatagramUtilities.class.getName()).log(Level.INFO,
                "Initialized DatagramUtilities for class " + cls + " using base url " + baseUrl);
        this.baseUrl = new URL(baseUrl.replaceAll("\\/$", ""));
    }

    public URL getBaseUrl() {
        return this.baseUrl;
    }

    public A findLast() throws IOException {
        try {
            URL url = new URL(baseUrl, "/ears3Nav/" + abbrevs.get(this.cls) + "/getLast/datagram");
            List<A> r = analyzeDatagram(url);
            return r.isEmpty() ? null : r.get(0);
        } catch (MalformedURLException ex) {
            Logger.getLogger(DatagramUtilities.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public A findNearest(OffsetDateTime at) throws IOException {
        if (at == null) {
            throw new IllegalArgumentException("Provided nearest time is null!");
        }
        String atString = at.withOffsetSameInstant(ZoneOffset.UTC).format(DateTimeFormatter.ISO_DATE_TIME);
        try {
            URL url = new URL(baseUrl, "/ears3Nav/" + abbrevs.get(this.cls) + "/getNearest/datagram?date=" + atString);
            List<A> r = analyzeDatagram(url);
            return r.isEmpty() ? null : r.get(0);
        } catch (MalformedURLException ex) {
            Logger.getLogger(DatagramUtilities.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public List<A> findBetween(OffsetDateTime start, OffsetDateTime stop) throws IOException {
        if (start == null) {
            throw new IllegalArgumentException("Provided start time is null!");
        }
        if (stop == null) {
            throw new IllegalArgumentException("Provided end time is null!");
        }
        String startString = start.withOffsetSameInstant(ZoneOffset.UTC).format(DateTimeFormatter.ISO_DATE_TIME);
        String endString = stop.withOffsetSameInstant(ZoneOffset.UTC).format(DateTimeFormatter.ISO_DATE_TIME);
        try {
            URL url = new URL(baseUrl, "/ears3Nav/" + abbrevs.get(this.cls) + "/getBetween/datagram?startDate="
                    + startString + "&endDate=" + endString);
            return analyzeDatagram(url);
        } catch (MalformedURLException ex) {
            Logger.getLogger(DatagramUtilities.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    private List<A> analyzeDatagram(URL endpoint) throws IOException, ConnectException {
        List<A> result = new ArrayList<>();
        URLConnection connection = tryConnect(endpoint);
        try (BufferedReader br = new BufferedReader(new InputStreamReader(
                connection.getInputStream()))) {
            String line;
            try {
                while ((line = br.readLine()) != null) {
                    if (line.startsWith("$")) {
                        log.log(Level.INFO, "   Read " + line + " from " + endpoint);
                        line = line.replace(",,", ", ,");
                        line = line.replaceAll(",$", ", ");
                        String[] vals = line.split(",");
                        A acquisitionValue = cls.getDeclaredConstructor().newInstance();
                        if (vals.length >= 1) {
                            // Date/time is no longer a separate pair of fields in the datagram (vals[1]/vals[2]);
                            // each field now carries its own timestamp via its @DatagramOrder(Instant) counterpart.
                            for (Field field : cls.getDeclaredFields()) {
                                if (field.isAnnotationPresent(DatagramOrder.class)) {
                                    DatagramOrder annotation = field.getAnnotation(DatagramOrder.class);
                                    int index = annotation.value();
                                    if (index >= vals.length) {
                                        continue;
                                    }
                                    String value = vals[index];
                                    if (value == null) {
                                        continue;
                                    }
                                    String trimmed = value.trim();
                                    if (trimmed.isEmpty()) {
                                        continue;
                                    }
                                    field.setAccessible(true);
                                    Class<?> fieldType = field.getType();
                                    try {
                                        if (fieldType == Double.class) {
                                            field.set(acquisitionValue, Double.valueOf(trimmed));
                                        } else if (fieldType == Instant.class) {
                                            LocalDateTime parsed = LocalDateTime.parse(trimmed, DATAGRAM_FIELD_TIMESTAMP_FORMAT);
                                            field.set(acquisitionValue, parsed.toInstant(ZoneOffset.UTC));
                                        } else if (fieldType == String.class) {
                                            field.set(acquisitionValue, trimmed);
                                        } else {
                                            log.log(Level.WARNING,
                                                    "Unsupported @DatagramOrder field type {0} for field {1} on {2}",
                                                    new Object[]{fieldType, field.getName(), cls.getName()});
                                        }
                                    } catch (NumberFormatException | DateTimeParseException parseEx) {
                                        log.log(Level.WARNING,
                                                "Could not parse value '" + trimmed + "' for field " + field.getName()
                                                        + " (" + fieldType.getSimpleName() + ") on " + cls.getName(),
                                                parseEx);
                                    }
                                }
                            }
                            result.add(acquisitionValue);
                        }
                    } else {
                        log.log(Level.INFO, "Line (" + line + ") is not an EARS datagram (using " + endpoint + ").");
                    }
                }
                br.close();

            } catch (InstantiationException | IllegalArgumentException | IllegalAccessException e) {
                Logger.getLogger(DatagramUtilities.class.getName()).log(Level.SEVERE,
                        "Could't set property of " + cls.getName() + " Acquisition entity", e);
            } catch (InvocationTargetException | NoSuchMethodException e) {
                throw new RuntimeException(e);
            }
        }
        return result;
    }

    public static URLConnection tryConnect(URL url) throws IOException {

        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setConnectTimeout(CONNECT_TIMEOUT * 1000);
        connection.setReadTimeout(240 * 1000);
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Accept", MediaType.TEXT_PLAIN_VALUE);
        connection.connect();
        int code = connection.getResponseCode();
        if (code == HttpURLConnection.HTTP_OK) {// status 200
            return connection;
        } else
            throw new IOException(
                    String.format("URL %s returned code %d%n", url.toString(), code));

    }

    public List<Coordinate> getCoordinates(OffsetDateTime start, OffsetDateTime stop) throws IOException {
        if (start == null) {
            throw new IllegalArgumentException("Provided start time is null!");
        }
        if (stop == null) {
            throw new IllegalArgumentException("Provided end time is null!");
        }
        String startString = start.withOffsetSameInstant(ZoneOffset.UTC).format(DateTimeFormatter.ISO_DATE_TIME);
        String endString = stop.withOffsetSameInstant(ZoneOffset.UTC).format(DateTimeFormatter.ISO_DATE_TIME);

        //example: https://ears.bmdc.be/ears3Nav/nav/getBetween/datagram?startDate=2014-07-07T08:00:00Z&endDate=2014-07-11T18:00:00Z
        URL url = new URL(String.format("%s/ears3Nav/%s/getBetween/datagram?startDate=%s&endDate=%s",
                baseUrl, abbrevs.get(this.cls), startString, endString));

        URLConnection connection = tryConnect(url);

        return readStreamForCoords(new InputStreamReader(connection.getInputStream()));
    }

    public List<Coordinate> readStreamForCoords(Reader reader) throws IOException {
        List<Coordinate> coords = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(reader)) {
            String line;
            int i = 0;
            while ((line = br.readLine()) != null) {
                Coordinate coord = getCoordinate(line);
                if (coords.size() < 2) { //always add the first and second one that is valid.
                    if (coord != null && coord.isValid()) {
                        coords.add(coord);
                    }
                } else if ((i + 1) % 2 == 0) {//if at an even numbered frame, evaluate n and n-1
                    int j = coords.size() - 1;
                    Coordinate coordn_1 = null;
                    Coordinate coordn_2 = null;

                    coordn_1 = coords.get(j - 1);

                    if (coords.size() > 2) {
                        coordn_2 = coords.get(j - 2);
                    }
                    if (coordn_1 != null) { //there is already a previous addition (at least n-1, possibly n-2)
                        Double heading1 = SpatialUtil.bearingByCoord(coordn_2, coordn_1);
                        Double heading2 = SpatialUtil.bearingByCoord(coordn_1, coord);

                        if (Math.abs(heading2 - heading1) > 0.5) {
                            if (coord.isValid() && !coord.testSpike(coord)) {
                                coords.add(coord);
                            } else {
                                System.out.printf("%s->%s IS INVALID OR A SPIKE%n", coords.get(i - 1), coords.get(i));
                            }
                        }
                    }
                }
                i++;
            }
        }
        return coords;
    }

    public Coordinate getCoordinate(String line) {
        Coordinate newCoordinate = null;
        if (line.startsWith("$")) { //EARS datagram
            String lon = null;
            String lat = null;
            try {
                lon = line.split(",", -1)[LON_INDEX];
            } catch (ArrayIndexOutOfBoundsException arrayE) {
                //it happens from time to time that a lat/lons are measured not as pairs but at 2 different timestamps. They are not combined to form 1 then. This is not logged to not flood the messages.

                log.log(Level.SEVERE, "ArrayIndexOutOfBoundsException for lon (index " + LON_INDEX + ") of line " + line);
            }
            try {
                lat = line.split(",", -1)[LAT_INDEX]; //-1 to ensure that ,,, is split as well
            } catch (ArrayIndexOutOfBoundsException arrayE) {
                log.log(Level.SEVERE, "ArrayIndexOutOfBoundsException for lat (index " + LAT_INDEX + ") of line " + line);
            }
            if (lat != null && !lat.isEmpty() && lon != null && !lon.isEmpty()) {
                newCoordinate = new Coordinate(Double.valueOf(lon), Double.valueOf(lat));
                return newCoordinate;
            }
        }
        return null;
    }
}