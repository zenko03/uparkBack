-- Update the localisation column from GEOGRAPHY to TEXT for easier testing
ALTER TABLE parking 
ALTER COLUMN localisation TYPE TEXT 
USING ST_AsText(localisation);

-- If the above doesn't work due to existing data, we can drop and recreate:
-- ALTER TABLE parking DROP COLUMN localisation;
-- ALTER TABLE parking ADD COLUMN localisation TEXT NOT NULL;