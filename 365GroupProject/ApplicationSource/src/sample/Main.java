package sample;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.TilePane;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import java.sql.*;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.logging.Filter;


public class Main extends Application {
    // Define useful constants
    final int WND_HEIGHT = 720;
    final int WND_WIDTH = 1280;
    final int POPUP_WIDTH = WND_WIDTH / 2;
    final int POPUP_HEIGHT = WND_HEIGHT / 4;

    // Define the 3 main Scenes in global data
    Scene welcomeScene;
    ActorsScene actorsScene;
    DirectorsScene directorsScene;
    MoviesScene moviesScene;

    // Define the 3 Pop Up Dialog
    Dialog<String> personsPopUp;
    Dialog<String> moviesPopUp;


    // Main method/Program entry point
    public static void main(String[] args) {
        launch(args);
    }
    

    @Override
    public void start(Stage primaryStage) throws Exception{
        // Get a connection to the SQL database
        Class.forName("com.mysql.cj.jdbc.Driver");
        Connection connect = DriverManager.getConnection(
                "jdbc:mysql://ambari-node5.csc.calpoly.edu:3306/arwj93?user=arwj93&password=pass123");
        Statement statement = connect.createStatement();

        // Set the title of the Dialog
        primaryStage.setTitle("Groovy Movies");

        // Initialize the Welcome Scene
        initializeWelcomeScene(primaryStage);

        // Initialize the 3 main Scenes
        initializeMainScenes(primaryStage, statement);

        // Initialize the 2 pop up dialogs
        initializePopUps();

        // Set the Stage's Scene to the Welcome Scene
        primaryStage.setScene(welcomeScene);

        // Display the Stage
        primaryStage.show();
    }


    // This method returns the Welcome scene to be used by the Application's start routine.
    // Additionally, this method must instantiate the 3 main Scenes in global data.
    void initializeWelcomeScene(Stage primaryStage) {
        // Create a Tile Pane for the Welcome Scene
        TilePane welcomeRoot = new TilePane();
        welcomeRoot.setAlignment(Pos.CENTER);
        welcomeRoot.setVgap(80);
        welcomeRoot.setId("background");

        // Create the Welcome Scene from the Tile Pane
        welcomeScene = new Scene(welcomeRoot, WND_WIDTH, WND_HEIGHT);
        welcomeScene.getStylesheets().add("sample/Main.css");
        Text title = new Text ("Groovy Movies");
        title.setId("title");

        GridPane gridpane = new GridPane();
        gridpane.setHgap(50);

        // Create the 3 Buttons to be displayed
        Button actorsButton = new Button("Actors");
        actorsButton.setMinSize(200, 100);
        actorsButton.setId("button");
        actorsButton.setOnAction(actionEvent -> primaryStage.setScene(actorsScene));

        Button directorsButton = new Button("Directors");
        directorsButton.setMinSize(200, 100);
        directorsButton.setId("button");
        directorsButton.setOnAction(actionEvent -> primaryStage.setScene(directorsScene));

        Button moviesButton = new Button("Movies");
        moviesButton.setMinSize(200, 100);
        moviesButton.setId("button");
        moviesButton.setOnAction(actionEvent -> primaryStage.setScene(moviesScene));

        gridpane.add(actorsButton, 1, 0); // column=1 row=0
        gridpane.add(directorsButton, 2, 0);  // column=2 row=0
        gridpane.add(moviesButton, 3, 0);  // column=3 row=0

        Text names = new Text (
                "By Rachel Izenson, Alexa Novaes Nichols, Wesley Reynolds, Jonny Schreiber\n" +
                        "CSC 365 - Spring 2022"
        );
        names.setId("names");

        // Add the 3 Buttons to the Welcome Scene's Tile Pane
        welcomeRoot.getChildren().addAll(title, gridpane, names);
    }


    // Initialize the 3 main Scenes to be used by the application
    void initializeMainScenes(Stage primaryStage, Statement statement) throws SQLException {
        // Initialize each of the 3 main Scenes
        initializeActorsScene(primaryStage, statement);
        initializeDirectorsScene(primaryStage, statement);
        initializeMoviesScene(primaryStage, statement);
    }

    ObservableList<Actor> queryActorTable(String query, Statement statement) throws SQLException {
        ResultSet rs = statement.executeQuery(query);
        ObservableList<Actor> tempData = FXCollections.observableArrayList();
        while (rs.next()) {
            int pid = rs.getInt("pid");
            int mid = rs.getInt("mid");
            String role = rs.getString("role");
            int billingPos = rs.getInt("billingPos");
            tempData.add(new Actor(pid, mid, role, billingPos));
        }

        ObservableList<Actor> data = FXCollections.observableArrayList();
        for (Actor a : tempData) {
            data.add(new Actor(a.pid, a.mid, a.role, a.billingPos, this, statement));
        }
        return data;

    }

