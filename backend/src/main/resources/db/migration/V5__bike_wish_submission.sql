CREATE TABLE bike_wish_submission (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    submission_source VARCHAR(32) NOT NULL
        CHECK (submission_source IN ('WEB_MANUAL', 'XML_IMPORT')),
    contact_email VARCHAR(320) NOT NULL,
    contact_name VARCHAR(200),
    contact_phone VARCHAR(50),
    payload_json JSONB NOT NULL,
    raw_xml_import TEXT
);

CREATE INDEX idx_bike_wish_submission_created ON bike_wish_submission(created_at DESC);
CREATE INDEX idx_bike_wish_submission_email ON bike_wish_submission(contact_email);
