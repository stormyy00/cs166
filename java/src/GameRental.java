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
                   case 2: updateProfile(esql); break;
                   case 3: viewCatalog(esql); break;
                   case 4: placeOrder(esql); break;
                   case 5: viewAllOrders(esql); break;
                   case 6: viewRecentOrders(esql); break;
                   case 7: viewOrderInfo(esql); break;
                   case 8: viewTrackingInfo(esql); break;
                   case 9: updateTrackingInfo(esql); break;
                   case 10: updateCatalog(esql); break;
                   case 11: updateUser(esql); break;



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
   public static void updateProfile(GameRental esql) {
      try{
       String query = "";
         System.out.println("This update profile");

       
      }catch(Exception e){
         System.err.println (e.getMessage());
      }
   }
   public static void viewCatalog(GameRental esql) {  
      try{
         BufferedReader in = new BufferedReader(new InputStreamReader(System.in));

            System.out.println("This views catalog");
            System.out.println("How would you like to view the catalog?");
            System.out.println("1. Genre");
            System.out.println("2. Price");
            System.out.println("3. Search Game ID");

            int choice = readChoice(); 
            String genre = null;
            Double priceFilter = null;
            String sortByPrice = null;
            String gameId = null;

            switch (choice) {
                case 1:
                    System.out.println("Enter Genre: ");
                    genre = in.readLine();
                    System.out.println("Sort by price (ASC/DESC): ");
                    sortByPrice = in.readLine();
                    break;
                case 2:
                    System.out.println("Enter maximum price: ");
                    priceFilter = Double.parseDouble(in.readLine());
                    System.out.println("Sort by price (ASC/DESC): ");
                    sortByPrice = in.readLine();
                    break;
                case 3:
                    System.out.println("Enter Game ID: ");
                    gameId = in.readLine();
                    break;
                default:
                    System.out.println("Invalid choice");
                    return;
            }

            StringBuilder query = new StringBuilder("SELECT gameName, genre, price FROM Catalog");
            boolean hasFilter = false;

            if (gameId != null && !gameId.isEmpty()) {
                query.append(" WHERE gameID = '").append(gameId).append("'");
                hasFilter = true;
            }
            if (genre != null && !genre.isEmpty()) {
                if (hasFilter) {
                    query.append(" AND");
                } else {
                    query.append(" WHERE");
                    hasFilter = true;
                }
                query.append(" genre = '").append(genre).append("'");
            }
            if (priceFilter != null) {
                if (hasFilter) {
                    query.append(" AND");
                } else {
                    query.append(" WHERE");
                    hasFilter = true;
                }
                query.append(" price <= ").append(priceFilter);
            }

            if (sortByPrice != null && !sortByPrice.isEmpty()) {
                query.append(" ORDER BY price ").append(sortByPrice.equalsIgnoreCase("ASC") ? "ASC" : "DESC");
            }

            String finalQuery = query.toString();
            System.out.println("Executing query: " + finalQuery);

            List<List<String>> viewCatalog = esql.executeQueryAndReturnResult(finalQuery);

            for (List<String> view : viewCatalog) {
                System.out.println("Game Name: " + view.get(0) + ", Genre: " + view.get(1) + ", Price: $" + view.get(2));
            }
      }catch(Exception e){
         System.err.println (e.getMessage());
      }
   }
   public static void placeOrder(GameRental esql) {
      try{
       String query = "";
         System.out.println("This places order");

       
      }catch(Exception e){
         System.err.println (e.getMessage());
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
      try{
       String query = "";
         System.out.println("This views recent orderse");

       
      }catch(Exception e){
         System.err.println (e.getMessage());
      }
   }
   public static void viewOrderInfo(GameRental esql) {
      try{
       String query = "";
         System.out.println("This views order info");

       
      }catch(Exception e){
         System.err.println (e.getMessage());
      }
   }
   public static void viewTrackingInfo(GameRental esql) {
      try{
       String query = "";
         System.out.println("This views trakcing info");

       
      }catch(Exception e){
         System.err.println (e.getMessage());
      }
   }
   public static void updateTrackingInfo(GameRental esql) {
      try{
       String query = "";
         System.out.println("This updates tracking info");

       
      }catch(Exception e){
         System.err.println (e.getMessage());
      }
   }
   public static void updateCatalog(GameRental esql) {
      try{
       String query = "";
         System.out.println("This updates catalog");

       
      }catch(Exception e){
         System.err.println (e.getMessage());
      }
   }
   public static void updateUser(GameRental esql) {
      try{
       String query = "";
         System.out.println("This updates user");

       
      }catch(Exception e){
         System.err.println (e.getMessage());
      }
   }


}//end GameRental