    ObservableList<Director> queryDirectorTable(String query, Statement statement) throws SQLException {
        ResultSet rs = statement.executeQuery(query);
        ObservableList<Director> tempData = FXCollections.observableArrayList();
        while (rs.next()) {
            int pid = rs.getInt("pid");
            int mid = rs.getInt("mid");
            int billingPos = rs.getInt("billingPos");
            tempData.add(new Director(pid, mid, billingPos));
        }

        ObservableList<Director> data = FXCollections.observableArrayList();
        for (Director d : tempData) {
            data.add(new Director(d.pid, d.mid, d.billingPos, this, statement));
        }
        return data;

    }

    ObservableList<Movie> queryMovieTable(String query, Statement statement) throws SQLException {
        ResultSet rs = statement.executeQuery(query);
        ObservableList<Movie> tempData = FXCollections.observableArrayList();
        while (rs.next()) {
            int mid = rs.getInt("mid");
            String title = rs.getString("title");
            String releaseDate = rs.getString("releaseDate");
            String genre = rs.getString("genre");
            float rating = rs.getFloat("rating");
            int minutes = rs.getInt("minutes");
            tempData.add(new Movie(mid, title, releaseDate, genre, rating, minutes, this, statement));
        }

        ObservableList<Movie> data = FXCollections.observableArrayList();
        for (Movie m : tempData) {
            data.add(new Movie(m.mid, m.title, m.releaseDate, m.genre, m.rating, m.minutes, this, statement));
        }
        return data;

    }


