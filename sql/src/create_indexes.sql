DROP INDEX IF EXISTS idx_users_phoneNum;
DROP INDEX IF EXISTS idx_catalog_genre;
DROP INDEX IF EXISTS idx_catalog_price;
DROP INDEX IF EXISTS idx_rentalOrder_login;
DROP INDEX IF EXISTS idx_trackingInfo_rentalOrderID;
DROP INDEX IF EXISTS idx_gamesInOrder_gameID;

CREATE INDEX idx_users_phoneNum ON Users(phoneNum);
CREATE INDEX idx_catalog_genre ON Catalog(genre);
CREATE INDEX idx_catalog_price ON Catalog(price);
CREATE INDEX idx_rentalOrder_login ON RentalOrder(login);
CREATE INDEX idx_trackingInfo_rentalOrderID ON TrackingInfo(rentalOrderID);
CREATE INDEX idx_gamesInOrder_gameID ON GamesInOrder(gameID);