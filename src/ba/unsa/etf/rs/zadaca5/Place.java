package ba.unsa.etf.rs.zadaca5;

public class Place {
    private int id;
    private String name, postalNumber;

    public Place() {
    }

    public Place(int id, String name, String postalNumber) {
        this.id = id;
        this.name = name;
        this.postalNumber = postalNumber;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPostalNumber() {
        return postalNumber;
    }

    public void setPostalNumber(String postalNumber) {
        this.postalNumber = postalNumber;
    }
}
