-- Se añade columna observaciones.
ALTER TABLE rentals
ADD COLUMN observation VARCHAR(255) DEFAULT 'Sin observaciones';