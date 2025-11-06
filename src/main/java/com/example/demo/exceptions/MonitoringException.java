package com.example.demo.exceptions;

/*Excepción base para todos los errores controlados de la aplicación
  de monitoreo. Al heredar de RuntimeException, evita el 'throws'
  en toda la lógica de negocio*/

public class MonitoringException extends RuntimeException {

    public MonitoringException(String message) {
        super(message);
    }

    public MonitoringException(String message, Throwable cause) {
        super(message, cause);
    }
}