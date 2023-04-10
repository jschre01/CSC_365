package sample;


import javafx.scene.control.Button;

import java.sql.SQLException;
import java.sql.Statement;

public class Movie {
    // Member variables of an Movie Object
    int mid;
    String title;
    String releaseDate;
    String genre;
    Float rating;
    int minutes;

    Button viewButton;


    // Constructor for the Actor Class
    public Movie(int mid, String title, String releaseDate, String genre, Float rating,  int minutes, Main main,  Statement statement) throws SQLException {
        this.mid = mid;
        this.title = title;
        this.releaseDate = releaseDate;
        this.genre = genre;
        this.rating = rating;
        this.minutes = minutes;

        this.viewButton = main.makeViewButton(this, statement);

    }

    public String getTitle() {
        return title;
    }

    public String getReleaseDate() { return releaseDate;}

    public String getGenre() { return genre;}

    public Float getRating() { return rating; }

    public Integer getMinutes() { return minutes;}

    public Button getViewButton() { return this.viewButton; }
    // The toString method for the Movie Class
    @Override
    public String toString() {
        return
                "\n\nMovie Title: " + title +
                "\n\nRelease Date: " + releaseDate +
                "\n\nGenre: " + genre +
                "\n\nRating: " + rating + "\n\nMinutes: " + minutes + "\n";
    }
}