    // Initialize the Actors Scene to be used by the Application
    void initializeActorsScene(Stage primaryStage, Statement statement) throws SQLException {
        // Create a Tile Pane for the Actors Scene
        GridPane actorsSceneRoot = new GridPane();
        actorsSceneRoot.setAlignment(Pos.BASELINE_CENTER);
        actorsSceneRoot.setHgap(50);
        actorsSceneRoot.setVgap(50);
        actorsSceneRoot.setId("background");

        // Initialize the Actors Scene (global data) with the Tile Pane
        actorsScene = new ActorsScene(actorsSceneRoot, WND_WIDTH, WND_HEIGHT);
        actorsScene.getStylesheets().add("sample/Main.css");

        // Create the Button for each of the other 2 main Scenes
        Button directorsButton = new Button("Directors");
        directorsButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                primaryStage.setScene(directorsScene);
            }
        });
        directorsButton.setId("topButton");

        Button moviesButton = new Button("Movies");
        moviesButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                primaryStage.setScene(moviesScene);
            }
        });
        moviesButton.setId("topButton");

        //////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        //init actor table
        ObservableList<Actor> data = queryActorTable("SELECT * FROM actors", statement);

        // Get the Table View that displays the Actor Information
        TableView<Actor> actorsTable = getActorsTable(data);
        actorsTable.setMinWidth(600);

        //Add query options
        ObservableList<String> options =
                FXCollections.observableArrayList(
                        "Sort actors by highest billing position",
                        "Sort actors by roles alphabetically"
                );
        ComboBox queryActor = new ComboBox(options);
        queryActor.setId("queryChoiceBox");

        Button applyQueryButton = new Button("Query");
        applyQueryButton.setId("queryButton");
        applyQueryButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                switch (queryActor.getValue().toString()){
                    case "Sort actors by highest billingPos":
                        ObservableList<Actor> query1 = null;
                        try {
                            query1 = queryActorTable("SELECT * FROM actors ORDER BY billingPos DESC", statement);
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                        actorsTable.setItems(query1);
                        break;
                    case "Sort actors by roles alphabetically":
                        ObservableList<Actor> query2 = null;
                        try {
                            query2 = queryActorTable("SELECT * FROM actors ORDER BY role", statement);
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                        actorsTable.setItems(query2);
                        break;
                }
                HBox hBox = initializeActorSearch(actorsTable);
                actorSceneSetUp(actorsSceneRoot, actorsTable, hBox, directorsButton, moviesButton, applyQueryButton, queryActor);
            }
        });

        HBox hBox = initializeActorSearch(actorsTable);
        actorSceneSetUp(actorsSceneRoot, actorsTable, hBox, directorsButton, moviesButton, applyQueryButton, queryActor);
        /////////////////////////////////////////////////////////
    }


    // Initialize the Directors Scene to be used by the Application
    void initializeDirectorsScene(Stage primaryStage, Statement statement) throws SQLException {
        // Create a Grid Pane for the Directors Scene
        GridPane directorsSceneRoot = new GridPane();
        directorsSceneRoot.setAlignment(Pos.BASELINE_CENTER);
        directorsSceneRoot.setHgap(50);
        directorsSceneRoot.setVgap(50);
        directorsSceneRoot.setId("background");

        // Initialize the Directors Scene (global data) with the Grid Pane
        directorsScene = new DirectorsScene(directorsSceneRoot, WND_WIDTH, WND_HEIGHT);
        directorsScene.getStylesheets().add("sample/Main.css");

        // Create the Button for each of the other 2 main Scenes
        Button actorsButton = new Button("Actors");
        actorsButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                primaryStage.setScene(actorsScene);
            }
        });
        actorsButton.setId("topButton");

        Button moviesButton = new Button("Movies");
        moviesButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                primaryStage.setScene(moviesScene);
            }
        });
        moviesButton.setId("topButton");

        //////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        //init director table
        ObservableList<Director> data = queryDirectorTable("SELECT * FROM directors", statement);

        // Get the Table View that displays the Actor Information
        TableView<Director> directorsTable = getDirectorsTable(data);
        directorsTable.setMinWidth(600);

        //Add query options
        ObservableList<String> options =
                FXCollections.observableArrayList(
                        "Sort directors by highest billing position"
                );
        ComboBox queryDirector = new ComboBox(options);
        queryDirector.setId("queryChoiceBox");

        Button applyQueryButton = new Button("Query");
        applyQueryButton.setId("queryButton");
        applyQueryButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                switch (queryDirector.getValue().toString()){
                    case "Sort actors by highest billingPos":
                        ObservableList<Director> query1 = null;
                        try {
                            query1 = queryDirectorTable("SELECT * FROM directors ORDER BY billingPos DESC", statement);
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                        directorsTable.setItems(query1);
                        break;
                }
                HBox hBox = initializeDirectorSearch(directorsTable);
                directorSceneSetUp(directorsSceneRoot, directorsTable, hBox, actorsButton, moviesButton, applyQueryButton, queryDirector);
            }
        });

        HBox hBox = initializeDirectorSearch(directorsTable);
        directorSceneSetUp(directorsSceneRoot, directorsTable, hBox, actorsButton, moviesButton, applyQueryButton, queryDirector);

        /////////////////////////////////////////////////////////
    }


    // Initialize the Movies Scene to be used by the Application
    void initializeMoviesScene(Stage primaryStage, Statement statement) throws SQLException {
        // Create a Tile Pane for the Actors Scene
        GridPane moviesSceneRoot = new GridPane();
        moviesSceneRoot.setAlignment(Pos.BASELINE_CENTER);
        moviesSceneRoot.setHgap(50);
        moviesSceneRoot.setVgap(50);
        moviesSceneRoot.setId("background");

        // Initialize the Actors Scene (global data) with the Tile Pane
        moviesScene = new MoviesScene(moviesSceneRoot, WND_WIDTH, WND_HEIGHT);
        moviesScene.getStylesheets().add("sample/Main.css");

        // Create the Button for each of the other 2 main Scenes
        Button directorsButton = new Button("Directors");
        directorsButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                primaryStage.setScene(directorsScene);
            }
        });
        directorsButton.setId("topButton");


        Button actorsButton = new Button("Actors");
        actorsButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                primaryStage.setScene(actorsScene);
            }
        });
        actorsButton.setId("topButton");


        //////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        //init actor table
        ObservableList<Movie> data = queryMovieTable("SELECT * FROM movies", statement);

        // Get the Table View that displays the Actor Information
        TableView<Movie> moviesTable = getMoviesTable(data);
        FilteredList<Movie> flPerson = new FilteredList(moviesTable.getItems(), p-> true);

        moviesTable.setMinWidth(600);

        //Add query options
        ObservableList<String> options =
                FXCollections.observableArrayList("Sort movies by title in alphabetical order",
                        "Find all movies released this century",
                        "Get all comedies",
                        "Sort all movies by rating",
                        "Get all movies that are shorter than 90 minutes"
                );
        ComboBox queryMovie = new ComboBox(options);
        queryMovie.setId("queryChoiceBox");

        Button applyQueryButton = new Button("Query");
        applyQueryButton.setId("queryButton");
        applyQueryButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                switch (queryMovie.getValue().toString()){
                    case "Sort movies by title in alphabetical order":
                        ObservableList<Movie> query1 = null;
                        try {
                            query1 = queryMovieTable("SELECT * FROM movies order by title asc", statement);
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                        moviesTable.setItems(query1);
                        break;
                    case "Find all movies released this century":
                        ObservableList<Movie> query2 = null;
                        try {
                            query2 = queryMovieTable("SELECT * FROM movies where releaseDate > 1999", statement);
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                        moviesTable.setItems(query2);
                        break;
                    case "Get all comedies":
                        ObservableList<Movie> query3 = null;
                        try {
                            query3 = queryMovieTable("SELECT * FROM movies WHERE genre=\"comedy\"", statement);
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                        moviesTable.setItems(query3);
                        break;
                    case "Sort all movies by rating":
                        ObservableList<Movie> query4 = null;
                        try {
                            query4 = queryMovieTable("SELECT * FROM movies order by rating desc", statement);
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                        moviesTable.setItems(query4);
                        break;
                    case "Get all movies that are shorter than 90 minutes":
                        ObservableList<Movie> query5 = null;
                        try {
                            query5 = queryMovieTable("SELECT * FROM movies where minutes < 90", statement);
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                        moviesTable.setItems(query5);
                        break;
                }
                HBox hBox = initializeMovieSearch(moviesTable);
                movieSceneSetUp(moviesSceneRoot, moviesTable, hBox, actorsButton, directorsButton, applyQueryButton, queryMovie);
            }
        });

        HBox hBox = initializeMovieSearch(moviesTable);
        movieSceneSetUp(moviesSceneRoot, moviesTable, hBox, actorsButton, directorsButton, applyQueryButton, queryMovie);

        // Add the Buttons to the Tile Panes
        /////////////////////////////////////////////////////////
    }

    void actorSceneSetUp(GridPane actorsSceneRoot, TableView<Actor> actorsTable, HBox hBox, Button directorsButton, Button moviesButton, Button applyQueryButton, ComboBox queryActor) {
        GridPane buttonsOnTop = new GridPane();
        buttonsOnTop.setAlignment(Pos.BASELINE_CENTER);
        buttonsOnTop.setHgap(50);
        buttonsOnTop.setVgap(10);
        buttonsOnTop.add(moviesButton, 0, 1);
        buttonsOnTop.add(directorsButton, 1, 1);

        Text sceneName = new Text ("ACTORS");
        sceneName.setId("sceneName");

        GridPane restOfPage = new GridPane();
        restOfPage.setAlignment(Pos.BASELINE_CENTER);
        restOfPage.setHgap(50);
        restOfPage.add(actorsTable, 2, 2);
        restOfPage.add(hBox, 0, 2);
        restOfPage.add(queryActor, 0, 2);
        restOfPage.add(applyQueryButton, 1, 2);

        actorsSceneRoot.add(buttonsOnTop, 0, 0);
        actorsSceneRoot.add(sceneName, 0, 1);
        actorsSceneRoot.add(restOfPage, 0, 2);
        actorsSceneRoot.setHalignment(sceneName, HPos.CENTER);


    }
    void directorSceneSetUp(GridPane directorsSceneRoot, TableView<Director> directorsTable, HBox hBox, Button actorsButton, Button moviesButton, Button applyQueryButton, ComboBox queryDirector) {
        GridPane buttonsOnTop = new GridPane();
        buttonsOnTop.setAlignment(Pos.BASELINE_CENTER);
        buttonsOnTop.setHgap(50);
        buttonsOnTop.setVgap(10);
        buttonsOnTop.add(moviesButton, 0, 1);
        buttonsOnTop.add(actorsButton, 1, 1);

        Text sceneName = new Text ("DIRECTORS");
        sceneName.setId("sceneName");

        GridPane restOfPage = new GridPane();
        restOfPage.setAlignment(Pos.BASELINE_CENTER);
        restOfPage.setHgap(50);
        restOfPage.add(directorsTable, 2, 2);
        restOfPage.add(hBox, 0, 2);
        restOfPage.add(queryDirector, 0, 2);
        restOfPage.add(applyQueryButton, 1, 2);

        directorsSceneRoot.add(buttonsOnTop, 0, 0);
        directorsSceneRoot.add(sceneName, 0, 1);
        directorsSceneRoot.add(restOfPage, 0, 2);
        directorsSceneRoot.setHalignment(sceneName, HPos.CENTER);
    }
    void movieSceneSetUp(GridPane moviesSceneRoot, TableView<Movie> moviesTable, HBox hBox, Button actorsButton, Button directorsButton, Button applyQueryButton, ComboBox queryMovie) {
        GridPane buttonsOnTop = new GridPane();
        buttonsOnTop.setAlignment(Pos.BASELINE_CENTER);
        buttonsOnTop.setHgap(50);
        buttonsOnTop.setVgap(10);
        buttonsOnTop.add(actorsButton, 0, 1);
        buttonsOnTop.add(directorsButton, 1, 1);

        Text sceneName = new Text ("MOVIES");
        sceneName.setId("sceneName");

        GridPane restOfPage = new GridPane();
        restOfPage.setAlignment(Pos.BASELINE_CENTER);
        restOfPage.setHgap(50);
        restOfPage.add(moviesTable, 2, 2);
        restOfPage.add(hBox, 0, 2);
        restOfPage.add(queryMovie, 0, 2);
        restOfPage.add(applyQueryButton, 1, 2);

        moviesSceneRoot.add(buttonsOnTop, 0, 0);
        moviesSceneRoot.add(sceneName, 0, 1);
        moviesSceneRoot.add(restOfPage, 0, 2);
        moviesSceneRoot.setHalignment(sceneName, HPos.CENTER);
    }

    HBox initializeActorSearch(TableView<Actor> actorsTable) {
        FilteredList<Actor> flPerson = new FilteredList(actorsTable.getItems(), p-> true);

        ChoiceBox<String> choiceBox = new ChoiceBox();
        choiceBox.setId("choiceBox");
        choiceBox.getItems().addAll("Name", "Movie", "Role");
        choiceBox.setValue("Name");

        TextField textField = new TextField();
        textField.setPromptText("Search");
        textField.textProperty().addListener((obs, oldValue, newValue)
                -> {
            switch (choiceBox.getValue())
            {
                case "Name":
                    flPerson.setPredicate(p -> p.getName().toLowerCase().contains(newValue.toLowerCase().trim()));
                    break;
                case "Movie":
                    flPerson.setPredicate(p -> p.getMovie().toLowerCase().contains(newValue.toLowerCase().trim()));
                    break;
                case "Role":
                    flPerson.setPredicate(p -> p.getRole().toLowerCase().contains(newValue.toLowerCase().trim()));
                    break;
            }
        });

        choiceBox.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal)
                -> {
            if (newVal != null) {
                textField.setText("");
            }
        });

        HBox hBox = new HBox(choiceBox, textField);
        hBox.setAlignment(Pos.TOP_CENTER);
        actorsTable.setItems(flPerson);
        return hBox;
    }

    HBox initializeDirectorSearch(TableView<Director> directorsTable) {
        FilteredList<Director> flPerson = new FilteredList(directorsTable.getItems(), p-> true);

        ChoiceBox<String> choiceBox = new ChoiceBox();
        choiceBox.setId("choiceBox");
        choiceBox.getItems().addAll("Name", "Movie");
        choiceBox.setValue("Name");

        TextField textField = new TextField();
        textField.setPromptText("Search");
        textField.textProperty().addListener((obs, oldValue, newValue)
                -> {
            switch (choiceBox.getValue())
            {
                case "Name":
                    flPerson.setPredicate(p -> p.getName().toLowerCase().contains(newValue.toLowerCase().trim()));
                    break;
                case "Movie":
                    flPerson.setPredicate(p -> p.getMovie().toLowerCase().contains(newValue.toLowerCase().trim()));
                    break;
            }
        });

        choiceBox.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal)
                -> {
            if (newVal != null) {
                textField.setText("");
            }
        });

        HBox hBox = new HBox(choiceBox, textField);
        hBox.setAlignment(Pos.TOP_CENTER);
        directorsTable.setItems(flPerson);
        return hBox;
    }

    HBox initializeMovieSearch(TableView<Movie> moviesTable) {
        System.out.println("Initializing search bar");
        FilteredList<Movie> flPerson = new FilteredList(moviesTable.getItems(), p-> true);

        ChoiceBox<String> choiceBox = new ChoiceBox();
        choiceBox.setId("choiceBox");
        choiceBox.getItems().addAll("Title", "Release Date", "Genre", "Rating", "Minutes");
        choiceBox.setValue("Title");

        TextField textField = new TextField();
        textField.setPromptText("Search");
        textField.textProperty().addListener((obs, oldValue, newValue)
                -> {
            switch (choiceBox.getValue())
            {
                case "Title":
                    flPerson.setPredicate(p -> p.getTitle().toLowerCase().contains(newValue.toLowerCase().trim()));
                    break;
                case "Release Date":
                    flPerson.setPredicate(p -> p.getReleaseDate().toLowerCase().contains(newValue.toLowerCase().trim()));
                    break;
                case "Genre":
                    flPerson.setPredicate(p -> p.getGenre().toLowerCase().contains(newValue.toLowerCase().trim()));
                    break;
                case "Rating":
                    flPerson.setPredicate(p -> p.getRating().toString().toLowerCase().contains(newValue.toLowerCase().trim()));
                    break;
                case "Minutes":
                    flPerson.setPredicate(p -> p.getMinutes().toString().toLowerCase().contains(newValue.toLowerCase().trim()));
                    break;
            }
        });

        choiceBox.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal)
                -> {
            if (newVal != null) {
                textField.setText("");
            }
        });

        HBox hBox = new HBox(choiceBox, textField);
        hBox.setAlignment(Pos.TOP_CENTER);
        moviesTable.setItems(flPerson);
        return hBox;
    }


    // Initialize the 3 Pop Up Dialogs to be used by the Application
    void initializePopUps() {
        initializePersonsPopUp();
        initializeMoviesPopUp();
    }


    // Initialize the Actors Pop Up Dialog
    void initializePersonsPopUp() {
        // Initialize the Actors Pop Up Dialog
        personsPopUp = getNewPopUpDialog();
        personsPopUp.getDialogPane().getStylesheets().add("sample/Main.css");

        // Define the Button to Close the Dialog
        ButtonType closeButton = new ButtonType("Close", ButtonBar.ButtonData.CANCEL_CLOSE);

        // Add the newly created Widget to the Dialog
        personsPopUp.getDialogPane().getButtonTypes().add(closeButton);
    }


    // Initialize the Movies Pop Up Dialog
    void initializeMoviesPopUp() {
        // Initialize the Movies Pop Up Dialog
        moviesPopUp = getNewPopUpDialog();
        moviesPopUp.getDialogPane().getStylesheets().add("sample/Main.css");

        // Define the Button to Close the Dialog
        ButtonType closeButton = new ButtonType("Close", ButtonBar.ButtonData.CANCEL_CLOSE);

        // Add the newly created Widget to the Dialog
        moviesPopUp.getDialogPane().getButtonTypes().add(closeButton);
    }


    // Update the Actors Pop Up Dialog to hold information about a specific Actor
    void updatePersonsPopUp(Person person, Statement statement) throws ParseException, SQLException {
        // Create a Tile Pane to store the needed Widgets
        GridPane personPane = new GridPane();

        Text personName = getNewPopUpText(person.getName());
        personName.setId("actorName");
        personName.setTextAlignment(TextAlignment.CENTER);

        // Get the new Text to display for the Person
        Text personText = getNewPopUpText(person.toString());
        personText.setId("personInformation");

        Text actedIn = getNewPopUpText("Has acted in:");
        actedIn.setId("listOfMovies");

        // Get the Tile Pane to store the Actor Movie Buttons
        TilePane movieActorPane = getMovieActorPane(POPUP_WIDTH, statement, person);

        Text directed = getNewPopUpText("Has directed:");
        directed.setId("listOfMovies");

        // Get the Tile Pane to store the Director Movie Buttons
        TilePane movieDirectorPane = getMovieDirectorPane(POPUP_WIDTH, statement, person);

        // Update the Pop Up dialog to display this new Text
        personPane.add(personName, 0, 0);
        personPane.add(personText, 0, 1);
        personPane.add(actedIn, 0, 2);
        personPane.add(movieActorPane, 0, 3);
        personPane.add(directed, 0, 4);
        personPane.add(movieDirectorPane, 0, 5);
        personsPopUp.getDialogPane().setContent(personPane);
    }


    // Update the Movies Pop Up Dialog to hold information about a specific Movie
    void updateMoviesPopUp(Movie movie, Statement statement) throws ParseException, SQLException {
        // Create a Tile Pane to store the needed Widgets
        GridPane moviePane = new GridPane();

        Text movieName = getNewPopUpText(movie.getTitle());
        movieName.setId("actorName");
        movieName.setTextAlignment(TextAlignment.CENTER);

        // Get the new Text to display for the Movie
        Text movieText = getNewPopUpText(movie.toString());
        movieText.setId("personInformation");

        Text actors = getNewPopUpText("Had the following actors:");
        actors.setId("listOfMovies");

        // Get the Tile Pane to store the Movie Actor Buttons
        TilePane actorMoviePane = getActorMoviePane(POPUP_WIDTH, statement, movie);

        Text directors = getNewPopUpText("Had the following directors:");
        directors.setId("listOfMovies");

        // Get the Tile Pane to store the Movie Director Buttons
        TilePane directorMoviePane = getDirectorMoviePane(POPUP_WIDTH, statement, movie);

        // Update the Pop Up dialog to display this new Text
        moviePane.add(movieName, 0, 0);
        moviePane.add(movieText, 0, 1);
        moviePane.add(actors, 0, 2);
        moviePane.add(actorMoviePane, 0, 3);
        moviePane.add(directors, 0, 4);
        moviePane.add(directorMoviePane, 0, 5);
        moviesPopUp.getDialogPane().setContent(moviePane);
    }


    // This method returns a new Dialog Object to be used as a Pop Up dialog
    Dialog<String> getNewPopUpDialog() {
        // Create a new Dialog object with the desired properties
        Dialog<String> dialog = new Dialog<String>();
        dialog.setWidth(POPUP_WIDTH);
        dialog.setHeight(POPUP_HEIGHT);

        // Return the new Dialog
        return dialog;
    }


    // This method returns a new Text Object to be used to display the main information in the Pop Up dialogs
    Text getNewPopUpText(String text) {
        // Create a new Text object with the desired properties
        Text newText = new Text(text);
        newText.setWrappingWidth(POPUP_WIDTH);

        // Return the new Text
        return newText;
    }


    // This method returns a Table View with the default Actors information
    TableView<Actor> getActorsTable(ObservableList<Actor> data) {
        TableView<Actor> table = new TableView<>();
        table.setEditable(true);

        TableColumn nameCol = new TableColumn("Name");
        nameCol.setMinWidth(100);
        nameCol.setCellValueFactory(new PropertyValueFactory<Actor, String>("name"));

        TableColumn movieTitleCol = new TableColumn("Movie");
        movieTitleCol.setMinWidth(80);
        movieTitleCol.setCellValueFactory(new PropertyValueFactory<Actor, String>("movieTitle"));

        TableColumn roleCol = new TableColumn("Role");
        roleCol.setMinWidth(80);
        roleCol.setCellValueFactory(new PropertyValueFactory<Actor, String>("role"));

        TableColumn billingPosCol = new TableColumn("Billing Position");
        roleCol.setMinWidth(80);
        billingPosCol.setCellValueFactory(new PropertyValueFactory<Actor, String>("billingPos"));

        ////////////////////////////////////////////////////////////////////////////
        TableColumn<Actor, Button> detailsCol = new TableColumn("Details");
        detailsCol.setMinWidth(45);
        detailsCol.setCellValueFactory(new PropertyValueFactory<>("viewButton"));
        ////////////////////////////////////////////////////////////////////////////

        table.setItems(data);
        //table.getColumns().addAll(firstNameCol, lastNameCol, emailCol, birthplaceCol);
        table.getColumns().addAll(nameCol, movieTitleCol, roleCol, billingPosCol, detailsCol);

        return table;
    }


    TableView<Director> getDirectorsTable(ObservableList<Director> data) {
        TableView<Director> table = new TableView<>();
        table.setEditable(true);

        TableColumn nameCol = new TableColumn("Name");
        nameCol.setMinWidth(100);
        nameCol.setCellValueFactory(new PropertyValueFactory<Actor, String>("name"));

        TableColumn movieTitleCol = new TableColumn("Movie");
        movieTitleCol.setMinWidth(80);
        movieTitleCol.setCellValueFactory(new PropertyValueFactory<Actor, String>("movieTitle"));

        TableColumn billingPosCol = new TableColumn("Billing Position");
        billingPosCol.setMinWidth(80);
        billingPosCol.setCellValueFactory(new PropertyValueFactory<Actor, String>("billingPos"));

        ////////////////////////////////////////////////////////////////////////////
        TableColumn<Director, Button> detailsCol = new TableColumn("Details");
        detailsCol.setMinWidth(45);
        detailsCol.setCellValueFactory(new PropertyValueFactory<>("viewButton"));
        ////////////////////////////////////////////////////////////////////////////

        table.setItems(data);
        //table.getColumns().addAll(firstNameCol, lastNameCol, emailCol, birthplaceCol);
        table.getColumns().addAll(nameCol, movieTitleCol, billingPosCol, detailsCol);

        return table;
    }

    TableView<Movie> getMoviesTable(ObservableList<Movie> data) {
        TableView<Movie> table = new TableView<>();
        table.setEditable(true);

        TableColumn titleCol = new TableColumn("Title");
        titleCol.setMinWidth(100);
        titleCol.setCellValueFactory(new PropertyValueFactory<Movie, String>("title"));

        TableColumn releaseDateCol = new TableColumn("Release Date");
        releaseDateCol.setMinWidth(80);
        releaseDateCol.setCellValueFactory(new PropertyValueFactory<Movie, String>("releaseDate"));

        TableColumn genreCol = new TableColumn("Genre");
        genreCol.setMinWidth(80);
        genreCol.setCellValueFactory(new PropertyValueFactory<Movie, String>("genre"));

        TableColumn ratingCol = new TableColumn("Rating");
        ratingCol.setMinWidth(80);
        ratingCol.setCellValueFactory(new PropertyValueFactory<Movie, String>("rating"));

        TableColumn minutesCol = new TableColumn("Minutes");
        minutesCol.setMinWidth(80);
        minutesCol.setCellValueFactory(new PropertyValueFactory<Movie, String>("minutes"));

        ////////////////////////////////////////////////////////////////////////////
        TableColumn<Movie, Button> detailsCol = new TableColumn("Details");
        detailsCol.setMinWidth(45);
        detailsCol.setCellValueFactory(new PropertyValueFactory<>("viewButton"));
        ////////////////////////////////////////////////////////////////////////////

        table.setItems(data);
        //table.getColumns().addAll(firstNameCol, lastNameCol, emailCol, birthplaceCol);
        table.getColumns().addAll(titleCol, releaseDateCol, genreCol, ratingCol, minutesCol, detailsCol);

        return table;
    }


    // Get a Person object for a given pid
    Person getPersonWithPID(int pid, Statement statement) throws SQLException {
        ResultSet rs = statement.executeQuery("SELECT * FROM persons WHERE pid = " + pid);
        rs.next();
        Person person = new Person(rs.getInt("pid"), rs.getString("name"),
                rs.getString("birthdate"), rs.getString("birthplace"),this, statement);
        return person;
    }


    // Get a Movie object for a given mid
    Movie getMovieWithMID(int mid, Statement statement) throws SQLException {
        ResultSet rs = statement.executeQuery("SELECT * FROM movies WHERE mid = " + mid);
        rs.next();
        Movie movie = new Movie(rs.getInt("mid"), rs.getString("title"), rs.getString("releaseDate"),
                rs.getString("genre"), rs.getFloat("rating"), rs.getInt("minutes"), this, statement);
        return movie;
    }


    // Get a Tile Pane to represent the Movies that this Person has been an Actor in
    public TilePane getMovieActorPane(int dlgWidth, Statement statement, Person person) throws SQLException {
        TilePane moviePane = new TilePane();

        ResultSet rs = statement.executeQuery("SELECT * FROM actors WHERE pid = " + person.pid);
        ArrayList<Integer> mids = new ArrayList<Integer>();
        while(rs.next()) {
            mids.add(rs.getInt("mid"));

        }

        for (Integer mid : mids) {
            rs = statement.executeQuery("SELECT * FROM movies WHERE mid = " + mid);
            rs.next();
            String movieTitle = rs.getString("title");
            Button tempBtn = new Button(movieTitle);
            tempBtn.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent actionEvent) {
                    try {
                        updateMoviesPopUp(getMovieWithMID(mid, statement), statement);
                    } catch (ParseException | SQLException e) {
                        e.printStackTrace();
                    }
                    moviesPopUp.show();
                    personsPopUp.close();
                }
            });
            moviePane.getChildren().add(tempBtn);
        }
        return moviePane;
    }


    // Get a Tile Pane to represent the Moives that this Person has been an Director in
    public TilePane getMovieDirectorPane(int dlgWidth, Statement statement, Person person) throws SQLException {
        TilePane moviePane = new TilePane();

        ResultSet rs = statement.executeQuery("SELECT * FROM directors WHERE pid = " + person.pid);
        ArrayList<Integer> mids = new ArrayList<Integer>();
        while(rs.next()) {
            mids.add(rs.getInt("mid"));
        }

        for (Integer mid : mids) {
            rs = statement.executeQuery("SELECT * FROM movies WHERE mid = " + mid);
            rs.next();
            String movieTitle = rs.getString("title");
            Button tempBtn = new Button(movieTitle);
            tempBtn.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent actionEvent) {
                    try {
                        updateMoviesPopUp(getMovieWithMID(mid, statement), statement);
                    } catch (ParseException | SQLException e) {
                        e.printStackTrace();
                    }
                    moviesPopUp.show();
                    personsPopUp.close();
                }
            });
            moviePane.getChildren().add(tempBtn);
        }
        return moviePane;
    }


    // Get a Tile Pane to represent the Actors that this Movie had
    public TilePane getActorMoviePane(int dlgWidth, Statement statement, Movie movie) throws SQLException {
        TilePane actorPane = new TilePane();

        ResultSet rs = statement.executeQuery("SELECT * FROM actors WHERE mid = " + movie.mid);
        ArrayList<Integer> pids = new ArrayList<Integer>();
        while(rs.next()) {
            pids.add(rs.getInt("pid"));

        }

        for (Integer pid : pids) {
            rs = statement.executeQuery("SELECT * FROM persons WHERE pid = " + pid);
            rs.next();
            String personName = rs.getString("name");
            Button tempBtn = new Button(personName);
            tempBtn.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent actionEvent) {
                    try {
                        updatePersonsPopUp(getPersonWithPID(pid, statement), statement);
                    } catch (ParseException | SQLException e) {
                        e.printStackTrace();
                    }
                    personsPopUp.show();
                    moviesPopUp.close();
                }
            });
            actorPane.getChildren().add(tempBtn);
        }

        return actorPane;
    }


    // Get a Tile Pane to represent the Directors that this Movie had
    public TilePane getDirectorMoviePane(int dlgWidth, Statement statement, Movie movie) throws SQLException {
        TilePane directorPane = new TilePane();

        ResultSet rs = statement.executeQuery("SELECT * FROM directors WHERE mid = " + movie.mid);
        ArrayList<Integer> pids = new ArrayList<Integer>();
        while(rs.next()) {
            pids.add(rs.getInt("pid"));

        }

        for (Integer pid : pids) {
            rs = statement.executeQuery("SELECT * FROM persons WHERE pid = " + pid);
            rs.next();
            String personName = rs.getString("name");
            Button tempBtn = new Button(personName);
            tempBtn.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent actionEvent) {
                    try {
                        updatePersonsPopUp(getPersonWithPID(pid, statement), statement);
                    } catch (ParseException | SQLException e) {
                        e.printStackTrace();
                    }
                    personsPopUp.show();
                    moviesPopUp.close();
                }
            });
            directorPane.getChildren().add(tempBtn);
        }

        return directorPane;
    }


    //
    public Button makeViewButton(Person person, Statement statement) {
        Button button = new Button("View");
        button.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                try {
                    updatePersonsPopUp(person, statement);
                } catch (SQLException | ParseException e) {
                    e.printStackTrace();
                }
                personsPopUp.show();
            }
        });

        return button;
    }

    public Button makeViewButton(Movie movie, Statement statement) {
        Button button = new Button("View");
        button.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                try {
                    updateMoviesPopUp(movie, statement);
                } catch (SQLException | ParseException e) {
                    e.printStackTrace();
                }
                moviesPopUp.show();
            }
        });

        return button;
    }
}
