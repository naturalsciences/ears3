/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.eurofleets.ears3.controller.rest;

import eu.eurofleets.ears3.domain.Cruise;
import eu.eurofleets.ears3.dto.CruiseDTO;
import eu.eurofleets.ears3.dto.CruiseDTOList;
import eu.eurofleets.ears3.service.CruiseService;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

/**
 *
 * @author Thomas Vandenberghe
 */
@RestController()
@RequestMapping(value = "/api/dto")
@CrossOrigin(origins = "*", maxAge = 3600)
public class CruiseDTOController {

    public static final String DEFAULT_VALUE = "_";
    @Autowired
    private CruiseService cruiseService;

    private CruiseDTOList getCruises(String platformCode) {
        Collection<Cruise> res;
        if (platformCode == null || "".equals(platformCode)) {
            res = this.cruiseService.findAll();
        } else {
            res = this.cruiseService.findAllByPlatformIdentifier(platformCode);
        }

        return new CruiseDTOList(res);
    }

    @RequestMapping(method = {RequestMethod.GET}, value = {"cruises"}, produces = {"application/xml; charset=utf-8", "application/json"})
    public CruiseDTOList getCruisesAt(@RequestParam(required = false, defaultValue = "") String platformIdentifier, @RequestParam(required = false) OffsetDateTime startDate, @RequestParam(required = false) OffsetDateTime endDate, @RequestParam(required = false) OffsetDateTime atDate) {
        if (startDate == null && endDate == null && atDate == null) {
            return getCruises(platformIdentifier);
        }
        if (startDate != null && endDate == null) {
            endDate = Instant.now().atOffset(ZoneOffset.UTC);
        }
        if (startDate == null && endDate != null) {
            startDate = OffsetDateTime.parse("1900-01-01", DateTimeFormatter.ISO_DATE);
        }

        Set<Cruise> res;
        if (atDate == null) {
            res = this.cruiseService.findAllBetweenDate(startDate, endDate, platformIdentifier);
        } else {
            res = this.cruiseService.findAtDate(atDate, platformIdentifier);
        }

        return new CruiseDTOList(res);
    }

    @RequestMapping(method = {RequestMethod.GET}, value = {"cruise/{id}"}, produces = {"application/xml; charset=utf-8", "application/json"})
    public CruiseDTO getCruiseById(@PathVariable(value = "id") String id) {
        Cruise cruise = this.cruiseService.findById(Long.parseLong(id));
        if (cruise != null) {
            return new CruiseDTO(cruise);
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "There is no cruise with id " + id);
        }
    }

    @RequestMapping(method = {RequestMethod.GET}, value = {"cruise"}, params = {"identifier"}, produces = {"application/xml; charset=utf-8", "application/json"})
    public CruiseDTO getCruiseByidentifier(@RequestParam(required = true, value = "identifier") String identifier) {
        Cruise cruise = this.cruiseService.findByIdentifier(identifier);
        if (cruise != null) {
            return new CruiseDTO(cruise);
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "There is no cruise with identifier " + identifier);
        }
    }

    @RequestMapping(method = {RequestMethod.GET}, value = {"cruise/current"}, produces = {"application/xml; charset=utf-8", "application/json"})
    public CruiseDTOList getCurrent() {
        return new CruiseDTOList(this.cruiseService.findCurrent());
    }
}
