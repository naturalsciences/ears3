package eu.eurofleets.ears3.utilities;

import eu.eurofleets.ears3.domain.Acquisition;
import eu.eurofleets.ears3.domain.Coordinate;
import eu.eurofleets.ears3.domain.Navigation;

import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import org.junit.Test;

import static org.junit.Assert.*;
import java.net.URL;
import java.net.URLConnection;

public class DatagramUtilitiesTest {

	@Test
	public void readStreamForCoords() throws IOException {
		Resource file = new ClassPathResource("datagram.txt");

		DatagramUtilities<Navigation> a = new DatagramUtilities<>(Navigation.class, "http://ostrea.rbins.be:8282");

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

	@Test
	public void getCoordinate() throws MalformedURLException {
		DatagramUtilities<Navigation> a = new DatagramUtilities<>(Navigation.class, "http://ostrea.rbins.be:8282");
		String line = "$BELPOS,140707,085900,3.198707,51.336128,35.0,,10.1,,0.1";
		Coordinate expected = new Coordinate(3.198707D, 51.336128D);
		Coordinate actual = a.getCoordinate(line);

		assertEquals(expected, actual);
	}

	@Test
	public void tryConnect() throws IOException {
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
