/*
 * Template JAVA User Interface
 * =============================
 *
 * Database Management Systems
 * Department of Computer Science &amp; Engineering
 * University of California - Riverside
 *
 * Target DBMS: 'Postgres'
 *
 */
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;

import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.io.File;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;
import java.util.ArrayList;
import java.lang.Math;
import java.sql.PreparedStatement;
import java.util.UUID;
import java.util.Calendar;
import java.sql.Timestamp;

/**
 * This class defines a simple embedded SQL utility class that is designed to
 * work with PostgreSQL JDBC drivers.
 *
 */
public class GameRental {

   // reference to physical database connection.
   private Connection _connection = null;

   // handling the keyboard inputs through a BufferedReader
   // This variable can be global for convenience.
   static BufferedReader in = new BufferedReader(
                                new InputStreamReader(System.in));

   /**
    * Creates a new instance of GameRental store
    *
    * @param hostname the MySQL or PostgreSQL server hostname
    * @param database the name of the database
    * @param username the user name used to login to the database
    * @param password the user login password
    * @throws java.sql.SQLException when failed to make a connection.
    */
   public GameRental(String dbname, String dbport, String user, String passwd) throws SQLException {

      System.out.print("Connecting to database...");
      try{
         // constructs the connection URL
         String url = "jdbc:postgresql://localhost:" + dbport + "/" + dbname;
         System.out.println ("Connection URL: " + url + "\n");

         // obtain a physical connection
         this._connection = DriverManager.getConnection(url, user, passwd);
         System.out.println("Done");
      }catch (Exception e){
         System.err.println("Error - Unable to Connect to Database: " + e.getMessage() );
         System.out.println("Make sure you started postgres on this machine");
         System.exit(-1);
      }//end catch
   }//end GameRental

   /**
    * Method to execute an update SQL statement.  Update SQL instructions
    * includes CREATE, INSERT, UPDATE, DELETE, and DROP.
    *
    * @param sql the input SQL string
    * @throws java.sql.SQLException when update failed
    */
   public void executeUpdate (String sql) throws SQLException {
      // creates a statement object
      Statement stmt = this._connection.createStatement ();

      // issues the update instruction
      stmt.executeUpdate (sql);

      // close the instruction
      stmt.close ();
   }//end executeUpdate

   /**
    * Method to execute an input query SQL instruction (i.e. SELECT).  This
    * method issues the query to the DBMS and outputs the results to
    * standard out.
    *
    * @param query the input query string
    * @return the number of rows returned
    * @throws java.sql.SQLException when failed to execute the query
    */
   public int executeQueryAndPrintResult (String query) throws SQLException {
      // creates a statement object
      Statement stmt = this._connection.createStatement ();

      // issues the query instruction
      ResultSet rs = stmt.executeQuery (query);

      /*
       ** obtains the metadata object for the returned result set.  The metadata
       ** contains row and column info.
       */
      ResultSetMetaData rsmd = rs.getMetaData ();
      int numCol = rsmd.getColumnCount ();
      int rowCount = 0;

      // iterates through the result set and output them to standard out.
      boolean outputHeader = true;
      while (rs.next()){
		 if(outputHeader){
			for(int i = 1; i <= numCol; i++){
			System.out.print(rsmd.getColumnName(i) + "\t");
			}
			System.out.println();
			outputHeader = false;
		 }
         for (int i=1; i<=numCol; ++i)
            System.out.print (rs.getString (i) + "\t");
         System.out.println ();
         ++rowCount;
      }//end while
      stmt.close();
      return rowCount;
   }//end executeQuery

   /**
    * Method to execute an input query SQL instruction (i.e. SELECT).  This
    * method issues the query to the DBMS and returns the results as
    * a list of records. Each record in turn is a list of attribute values
    *
    * @param query the input query string
    * @return the query result as a list of records
    * @throws java.sql.SQLException when failed to execute the query
    */
   public List<List<String>> executeQueryAndReturnResult (String query) throws SQLException {
      // creates a statement object
      Statement stmt = this._connection.createStatement ();

      // issues the query instruction
      ResultSet rs = stmt.executeQuery (query);

      /*
       ** obtains the metadata object for the returned result set.  The metadata
       ** contains row and column info.
       */
      ResultSetMetaData rsmd = rs.getMetaData ();
      int numCol = rsmd.getColumnCount ();
      int rowCount = 0;

      // iterates through the result set and saves the data returned by the query.
      boolean outputHeader = false;
      List<List<String>> result  = new ArrayList<List<String>>();
      while (rs.next()){
        List<String> record = new ArrayList<String>();
		for (int i=1; i<=numCol; ++i)
			record.add(rs.getString (i));
        result.add(record);
      }//end while
      stmt.close ();
      return result;
   }//end executeQueryAndReturnResult

