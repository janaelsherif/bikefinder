CREATE EXTENSION IF NOT EXISTS vector;

CREATE TABLE bf_user (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    email VARCHAR(320) NOT NULL UNIQUE,
    preferred_lang VARCHAR(10) NOT NULL DEFAULT 'de-CH',
    region_ch VARCHAR(32),
    notification_prefs JSONB,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE source (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(100) NOT NULL,
    country_code CHAR(2) NOT NULL,
    type VARCHAR(32) NOT NULL CHECK (type IN ('refurbisher','classifieds','dealer_marketplace','oem_outlet')),
    base_url TEXT NOT NULL,
    crawl_enabled BOOLEAN NOT NULL DEFAULT TRUE,
    refresh_interval_min INT NOT NULL DEFAULT 240,
    robots_compliant BOOLEAN NOT NULL DEFAULT TRUE,
    last_crawl_at TIMESTAMPTZ,
    last_crawl_status VARCHAR(16) CHECK (last_crawl_status IS NULL OR last_crawl_status IN ('success','partial','failed')),
    avg_offers_per_crawl INT,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE extraction_template (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    source_id UUID NOT NULL UNIQUE REFERENCES source(id) ON DELETE CASCADE,
    listing_url_pattern TEXT,
    selectors_json JSONB NOT NULL DEFAULT '{}',
    llm_prompt_override TEXT,
    last_validated_at TIMESTAMPTZ,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE raw_offer (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    source_id UUID NOT NULL REFERENCES source(id) ON DELETE CASCADE,
    url TEXT NOT NULL,
    raw_body TEXT NOT NULL,
    extracted BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE fx_rate (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    currency_pair VARCHAR(16) NOT NULL,
    rate DECIMAL(18, 8) NOT NULL,
    effective_date DATE NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE (currency_pair, effective_date)
);

CREATE TABLE swiss_price_reference (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    brand VARCHAR(100) NOT NULL,
    bike_category VARCHAR(32) NOT NULL CHECK (bike_category IN ('city','trekking','cargo','mtb','road','gravel','kids')),
    spec_tier VARCHAR(16) NOT NULL CHECK (spec_tier IN ('entry','mid','premium')),
    median_chf DECIMAL(12, 2) NOT NULL,
    p25_chf DECIMAL(12, 2),
    p75_chf DECIMAL(12, 2),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE (brand, bike_category, spec_tier)
);

CREATE TABLE bike_offer (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    source_id UUID NOT NULL REFERENCES source(id) ON DELETE CASCADE,
    source_offer_id VARCHAR(200) NOT NULL,
    source_url TEXT NOT NULL,
    status VARCHAR(16) NOT NULL DEFAULT 'active' CHECK (status IN ('active','sold','expired','flagged')),
    brand VARCHAR(100),
    model VARCHAR(200),
    model_line VARCHAR(100),
    bike_category VARCHAR(32) CHECK (bike_category IS NULL OR bike_category IN ('city','trekking','cargo','mtb','road','gravel','kids')),
    frame_type VARCHAR(16) CHECK (frame_type IS NULL OR frame_type IN ('diamond','low_step','mixte','folding')),
    frame_size VARCHAR(20),
    wheel_size_inch DECIMAL(4,1),
    drivetrain_type VARCHAR(16) CHECK (drivetrain_type IS NULL OR drivetrain_type IN ('chain','belt')),
    gears_count INT,
    groupset VARCHAR(100),
    motor_brand VARCHAR(100),
    motor_model VARCHAR(100),
    motor_position VARCHAR(16) CHECK (motor_position IS NULL OR motor_position IN ('mid','rear','front')),
    motor_power_w INT,
    battery_wh INT,
    battery_cycles INT,
    range_estimate_km INT,
    model_year INT,
    mileage_km INT,
    bike_condition VARCHAR(32) NOT NULL CHECK (bike_condition IN ('new','like_new','refurbished','used')),
    refurbisher_name VARCHAR(100),
    warranty_type VARCHAR(32) NOT NULL DEFAULT 'none' CHECK (warranty_type IN ('none','seller','manufacturer','certified_refurb')),
    warranty_months INT,
    list_price_local DECIMAL(10,2),
    currency_code CHAR(3) NOT NULL DEFAULT 'EUR',
    fees_local DECIMAL(10,2),
    shipping_cost_local DECIMAL(10,2),
    total_price_local DECIMAL(10,2),
    price_chf DECIMAL(10,2),
    shipping_estimate_chf DECIMAL(10,2),
    import_surcharge_chf DECIMAL(10,2),
    landed_price_chf DECIMAL(10,2),
    swiss_median_price_chf DECIMAL(10,2),
    discount_vs_swiss_pct DECIMAL(5,2),
    is_bargain BOOLEAN NOT NULL DEFAULT FALSE,
    quality_score DECIMAL(3,1),
    embedding vector(1536),
    description_raw TEXT,
    spec_raw_json JSONB,
    images TEXT[],
    first_seen_at TIMESTAMPTZ,
    last_seen_at TIMESTAMPTZ,
    extraction_method VARCHAR(16) NOT NULL DEFAULT 'heuristic' CHECK (extraction_method IN ('heuristic','llm','manual')),
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE (source_id, source_offer_id)
);

CREATE INDEX idx_bike_offer_source_status ON bike_offer(source_id, status);
CREATE INDEX idx_bike_offer_landed ON bike_offer(landed_price_chf);
CREATE INDEX idx_bike_offer_discount ON bike_offer(discount_vs_swiss_pct DESC);

CREATE TABLE price_snapshot (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    bike_offer_id UUID NOT NULL REFERENCES bike_offer(id) ON DELETE CASCADE,
    snapshot_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    list_price_local DECIMAL(10,2),
    landed_price_chf DECIMAL(10,2)
);

CREATE INDEX idx_price_snapshot_offer ON price_snapshot(bike_offer_id, snapshot_at DESC);

CREATE TABLE saved_search (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    bf_user_id UUID NOT NULL REFERENCES bf_user(id) ON DELETE CASCADE,
    name VARCHAR(200) NOT NULL,
    filter_json JSONB NOT NULL,
    alert_channels JSONB,
    min_discount_pct DECIMAL(5,2),
    last_alert_sent_at TIMESTAMPTZ,
    last_alert_check_at TIMESTAMPTZ,
    notification_frequency VARCHAR(32) DEFAULT 'daily',
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE alert_event (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    saved_search_id UUID NOT NULL REFERENCES saved_search(id) ON DELETE CASCADE,
    bike_offer_id UUID NOT NULL REFERENCES bike_offer(id) ON DELETE CASCADE,
    channel VARCHAR(32) NOT NULL,
    dispatched_at TIMESTAMPTZ,
    status VARCHAR(32) NOT NULL DEFAULT 'pending',
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE (saved_search_id, bike_offer_id, channel)
);
