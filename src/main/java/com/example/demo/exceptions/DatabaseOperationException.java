package com.example.demo.exceptions;

/**
 * Se lanza cuando falla una operación de base de datos (ej. SQLException).
 * Generalmente debería traducirse en un HTTP 500 Internal Server Error.
 */
public class DatabaseOperationException extends MonitoringException {

    public DatabaseOperationException(String message, Throwable cause) {
        super(message, cause);
    }
}