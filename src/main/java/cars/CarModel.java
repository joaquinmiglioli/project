package cars;

public class CarModel {
    private long modelid;
    private String name;
    private CarBrand brand; // FK a carbrands

    public CarModel() {}

    public CarModel(long modelid, String name, CarBrand brand) {
        this.modelid = modelid;
        this.name = name;
        this.brand = brand;
    }

    public long getModelid() {
        return modelid;
    }
    public void setModelid(long modelid) {
        this.modelid = modelid;
    }

    public String getName() {
        return name; }
    public void setName(String name) {
        this.name = name; }

    public CarBrand getBrand() {
        return brand; }
    public void setBrand(CarBrand brand) {
        this.brand = brand; }
}