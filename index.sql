CREATE INDEX index_movie_ratings
ON ratings (rating DESC, movieId);

CREATE INDEX index_movie_genres
ON genres_in_movies (movieId, genreId);

CREATE INDEX index_movie_stars
ON stars_in_movies (movieId, starId);