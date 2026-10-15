import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Trip implements Serializable {
    private static final long serialVersionUID = 1L;

    private String id;
    private String vehicleId;
    private String driverId;
    private String routeId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String status;

    public Trip(String id, String vehicleId, String driverId,
                String routeId, LocalDateTime startTime,
                LocalDateTime endTime, String status) {
        this.id = id;
        this.vehicleId = vehicleId;
        this.driverId = driverId;
        this.routeId = routeId;
        this.startTime = startTime;
        this.endTime = endTime;
        this.status = status;
    }

    public String getId() {
        return id;
    }

    public String getVehicleId() {
        return vehicleId;
    }

    public String getDriverId() {
        return driverId;
    }

    public String getRouteId() {
        return routeId;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public boolean overlaps(LocalDateTime start,
                           LocalDateTime end) {
        return startTime.isBefore(end)
                && start.isBefore(endTime);
    }

    public String getFormattedStart() {
        return startTime.format(
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
    }

    public String getFormattedEnd() {
        return endTime.format(
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
    }
}