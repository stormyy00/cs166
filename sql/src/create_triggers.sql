CREATE OR REPLACE FUNCTION update_overdue_games() 
RETURNS TRIGGER AS $$
BEGIN
    IF NEW.dueDate < NOW() THEN
        UPDATE Users SET numOverDueGames = numOverDueGames + 1 WHERE login = NEW.login;
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;


CREATE TRIGGER check_overdue_games
AFTER INSERT OR UPDATE ON RentalOrder
FOR EACH ROW
EXECUTE PROCEDURE update_overdue_games();



CREATE OR REPLACE FUNCTION update_total_price()
RETURNS TRIGGER AS $$
DECLARE
    game_price DECIMAL(10,2);
    total_price DECIMAL(10,2) := 0;
BEGIN
    FOR game_price IN
        SELECT price * unitsOrdered
        FROM GamesInOrder JOIN Catalog ON GamesInOrder.gameID = Catalog.gameID
        WHERE rentalOrderID = NEW.rentalOrderID
    LOOP
        total_price := total_price + game_price;
    END LOOP;
    UPDATE RentalOrder SET totalPrice = total_price WHERE rentalOrderID = NEW.rentalOrderID;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER recalculate_total_price
AFTER INSERT OR UPDATE ON GamesInOrder
FOR EACH ROW
EXECUTE PROCEDURE update_total_price();