   /**
    * Method to execute an input query SQL instruction (i.e. SELECT).  This
    * method issues the query to the DBMS and returns the number of results
    *
    * @param query the input query string
    * @return the number of rows returned
    * @throws java.sql.SQLException when failed to execute the query
    */
   public int executeQuery (String query) throws SQLException {
       // creates a statement object
       Statement stmt = this._connection.createStatement ();

       // issues the query instruction
       ResultSet rs = stmt.executeQuery (query);

       int rowCount = 0;

       // iterates through the result set and count nuber of results.
       while (rs.next()){
          rowCount++;
       }//end while
       stmt.close ();
       return rowCount;
   }

   /**
    * Method to fetch the last value from sequence. This
    * method issues the query to the DBMS and returns the current
    * value of sequence used for autogenerated keys
    *
    * @param sequence name of the DB sequence
    * @return current value of a sequence
    * @throws java.sql.SQLException when failed to execute the query
    */
   public int getCurrSeqVal(String sequence) throws SQLException {
	Statement stmt = this._connection.createStatement ();

	ResultSet rs = stmt.executeQuery (String.format("Select currval('%s')", sequence));
	if (rs.next())
		return rs.getInt(1);
	return -1;
   }

   /**
    * Method to close the physical connection if it is open.
    */
   public void cleanup(){
      try{
         if (this._connection != null){
            this._connection.close ();
         }//end if
      }catch (SQLException e){
         // ignored.
      }//end try
   }//end cleanup

   /**
    * The main execution method
    *
    * @param args the command line arguments this inclues the <mysql|pgsql> <login file>
    */
   public static void main (String[] args) {
      if (args.length != 3) {
         System.err.println (
            "Usage: " +
            "java [-classpath <classpath>] " +
            GameRental.class.getName () +
            " <dbname> <port> <user>");
         return;
      }//end if

      Greeting();
      GameRental esql = null;
      try{
         // use postgres JDBC driver.
         Class.forName ("org.postgresql.Driver").newInstance ();
         // instantiate the GameRental object and creates a physical
         // connection.
         String dbname = args[0];
         String dbport = args[1];
         String user = args[2];
         esql = new GameRental (dbname, dbport, user, "");

         boolean keepon = true;
         while(keepon) {
            // These are sample SQL statements
            System.out.println("MAIN MENU");
            System.out.println("---------");
            System.out.println("1. Create user");
            System.out.println("2. Log in");
            System.out.println("9. < EXIT");
            String authorisedUser = null;
            switch (readChoice()){
               case 1: CreateUser(esql); break;
               case 2: authorisedUser = LogIn(esql); break;
               case 9: keepon = false; break;
               default : System.out.println("Unrecognized choice!"); break;
            }//end switch
            if (authorisedUser != null) {
              boolean usermenu = true;
              while(usermenu) {
                System.out.println("MAIN MENU");
                System.out.println("---------");
                System.out.println("1. View Profile");
                System.out.println("2. Update Profile");
                System.out.println("3. View Catalog");
                System.out.println("4. Place Rental Order");
                System.out.println("5. View Full Rental Order History");
                System.out.println("6. View Past 5 Rental Orders");
                System.out.println("7. View Rental Order Information");
                System.out.println("8. View Tracking Information");

                //the following functionalities basically used by employees & managers
                System.out.println("9. Update Tracking Information");

                //the following functionalities basically used by managers
                System.out.println("10. Update Catalog");
                System.out.println("11. Update User");

                System.out.println(".........................");
                System.out.println("20. Log out");
                switch (readChoice()){
                   case 1: viewProfile(esql, authorisedUser); break;
                   case 2: authorisedUser = updateProfile(esql, authorisedUser); break;
                   case 3: viewCatalog(esql); break;
                   case 4: placeOrder(esql, authorisedUser); break;
                   case 5: viewAllOrders(esql); break;
                   case 6: viewRecentOrders(esql); break;
                   case 7: viewOrderInfo(esql, authorisedUser); break;
                   case 8: viewTrackingInfo(esql); break;
                   case 9: updateTrackingInfo(esql, authorisedUser); break;
                   case 10: updateCatalog(esql, authorisedUser); break;
                   case 11: updateUser(esql, authorisedUser); break;



                   case 20: usermenu = false; break;
                   default : System.out.println("Unrecognized choice!"); break;
                }
              }
            }
         }//end while
      }catch(Exception e) {
         System.err.println (e.getMessage ());
      }finally{
         // make sure to cleanup the created table and close the connection.
         try{
            if(esql != null) {
               System.out.print("Disconnecting from database...");
               esql.cleanup ();
               System.out.println("Done\n\nBye !");
            }//end if
         }catch (Exception e) {
            // ignored.
         }//end try
      }//end try
   }//end main

