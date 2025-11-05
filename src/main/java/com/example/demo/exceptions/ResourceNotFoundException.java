package com.example.demo.exceptions;

/**
 * Se lanza cuando no se encuentra un recurso específico (ej. Dispositivo, Auto).
 * Generalmente debería traducirse en un HTTP 404 Not Found.
 */
public class ResourceNotFoundException extends MonitoringException {

    public ResourceNotFoundException(String resourceType, String resourceId) {
        super("Resource '" + resourceType + "' wtih ID: " + resourceId + "not found");
    }
}