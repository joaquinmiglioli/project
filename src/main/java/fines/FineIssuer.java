package fines;

import java.util.Map;

public interface FineIssuer {
    Fine issue(FineType type,
               String deviceId,
               String photoUrl,
               Map<String, Object> meta);
}
