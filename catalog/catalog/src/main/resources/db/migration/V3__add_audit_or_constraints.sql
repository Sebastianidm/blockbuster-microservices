ALTER TABLE categories
    ADD CONSTRAINT chk_categories_name_not_blank CHECK (char_length(trim(name)) > 0);

ALTER TABLE movies
    ADD CONSTRAINT chk_movies_title_not_blank CHECK (char_length(trim(title)) > 0);

ALTER TABLE movies
    ADD CONSTRAINT chk_movies_stock_non_negative CHECK (stock >= 0);

ALTER TABLE movies
    ADD CONSTRAINT chk_movies_release_year_range CHECK (release_year BETWEEN 1900 AND 2100);
