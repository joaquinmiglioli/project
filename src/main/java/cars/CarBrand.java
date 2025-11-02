package cars;

public class CarBrand {
    private long idbrand;
    private String name;

    public CarBrand() {}
    public CarBrand( long idbrand, String name) {
        this.idbrand = idbrand;
        this.name = name;
    }

    public long getIdbrand() {
        return idbrand;
    }
    public void setIdbrand(long idbrand) {
        this.idbrand = idbrand;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
}