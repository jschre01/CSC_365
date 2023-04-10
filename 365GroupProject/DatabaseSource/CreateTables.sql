# Select the Database to use
USE arwj93;
#################################################################################################################
# Create the movies table
CREATE TABLE movies (
	mid INTEGER PRIMARY KEY,
	title VARCHAR(100) NOT NULL,
	releaseDate INTEGER NOT NULL,
	genre VARCHAR(100),
	rating FLOAT CHECK (RTRating >= 0 AND RTRating <= 10),
	minutes INTEGER NOT NULL
);
#################################################################################################################
# Create the persons table
CREATE TABLE persons (
	pid INTEGER PRIMARY KEY,
	name VARCHAR(100) NOT NULL,
	birthdate DATE NOT NULL,
	birthplace VARCHAR(150) NOT NULL
);
#################################################################################################################
# Create the actors table
CREATE TABLE actors (
	pid INTEGER NOT NULL,
	mid INTEGER NOT NULL,
	role VARCHAR (100) NOT NULL,
    billingPos INTEGER NOT NULL,
	PRIMARY KEY (mid, pid),
	FOREIGN KEY (mid) REFERENCES movies(mid) ON DELETE CASCADE,
	FOREIGN KEY (pid) REFERENCES persons(pid) ON DELETE CASCADE
);
#################################################################################################################
# Create the directors table
CREATE TABLE directors (
	pid INTEGER NOT NULL,
	mid INTEGER NOT NULL,
	billingPos INTEGER NOT NULL,
	PRIMARY KEY (mid, pid),
	FOREIGN KEY (mid) REFERENCES movies(mid) ON DELETE CASCADE,
	FOREIGN KEY (pid) REFERENCES persons(pid) ON DELETE CASCADE
);
