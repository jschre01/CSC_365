# Select the Database to use
USE arwj93;
#################################################################################################################
# View the movies table
SELECT *
FROM movies;

# View the persons table
SELECT *
FROM persons;

# View the actors table
SELECT *
FROM actors;

# View the directors table
SELECT *
FROM directors;

# View the name of each movie
SELECT title
FROM movies;

# View the name of each director alongside the movie they directed
SELECT DISTINCT name, title
FROM persons NATURAL JOIN directors NATURAL JOIN movies;

# View the name of each actor alongside the movie they acted in and their role
SELECT DISTINCT name, title, role
FROM persons NATURAL JOIN actors NATURAL JOIN movies
ORDER BY name ASC;
