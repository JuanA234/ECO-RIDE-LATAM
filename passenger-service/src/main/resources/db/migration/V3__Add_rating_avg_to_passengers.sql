CREATE TABLE ratings (
    id BIGSERIAL PRIMARY KEY,
    trip_id BIGINT NOT NULL,
    from_id BIGINT NOT NULL,
    to_id BIGINT NOT NULL,
    score INTEGER NOT NULL,
    comment VARCHAR(500),
    CONSTRAINT fk_rating_from FOREIGN KEY (from_id)
        REFERENCES passengers(id) ON DELETE CASCADE,
    CONSTRAINT fk_rating_to FOREIGN KEY (to_id)
        REFERENCES passengers(id) ON DELETE CASCADE
);

ALTER TABLE ratings ADD CONSTRAINT chk_ratings_score
    CHECK (score >= 1 AND score <= 5);

ALTER TABLE ratings ADD CONSTRAINT chk_ratings_not_self
    CHECK (from_id != to_id);

ALTER TABLE ratings ADD CONSTRAINT unique_rating_per_trip
    UNIQUE (trip_id, from_id, to_id);

COMMENT ON TABLE ratings IS 'Passenger ratings after completed trips';
COMMENT ON COLUMN ratings.trip_id IS 'Reference to trip from TripService (external reference)';
COMMENT ON COLUMN ratings.from_id IS 'Passenger who gives the rating';
COMMENT ON COLUMN ratings.to_id IS 'Passenger who receives the rating';
COMMENT ON COLUMN ratings.score IS 'Rating score from 1 to 5';
COMMENT ON COLUMN ratings.comment IS 'Optional comment (max 500 characters)';