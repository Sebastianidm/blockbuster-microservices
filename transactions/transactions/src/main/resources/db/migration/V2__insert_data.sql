-- Arriendos
INSERT INTO rentals (user_id, rental_date, return_date, status, total_amount)
VALUES
    (901, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP + INTERVAL '3 days', 'ACTIVE', 5000.00),
    (902, CURRENT_TIMESTAMP - INTERVAL '2 days', CURRENT_TIMESTAMP + INTERVAL '1 day', 'ACTIVE', 2500.00),
    (903, CURRENT_TIMESTAMP - INTERVAL '5 days', CURRENT_TIMESTAMP - INTERVAL '2 days', 'RETURNED', 7500.00);

-- Detalle arriendos
INSERT INTO rental_details (rental_id, movie_id, quantity, price_at_moment)
SELECT id, 42, 1, 2500.00 FROM rentals WHERE user_id = 901;
INSERT INTO rental_details (rental_id, movie_id, quantity, price_at_moment)
SELECT id, 12, 1, 2500.00 FROM rentals WHERE user_id = 901;
INSERT INTO rental_details (rental_id, movie_id, quantity, price_at_moment)
SELECT id, 88, 1, 2500.00 FROM rentals WHERE user_id = 902;
INSERT INTO rental_details (rental_id, movie_id, quantity, price_at_moment)
SELECT id, 15, 2, 2500.00 FROM rentals WHERE user_id = 903;
INSERT INTO rental_details (rental_id, movie_id, quantity, price_at_moment)
SELECT id, 99, 1, 2500.00 FROM rentals WHERE user_id = 903;