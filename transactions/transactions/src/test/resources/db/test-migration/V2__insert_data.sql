INSERT INTO rentals (user_id, rental_date, return_date, status, total_amount)
VALUES
    (901, CURRENT_TIMESTAMP, DATEADD('DAY', 3, CURRENT_TIMESTAMP), 'ACTIVE', 5000.00),
    (902, DATEADD('DAY', -2, CURRENT_TIMESTAMP), DATEADD('DAY', 1, CURRENT_TIMESTAMP), 'ACTIVE', 2500.00),
    (903, DATEADD('DAY', -5, CURRENT_TIMESTAMP), DATEADD('DAY', -2, CURRENT_TIMESTAMP), 'RETURNED', 7500.00);

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
