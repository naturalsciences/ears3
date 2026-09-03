package eu.eurofleets.ears3.utilities;

import eu.eurofleets.ears3.domain.Coordinate;
import eu.eurofleets.ears3.domain.Navigation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.junit.jupiter.api.Test;

public class DatagramUtilitiesTest {

	@Test
	public void readStreamForCoordsTest() throws IOException {
		Resource file = new ClassPathResource("datagram.txt");

		DatagramUtilities<Navigation> a = new DatagramUtilities<>(Navigation.class, "http://localhost"); //ie ears3-server needs to run

		String datagram = new String(Files.readAllBytes(Paths.get(file.getURI())));
		System.out.println(datagram);
		long count = datagram.lines().count();
		assertEquals(60, count); //do not modify the file, keep at 60
		List<Coordinate> actual = null;
		//	Coordinate tooCloseToPrev = new Coordinate();
		try (Reader reader = new InputStreamReader(file.getInputStream())) {
			actual = a.readStreamForCoords(reader);

		}
		for (Coordinate coord : actual) {
			System.out.println(coord.toString());
		}
		assertEquals(31, actual.size()); //reduced to 31
	}
// eu.eurofleets.ears3.utilities.DatagramUtilities.analyzeDatagram    Read $EFPOS,,,4702004,42.23121748,260903T131913,4702002,,,4705001,,,,,,3702001,,,4704001,,,4704003 from http://host.docker.internal:80/ears3Nav/nav/getNearest/datagram?date=2026-09-03T13:26:38.930297556Z
	@Test
	public void getCoordinateTest() throws MalformedURLException {
		DatagramUtilities<Navigation> a = new DatagramUtilities<>(Navigation.class, "http://fake.rbins.be:8282");
		String line = "$EFPOS,2.23121748,260903T131913,4702004,52.23121748,260903T131913,4702002,,,4705001,,,,,,3702001,,,4704001,,,4704003";
		Coordinate expected = new Coordinate(2.23121748D, 52.23121748D);
		Coordinate actual = a.getCoordinate(line);

		assertEquals(expected, actual);
	}

	@Test
	public void tryConnectTest() throws IOException {
		DatagramUtilities<Navigation> datagramUtilities = new DatagramUtilities<>(Navigation.class,
				"http://www.kzfgks8921drghdqjb.com");
		try {
			DatagramUtilities.tryConnect(datagramUtilities.getBaseUrl());
			fail();
		} catch (IOException e) {

		}
		datagramUtilities = new DatagramUtilities<>(Navigation.class,
				"http://www.google.com");
		try {
			DatagramUtilities.tryConnect(datagramUtilities.getBaseUrl());
		} catch (IOException e) {
			fail();
		}

	}
}
