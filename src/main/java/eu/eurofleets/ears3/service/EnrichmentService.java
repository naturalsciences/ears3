package eu.eurofleets.ears3.service;

import eu.eurofleets.ears3.domain.Event;
import eu.eurofleets.ears3.domain.Navigation;
import eu.eurofleets.ears3.domain.Thermosal;
import eu.eurofleets.ears3.domain.Weather;
import eu.eurofleets.ears3.utilities.DatagramUtilities;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class EnrichmentService {

    private static final Logger logger = Logger.getLogger(EnrichmentService.class.getName());

    private DatagramUtilities<Navigation> navUtil;
    private DatagramUtilities<Thermosal> thermosalUtil;
    private DatagramUtilities<Weather> weatherUtil;

    private final NavigationService navigationService;
    private final ThermosalService thermosalService;
    private final WeatherService weatherService;
    private final EventRepository eventRepository;

    public EnrichmentService(@Lazy EventRepository eventRepository, @Value("${app.navigation.server}") String navServer,
                             NavigationService navigationService, ThermosalService thermosalService, WeatherService weatherService) {
        this.eventRepository = eventRepository;
        this.navigationService = navigationService;
        this.thermosalService = thermosalService;
        this.weatherService = weatherService;

        try {
            navUtil = new DatagramUtilities<>(Navigation.class, navServer);
            thermosalUtil = new DatagramUtilities<>(Thermosal.class, navServer);
            weatherUtil = new DatagramUtilities<>(Weather.class, navServer);
        } catch (MalformedURLException ex) {
            Logger.getLogger(EventService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Async
    public void enrichEventWithAcquisitionAsync(Event event) {
        try {
            enrichEventWithAcquisition(event);
        } catch (IOException ex) {
            logger.log(Level.SEVERE, null, ex);
        }
    }

    private void enrichEventWithAcquisition(Event event) throws IOException {
        Collection<Navigation> navigations = new ArrayList<>();
        Collection<Weather> weathers = new ArrayList<>();
        Collection<Thermosal> thermosals = new ArrayList<>();

        /// boolean tooOld;
        // boolean persistAcquisition = false;
        // Navigation nearestNav = navigationService.findNearest(event.getTimeStamp());
        // log.log(Level.INFO, "Enriching " + event.toString() + ": nearest nav in db: "
        /// + nearestNav);
        // if (acqDataIsNullTooOldOrUncomparable(nearestNav, event)) {//if we don't find
        /// it directly via the database, or if we found it but it is too old, look in
        /// the ears3Nav webservice itself
        Navigation nearestNav = navUtil.findNearest(event.getTimeStamp());
        // log.log(Level.INFO, "Enriching " + event.toString() + ": nearest nav in ws: "
        // + nearestNav);
        // persistAcquisition = true;
        // }
        if (nearestNav != null) {
            // tooOld = acqDataIsNullTooOldOrUncomparable(nearestNav, event);
            // log.log(Level.INFO, "Nearest nav " + (tooOld ? " (too old):" : ":") +
            // nearestNav.toString());
            // if (!tooOld) {
            // if (persistAcquisition) {
            Collection<Event> events = new ArrayList<>();
            events.add(event);
            nearestNav.setEvents(events);
            navigationService.save(nearestNav);
            // }
            navigations.add(nearestNav);
            event.setNavigation(navigations);
        }
        // }
        // persistAcquisition = false;
        // Weather nearestWeather = weatherService.findNearest(event.getTimeStamp());
        // if (acqDataIsNullTooOldOrUncomparable(nearestWeather, event)) {//if we don't
        // find it directly via the database, or if we found it but it is too old, look
        // in the ears3Nav webservice itself
        Weather nearestWeather = weatherUtil.findNearest(event.getTimeStamp()); // find it via the webservices
        // persistAcquisition = true;
        // }
        if (nearestWeather != null) {
            // tooOld = acqDataIsNullTooOldOrUncomparable(nearestWeather, event);
            // log.log(Level.INFO, "Enriching " + event.toString() + ": nearest met" +
            // (tooOld ? " (too old):" : ":") + nearestWeather.toString());
            // if (!tooOld) {
            // if (persistAcquisition) {
            weatherService.save(nearestWeather);
            // }
            weathers.add(nearestWeather);
            event.setWeather(weathers);
        }
        // }
        // persistAcquisition = false;
        // Thermosal nearestThermosal =
        // thermosalService.findNearest(event.getTimeStamp());
        // if (acqDataIsNullTooOldOrUncomparable(nearestThermosal, event)) {//if we
        // don't find it directly via the database, or if we found it but it is too old,
        // look in the ears3Nav webservice itself
        Thermosal nearestThermosal = thermosalUtil.findNearest(event.getTimeStamp());
        // persistAcquisition = true;
        // }
        if (nearestThermosal != null) {
            // tooOld = acqDataIsNullTooOldOrUncomparable(nearestThermosal, event);
            // log.log(Level.INFO, "Enriching " + event.toString() + ": nearest tss" +
            // (tooOld ? " (too old):" : ":") + nearestThermosal.toString());
            // if (!tooOld) {
            // if (persistAcquisition) {
            thermosalService.save(nearestThermosal);
            // }
            thermosals.add(nearestThermosal);
            event.setThermosal(thermosals);
        }
        // }
        this.eventRepository.save(event);
    }
}