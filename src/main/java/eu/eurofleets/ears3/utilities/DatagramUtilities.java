/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.eurofleets.ears3.utilities;

import eu.eurofleets.ears3.domain.Acquisition;
import eu.eurofleets.ears3.domain.Navigation;
import eu.eurofleets.ears3.domain.Thermosal;
import eu.eurofleets.ears3.domain.Weather;
import eu.eurofleets.ears3.utilities.DatagramOrder;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.net.ConnectException;
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

/**
 *
 * @author thomas
 */
public class DatagramUtilities<A extends Acquisition> {

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
        Logger.getLogger(DatagramUtilities.class.getName()).log(Level.INFO, "Initialized DatagramUtilities for class " + cls + " using base url " + baseUrl);
        this.baseUrl = new URL(baseUrl.replaceAll("\\/$", ""));
    }

    public URL getBaseUrl() {
        return this.baseUrl;
    }

    private URL getEndpointUrl(String endpoint) {
        try {
            if (this.baseUrl == null) {
                return new URL("http://" + endpoint);
            } else {
                return new URL(baseUrl, endpoint);
            }
        } catch (MalformedURLException ex) {
            Logger.getLogger(DatagramUtilities.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
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
            URL url = new URL(baseUrl, "/ears3Nav/" + abbrevs.get(this.cls) + "/getBetween/datagram?startDate=" + startString + "&endDate=" + endString);
            return analyzeDatagram(url);
        } catch (MalformedURLException ex) {
            Logger.getLogger(DatagramUtilities.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    private List<A> analyzeDatagram(URL endpoint) throws IOException, ConnectException {
        URLConnection connection = endpoint.openConnection();
        InputStreamReader ipsr = new InputStreamReader(connection.getInputStream());
        BufferedReader br = new BufferedReader(ipsr);
        String line;
        List<A> result = new ArrayList<>();
        try {
            while ((line = br.readLine()) != null) {
                if (line.startsWith("$")) {
                    //  log.log(Level.INFO, "   Read " + line + " from " + endpoint);
                    line = line.replace(",,", ", ,");
                    line = line.replaceAll(",$", ", ");
                    String[] vals = line.split(",");
                    A acquisitionValue = cls.newInstance();
                    if (vals.length >= 3) {
                        String dt = "20" + vals[1].substring(0, 2) + "-" + vals[1].substring(2, 4) + "-" + vals[1].substring(4, 6);
                        String tm = vals[2].substring(0, 2) + ":" + vals[2].substring(2, 4) + ":" + vals[2].substring(4, 6) + "Z";
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
            Logger.getLogger(DatagramUtilities.class.getName()).log(Level.SEVERE, "Could't set property of " + cls.getName() + " Acquisition entity", e);
        }
        return result;
    }
}
