package Cars;

import java.util.Objects;

/** Vehículo registrado en el sistema. */
public class Car {
    private String licensePlate;   // clave natural
    private String owner;
    private String address;
    private String colour;

    private CarBrand brand;        // opcional: marca
    private Model model;           // opcional: modelo

    public Car() {}

    public Car(String licensePlate, String owner, String address, String colour) {
        this.licensePlate = licensePlate;
        this.owner = owner;
        this.address = address;
        this.colour = colour;
    }

    public Car(String licensePlate, String owner, String address, String colour,
               CarBrand brand, Model model) {
        this(licensePlate, owner, address, colour);
        this.brand = brand;
        this.model = model;
    }

    public String getLicensePlate() {
        return licensePlate;
    }
    public void setLicensePlate(String licensePlate) {
        this.licensePlate = licensePlate;
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

    public CarBrand getBrand() {
        return brand;
    }
    public void setBrand(CarBrand brand) {
        this.brand = brand;
    }

    public Model getModel() {
        return model;
    }
    public void setModel(Model model) {
        this.model = model;
    }

    @Override public String toString() {
        return licensePlate + (brand != null ? " " + brand : "") + (model != null ? " " + model : "");
    }

    /** Igualdad por patente (lo típico en dominios municipales). */
    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Car)) return false;
        Car car = (Car) o;
        return Objects.equals(licensePlate, car.licensePlate);
    }
    @Override public int hashCode() {
        return Objects.hash(licensePlate);
    }
}