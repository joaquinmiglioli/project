package devices;

/**
 * Define los servicios que el AppContext debe proveer a los
 * métodos de mantenimiento polimórficos de los Dispositivos.
 * Esto evita dependencias circulares, permitiendo que el dominio (Device)
 * llame a servicios de la aplicación (AppContext -> TrafficLightCycleService).
 */
public interface IMaintenanceContext {
    void pauseTrafficLight(String deviceId);
    void resumeTrafficLight(String deviceId);
}