   public static void Greeting(){
      System.out.println(
         "\n\n*******************************************************\n" +
         "              User Interface      	               \n" +
         "*******************************************************\n");
   }//end Greeting

   /*
    * Reads the users choice given from the keyboard
    * @int
    **/
   public static int readChoice() {
      int input;
      // returns only if a correct value is given.
      do {
         System.out.print("Please make your choice: ");
         try { // read the integer, parse it and break.
            input = Integer.parseInt(in.readLine());
            break;
         }catch (Exception e) {
            System.out.println("Your input is invalid!");
            continue;
         }//end try
      }while (true);
      return input;
   }//end readChoice

   /*
    * Creates a new user
    **/
   public static void CreateUser(GameRental esql){
      try{
         System.out.println("\tEnter name: ");
         String name = in.readLine();
         System.out.println("\tEnter password: ");
         String pwd = in.readLine();
         System.out.println("\tEnter role: ");
         String role = in.readLine();
         System.out.println("\tEnter favorite game: ");
         String favGames = in.readLine();
         System.out.println("\tEnter phone number ");
         String num = in.readLine();

         String query = String.format("INSERT INTO Users (login, password, role, favGames, phoneNum, numOverDueGames) VALUES ('%s', '%s', '%s', '%s', '%s', 0)", name, pwd, role, favGames, num);

         esql.executeUpdate(query);
         System.out.println("User successfully created!");

      }catch (Exception e){
         System.err.println (e.getMessage ());
      }
   }//end CreateUser


   /*
    * Check log in credentials for an existing user
    * @return User login or null is the user does not exist
    **/
   public static String LogIn(GameRental esql){
      try{
         System.out.println("\tEnter name: ");
         String login = in.readLine();
         System.out.println("\tEnter password: ");
         String pwd = in.readLine(); 

         String query = String.format("SELECT login FROM USERS WHERE login = '%s' AND password = '%s'", login, pwd);
         List<List<String>> userID = esql.executeQueryAndReturnResult(query);
	         if (userID.size() > 0)
		         return userID.get(0).get(0);
            return null;
      }catch(Exception e){
         System.err.println (e.getMessage ());
         return null;
      }
   }//end

// Rest of the functions definition go in here

