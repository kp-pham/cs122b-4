DELIMITER $$

CREATE PROCEDURE add_movie (
    IN title VARCHAR(100),
    IN year INTEGER,
    IN director VARCHAR(100),
    IN star_name VARCHAR(100),
    IN genre_name VARCHAR(32)
)
BEGIN
    DECLARE movie_id VARCHAR(10);
    DECLARE star_id VARCHAR(10);
    DECLARE genre_id INTEGER;

    DECLARE EXIT HANDLER FOR SQLEXCEPTION
    BEGIN
        ROLLBACK;
        SELECT "Error occurred. Transaction rolled back." AS message;
    END;

    START TRANSACTION;

    CALL get_next_movie_id(movie_id);

    INSERT INTO movies (id, title, year, director) VALUES (movie_id, title, year, director);

IF star_name IS NOT NULL THEN
        SET star_id = NULL;
        SELECT id INTO star_id FROM stars WHERE name = star_name LIMIT 1;

        IF star_id IS NULL THEN
           CALL get_next_star_id(star_id);
           INSERT INTO stars (id, name, birthYear) VALUES ( star_id, star_name, NULL);
        END IF;

        INSERT INTO stars_in_movies(starId, movieId) VALUES (star_id, movie_id);
    END IF;

    IF genre_name IS NOT NULL THEN
        SET genre_id = NULL;
        SELECT id INTO genre_id FROM genres WHERE name = genre_name LIMIT 1;

        IF genre_id IS NULL THEN
           CALL get_next_genre_id(genre_id);
           INSERT INTO genres (id, name) VALUES (genre_id, genre_name);
        END IF;

        INSERT INTO genres_in_movies (genreId, movieId) VALUES (genre_id, movie_id);
    END IF;

    COMMIT;
END;

CREATE PROCEDURE get_next_movie_id(OUT movie_id VARCHAR(10))
BEGIN
    DECLARE id VARCHAR(10);
    DECLARE prefix VARCHAR(10);
    DECLARE number VARCHAR(10);

    SELECT MAX(id) INTO id FROM movies;

    SET prefix = REGEXP_REPLACE(id, [0-9], "");
    SET number = REGEXP_REPLACE(id, [a-zA-z], "");

    SET movie_id = CONCAT(prefix, number + 1);
END;

CREATE PROCEDURE get_next_star_id(OUT star_id VARCHAR(10))
BEGIN
    DECLARE id VARCHAR(10);
    DECLARE prefix VARCHAR(10);
    DECLARE number INTEGER;

    SELECT MAX(id) INTO id FROM stars;

    SET prefix = REGEXP_REPLACE(id, [0-9], "");
    SET number = REGEXP_REPLACE(id, [a-zA-z], "");

    SET star_id = CONCAT(prefix, number + 1);
END;

CREATE PROCEDURE get_next_genre_id(OUT genre_id INTEGER)
BEGIN
    DECLARE id INTEGER;

    SELECT MAX(id) INTO id FROM genres;

    SET genre_id = id + 1;
END;

$$

DELIMITER ;