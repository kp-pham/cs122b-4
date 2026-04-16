DELIMITER $$

CREATE PROCEDURE add_movie (
    IN title VARCHAR(100),
    IN year INTEGER,
    IN director VARCHAR(100),
    IN star_name VARCHAR(100),
    IN genre_name VARCHAR(32),
)
BEGIN
    DECLARE star_id VARCHAR(10);

    IF NOT EXISTS (SELECT 1 FROM stars WHERE name = star_name) THEN
       SET star_id = CALL get_next_star_id();
       INSERT INTO stars (id, name, birthYear) VALUES ( star_id, star_name, NULL);
    END IF;
--     IF star_name IS NOT NULL THEN
--         IF EXISTS (SELECT 1 FROM stars WHERE name = star_name) THEN
--
--         ELSE
--
--         END IF;
--     END IF;
END

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