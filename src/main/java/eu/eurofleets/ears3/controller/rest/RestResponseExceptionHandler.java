/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.eurofleets.ears3.controller.rest;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
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
 * @author thomas
 */
@ControllerAdvice
public class RestResponseExceptionHandler
        extends ResponseEntityExceptionHandler {

    @XmlRootElement
    @XmlAccessorType(XmlAccessType.FIELD)
    public static class WebErrorResponse {

        /**
         * The error message.
         */
        public String message;

        /**
         * The status code.
         */
        public int code;

        /**
         * The error type.
         */
        public String type;
    }

    @ExceptionHandler(value = {IllegalArgumentException.class, IllegalStateException.class, ResponseStatusException.class, ArrayIndexOutOfBoundsException.class})
    protected ResponseEntity<Object> handleConflict(
            RuntimeException ex, WebRequest request) {
        HttpStatus status = null;
        if (ex instanceof ResponseStatusException) {
            ResponseStatusException rpex = (ResponseStatusException) ex;
            status = rpex.getStatus();
        } else {
            status = HttpStatus.CONFLICT;
        }
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(getContentType(request));

        // build response body
        WebErrorResponse response = new WebErrorResponse();

        response.type = ex.getClass().getSimpleName();
        response.message = ex.getMessage();
        response.code = status.value();

        //   headers.setContentType(getContentType(request));
        return new ResponseEntity<>(response, headers, status);
        // return handleExceptionInternal(ex, response, headers, status, request);
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
                return MediaType.APPLICATION_JSON_UTF8;
            } // other
            else {
                return MediaType.APPLICATION_XML;
            }
        } else {
            return MediaType.APPLICATION_XML;
        }
    }
}