   public static void viewProfile(GameRental esql, String authorisedUser) {
        try {

            String query = String.format("SELECT login, password, role, favGames, phoneNum, numOverDueGames FROM USERS WHERE login = '%s'", authorisedUser);
            List<List<String>> profile = esql.executeQueryAndReturnResult(query);

            if (!profile.isEmpty()) {

                System.out.println("===========================");
                System.out.println("\tUser Profile");
                System.out.println("===========================");
                for (List<String> row : profile) {
                    System.out.println("Name: " + row.get(0));
                    System.out.println("Password: " + row.get(1));
                    System.out.println("Role: " + row.get(2));
                    System.out.println("Favorite Games: " + row.get(3));
                    System.out.println("Phone Number: " + row.get(4));
                    System.out.println("Overdue Games: " + row.get(5));
                }
            } else {
                System.out.println("No user found with the login: " + authorisedUser);
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
   }

   public static String updateProfile(GameRental esql, String authorisedUser) {
    try {
        System.out.println("This updates profile");

        
        String checkUserQuery = "SELECT COUNT(*) FROM users WHERE login = ?";
        PreparedStatement checkUserStmt = esql._connection.prepareStatement(checkUserQuery);
        checkUserStmt.setString(1, authorisedUser);
        ResultSet rs = checkUserStmt.executeQuery();
        rs.next();
        int userCount = rs.getInt(1);
        checkUserStmt.close();

        if (userCount == 0) {
            System.out.println("No user found with the login: " + authorisedUser);
            return authorisedUser;
        }

        System.out.println("Enter the new login:");
        String login = in.readLine();
        System.out.println("Enter the new password:");
        String password = in.readLine();
        System.out.println("Enter the new favorite games:");
        String favGames = in.readLine();
        System.out.println("Enter the new phone number:");
        String phoneNum = in.readLine();

        List<String> updates = new ArrayList<>();
        if (!login.isEmpty()) updates.add("login = '" + login + "'");
        if (!password.isEmpty()) updates.add("password = '" + password + "'");
        if (!favGames.isEmpty()) updates.add("favGames = '" + favGames + "'");
        if (!phoneNum.isEmpty()) updates.add("phoneNum = '" + phoneNum + "'");

        if (updates.isEmpty()) {
            System.out.println("No updates were provided.");
            return authorisedUser;
        }

        String updateQuery = "UPDATE users SET " + String.join(", ", updates) + " WHERE login = ?";
        PreparedStatement pstmt = esql._connection.prepareStatement(updateQuery);
        pstmt.setString(1, authorisedUser);

        int rowsUpdated = pstmt.executeUpdate();
        if (rowsUpdated > 0) {
            System.out.println("Profile updated successfully.");
            if (!login.isEmpty()) {
                authorisedUser = login;
            }
        } else {
            System.out.println("Profile update failed.");
        }

         pstmt.close();
      } catch (Exception e) {
         System.err.println(e.getMessage());
      }
      return authorisedUser;
   }




   public static void viewCatalog(GameRental esql) {  
      try{
         System.out.println("Enter the criteria to search by (1. gameID, 2. genre, or 3. price):");
         int criteria = readChoice();
         String query = "";
         
         switch (criteria) {
            case 1:
                  System.out.println("Enter the GameID:");
                  String gameId = in.readLine();
                  query = String.format("SELECT gameName, genre, price FROM Catalog WHERE gameID = '%s';", gameId);
                  break;
            case 2:
                  System.out.println("Enter the Genre:");
                  String genre = in.readLine();
                  query = String.format("SELECT gameName, genre, price FROM Catalog WHERE genre = '%s';", genre);
                  break;
            case 3:
                  System.out.println("Enter the Price:");
                  String price = in.readLine();
                  query = String.format("SELECT gameName, genre, price FROM Catalog WHERE price = '%s';", price);
                  break;
            default:
                  System.out.println("Invalid criteria. Please enter 'gameID', 'genre', or 'price'.");
                  return; // Exit the method if the criteria is invalid
         }
         System.out.println("Do you want to sort the results? Enter 'asc' for ascending, 'desc' for descending, or 'none' for no sorting:");
         String sortChoice = in.readLine().trim().toLowerCase();;
         
         if (sortChoice.equals("asc") || sortChoice.equals("desc")) {
            query += String.format(" ORDER BY price %s", sortChoice.toUpperCase()); // Assuming sorting by price for demonstration
         } else if (!sortChoice.equals("none")) {
            System.out.println("Results will be displayed without sorting.");
         }
    
         List<List<String>> catalogView = esql.executeQueryAndReturnResult(query);
         if (catalogView.size() == 0) {
            System.out.println("❌ No games found for the given filter.");
         } else {
            displayCatalog(catalogView);
            // System.out.println("⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️");
            // for (List<String> product : storeProducts) {
            //       System.out.println("Game Name: " + product.get(0) + " Genre: " + product.get(1) + " Price: " + product.get(2));
            // }
            // System.out.println("⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️");

            // System.out.println("Do you want to sort the results? (1. yes/2. no)");
            //    int sortResponse = readChoice();
            //     if (sortResponse == 1) {
            //         System.out.println("Enter the field to sort by (gameName, genre, price):");
            //         String sortField = in.readLine().trim().toLowerCase();
            //         System.out.println("Enter the order (asc/desc):");
            //         String sortOrder = in.readLine().trim();
                     
            //         query = query.replace(";", "") + String.format(" ORDER BY %s %s;", sortField, sortOrder);
            //         System.out.println(query);

            //         catalogView = esql.executeQueryAndReturnResult(query);
            //         displayCatalog(catalogView);
                  }
      }catch(Exception e){
         System.err.println (e.getMessage());
      }
   }
   public static void displayCatalog(List<List<String>> results) {
        System.out.println("⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️");
        for (List<String> catalog : results) {
            System.out.println("Game Name: " + catalog.get(0) + " catalog: " + catalog.get(1) + " catalog: " + catalog.get(2));
        }
        System.out.println("⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️");
    }

public static void placeOrder(GameRental esql, String authorisedUser) {
    try {
        List<String> gameIDs = new ArrayList<>();
        List<Integer> unitsOrdered = new ArrayList<>();
        double totalPrice = 0.0;

        while (true) {
            System.out.println("Enter the game ID to rent (or type 'done' to finish):");
            String gameID = in.readLine();

            if (gameID.equalsIgnoreCase("done")) {
                break;
            }

            System.out.println("Enter the number of units for game ID " + gameID + ":");
            int units = Integer.parseInt(in.readLine());

            String priceQuery = String.format("SELECT price FROM Catalog WHERE gameID = '%s'", gameID);
            List<List<String>> result = esql.executeQueryAndReturnResult(priceQuery);
            if (result.isEmpty()) {
                System.out.println("Game ID " + gameID + " not found in catalog.");
                continue;
            }
            double price = Double.parseDouble(result.get(0).get(0));
            totalPrice += price * units;

            gameIDs.add(gameID);
            unitsOrdered.add(units);
        }

        if (gameIDs.isEmpty()) {
            System.out.println("No games selected for rental. Order cancelled.");
            return;
        }

        System.out.println("Total price of rental order: $" + totalPrice);

        
        String rentalOrderID = UUID.randomUUID().toString();

        Timestamp orderTimestamp = new Timestamp(System.currentTimeMillis());
        Calendar cal = Calendar.getInstance();
        cal.setTime(orderTimestamp);
        cal.add(Calendar.DATE, 7);
        Timestamp dueDate = new Timestamp(cal.getTimeInMillis());

        String insertOrderQuery = "INSERT INTO RentalOrder (rentalOrderID, login, noOfGames, totalPrice, orderTimestamp, dueDate) VALUES (?, ?, ?, ?, ?, ?)";
        PreparedStatement insertOrderStmt = esql._connection.prepareStatement(insertOrderQuery);
        insertOrderStmt.setString(1, rentalOrderID);
        insertOrderStmt.setString(2, authorisedUser);
        insertOrderStmt.setInt(3, gameIDs.size());
        insertOrderStmt.setDouble(4, totalPrice);
        insertOrderStmt.setTimestamp(5, orderTimestamp);
        insertOrderStmt.setTimestamp(6, dueDate);
        insertOrderStmt.executeUpdate();
        insertOrderStmt.close();

        
        String insertGameQuery = "INSERT INTO GamesInOrder (rentalOrderID, gameID, unitsOrdered) VALUES (?, ?, ?)";
        PreparedStatement insertGameStmt = esql._connection.prepareStatement(insertGameQuery);
        for (int i = 0; i < gameIDs.size(); i++) {
            insertGameStmt.setString(1, rentalOrderID);
            insertGameStmt.setString(2, gameIDs.get(i));
            insertGameStmt.setInt(3, unitsOrdered.get(i));
            insertGameStmt.addBatch();
        }
        insertGameStmt.executeBatch();
        insertGameStmt.close();


        String trackingID = UUID.randomUUID().toString();

        
        String insertTrackingQuery = "INSERT INTO TrackingInfo (trackingID, rentalOrderID, status, currentLocation, courierName, lastUpdateDate) VALUES (?, ?, ?, ?, ?, ?)";
        PreparedStatement insertTrackingStmt = esql._connection.prepareStatement(insertTrackingQuery);
        insertTrackingStmt.setString(1, trackingID);
        insertTrackingStmt.setString(2, rentalOrderID);
        insertTrackingStmt.setTimestamp(3, orderTimestamp);
        insertTrackingStmt.executeUpdate();
        insertTrackingStmt.close();

        System.out.println("Rental order placed successfully with Order ID: " + rentalOrderID);
        System.out.println("Tracking ID: " + trackingID);

    } catch (Exception e) {
        System.err.println("Error: " + e.getMessage());
    }
}









   public static void viewAllOrders(GameRental esql) {
      try{
       String query = "";
         System.out.println("This views all orders");

       
      }catch(Exception e){
         System.err.println (e.getMessage());
      }
   }

   public static void viewRecentOrders(GameRental esql) {
      try {
         
         System.out.println("Enter your user login:");
         String userLogin = in.readLine();

         
         String query = String.format(
               "SELECT R.rentalOrderID, R.orderTimestamp, R.dueDate, R.totalPrice, R.noOfGames, T.trackingID " +
               "FROM RentalOrder R " +
               "LEFT JOIN TrackingInfo T ON R.rentalOrderID = T.rentalOrderID " +
               "WHERE R.login = '%s' " +
               "ORDER BY R.orderTimestamp DESC " +
               "LIMIT 5",
               userLogin
         );

         List<List<String>> result = esql.executeQueryAndReturnResult(query);

         if (result.isEmpty()) {
               System.out.println("No recent orders found.");
         } else {
               System.out.println("===========================");
               System.out.println("Recent Orders");
               System.out.println("===========================");
               for (List<String> row : result) {
                  System.out.println("Rental Order ID: " + row.get(0));
                  System.out.println("Order Timestamp: " + row.get(1));
                  System.out.println("Due Date: " + row.get(2));
                  System.out.println("Total Price: " + row.get(3));
                  System.out.println("Number of Games: " + row.get(4));
                  System.out.println("Tracking ID: " + row.get(5));
                  System.out.println("---------------------------");
               }
         }
      } catch (Exception e) {
         System.err.println(e.getMessage());
      }
   }


   public static void viewOrderInfo(GameRental esql, String authorisedUser) {
      try {
         
         String roleQuery = String.format("SELECT role FROM Users WHERE login = '%s'", authorisedUser);
         List<List<String>> roleResult = esql.executeQueryAndReturnResult(roleQuery);
         if (roleResult.isEmpty()) {
               System.out.println("User not found.");
               return;
         }

        
         String query;
         if (roleResult.get(0).get(0).trim().equalsIgnoreCase("manager")) {
               System.out.println("Enter the rentalOrder login of the user:");
               String rentalOrderLogin = in.readLine();
               query = "SELECT R.orderTimestamp, R.dueDate, R.totalPrice, T.trackingID, G.gameID, G.unitsOrdered " +
                        "FROM RentalOrder R " +
                        "JOIN TrackingInfo T ON R.rentalOrderID = T.rentalOrderID " +
                        "JOIN GamesInOrder G ON R.rentalOrderID = G.rentalOrderID " +
                        "WHERE R.login = '" + rentalOrderLogin + "'";
         } else {
               query = String.format(
                     "SELECT R.orderTimestamp, R.dueDate, R.totalPrice, T.trackingID, G.gameID, G.unitsOrdered " +
                              "FROM RentalOrder R " +
                              "JOIN TrackingInfo T ON R.rentalOrderID = T.rentalOrderID " +
                              "JOIN GamesInOrder G ON R.rentalOrderID = G.rentalOrderID " +
                              "WHERE R.login = '%s'",
                     authorisedUser
               );
         }

         
         List<List<String>> result = esql.executeQueryAndReturnResult(query);

        
         if (result.isEmpty()) {
               System.out.println("No rental orders found.");
               return;
         }

         
         System.out.println("Order Information:");
         System.out.println("==================");

         
         for (List<String> row : result) {
               System.out.println("Order Timestamp: " + row.get(0));
               System.out.println("Due Date: " + row.get(1));
               System.out.println("Total Price: $" + row.get(2));
               System.out.println("Tracking ID: " + row.get(3));
               System.out.println("Game ID: " + row.get(4));
               System.out.println("Units Ordered: " + row.get(5));
               System.out.println("------------------");
         }
      } catch (Exception e) {
         System.err.println(e.getMessage());
      }
   }




   public static void viewTrackingInfo(GameRental esql) {
      try {
            System.out.println("Enter your user login:");
            String userLogin = in.readLine();

            String roleQuery = String.format("SELECT role FROM Users WHERE login = '%s'", userLogin);
            List<List<String>> roleResult = esql.executeQueryAndReturnResult(roleQuery);
            if (roleResult.isEmpty()) {
               System.out.println("User not found.");
               return;
            }

            System.out.println("Enter the Tracking ID:");
            String trackingID = in.readLine();

            String query;
            if (roleResult.get(0).get(0).trim().equalsIgnoreCase("manager") || roleResult.get(0).get(0).trim().equalsIgnoreCase("employee")) {
               System.out.println("Enter the rentalOrder login of the user:");
               String rentalOrderLogin = in.readLine();

               query = String.format(
                        "SELECT T.trackingID, T.courierName, T.rentalOrderID, T.currentLocation, T.status, T.lastUpdateDate, T.additionalComments " +
                              "FROM TrackingInfo T, RentalOrder R " +
                              "WHERE T.trackingID = '%s' AND T.rentalOrderID = R.rentalOrderID AND R.login = '%s'",
                        trackingID, rentalOrderLogin
               );
            } else {
               query = String.format(
                        "SELECT T.trackingID, T.courierName, T.rentalOrderID, T.currentLocation, T.status, T.lastUpdateDate, T.additionalComments " +
                              "FROM TrackingInfo T, RentalOrder R " +
                              "WHERE T.trackingID = '%s' AND T.rentalOrderID = R.rentalOrderID AND R.login = '%s'",
                        trackingID, userLogin
               );
            }

            List<List<String>> result = esql.executeQueryAndReturnResult(query);

            if (result.isEmpty()) {
               System.out.println("No tracking information found.");
            } else {
               System.out.println("===========================");
               System.out.println("Tracking Information");
               System.out.println("===========================");
               for (List<String> row : result) {
                  System.out.println("Tracking ID: " + row.get(0));
                  System.out.println("Courier Name: " + row.get(1));
                  System.out.println("Rental Order ID: " + row.get(2));
                  System.out.println("Current Location: " + row.get(3));
                  System.out.println("Status: " + row.get(4));
                  System.out.println("Last Updated Date: " + row.get(5));
                  System.out.println("Additional Comments: " + row.get(6));
               }
            }
      } catch (Exception e) {
            System.err.println(e.getMessage());
      }
   }

   public static void updateTrackingInfo(GameRental esql, String authorisedUser) {
      try {
         String roleQuery = String.format("SELECT role FROM users WHERE login = '%s'", authorisedUser);
         List<List<String>> userRole = esql.executeQueryAndReturnResult(roleQuery);

         if (userRole.isEmpty()) {
               System.out.println("No user found with the login: " + authorisedUser);
               return;
         } else {
               System.out.println("User role: " + userRole.get(0).get(0).trim());
         }

         if (!userRole.get(0).get(0).trim().equalsIgnoreCase("employee") &&
               !userRole.get(0).get(0).trim().equalsIgnoreCase("manager")) {
               System.out.println("Only employees and managers are allowed to update tracking information.");
               return;
         }

         System.out.println("Enter the Tracking ID of the tracking information to update:");
         String trackingID = in.readLine();

         String checkTrackingQuery = "SELECT COUNT(*) FROM TrackingInfo WHERE trackingID = ?";
         PreparedStatement checkTrackingStmt = esql._connection.prepareStatement(checkTrackingQuery);
         checkTrackingStmt.setString(1, trackingID);
         ResultSet rs = checkTrackingStmt.executeQuery();
         rs.next();
         int trackingCount = rs.getInt(1);
         checkTrackingStmt.close();

         if (trackingCount == 0) {
               System.out.println("No tracking information found with the Tracking ID: " + trackingID);
               return;
         }

         System.out.println("Enter new status:");
         String status = in.readLine();

         System.out.println("Enter new current location:");
         String currentLocation = in.readLine();

         System.out.println("Enter new courier name:");
         String courierName = in.readLine();

         System.out.println("Enter additional comments:");
         String additionalComments = in.readLine();

         List<String> updates = new ArrayList<>();
         if (!status.isEmpty()) updates.add("status = '" + status + "'");
         if (!currentLocation.isEmpty()) updates.add("currentLocation = '" + currentLocation + "'");
         if (!courierName.isEmpty()) updates.add("courierName = '" + courierName + "'");
         if (!additionalComments.isEmpty()) updates.add("additionalComments = '" + additionalComments + "'");

         if (updates.isEmpty()) {
               System.out.println("No updates were provided.");
               return;
         }

         String updateQuery = "UPDATE TrackingInfo SET " + String.join(", ", updates) + ", lastUpdateDate = CURRENT_TIMESTAMP WHERE trackingID = ?";
         PreparedStatement pstmt = esql._connection.prepareStatement(updateQuery);
         pstmt.setString(1, trackingID);

         int rowsUpdated = pstmt.executeUpdate();
         if (rowsUpdated > 0) {
               System.out.println("Tracking information updated successfully.");
         } else {
               System.out.println("Tracking information update failed.");
         }

         pstmt.close();
      } catch (Exception e) {
         System.err.println(e.getMessage());
      }
   }


   
   public static void updateCatalog(GameRental esql, String authorisedUser) {
    try {
        
        String roleQuery = String.format("SELECT role FROM users WHERE login = '%s'", authorisedUser);
        List<List<String>> userRole = esql.executeQueryAndReturnResult(roleQuery);

        if (userRole.isEmpty() || !userRole.get(0).get(0).equalsIgnoreCase("manager")) {
            System.out.println("Only managers are allowed to update the catalog.");
            return;
        }

        System.out.println("This updates catalog");

        
        System.out.println("Enter the Game ID to update:");
        String gameID = in.readLine();

        
        ArrayList<String> updates = new ArrayList<>();

        
        System.out.println("Enter the new game name:");
        String gameName = in.readLine();
        if (!gameName.isEmpty()) {
            updates.add(String.format("gameName = '%s'", gameName));
        }

        System.out.println("Enter the new genre:");
        String genre = in.readLine();
        if (!genre.isEmpty()) {
            updates.add(String.format("genre = '%s'", genre));
        }

        System.out.println("Enter the new price:");
        String priceInput = in.readLine();
        if (!priceInput.isEmpty()) {
            try {
                Double price = Double.parseDouble(priceInput);
                updates.add(String.format("price = %s", price));
            } catch (NumberFormatException e) {
                System.out.println("Invalid price format.");
                return;
            }
        }

        
        if (updates.isEmpty()) {
            System.out.println("No updates were provided.");
            return;
        }

        String query = "UPDATE Catalog SET " + String.join(", ", updates) + String.format(" WHERE gameID = '%s'", gameID);

        
        esql.executeUpdate(query);
        System.out.println("Catalog updated successfully.");

      } catch (Exception e) {
         System.err.println(e.getMessage());
      }
   }


   public static void updateUser(GameRental esql, String authorisedUser) {
      try {
        
         String roleQuery = String.format("SELECT role FROM users WHERE login = '%s'", authorisedUser);
         List<List<String>> userRole = esql.executeQueryAndReturnResult(roleQuery);

         if (userRole.isEmpty()) {
               System.out.println("No user found with the login: " + authorisedUser);
               return;
         } else {
               System.out.println("User role: " + userRole.get(0).get(0).trim());
         }

         if (!userRole.get(0).get(0).trim().equalsIgnoreCase("manager")) {
               System.out.println("Only managers are allowed to update user information.");
               return;
         }

   
         System.out.println("Enter the login of the user to update:");
         String userLogin = in.readLine();

         String checkUserQuery = "SELECT COUNT(*) FROM users WHERE login = ?";
         PreparedStatement checkUserStmt = esql._connection.prepareStatement(checkUserQuery);
         checkUserStmt.setString(1, userLogin);
         ResultSet rs = checkUserStmt.executeQuery();
         rs.next();
         int userCount = rs.getInt(1);
         checkUserStmt.close();

         if (userCount == 0) {
               System.out.println("No user found with the login: " + userLogin);
               return;
         }

         
         System.out.println("Enter the new login:");
         String newLogin = in.readLine();

         System.out.println("Enter the new role:");
         String newRole = in.readLine();

         System.out.println("Enter the new number of overdue games:");
         String newNumOverdueGames = in.readLine();

         List<String> updates = new ArrayList<>();
         if (!newLogin.isEmpty()) updates.add("login = '" + newLogin + "'");
         if (!newRole.isEmpty()) updates.add("role = '" + newRole + "'");
         if (!newNumOverdueGames.isEmpty()) updates.add("numOverDueGames = " + Integer.parseInt(newNumOverdueGames));

         if (updates.isEmpty()) {
               System.out.println("No updates were provided.");
               return;
         }

         String updateQuery = "UPDATE users SET " + String.join(", ", updates) + " WHERE login = ?";
         PreparedStatement pstmt = esql._connection.prepareStatement(updateQuery);
         pstmt.setString(1, userLogin);

         int rowsUpdated = pstmt.executeUpdate();
         if (rowsUpdated > 0) {
               System.out.println("User updated successfully.");
         } else {
               System.out.println("User update failed.");
         }

         pstmt.close();
      } catch (Exception e) {
         System.err.println(e.getMessage());
      }
   }


}//end GameRental

