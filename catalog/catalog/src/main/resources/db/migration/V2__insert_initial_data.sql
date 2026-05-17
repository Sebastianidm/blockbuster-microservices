INSERT INTO categories (name, description) VALUES
    ('Action', 'Peliculas de alto impacto, persecuciones y enfrentamientos intensos'),
    ('Comedy', 'Peliculas orientadas al humor, situaciones absurdas y entretencion ligera'),
    ('Sci-Fi', 'Historias futuristas con tecnologia, viajes en el tiempo o universos alternos'),
    ('Drama', 'Relatos centrados en conflictos humanos, emociones y decisiones complejas'),
    ('Horror', 'Peliculas de suspenso, tension psicologica o terror sobrenatural'),
    ('Family', 'Peliculas pensadas para compartir en familia con tono accesible');

INSERT INTO movies (title, category_id, release_year, stock, available) VALUES
    ('The Matrix', (SELECT id FROM categories WHERE name = 'Sci-Fi'), 1999, 4, TRUE),
    ('Back to the Future', (SELECT id FROM categories WHERE name = 'Sci-Fi'), 1985, 5, TRUE),
    ('Interstellar', (SELECT id FROM categories WHERE name = 'Sci-Fi'), 2014, 3, TRUE),
    ('Mad Max: Fury Road', (SELECT id FROM categories WHERE name = 'Action'), 2015, 3, TRUE),
    ('Rush Hour', (SELECT id FROM categories WHERE name = 'Action'), 1998, 2, TRUE),
    ('John Wick', (SELECT id FROM categories WHERE name = 'Action'), 2014, 1, TRUE),
    ('The Mask', (SELECT id FROM categories WHERE name = 'Comedy'), 1994, 2, TRUE),
    ('Home Alone', (SELECT id FROM categories WHERE name = 'Comedy'), 1990, 4, TRUE),
    ('The Pursuit of Happyness', (SELECT id FROM categories WHERE name = 'Drama'), 2006, 2, TRUE),
    ('Titanic', (SELECT id FROM categories WHERE name = 'Drama'), 1997, 1, TRUE),
    ('The Conjuring', (SELECT id FROM categories WHERE name = 'Horror'), 2013, 2, TRUE),
    ('Coraline', (SELECT id FROM categories WHERE name = 'Family'), 2009, 3, TRUE),
    ('Toy Story', (SELECT id FROM categories WHERE name = 'Family'), 1995, 6, TRUE),
    ('A Quiet Place', (SELECT id FROM categories WHERE name = 'Horror'), 2018, 0, FALSE);
