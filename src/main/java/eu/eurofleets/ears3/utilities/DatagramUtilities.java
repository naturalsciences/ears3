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
import eu.eurofleets.ears3.utilities.DatagramOrder;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
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
        this.baseUrl = new URL(baseUrl);
    }

    public A last() throws IOException {
        try {
            List<A> r = analyzeDatagram(new URL(baseUrl, "ears2Nav/" + abbrevs.get(this.cls) + "/getLast/datagram"));
            return r.isEmpty() ? null : r.get(0);
        } catch (MalformedURLException ex) {
            Logger.getLogger(DatagramUtilities.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public A nearest(OffsetDateTime at) throws IOException {
        String atString = at.withOffsetSameInstant(ZoneOffset.UTC).format(DateTimeFormatter.ISO_DATE_TIME);
        try {
            List<A> r = analyzeDatagram(new URL(baseUrl, "ears2Nav/" + abbrevs.get(this.cls) + "/getNearest/datagram?date=" + atString));
            return r.isEmpty() ? null : r.get(0);
        } catch (MalformedURLException ex) {
            Logger.getLogger(DatagramUtilities.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public List<A> between(OffsetDateTime start, OffsetDateTime stop) throws IOException {
        String startString = start.withOffsetSameInstant(ZoneOffset.UTC).format(DateTimeFormatter.ISO_DATE_TIME);
        String endString = stop.withOffsetSameInstant(ZoneOffset.UTC).format(DateTimeFormatter.ISO_DATE_TIME);
        try {
            return analyzeDatagram(new URL(baseUrl, "ears2Nav/" + abbrevs.get(this.cls) + "/getBetween/datagram?startDate=" + startString + "&endDate=" + endString));
        } catch (MalformedURLException ex) {
            Logger.getLogger(DatagramUtilities.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    private List<A> analyzeDatagram(URL endpoint) throws IOException {

        // URL endpoint = new URL(env.getProperty("ears.navigation.server") + "nav/getNearest/datagram?date=" + endDate);
        URLConnection connection = endpoint.openConnection();

        InputStreamReader ipsr = new InputStreamReader(connection.getInputStream());
        BufferedReader br = new BufferedReader(ipsr);
        String line;

        List<A> result = new ArrayList<>();
        try {
            while ((line = br.readLine()) != null) {
                line = line.replace(",,", ", ,");
                line = line.replaceAll(",$", ", ");
                String[] vals = line.split(",");
                A a = cls.newInstance();

                try {
                    String dt = "20" + vals[1].substring(0, 2) + "-" + vals[1].substring(2, 4) + "-" + vals[1].substring(4, 6);
                    String tm = vals[2].substring(0, 2) + ":" + vals[2].substring(2, 4) + ":" + vals[2].substring(4, 6) + "Z";
                    a.setInstrumentTime(Instant.parse(dt + "T" + tm));
                } catch (Exception e) {
                    int b = 5;
                }

                for (Field field : cls.getDeclaredFields()) {
                    if (field.isAnnotationPresent(DatagramOrder.class)) {
                        DatagramOrder annotation = field.getAnnotation(DatagramOrder.class);
                        int index = annotation.value();
                        String value = vals[index];
                        if (!" ".equals(value) && value.contains(".")) {
                            field.setAccessible(true);
                            field.set(a, Double.valueOf(value));
                        }
                    }
                }
                result.add(a);
            }
            br.close();
        } catch (InstantiationException | IllegalArgumentException | IllegalAccessException e) {
            Logger.getLogger(DatagramUtilities.class.getName()).log(Level.SEVERE, null, e);
        }
        return result;
    }

}
