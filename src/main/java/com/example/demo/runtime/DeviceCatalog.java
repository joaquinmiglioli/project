package com.example.demo.runtime;

import devices.Device;
import java.util.*;

public class DeviceCatalog {
    private final Map<String, Device> byId = new LinkedHashMap<>();

    public void put(Device d) {
        if (d == null || d.getDeviceId() == null) return;
        byId.put(d.getDeviceId(), d);
    }

    public Device get(String id) { return byId.get(id); }

    public Collection<Device> all() {
        return Collections.unmodifiableCollection(byId.values());
    }

    public boolean contains(String id) { return byId.containsKey(id); }
}