ALTER TABLE movies
ADD COLUMN price DECIMAL(10, 2) NOT NULL;

UPDATE movies
SET price =
        FLOOR(1 + RAND() * 30) +
        ELT(FLOOR(1 + RAND() * 3), 0.99, 0.49, 0.00);