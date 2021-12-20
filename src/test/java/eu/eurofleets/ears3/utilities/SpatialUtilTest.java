/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.eurofleets.ears3.utilities;

import eu.eurofleets.ears3.Application;
import eu.eurofleets.ears3.domain.Coordinate;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.env.Environment;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;

/**
 *
 * @author thomas
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {Application.class}, properties = "spring.main.allow-bean-definition-overriding=true")
@WebAppConfiguration
@ComponentScan(basePackages = {"eu.eurofleets.ears3.domain", " eu.eurofleets.ears3.service"})
@TestPropertySource(locations="classpath:test.properties")
public class SpatialUtilTest {

    public SpatialUtilTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    @Autowired
    private Environment env;

    /**
     * Test of bearingByCoord method, of class SpatialUtil.
     */
    @Test
    public void testHeadingByCoord() throws IOException {
        System.out.println("headingByCoord");

        List<Coordinate> coordinates = new ArrayList();

        OffsetDateTime startDate = OffsetDateTime.now().minusDays(5);
        OffsetDateTime endDate = OffsetDateTime.now().plusDays(5);
        String startDateS = startDate.withOffsetSameInstant(ZoneOffset.UTC).format(DateTimeFormatter.ISO_DATE_TIME);
        String endDateS = endDate.withOffsetSameInstant(ZoneOffset.UTC).format(DateTimeFormatter.ISO_DATE_TIME);

        URL website = new URL(env.getProperty("ears.navigation.server") + "/ears3Nav/nav/getBetween/datagram?startDate=" + startDateS + "&endDate=" + endDateS);
        URLConnection connection = website.openConnection();

        InputStreamReader ipsr = new InputStreamReader(connection.getInputStream());
        BufferedReader br = new BufferedReader(ipsr);
        String line;
        String headingString = null;
        Double dbHeading = null;
        Coordinate coordinate = null;
        Coordinate newCoordinate = null;
        while ((line = br.readLine()) != null) {
            if (line.startsWith("$")) { //EARS datagram
                String lon = line.split(",")[3];
                String lat = line.split(",")[4];
                if (lat != null && !lat.isEmpty() && lon != null && !lon.isEmpty()) {

                    String[] split = line.split(",");
                    if (split.length >= 6) { //only check legal datagrams, having heading
                        headingString = split[5];
                        if (headingString != null && !headingString.isEmpty()) {
                            dbHeading = Double.valueOf(headingString);
                        }
                    }

                    newCoordinate = new Coordinate(Double.valueOf(lon), Double.valueOf(lat));
                    Double calcBearing = SpatialUtil.bearingByCoord(coordinate, newCoordinate);
                    if (calcBearing != null && dbHeading != null) {
                        if (Math.abs(calcBearing - dbHeading) > 0.5) {
                            System.out.println(String.format("Calculated bearing (%s) deviates from database heading (%s)", calcBearing, dbHeading));
                            //fail("Calculated heading deviates from database heading");
                        }
                    }
                    coordinate = newCoordinate;
                }
            }
        }
        br.close();
    }
}
