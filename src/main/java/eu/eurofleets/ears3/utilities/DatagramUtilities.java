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
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Field;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.web.client.ResourceAccessException;

/**
 * A class to easily retrieve Objects of a certain acquisition type (nav, met, tss)
 * @author Thomas Vandenberghe
 */
public class DatagramUtilities<A extends Acquisition> {

    public static final int CONNECT_TIMEOUT = 15; //15 seconds

    public static Logger log = Logger.getLogger(DatagramUtilities.class.getSimpleName());

    private final Class<A> cls;
    private final URL baseUrl;

    private static Map<Class, String> abbrevs = new HashMap<>();

    static {
        abbrevs.put(Navigation.class, "nav");
        abbrevs.put(Thermosal.class, "tss");
        abbrevs.put(Weather.class, "met");
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
            //log.log(Level.INFO, "Read {0}", url);
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
                        //  log.log(Level.INFO, "   Read " + line + " from " + endpoint);
                        line = line.replace(",,", ", ,");
                        line = line.replaceAll(",$", ", ");
                        String[] vals = line.split(",");
                        A acquisitionValue = cls.newInstance();
                        if (vals.length >= 3) {
                            String dt = "20" + vals[1].substring(0, 2) + "-" + vals[1].substring(2, 4) + "-"
                                    + vals[1].substring(4, 6);
                            String tm = vals[2].substring(0, 2) + ":" + vals[2].substring(2, 4) + ":"
                                    + vals[2].substring(4, 6) + "Z";
                            acquisitionValue.setInstrumentTime(Instant.parse(dt + "T" + tm).atOffset(ZoneOffset.UTC));
                            acquisitionValue.setTimeStamp(Instant.parse(dt + "T" + tm).atOffset(ZoneOffset.UTC));
                            for (Field field : cls.getDeclaredFields()) {
                                if (field.isAnnotationPresent(DatagramOrder.class)) {
                                    DatagramOrder annotation = field.getAnnotation(DatagramOrder.class);
                                    int index = annotation.value();
                                    String value = null;
                                    if (index < vals.length) {
                                        value = vals[index];
                                    }
                                    if (value != null && !" ".equals(value) && value.contains(".")) {
                                        field.setAccessible(true);
                                        field.set(acquisitionValue, Double.valueOf(value));
                                    }
                                }
                            }
                            result.add(acquisitionValue);
                        }
                    } else {
                        log.log(Level.INFO, "Line is not an EARS datagram (using " + endpoint + ").");
                    }
                }
                br.close();

            } catch (InstantiationException | IllegalArgumentException | IllegalAccessException e) {
                Logger.getLogger(DatagramUtilities.class.getName()).log(Level.SEVERE,
                        "Could't set property of " + cls.getName() + " Acquisition entity", e);
            }
        }
        return result;
    }

    public static URLConnection tryConnect(URL url) throws IOException {

        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setConnectTimeout(CONNECT_TIMEOUT * 1000);
        connection.setReadTimeout(240 * 1000);
        connection.setRequestMethod("GET");
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
                lon = line.split(",", -1)[3];
            } catch (ArrayIndexOutOfBoundsException arrayE) {
                //it happens from time to time that a lat/lons are measured not as pairs but at 2 different timestamps. They are not combined to form 1 then. This is not logged to not flood the messages.

                log.log(Level.SEVERE, "ArrayIndexOutOfBoundsException for lon (index 3) of line " + line);
            }
            try {
                lat = line.split(",", -1)[4]; //-1 to ensure that ,,, is split as well
            } catch (ArrayIndexOutOfBoundsException arrayE) {
                log.log(Level.SEVERE, "ArrayIndexOutOfBoundsException for lon (index 4) of line " + line);
            }
            if (lat != null && !lat.isEmpty() && lon != null && !lon.isEmpty()) {
                newCoordinate = new Coordinate(Double.valueOf(lon), Double.valueOf(lat));
                return newCoordinate;
            }
        }
        return null;
    }
}
