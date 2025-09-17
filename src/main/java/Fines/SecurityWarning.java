package Fines;

/** Por ahora solo descripción; lo podés usar para “alertas de seguridad”. */
public class SecurityWarning {
    private String description;

    public SecurityWarning() {}

    public SecurityWarning(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
}