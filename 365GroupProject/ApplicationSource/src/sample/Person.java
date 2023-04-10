package sample;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;

import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.util.ArrayList;


public class Person {
    // Member variables of an Person Object
    int pid;
    String name;
    String birthdate;
    String birthplace;

    Button viewButton;


    // Additional member variables for a Person Object
    ArrayList<Movie> moviesActor;
    ArrayList<Movie> moviesDirector;


    // Constructor for the Person Class
    public Person(int pid, String name, String birthdate, String birthplace, Main main, Statement statement) {
        this.pid = pid;
        this.name = name;
        this.birthdate = birthdate;
        this.birthplace = birthplace;
        this.viewButton = main.makeViewButton(this, statement);
    }


    // The toString method for the Person Class
    @Override
    public String toString() {
        return
                "\n\nName: " + name +
                "\n\nBirthday: " + birthdate +
                "\n\nBirth Place: " + birthplace + "\n";
    }


    public int getPid() {
        return pid;
    }

    public void setPid(int pid) {
        this.pid = pid;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBirthdate() {
        return birthdate;
    }

    public void setBirthdate(String birthdate) {
        this.birthdate = birthdate;
    }

    public String getBirthplace() {
        return birthplace;
    }

    public void setBirthplace(String birthplace) {
        this.birthplace = birthplace;
    }

    public Button getViewButton() { return this.viewButton; }
}
