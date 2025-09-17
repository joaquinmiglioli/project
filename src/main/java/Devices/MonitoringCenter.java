package Devices;

public class MonitoringCenter {
    private TrafficLightController trafficLightController;

    public MonitoringCenter(String principalStreet,String principalOrientation,boolean principalP,String secondaryStreet,String secondaryOrientation,boolean principalS){
        this.trafficLightController=new TrafficLightController(principalStreet,principalOrientation,principalP,secondaryStreet,secondaryOrientation,principalS);
    }

    @Override
    public String toString() {
        return "Monitoring center: " +
                "trafficLightController=" + trafficLightController ;
    }

}
