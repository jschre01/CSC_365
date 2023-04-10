package sample;

import javafx.scene.control.Button;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;


public class Actor {
    // Member variables of an Actor Object
    int pid;
    int mid;
    String role;
    int billingPos;

    // Other useful variables for viewing information (from persons/movies)
    String name;
    String movieTitle;

    Button viewButton;


    // Constructor for the Actor Class
    public Actor(int pid, int mid, String role, int billingPos, Main main,  Statement statement) throws SQLException {
        this.pid = pid;
        this.mid = mid;
        this.role = role;
        this.billingPos = billingPos;

        Person person = main.getPersonWithPID(pid, statement);
        this.name = person.name;

        Movie movie = main.getMovieWithMID(mid, statement);
        this.movieTitle = movie.title;

        this.viewButton = main.makeViewButton(person, statement);
    }

    // Incomplete Constructor
    public Actor(int pid, int mid, String role, int billingPos) {
        this.pid = pid;
        this.mid = mid;
        this.role = role;
        this.billingPos = billingPos;
    }


    // The toString method for the Actor Class
    @Override
    public String toString() {
        return "Actor{" +
                "pid=" + pid +
                ", mid=" + mid +
                ", role='" + role + '\'' +
                ", billingPos=" + billingPos +
                '}';
    }


    //
    public String getName() {
        return this.name;
    }


    //
    public String getMovie() {
        return this.movieTitle;
    }


    //
    public String getRole() {
        return this.role;
    }


    //
    public Button getViewButton() { return this.viewButton; }



    //
    public String getMovieTitle() { return this.movieTitle; }


    //
    public String getBillingPos() { return "" + this.billingPos; }


//    // This method returns a Person object for this Actor
//    public Person getPerson(Statement statement) throws SQLException {
//        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
//        //Person samplePerson = new Person(1, "Sylvester", "Stallone", "1946-07-06",
//        //        "New York City, New York, USA", "333224444", "./sylvesterStallone.png");
//        //return samplePerson;
//
//        ResultSet rs = statement.executeQuery("SELECT * FROM persons WHERE pid = " + this.pid);
//        rs.next();
//        Person person = new Person(rs.getInt("pid"), rs.getString("firstName"), rs.getString("lastName"),
//                rs.getString("birthdate"), rs.getString("birthplace"), rs.getString("SSN"),
//                rs.getString("imageP"));
//        return person;
//    }
}
