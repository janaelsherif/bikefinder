-- Hibernate maps String length=1 to VARCHAR; CHAR(1) becomes bpchar and fails validation.
ALTER TABLE price_sense_query
    ALTER COLUMN condition_grade TYPE VARCHAR(1) USING trim(condition_grade::text);
