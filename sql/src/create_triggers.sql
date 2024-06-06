CREATE OR REPLACE FUNCTION update_catalog_units()
RETURNS TRIGGER AS $$
BEGIN
    UPDATE GamesInOrder
    SET unitsOrdered = unitsOrdered - NEW.unitsordered 
    WHERE gameID AND gameName = NEW.gameName;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

