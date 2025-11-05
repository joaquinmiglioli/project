package com.example.demo.exceptions;

/**
 * Se lanza al intentar insertar un recurso que viola una restricción
 * de unicidad (ej. una patente de auto duplicada).
 * Generalmente debería traducirse en un HTTP 409 Conflict.
 */
public class DuplicateResourceException extends MonitoringException {

    public DuplicateResourceException(String resourceType, String field, String value) {
        super("Resource '" + resourceType + "' with " + field + ": " + value + "already exists");
    }
}