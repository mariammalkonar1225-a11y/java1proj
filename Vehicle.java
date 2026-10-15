import java.io.Serializable;

public class Vehicle implements Serializable {
    private static final long serialVersionUID = 1L;

    private String id;
    private String registrationNumber;
    private String type;
    private int capacity;
    private String status;

    public Vehicle(String id, String registrationNumber,
                   String type, int capacity, String status) {
        this.id = id;
        this.registrationNumber = registrationNumber;
        this.type = type;
        this.capacity = capacity;
        this.status = status;
    }

    public String getId() {
        return id;
    }

    public String getRegistrationNumber() {
        return registrationNumber;
    }

    public String getType() {
        return type;
    }

    public int getCapacity() {
        return capacity;
    }

    public String getStatus() {
        return status;
    }

    public String toString() {
        return registrationNumber + " (" + type + ")";
    }
}