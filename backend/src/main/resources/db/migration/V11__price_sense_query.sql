-- PatrickBike PriceSense-style audit log (competitive sell-price recommendation).
CREATE TABLE price_sense_query (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    queried_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    brand VARCHAR(100) NOT NULL,
    model VARCHAR(100) NOT NULL,
    model_year INT,
    condition_grade CHAR(1) NOT NULL,
    buyin_cost_chf NUMERIC(12, 2) NOT NULL,
    n_ch INT NOT NULL DEFAULT 0,
    n_de INT NOT NULL DEFAULT 0,
    p_median_chf NUMERIC(12, 2),
    p_p25_chf NUMERIC(12, 2),
    p_p75_chf NUMERIC(12, 2),
    p_target_raw_chf NUMERIC(12, 2),
    p_floor_chf NUMERIC(12, 2) NOT NULL,
    p_recommend_chf NUMERIC(12, 2),
    gross_margin_pct NUMERIC(6, 2),
    fallback_used BOOLEAN NOT NULL DEFAULT FALSE,
    f_ch_applied NUMERIC(6, 3),
    eur_chf_rate NUMERIC(10, 6),
    confidence VARCHAR(16),
    margin_conflict BOOLEAN NOT NULL DEFAULT FALSE,
    notes TEXT
);

CREATE INDEX idx_price_sense_query_queried ON price_sense_query(queried_at DESC);
