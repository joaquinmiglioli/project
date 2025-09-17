package Cars;

import java.util.Objects;

/** Marca del automóvil (e.g., Toyota, Ford). */
public class CarBrand {
    private String name;

    public CarBrand() {}
    public CarBrand(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    @Override public String toString() {
        return name == null ? "" : name;
    }
    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CarBrand)) return false;
        CarBrand that = (CarBrand) o;
        return Objects.equals(name, that.name);
    }
    @Override public int hashCode() {
        return Objects.hash(name);
    }
}