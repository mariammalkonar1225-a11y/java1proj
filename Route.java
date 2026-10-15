import java.io.Serializable;

public class Route implements Serializable {
    private static final long serialVersionUID = 1L;

    private String id;
    private String origin;
    private String destination;
    private String stops;

    public Route(String id, String origin,
                 String destination, String stops) {
        this.id = id;
        this.origin = origin;
        this.destination = destination;
        this.stops = stops;
    }

    public String getId() {
        return id;
    }

    public String getOrigin() {
        return origin;
    }

    public String getDestination() {
        return destination;
    }

    public String getStops() {
        return stops;
    }

    public String toString() {
        return origin + " to " + destination;
    }
}