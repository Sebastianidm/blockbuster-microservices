CREATE TABLE categories (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    description VARCHAR(255)
);

CREATE TABLE movies (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(150) NOT NULL,
    category_id BIGINT NOT NULL,
    release_year INT NOT NULL,
    stock INT NOT NULL DEFAULT 0,
    available BOOLEAN NOT NULL DEFAULT TRUE,
    CONSTRAINT fk_movies_category FOREIGN KEY (category_id) REFERENCES categories(id)
);
