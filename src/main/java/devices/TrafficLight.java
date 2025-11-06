package devices;

public class TrafficLight {
    private String street;
    private String orientation;
    private boolean principal;
    private TrafficLightStatus status;

    public TrafficLight(String street, String orientation, boolean principal){
        this.street = street;
        this.orientation = orientation;
        this.principal = principal;
        this.status = TrafficLightStatus.RED;
    }

    public String getStreet() { return street; }
    public String getOrientation() { return orientation; }
    public boolean isPrincipal() { return principal; }
    public TrafficLightStatus getStatus() { return status; }

    public void setPrincipalStatus(TrafficLightStatus status){ this.status = status; }
    public void setSecondaryStatus(TrafficLightStatus status){ this.status = status; }

    @Override
    public String toString() {
        return "Street=" + street +
                ", Orientation=" + orientation +
                ", principal=" + principal +
                ", status=" + status;
    }

}
