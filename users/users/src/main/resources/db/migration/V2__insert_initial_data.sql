INSERT INTO roles (name) VALUES
    ('ROLE_USER'),
    ('ROLE_EMPLOYEE'),
    ('ROLE_ADMIN');

INSERT INTO users (username, email, password, role_id)
VALUES (
    'admin',
    'admin@blockbuster.com',
    '$2a$10$YUgU0/JqvvjF66JjKyUpEOTdygN8S./FDDhDHcUevBN3InHq5vl0y',
    (SELECT id FROM roles WHERE name = 'ROLE_ADMIN')
);
