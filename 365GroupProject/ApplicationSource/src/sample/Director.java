package sample;

import javafx.scene.control.Button;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;


public class Director {
    // Member variables of an Actor Object
    int pid;
    int mid;
    int billingPos;

    // Other useful variables for viewing information (from persons/movies)
    String name;
    String movieTitle;

    Button viewButton;


    // Constructor for the Actor Class
    public Director(int pid, int mid, int billingPos, Main main, Statement statement) throws SQLException {
        this.pid = pid;
        this.mid = mid;
        this.billingPos = billingPos;

        Person person = main.getPersonWithPID(pid, statement);
        this.name = person.name;

        Movie movie = main.getMovieWithMID(mid, statement);
        this.movieTitle = movie.title;

        this.viewButton = main.makeViewButton(person, statement);
    }

    // Incomplete Constructor
    public Director(int pid, int mid, int billingPos) {
        this.pid = pid;
        this.mid = mid;
        this.billingPos = billingPos;
    }


    // The toString method for the Actor Class
    @Override
    public String toString() {
        return "Actor{" +
                "pid=" + pid +
                ", mid=" + mid +
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
    public Button getViewButton() {
        return this.viewButton;
    }


    //
    public String getMovieTitle() {
        return this.movieTitle;
    }


    //
    public String getBillingPos() {
        return "" + this.billingPos;
    }
}
