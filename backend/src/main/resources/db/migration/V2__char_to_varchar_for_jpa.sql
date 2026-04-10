-- Hibernate maps String to VARCHAR; PostgreSQL CHAR becomes bpchar and fails validation.
ALTER TABLE bike_offer ALTER COLUMN currency_code TYPE VARCHAR(3);
ALTER TABLE source ALTER COLUMN country_code TYPE VARCHAR(2);
