import java.io.Serializable;

public class Driver implements Serializable {
    private static final long serialVersionUID = 1L;

    private String id;
    private String name;
    private String phone;
    private boolean eligible;

    public Driver(String id, String name, String phone,
                  boolean eligible) {
        this.id = id;
        this.name = name;
        this.phone = phone;
        this.eligible = eligible;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getPhone() {
        return phone;
    }

    public boolean isEligible() {
        return eligible;
    }

    public String toString() {
        return name + " (" + phone + ")";
    }
}