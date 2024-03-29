/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.eurofleets.ears3.controller.rest;

import be.naturalsciences.bmdc.cruise.csr.IllegalCSRArgumentException;
import eu.eurofleets.ears3.domain.StringMessage;

import java.time.format.DateTimeParseException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.hibernate.exception.ConstraintViolationException;
import org.postgresql.util.PSQLException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

/**
 *
 * @author Thomas Vandenberghe
 */
@ControllerAdvice
public class RestResponseExceptionHandler
        extends ResponseEntityExceptionHandler {

    @ExceptionHandler(value = { IllegalCSRArgumentException.class, IllegalArgumentException.class,
            IllegalStateException.class, ResponseStatusException.class, ArrayIndexOutOfBoundsException.class,
            PSQLException.class, DataIntegrityViolationException.class, ConstraintViolationException.class,
            ClassNotFoundException.class, DateTimeParseException.class })
    protected ResponseEntity<Object> handleConflict(
            RuntimeException ex, WebRequest request) throws Exception {
        HttpStatus status = null;
        if (ex instanceof ResponseStatusException) {
            ResponseStatusException rpex = (ResponseStatusException) ex;
            status = rpex.getStatus();
        } else {
            status = HttpStatus.CONFLICT;
        }
        Logger.getLogger(RestResponseExceptionHandler.class.getName()).log(Level.INFO, "Error captured.", ex);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(getContentType(request));

        // build response body
        StringMessage response = new StringMessage(ex.getMessage(), status.value(), null,
                ex.getClass().getSimpleName());

        if (ex instanceof IllegalCSRArgumentException) {
            IllegalCSRArgumentException CSRex = (IllegalCSRArgumentException) ex;
            response.messages = CSRex.getInvalidArguments();
        }

        return new ResponseEntity<>(response, headers, status);
    }

    private static MediaType getContentType(WebRequest request) throws NullPointerException, IllegalArgumentException {
        String accepts = request.getHeader(HttpHeaders.ACCEPT);
        if (accepts != null) {
            // XML
            if (accepts.contains(MediaType.APPLICATION_XML_VALUE)
                    || accepts.contains(MediaType.TEXT_XML_VALUE)
                    || accepts.contains(MediaType.APPLICATION_XHTML_XML_VALUE)) {
                return MediaType.APPLICATION_XML;
            } // JSON
            else if (accepts.contains(MediaType.APPLICATION_JSON_VALUE)) {
                return MediaType.APPLICATION_JSON;
            } // other
            else {
                return MediaType.APPLICATION_XML;
            }
        } else {
            return MediaType.APPLICATION_XML;
        }
    }
}
