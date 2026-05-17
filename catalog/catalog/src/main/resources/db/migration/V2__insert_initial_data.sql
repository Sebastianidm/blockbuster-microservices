INSERT INTO categories (name, description) VALUES
    ('Action', 'Películas con ritmo alto, persecuciones y escenas intensas'),
    ('Comedy', 'Películas enfocadas en el humor y el entretenimiento'),
    ('Sci-Fi', 'Historias futuristas con ciencia y tecnología');

INSERT INTO movies (title, category_id, release_year, stock, available) VALUES
    ('The Matrix', (SELECT id FROM categories WHERE name = 'Sci-Fi'), 1999, 4, TRUE),
    ('Mad Max: Fury Road', (SELECT id FROM categories WHERE name = 'Action'), 2015, 3, TRUE),
    ('Back to the Future', (SELECT id FROM categories WHERE name = 'Sci-Fi'), 1985, 5, TRUE),
    ('The Mask', (SELECT id FROM categories WHERE name = 'Comedy'), 1994, 2, TRUE),
    ('Rush Hour', (SELECT id FROM categories WHERE name = 'Action'), 1998, 1, TRUE);
