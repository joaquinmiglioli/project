package cars;

public class Car {
    private long carid;
    private CarBrand brand;     // carbrand → idbrand
    private CarModel model;     // carmodel → modelid
    private String plate;       // "licensePlate"
    private String owner;
    private String address;
    private String colour;

    public long getCarid() {
        return carid;
    }
    public void setCarid(long carid) {
        this.carid = carid;
    }

    public CarBrand getBrand() {
        return brand;
    }
    public void setBrand(CarBrand brand) {
        this.brand = brand;
    }

    public CarModel getModel() {
        return model;
    }
    public void setModel(CarModel model) {
        this.model = model;
    }

    public String getPlate() {
        return plate;
    }
    public void setPlate(String plate) {
        this.plate = plate;
    }

    public String getOwner() {
        return owner;
    }
    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getAddress() {
        return address;
    }
    public void setAddress(String address) {
        this.address = address;
    }

    public String getColour() {
        return colour;
    }
    public void setColour(String colour) {
        this.colour = colour;
    }
}