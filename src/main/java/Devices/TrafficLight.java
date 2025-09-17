package Devices;

public class TrafficLight {
    private String street;
    private String orientation;
    private boolean principal;
    private TrafficLightStatus status;

    public TrafficLight(String street,String orientation,boolean principal){
        this.street=street;
        this.orientation=orientation;
        this.principal=principal;
    }

    @Override
    public String toString() {
        return "Street= " + street   + '\'' +
                ", Orientation= " + orientation + '\'' +
                ", principal= " + principal +
                ", status= " + status;
    }

    public void setPrincipalStatus(TrafficLightStatus status){
        this.status=status;
    }

    public void setSecondaryStatus(TrafficLightStatus status){
        this.status=status;
    }
}
