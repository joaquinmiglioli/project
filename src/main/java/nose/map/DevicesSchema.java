/*
package nose.map;

import java.util.List;

*/
/** POJOs para deserializar el JSON de la cátedra. *//*

public class DevicesSchema {
    public List<SemaphoreController> semaphoreControllers;
    public List<PointDevice> radars;
    public List<PointDevice> securityCameras;
    public List<ParkingCamera> parkingCameras;

    public static class LatLng { public double lat, lng; }

    public static class PointDevice extends LatLng { public String id, status; }

    public static class ParkingCamera extends PointDevice { public int toleranceTime; }

    public static class SemaphoreController extends LatLng {
        public String id, status;
        public Arm semA, semB;
        public static class Arm {
            public String id, street, orientation, status;
            public boolean main;
        }
    }
}*/
