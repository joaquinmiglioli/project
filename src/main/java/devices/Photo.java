package devices;

import java.io.IOException;
import java.nio.file.*;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

//Representa una foto (guardamos sólo el filename).
public class Photo {
    private String path; // ej: "FinesPhoto2.jpg"

    public Photo() {}
    public Photo(String path) { this.path = path; }

    public String getPath() { return path; }
    public void setPath(String path) { this.path = path; }

    /*Devuelve un filename aleatorio desde src/main/resources/static/images/fines.
      Si no hay imágenes, devuelve "fallback.png".*/

    public static String randomFinePhotoFilename() {
        try {
            Path dir = Paths.get("src/main/resources/static/images/fines");
            if (!Files.exists(dir)) return "fallback.png";
            List<Path> files = Files.list(dir)
                    .filter(p -> {
                        String n = p.getFileName().toString().toLowerCase();
                        return n.endsWith(".jpg") || n.endsWith(".jpeg") || n.endsWith(".png");
                    })
                    .collect(Collectors.toList());
            if (files.isEmpty()) return "fallback.png";
            Path rnd = files.get(new Random().nextInt(files.size()));
            return rnd.getFileName().toString(); // sólo el nombre
        } catch (IOException e) {
            return "fallback.png";
        }
    }
}
