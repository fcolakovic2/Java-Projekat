package ba.unsa.etf.rs.zadaca5;

import javafx.beans.Observable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.*;

public class VehicleDAOBase implements VehicleDAO {


    private Connection conn;
    private PreparedStatement getOwnersUpit, izvuciMjestoUpit, getVehicleUpit, izvuciManuUpit, izvuciOwnUpit, getPlaceUpit, getManuFactUpit, addOwnerUpit,
            nextOwnerIDUpit, getPlaceUpitByID, nextIDInPlacesUpit, addPlaceUpit, changeOwnerUpit, OwnerOfVehicleUpit, deleteOwner, returnVehiclesUpit, getManuById, getOwnerById,
            deleteVehicleUpit;


    VehicleDAOBase() {
        try {
            conn = DriverManager.getConnection("jdbc:sqlite:vehicles.db");
            getOwnersUpit = conn.prepareStatement("SELECT  * from owner ORDER BY id");
            izvuciMjestoUpit = conn.prepareStatement("SELECT * from place where id=?");
            getVehicleUpit = conn.prepareStatement("SELECT  * from vehicle ORDER BY id");
            getPlaceUpit = conn.prepareStatement("SELECT  * from place ORDER BY name");
            getManuFactUpit = conn.prepareStatement("SELECT * from manufacturer ORDER BY name");
            addOwnerUpit = conn.prepareStatement("INSERT INTO owner VALUES(?,?,?,?,?,?,?,?,?)");
            nextOwnerIDUpit = conn.prepareStatement("SELECT MAX(id)+1 from owner");
            getPlaceUpitByID = conn.prepareStatement("SELECT * FROM place WHERE id=?");
            nextIDInPlacesUpit = conn.prepareStatement("SELECT MAX(id)+1 FROM place"); //vraca naredni najveci ID
            addPlaceUpit = conn.prepareStatement("INSERT INTO place VALUES(?,?,?)");
            changeOwnerUpit = conn.prepareStatement("UPDATE owner SET name=?, surname=?, parent_name=?, date_of_birth=?, place_of_birth=?, living_address=?, living_place=?, jmbg=? WHERE id=?");
            OwnerOfVehicleUpit = conn.prepareStatement("SELECT COUNT(*) FROM vehicle WHERE owner=?");
            deleteOwner = conn.prepareStatement("DELETE FROM owner WHERE id=?");
            returnVehiclesUpit = conn.prepareStatement("SELECT * FROM vehicle");
            getManuById = conn.prepareStatement("SELECT * FROM manufacturer WHERE id=?");
            getOwnerById = conn.prepareStatement("SELECT * FROM owner WHERE id=?");
            deleteVehicleUpit = conn.prepareStatement("DELETE FROM vehicle WHERE id=?");


        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    @Override
    public ObservableList<Owner> getOwners() {
        ObservableList<Owner> obsOwners = FXCollections.observableArrayList();
        try {
            ResultSet allOwners = getOwnersUpit.executeQuery();
            while (allOwners.next()) {
                Owner owner = izvuciVlasnikaRS(allOwners);
                obsOwners.add(owner);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return obsOwners;
    }

    @Override
    public ObservableList<Vehicle> getVehicles() {
         return null;
    }

    @Override
    public ObservableList<Place> getPlaces() {
        ObservableList<Place> obsPlaces = FXCollections.observableArrayList();

        try {
            ResultSet placesRS = getPlaceUpit.executeQuery();
            while (placesRS.next()) {
                int id = placesRS.getInt(1);
                String name = placesRS.getString(2);
                String postNumber = placesRS.getString(3);

                Place mjesto = new Place(id, name, postNumber);
                obsPlaces.add(mjesto);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return obsPlaces;
    }

    @Override
    public ObservableList<Manufacturer> getManufacturers() {
        ObservableList<Manufacturer> obsManu = FXCollections.observableArrayList();
        try {
            ResultSet manuRS = getManuFactUpit.executeQuery();
            while (manuRS.next()) {
                int id = manuRS.getInt(1);
                String name = manuRS.getString(2);

                Manufacturer manu = new Manufacturer(id, name);

                obsManu.add(manu);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return obsManu;
    }

    @Override
    public void addOwner(Owner owner) {
        try {

            owner.setPlaceOfBirth(addNullPlace(owner.getPlaceOfBirth()));
            owner.setLivingPlace(addNullPlace(owner.getLivingPlace()));

            ResultSet nextIDRS = nextOwnerIDUpit.executeQuery(); //uzimam naredni id , ako je prazna baza prvi owner ce imat ID 1 , a svaki naredni prethodni+1
            int id = 1;
            if (nextIDRS.next()) {
                id = nextIDRS.getInt(1);
            }

            addOwnerUpit.setInt(1, id);
            addOwnerUpit.setString(2, owner.getName());
            addOwnerUpit.setString(3, owner.getSurname());
            addOwnerUpit.setString(4, owner.getParentName());
            addOwnerUpit.setDate(5, Date.valueOf(owner.getDateOfBirth()));
            addOwnerUpit.setInt(6, owner.getPlaceOfBirth().getId());
            addOwnerUpit.setString(7, owner.getLivingAddress());
            addOwnerUpit.setInt(8, owner.getLivingPlace().getId());
            addOwnerUpit.setString(9, owner.getJmbg());
            addOwnerUpit.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public Place addNullPlace(Place place) {
        try {
            getPlaceUpitByID.setInt(1, place.getId());
            ResultSet placeRS = getPlaceUpitByID.executeQuery();
            if (!placeRS.next()) {
                int id = 1;
                ResultSet rs2 = nextIDInPlacesUpit.executeQuery();
                if (rs2.next()) {
                    id = rs2.getInt(1);
                }
                addPlaceUpit.setInt(1, id);
                addPlaceUpit.setString(2, place.getName());
                addPlaceUpit.setString(3, place.getPostalNumber());
                addPlaceUpit.executeUpdate();
                place.setId(id);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return place;
    }

    @Override
    public void changeOwner(Owner owner) {
        owner.setPlaceOfBirth(addNullPlace(owner.getPlaceOfBirth()));
        owner.setLivingPlace(addNullPlace(owner.getLivingPlace()));
        try {

            changeOwnerUpit.setString(1, owner.getName());
            changeOwnerUpit.setString(2, owner.getSurname());
            changeOwnerUpit.setString(3, owner.getParentName());
            changeOwnerUpit.setDate(4, Date.valueOf(owner.getDateOfBirth()));
            changeOwnerUpit.setInt(5, owner.getPlaceOfBirth().getId());
            changeOwnerUpit.setString(6, owner.getLivingAddress());
            changeOwnerUpit.setInt(7, owner.getLivingPlace().getId());
            changeOwnerUpit.setString(8, owner.getJmbg());
            changeOwnerUpit.setInt(9, owner.getId());
            changeOwnerUpit.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void deleteOwner(Owner owner) {
        try {
            OwnerOfVehicleUpit.setInt(1, owner.getId());
            ResultSet numOfOwners = OwnerOfVehicleUpit.executeQuery();
            if (numOfOwners.next())
                if (numOfOwners.getInt(1) > 0)
                    throw new IllegalArgumentException("Vlasnik ima vozila!");

            deleteOwner.setInt(1, owner.getId());
            deleteOwner.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void addVehicle(Vehicle vehicle) {

    }

    @Override
    public void changeVehicle(Vehicle vehicle) {

    }

    @Override
    public void deleteVehicle(Vehicle vehicle) {
        try {
           deleteVehicleUpit.setInt(1, vehicle.getId());
            deleteVehicleUpit.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }


    @Override
    public void close() {
        try {
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    private Owner izvuciVlasnikaRS(ResultSet rs) throws SQLException {

        Place mjestoRodjenja = null;  //a//
        izvuciMjestoUpit.setInt(1, rs.getInt(6));   //izvlacim mjesto rodjenja, uzimam broj iz tabele mjesto tamo gdje je taj broj id ownera(tako znam
        ResultSet mjestoRS = izvuciMjestoUpit.executeQuery();                 //da je to njegovo mjesto
        while (mjestoRS.next()) {
            mjestoRodjenja = new Place(mjestoRS.getInt(1), mjestoRS.getString(2), mjestoRS.getString(3));
        }

        Place mjestoPrebivalista = null;
        izvuciMjestoUpit.setInt(1, rs.getInt(8)); //izvlacim broj iz osme kolone koji je ujedno id vlasnika u tabeli owner, tako spajam ownera sa svojim prebivalistem
        ResultSet mjestoPB = izvuciMjestoUpit.executeQuery();
        while (mjestoPB.next()) {
            mjestoPrebivalista = new Place(mjestoPB.getInt(1), mjestoPB.getString(2), mjestoPB.getString(3)); //nisam mogao izvlaciti nikako direktno iz vlasnika jednim upitom mjesto
        }                                                                                                                       //jer je mjesto sacinjeno od 3 primitivna tipa

        Owner owner = new Owner(rs.getInt(1), rs.getString(2), rs.getString(3), rs.getString(4),
                rs.getDate(5).toLocalDate(), mjestoRodjenja, rs.getString(7), mjestoPrebivalista, rs.getString(9));
        return owner;
    }

}

