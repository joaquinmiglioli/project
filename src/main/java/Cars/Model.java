package Cars;

import java.util.Objects;

/** Modelo del automóvil (e.g., Corolla, Fiesta). */
public class Model {
    private String name;

    public Model() {}
    public Model(String name) {
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
        if (!(o instanceof Model)) return false;
        Model that = (Model) o;
        return Objects.equals(name, that.name);
    }
    @Override public int hashCode() {
        return Objects.hash(name);
    }
}