
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
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;

/**
 * This class defines a simple embedded SQL utility class that is designed to
 * work with PostgreSQL JDBC drivers.
 *
 */
public class Cafe {
   // reference to physical database connection.
   private Connection _connection = null;
   // handling the keyboard inputs through a BufferedReader
   // This variable can be global for convenience.
   static BufferedReader in = new BufferedReader(
         new InputStreamReader(System.in));

   /**
    * Creates a new instance of Cafe
    *
    * @param hostname the MySQL or PostgreSQL server hostname
    * @param database the name of the database
    * @param username the user name used to login to the database
    * @param password the user login password
    * @throws java.sql.SQLException when failed to make a connection.
    */
   public Cafe(String dbname, String dbport, String user, String passwd) throws SQLException {
      System.out.print("Connecting to database...");
      try {
         // constructs the connection URL
         String url = "jdbc:postgresql://localhost:" + dbport + "/" + dbname;
         System.out.println("Connection URL: " + url + "\n");
         // obtain a physical connection
         this._connection = DriverManager.getConnection(url, user, passwd);
         System.out.println("Done");
      } catch (Exception e) {
         System.err.println("Error - Unable to Connect to Database: " +
               e.getMessage());
         System.out.println("Make sure you started postgres on this machine");
         System.exit(-1);
      } // end catch
   }// end Cafe

   /**
    * Method to execute an update SQL statement. Update SQL instructions
    * includes CREATE, INSERT, UPDATE, DELETE, and DROP.
    *
    * @param sql the input SQL string
    * @throws java.sql.SQLException when update failed
    */
   public void executeUpdate(String sql) throws SQLException {
      // creates a statement object
      Statement stmt = this._connection.createStatement();
      // issues the update instruction
      stmt.executeUpdate(sql);
      // close the instruction
      stmt.close();
   }// end executeUpdate

   /**
    * Method to execute an input query SQL instruction (i.e. SELECT). This
    * method issues the query to the DBMS and outputs the results to
    * standard out.
    *
    * @param query the input query string
    * @return the number of rows returned
    * @throws java.sql.SQLException when failed to execute the query
    */
   public int executeQueryAndPrintResult(String query) throws SQLException {
      // creates a statement object
      Statement stmt = this._connection.createStatement();
      // issues the query instruction
      ResultSet rs = stmt.executeQuery(query);
      /*
       ** obtains the metadata object for the returned result set. The metadata
       ** contains row and column info.
       */
      ResultSetMetaData rsmd = rs.getMetaData();
      int numCol = rsmd.getColumnCount();
      int rowCount = 0;
      // iterates through the result set and output them to standard out.
      boolean outputHeader = true;
      while (rs.next()) {
         if (outputHeader) {
            for (int i = 1; i <= numCol; i++) {
               System.out.print(rsmd.getColumnName(i) + "\t");
            }
            System.out.println();
            outputHeader = false;
         }
         for (int i = 1; i <= numCol; ++i)
            System.out.print(rs.getString(i) + "\t");
         System.out.println();
         ++rowCount;
      } // end while
      stmt.close();
      return rowCount;
   }// end executeQuery

   /**
    * Method to execute an input query SQL instruction (i.e. SELECT). This
    * method issues the query to the DBMS and returns the results as
    * a list of records. Each record in turn is a list of attribute values
    *
    * @param query the input query string
    * @return the query result as a list of records
    * @throws java.sql.SQLException when failed to execute the query
    */
   public List<List<String>> executeQueryAndReturnResult(String query) throws SQLException {
      // creates a statement object
      Statement stmt = this._connection.createStatement();
      // issues the query instruction
      ResultSet rs = stmt.executeQuery(query);
      /*
       ** obtains the metadata object for the returned result set. The metadata
       ** contains row and column info.
       */
      ResultSetMetaData rsmd = rs.getMetaData();
      int numCol = rsmd.getColumnCount();
      int rowCount = 0;
      // iterates through the result set and saves the data returned by the query.
      boolean outputHeader = false;
      List<List<String>> result = new ArrayList<List<String>>();
      while (rs.next()) {
         List<String> record = new ArrayList<String>();
         for (int i = 1; i <= numCol; ++i)
            record.add(rs.getString(i));
         result.add(record);
      } // end while
      stmt.close();
      return result;
   }// end executeQueryAndReturnResult

   /**
    * Method to execute an input query SQL instruction (i.e. SELECT). This
    * method issues the query to the DBMS and returns the number of results
    *
    * @param query the input query string
    * @return the number of rows returned
    * @throws java.sql.SQLException when failed to execute the query
    */
   public int executeQuery(String query) throws SQLException {
      // creates a statement object
      Statement stmt = this._connection.createStatement();
      // issues the query instruction
      ResultSet rs = stmt.executeQuery(query);
      int rowCount = 0;
      // iterates through the result set and count nuber of results.
      while (rs.next()) {
         rowCount++;
      } // end while
      stmt.close();
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
      Statement stmt = this._connection.createStatement();
      ResultSet rs = stmt.executeQuery(String.format("Select currval('%s')",
            sequence));
      if (rs.next())
         return rs.getInt(1);
      return -1;
   }

   /**
    * Method to close the physical connection if it is open.
    */
   public void cleanup() {
      try {
         if (this._connection != null) {
            this._connection.close();
         } // end if
      } catch (SQLException e) {
         // ignored.
      } // end try
   }// end cleanup

   /**
    * The main execution method
    *
    * @param args the command line arguments this inclues the <mysql|pgsql> <login
    *             file>
    */
   public static void main(String[] args) {
      if (args.length != 3) {
         System.err.println(
               "Usage: " +
                     "java [-classpath <classpath>] " +
                     Cafe.class.getName() +
                     " <dbname> <port> <user>");
         return;
      } // end if
      Greeting();
      Cafe esql = null;
      try {
         // use postgres JDBC driver.
         Class.forName("org.postgresql.Driver").newInstance();
         // instantiate the Cafe object and creates a physical
         // connection.
         String dbname = args[0];
         String dbport = args[1];
         String user = args[2];
         esql = new Cafe(dbname, dbport, user, "");
         boolean keepon = true;
         while (keepon) {
            // These are sample SQL statements
            System.out.println("MAIN MENU");
            System.out.println("---------");
            System.out.println("1. Create user");
            System.out.println("2. Log in");
            System.out.println("9. < EXIT");
            String authorisedUser = null;
            switch (readChoice()) {
               case 1:
                  CreateUser(esql);
                  break;
               case 2:
                  authorisedUser = LogIn(esql);
                  break;
               case 9:
                  keepon = false;
                  break;
               default:
                  System.out.println("Unrecognized choice!");
                  break;
            }// end switch
            if (authorisedUser != null) {
               boolean usermenu = true;
               while (usermenu) {
                  System.out.println("MAIN MENU");
                  System.out.println("---------");
                  System.out.println("1. Goto Menu");
                  System.out.println("2. Update Profile");
                  System.out.println("3. Place a Order");
                  System.out.println("4. Update a Order");
                  System.out.println(".........................");
                  System.out.println("9. Log out");
                  switch (readChoice()) {
                     case 1:
                        Menu(esql, authorisedUser);
                        break;
                     case 2:
                        
			authorisedUser = UpdateProfile(esql, authorisedUser);
                        break;
                     case 3:
                        PlaceOrder(esql, authorisedUser);
                        break;
                     case 4:
                        UpdateOrder(esql, authorisedUser);
                        break;
                     case 9:
                        usermenu = false;
                        break;
                     default:
                        System.out.println("Unrecognized choice!");
                        break;
                  }
               }
            }
         } // end while
      } catch (Exception e) {
         System.err.println(e.getMessage());
      } finally {
         // make sure to cleanup the created table and close the connection.
         try {
            if (esql != null) {
               System.out.print("Disconnecting from database...");
               esql.cleanup();
               System.out.println("Done\n\nBye !");
            } // end if
         } catch (Exception e) {
            // ignored.
         } // end try
      } // end try
   }// end main

   public static void Greeting() {
      System.out.println(
            "\n\n*******************************************************\n" +
                  "              User Interface                      \n" +
                  "*******************************************************\n");
   }// end Greeting
   /*
    * Reads the users choice given from the keyboard
    * 
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
         } catch (Exception e) {
            System.out.println("Your input is invalid!");
            continue;
         } // end try
      } while (true);
      return input;
   }// end readChoice
   /*
    * Creates a new user with privided login, passowrd and phoneNum
    **/

   public static void CreateUser(Cafe esql){
      try{
         System.out.print("\tEnter user login: ");
         String login = in.readLine();
         System.out.print("\tEnter user password: ");
         String password = in.readLine();
         System.out.print("\tEnter user phone: ");
         String phone = in.readLine();
         
    String type="Customer";
    String favItems="";
 String query = String.format("INSERT INTO USERS (phoneNum, login, password, favItems, type) VALUES ('%s','%s','%s','%s','%s')", phone, login, password, favItems, type);
         esql.executeUpdate(query);
         System.out.println ("User successfully created!");
      }catch(Exception e){
         System.err.println (e.getMessage ());
      }
   }// end CreateUser
   /*
    * Check log in credentials for an existing user
    * 
    * @return User login or null is the user does not exist
    **/

   public static String LogIn(Cafe esql){
      try{
         System.out.print("\tEnter user login: ");
         String login = in.readLine();
         System.out.print("\tEnter user password: ");
         String password = in.readLine();
         String query = String.format("SELECT * FROM USERS WHERE login = '%s' AND password = '%s'", login, password);
         int userNum = esql.executeQuery(query);
 if (userNum > 0)
return login;
         return null;
      }catch(Exception e){
         System.err.println (e.getMessage ());
         return null;
      }
   }// end
   // Rest of the functions definition go in here

   public static void Menu(Cafe esql, String login) {

      String query, loginType;
      List<List<String>> result = new ArrayList<List<String>>();

      // run query to get the type of current user
      try {

         // query to get the typefrom the current user
         query = String.format("SELECT type FROM Users WHERE login = '%s'", login);
         result = esql.executeQueryAndReturnResult(query);

      } catch (Exception e) {
         System.err.println(e.getMessage());
      }

      boolean inMenu = false;

  	loginType = result.get(0).get(0);
	loginType = loginType.replaceAll(" ", "");
      	
      if (!loginType.equals("Manager")) {

         do {
            inMenu = true;
            System.out.println("Welcome to the menu. How would you like to proceed?");
            System.out.println("1. Show full menu");
            System.out.println("2. Search item name");
            System.out.println("3. Search item type");
            System.out.println("4. Go back to main menu");
            switch (readChoice()) {
               case 1:
                  outputFullMenu(esql);
                  break;
               case 2:
                  searchItemName(esql);
                  break;
               case 3:
                  searchItemType(esql);
                  break;
               case 4:
                  inMenu = false;
                  break;
               default:
                  System.out.println("Choice not recognized!");
                  break;
            }

         } while (inMenu);

      } // end if result != manager
      else if (loginType.equals("Manager")) {

         do {
            inMenu = true;

            System.out.println("Welcome to the menu. How would you like to proceed?");
            System.out.println("1. Show full menu");
            System.out.println("2. Search item name");
            System.out.println("3. Search item type");
            System.out.println("4. Add item");
            System.out.println("5. Delete item");
            System.out.println("6. Update item");
            System.out.println("7. Go back to main menu");
            switch (readChoice()) {
               case 1:
                  outputFullMenu(esql);
                  break;
               case 2:
                  searchItemName(esql);
                  break;
               case 3:
                  searchItemType(esql);
                  break;
               case 4:
                  addItem(esql);
                  break;
               case 5:
                  deleteItem(esql);
                  break;
               case 6:
                  updateItem(esql);
                  break;
               case 7:
                  inMenu = false;
                  break;
               default:
                  System.out.println("Choice not recognized!");
                  break;
            }
         } while (inMenu);

      } // end if result == manager

      // end function menu
   }

   public static void outputFullMenu(Cafe esql) {

      List<List<String>> result = new ArrayList<List<String>>();

      try{
         String query;
         query = String.format("SELECT * FROM Menu");

         result = esql.executeQueryAndReturnResult(query);

      }catch(Exception e){
         System.out.println("Unable to get menu, please contact developers");
      }

      for(int i = 0; i < result.size(); i++){
         for(int j = 0; j < result.get(i).size(); j++){
               System.out.printf(result.get(i).get(j) + " ");
         }
         System.out.println();
      }

   }

   public static void searchItemName(Cafe esql) {

      boolean searchItem = false;
      String itemName;
      List<List<String>> result = new ArrayList<List<String>>();
      do {
         searchItem = true;
         while (true) {
            try {
               System.out.print("Please input the name of item you would like to search: ");
               itemName = in.readLine();
               break;
            } catch (Exception e) {
               System.out.println("Please input a valid input");
               continue;
            }
         }

         try {
            String query;
            query = String.format("SELECT * FROM Menu WHERE itemName = '%s'", itemName);
            if (esql.executeQuery(query) == 0) {
               System.out.println("This item does not exist in the menu.");
            } else {
               // execute the query to print out nicely into output stream
               result = esql.executeQueryAndReturnResult(query);

               for(int i = 0; i < result.size(); i++){
                  for(int j = 0; j < result.get(i).size(); j++){
                        System.out.printf(result.get(i).get(j) + " ");
                  }
                  System.out.println();
               }

            }
         } catch (Exception e) {
            System.err.println(e.getMessage());
         }

         System.out.println("Would you like to search another item?");
         System.out.println("1. YES");
         System.out.println("2. NO");
         switch (readChoice()) {
            case 1:
               break;
            case 2:
               searchItem = false;
               break;
            default:
               System.out.println("Please input a valid choice.");
               break;
         }

      } while (searchItem);

   }

   public static void searchItemType(Cafe esql) {

      boolean searchType = false;
      String itemType;
      List<List<String>> result = new ArrayList<List<String>>();

      do {

         searchType = true;

         while (true) {
            try {
               System.out.print("Please input the type of item you would like to search: ");
               itemType = in.readLine();
               break;
            } catch (Exception e) {
               System.out.println("Please input a valid input");
               continue;
            }
         }

         try {
            String query;
            query = String.format("SELECT * FROM Menu WHERE type = '%s'", itemType);
            if (esql.executeQuery(query) == 0) {
               System.out.println("This item does not exist in the menu.");
            } else {
               // execute the query again to print out nicely into output stream
               result = esql.executeQueryAndReturnResult(query);

               for(int i = 0; i < result.size(); i++){
                  for(int j = 0; j < result.get(i).size(); j++){
                        System.out.printf(result.get(i).get(j) + " ");
                  }
                  System.out.println();
               }
            }
         } catch (Exception e) {
            System.err.println(e.getMessage());
         }

         System.out.println("Would you like to search another item?");
         System.out.println("1. YES");
         System.out.println("2. NO");
         switch (readChoice()) {
            case 1:
               break;
            case 2:
               searchType = false;
               break;
            default:
               System.out.println("Please input a valid choice.");
               break;
         }

      } while (searchType);

   }

   public static void addItem(Cafe esql) {

      boolean addItem = false;
      String itemName, itemType, itemPrice, itemDescription, itemURL;

      do {
         addItem = true;
         while (true) {
            try {
               System.out.println("What is the new item name?");
               itemName = in.readLine();
               System.out.println("What is the new item type?");
               itemType = in.readLine();
               System.out.println("What is the new item price? (do not include any symbols $)");
               itemPrice = in.readLine();
               System.out.println("What is the new item description?");
               itemDescription = in.readLine();
               System.out.println("What is the new item URL? (Enter 1 if empty)");
               itemURL = in.readLine();

               break;
            } catch (Exception e) {
               System.out.println("Input error. Please input corret values");
               continue;
            }
         }

         try {

            String query;
            query = String.format("SELECT itemName FROM Menu WHERE itemName = '%s'", itemName);

            if (esql.executeQuery(query) > 0) {
               // check if item is in menu
               System.out.println("Item is already in menu! Please retry");
            } else {

               query = String.format(
                     "INSERT INTO Menu (itemName, type, price, description, imageURL) VALUES ('%s', '%s', '%s', '%s', '%s')",
                     itemName, itemType, itemPrice, itemDescription, itemURL);
               esql.executeUpdate(query);
               System.out.println("New item added successfully!");
            }

         } catch (Exception e) {
            System.err.println(e.getMessage());
         }

         System.out.println("Would you like to add another item?");
         System.out.println("1. YES");
         System.out.println("2. NO");
         switch (readChoice()) {
            case 1:
               break;
            case 2:
               addItem = false;
               break;
            default:
               System.out.println("Please input a valid choice.");
               break;
         }

      } while (addItem);

   }

   public static void deleteItem(Cafe esql) {

      boolean delItem = false;
      String itemName;
      do {
         delItem = true;

         while (true) {
            try {
               System.out.println("Which item from the menu would you like to delete");
               itemName = in.readLine();
               break;
            } catch (Exception e) {
               System.out.println("Please input a valid input");
               continue;
            }
         }

         try {
            String query;

            query = String.format("DELETE FROM Menu WHERE itemName = '%s'", itemName);
            esql.executeUpdate(query);
            System.out.println("Item deleted successfully!");

         } catch (Exception e) {
            System.out.println("Some error occured. Please re-try, or either the item does not exist in Menu ");
         }

         System.out.println("Would you like to delete another item?");
         System.out.println("1. YES");
         System.out.println("2. NO");
         switch (readChoice()) {
            case 1:
               break;
            case 2:
               delItem = false;
               break;
            default:
               System.out.println("Please input a valid choice.");
               break;
         }

      } while (delItem);

   }

   public static void updateItem(Cafe esql) {

      boolean upItem = false;
      String itemName;
      int ifExists = 0;

      do {
         upItem = true;

         while (true) {
            try {
               System.out.println("What item would you like to update (itemName)");
               itemName = in.readLine();
               break;
            } catch (Exception e) {
               System.out.println("Please input a valid input");
               continue;
            }
         }

         try {
            String query = String.format("SELECT itemName FROM Menu WHERE itemName = '%s'", itemName);
            ifExists = esql.executeQuery(query);
         } catch (Exception e) {
            System.out.println("Error getting item, please input an exisitng item, or item does not exist");
         }

         if (ifExists > 0) {
            boolean currentItem = false;
            String newItemName = itemName;

            do {
               currentItem = true;

               System.out.printf("What item [%s] would you like to update?\n", itemName);
               System.out.println("1. Name");
               System.out.println("2. Type");
               System.out.println("3. Price");
               System.out.println("4. Description");
               System.out.println("5. imageURL");
               System.out.println("6. Exit updating current item");
               switch (readChoice()) {
                  case 1:
                     newItemName = updateMenuName(esql, itemName);
                     break;
                  case 2:
                     updateMenuType(esql, newItemName);
                     break;
                  case 3:
                     updateMenuPrice(esql, newItemName);
                     break;
                  case 4:
                     updateMenuDescription(esql, newItemName);
                     break;
                  case 5:
                     updateMenuImageURL(esql, newItemName);
                     break;
                  case 6:
                     currentItem = false;
                     break;
                  default:
                     System.out.println("Please input a valid choice.");
                     break;

               }
            } while (currentItem);

         } else if (ifExists < 0) {
            System.out.println("Item does not exist in Menu, try adding instead of updating. ");
         }

         System.out.println("Would you like to update another item?");
         System.out.println("1. YES");
         System.out.println("2. NO");
         switch (readChoice()) {
            case 1:
               break;
            case 2:
               upItem = false;
               break;
            default:
               System.out.println("Please input a valid choice.");
               break;
         }

      } while (upItem);

   }

   public static String updateMenuName(Cafe esql, String itemName) {

      String newName;
      while (true) {
         try {
            System.out.printf("Enter new name to be updated for item [%s]\n", itemName);
            newName = in.readLine();

            // if(nameName.length() > 50){
            // throw new SQLException("Name exceeds limit. Please try a smaller name.");
            // }
            break;
         } catch (Exception e) {
            System.out.println("ERROR: Please input a name");
            continue;
         }
      }

      try {
         String query;
         query = String.format("SELECT itemName FROM Menu WHERE itemName = '%s'", newName);

         if (esql.executeQuery(query) > 0) {
            System.out.println("The name you choose already exists, please re-try and choose a new one.");

            return itemName;
         } else {

            query = String.format("UPDATE Menu SET itemName = '%s' WHERE itemName = '%s'", newName, itemName);
            esql.executeUpdate(query);
            System.out.println("Name updated successfully!");
         }

      } catch (Exception e) {
         System.out.println("Error when updating, please re-try or contact IT.");
      }

      return newName;
   }

   public static void updateMenuType(Cafe esql, String itemName) {

      String newType;
      while (true) {
         try {
            System.out.printf("Enter new type to be updated for item [%s]\n", itemName);
            newType = in.readLine();

            // if(nameName.length() > 50){
            // throw new SQLException("Name exceeds limit. Please try a smaller name.");
            // }
            break;
         } catch (Exception e) {
            System.out.println("ERROR: Please input a type");
            continue;
         }
      }

      try {
         String query;
         query = String.format("UPDATE Menu SET type = '%s' WHERE itemName = '%s'", newType, itemName);
         esql.executeUpdate(query);
         System.out.printf("Typed updated successfully for item [%s]\n", itemName);

      } catch (Exception e) {
         System.out.println("Error when updating, please re-try or contact IT.");
      }

   }

   public static void updateMenuPrice(Cafe esql, String itemName) {

      String newPrice;
      while (true) {
         try {
            System.out.printf("Enter new price to be updated for item [%s] (do not include $)\n", itemName);
            newPrice = in.readLine();

            // if(nameName.length() > 50){
            // throw new SQLException("Name exceeds limit. Please try a smaller name.");
            // }
            break;
         } catch (Exception e) {
            System.out.println("ERROR: Please input a type");
            continue;
         }
      }

      try {
         String query;
         query = String.format("UPDATE Menu SET price = '%s' WHERE itemName = '%s'", newPrice, itemName);
         esql.executeUpdate(query);
         System.out.printf("Price updated successfully for item [%s]\n", itemName);

      } catch (Exception e) {
         System.out.println("Error when updating, please re-try or contact IT.");
      }

   }

   public static void updateMenuDescription(Cafe esql, String itemName) {

      String newDes;

      while (true) {
         try {
            System.out.printf("Enter new description to be updated for item [%s]\n", itemName);
            newDes = in.readLine();
            break;
         } catch (Exception e) {
            System.out.println("Please input a valid input");
            continue;
         }
      }

      try {
         String query;
         query = String.format("UPDATE Menu SET description = '%s' WHERE itemName = '%s'", newDes, itemName);
         esql.executeUpdate(query);
         System.out.printf("Description updated successfully for item [%s]\n", itemName);

      } catch (Exception e) {
         System.out.println("Error when updating, please re-try or contact IT.");
      }

   }

   public static void updateMenuImageURL(Cafe esql, String itemName) {

      String newURL;

      while (true) {
         try {
            System.out.printf("Enter new description to be updated for item [%s]\n", itemName);
            newURL = in.readLine();
            break;
         } catch (Exception e) {
            System.out.println("Please input a valid input");
            continue;
         }
      }

      try {
         String query;
         query = String.format("UPDATE Menu SET imageURL = '%s' WHERE itemName = '%s'", newURL, itemName);
         esql.executeUpdate(query);
         System.out.printf("imageURL updated successfully for item [%s]\n", itemName);

      } catch (Exception e) {
         System.out.println("Error when updating, please re-try or contact IT.");
      }

   }

   public static String UpdateProfile(Cafe esql, String login) {

      String query, loginType;
      List<List<String>> result = new ArrayList<List<String>>();
  

      // run query to get the type of current user
      try {

         // query to get the typefrom the current user
         query = String.format("SELECT type FROM Users WHERE login = '%s'", login);
         result = esql.executeQueryAndReturnResult(query);

      } catch (Exception e) {
         System.err.println(e.getMessage());
      }

      boolean inMenu = false;
      loginType = result.get(0).get(0);
 	   loginType = loginType.replaceAll(" ", "");

      if (!loginType.equals("Manager")) {

         String userName = login;
         do {
            inMenu = true;
            System.out.println("Which information would you like to update?");
            System.out.println("1. Login");
            System.out.println("2. Phone Number");
            System.out.println("3. Password");
            System.out.println("4. Favorite Items");
            System.out.println("5. Go back to Main Menu");
            switch (readChoice()) {
               case 1:
                  userName = updateLogin(esql, login);
                  break;
               case 2:
                  updatePhoneNumber(esql, userName);
                  break;
               case 3:
                  updatePassword(esql, userName);
                  break;
               case 4:
                  updateFavItems(esql, userName);
                  break;
               case 5:
                  inMenu = false;
                  break;
               default:
                  System.out.println("Choice not recognized!");
                  break;
            }

         } while (inMenu);
	
	login = userName;

      } // end if result != manager
      else if (loginType.equals("Manager")) {

         String userName = login;

         do {
            inMenu = true;

            System.out.println("Which information would you like to update?");
            System.out.println("1. Login");
            System.out.println("2. Phone Number");
            System.out.println("3. Password");
            System.out.println("4. Favorite Items");
            System.out.println("5. Change type");
            System.out.println("6. Modify other user");
            System.out.println("7. Go back to Main Menu");
            switch (readChoice()) {
               case 1:
                  userName = updateLogin(esql, login);
                  break;
               case 2:
                  updatePhoneNumber(esql, userName);
                  break;
               case 3:
                  updatePassword(esql, userName);
                  break;
               case 4:
                  updateFavItems(esql, userName);
                  break;
               case 5:
                  updateType(esql, userName);
                  break;
               case 6:
                  managerUpdateUser(esql, userName);
                  break;
               case 7:
                  inMenu = false;
                  break;
               default:
                  System.out.println("Choice not recognized!");
                  break;
            }
         } while (inMenu);
	login = userName;
      } // end if result == manager
	return login;
      // end function updateUser

   }

   public static String updateLogin(Cafe esql, String login) {

      String newLogin;
      while (true) {
         try {

            System.out.printf("Please enter new user login for [%s]\n", login);
            newLogin = in.readLine();
            break;
         } catch (Exception e) {
            System.out.println("Please enter a valid login. ");
            continue;
         }
      }

      try {
         String query;
         query = String.format("SELECT login FROM USERS WHERE login = '%s'", newLogin);

         if (esql.executeQuery(query) > 0) {
            System.out.println("The login you choose already exists, please re-try and choose a new one.");

            return login;
         } else {
            query = String.format("UPDATE Users SET login = '%s' WHERE login = '%s'", newLogin, login);
            esql.executeUpdate(query);
            System.out.println("Login updated successfully!");
         }
      } catch (Exception e) {
         System.out.println("Error updating User, please re-try or contact IT");
      }
	try{
		String query;
		query = String.format("UPDATE Orders SET login = '%s' WHERE login = '%s'", newLogin, login);
		esql.executeUpdate(query);
	}catch(Exception e){
		System.out.println("Unable to update the user records, please re-try!");
		
	}

      return newLogin;

   }

   public static void updatePhoneNumber(Cafe esql, String login) {

      String newPhoneNumber;
      while (true) {
         try {

            System.out.printf("Please enter new user phone number for [%s]\n", login);

            // TODO add line where it shows the user current phone number
            newPhoneNumber = in.readLine();
            break;
         } catch (Exception e) {
            System.out.println("Please enter a valid phone number.");
            continue;
         }
      }

      try {
         String query;
         query = String.format("SELECT phoneNum FROM Users WHERE phoneNum = '%s'", newPhoneNumber);

         if (esql.executeQuery(query) > 0) {
            System.out.println("The phone number you choose already exists, please re-try and choose a new one.");

         } else {
            query = String.format("UPDATE Users SET phoneNum = '%s' WHERE login = '%s'", newPhoneNumber, login);
            esql.executeUpdate(query);
            System.out.printf("Phone number updated successfully for user [%s]!\n", login);
         }
      } catch (Exception e) {
         System.out.println("Error updating User, please re-try or contact IT");
      }

   }

   public static void updatePassword(Cafe esql, String login) {

      String newPassword, passwordTwo;
      while (true) {
         try {

            System.out.printf("Please enter new user password for [%s]\n", login);
            // TODO add line where it shows the user current phone number
            newPassword = in.readLine();

            System.out.println("Please re-type password");
            passwordTwo = in.readLine();

            if (!passwordTwo.equals(newPassword)) {
               throw new SQLException("Passwords do not match, please re-type!");
            }

            break;
         } catch (Exception e) {
            System.out.println("Please enter a valid password.");
            continue;
         }
      }

      try {

         String query;
         query = String.format("UPDATE Users SET password = '%s' WHERE login = '%s'", newPassword, login);
         esql.executeUpdate(query);
         System.out.printf("Password updated successfully for user [%s]\n!", login);

      } catch (Exception e) {
         System.out.println("Error updating User, please re-try or contact IT");
      }
   }

   public static void updateFavItems(Cafe esql, String login) {

      String newFavItems;

      while (true) {
         try {
            System.out.printf("Please enter new favorite items for [%s]\n", login);
            // TODO add line where it shows the user current phone number
            newFavItems = in.readLine();
            break;
         } catch (Exception e) {
            System.out.println("Please input a valid input");
            continue;
         }
      }

      try {

         String query;
         query = String.format("UPDATE Users SET favItems = '%s' WHERE login = '%s'", newFavItems, login);
         esql.executeUpdate(query);
         System.out.printf("Favorite items updated successfully for user [%s]!\n", login);

      } catch (Exception e) {
         System.out.println("Error updating User, please re-try or contact IT");
      }

   }

   public static void updateType(Cafe esql, String login) {

      String newType = null;
      String input;

      System.out.println(
            "DANGEROUS ACTION! If you are admin and remove admin status for yourself, you will not have admin access anymore.");

      while (true) {
         try {
            System.out.println("Proceed!???? 1 for yes 0 for no");
            input = in.readLine();
            break;
         } catch (Exception e) {
            System.out.println("Please input a valid input");
            continue;
         }
      }

      if (!input.equals("1")) {
         return;
      }

      while (true) {
         try {

            System.out.printf("Please enter choice for new user type for [%s]\n", login);
            System.out.println("1. Employee");
            System.out.println("2. Customer");
            switch (readChoice()) {
               case 1:
                  newType = "Employee";
                  break;
               case 2:
                  newType = "Customer";
                  break;
               default:
                  System.out.println("Please enter valid choice");
                  break;
            }

            if (newType == null) {
               throw new SQLException("Re-trying choice ...");
            }

            break;
         } catch (Exception e) {
            continue;
         }
      }

      try {

         String query;
         query = String.format("UPDATE Users SET type = '%s' WHERE login = '%s'", newType, login);
         esql.executeUpdate(query);
         System.out.printf("User type updated successfully for user [%s]!\n", login);

      } catch (Exception e) {
         System.out.println("Error updating User, please re-try or contact IT");
      }

   }

   public static void managerUpdateUser(Cafe esql, String login) {

      String editUserLogin;

      // get user to edit from manager
      while (true) {
         try {
            System.out.println("Please enter a user login to edit");
            editUserLogin = in.readLine();

            break;
         } catch (Exception e) {
            System.out.println("Please enter an input");
            continue;
         }
      }

      // once have user login, check if the user exists
      int userExists = 0;

      try {
         String query;
         query = String.format("SELECT login FROM Users WHERE login = '%s'", editUserLogin);

         userExists = esql.executeQuery(query);

      } catch (Exception e) {
         System.out.println("ERROR processing user find, please re-try");
      }

      boolean inMenu = false;

      if (userExists > 0) {

         String userName = editUserLogin;

         do {
            inMenu = true;

            System.out.printf("Which information would you like to update for user [%s]?\n", userName);
            System.out.println("1. Login");
            System.out.println("2. Phone Number");
            System.out.println("3. Password");
            System.out.println("4. Favorite Items");
            System.out.println("5. Change type");
            System.out.println("6. Go back to Manager Menu");
            switch (readChoice()) {
               case 1:
                  userName = updateLogin(esql, editUserLogin);
                  break;
               case 2:
                  updatePhoneNumber(esql, userName);
                  break;
               case 3:
                  updatePassword(esql, userName);
                  break;
               case 4:
                  updateFavItems(esql, userName);
                  break;
               case 5:
                  updateType(esql, userName);
                  break;
               case 6:
                  inMenu = false;
                  break;
               default:
                  System.out.println("Choice not recognized!");
                  break;
            }
         } while (inMenu);

      } else {
         System.out.println("Could not find user, please re-try");
      }

   }



public static void PlaceOrder(Cafe esql, String login) {

   String paidStatus = "f", timeStampRecieved;

   String price;
   double priceOrder = 0;
   List<String> itemNames = new ArrayList<>();
   boolean inMenu = false;

   do{

      inMenu = true;
      System.out.println("Hello, what items would you like to order today? Please enter the name of the items you would like to order!");
      System.out.println("1. See Menu");
      System.out.println("2. Enter item to order");
      System.out.println("3. Enter 3 to finish ordering!");
      switch(readChoice()){
               case 1: outputFullMenu(esql);           break;
               case 2: {

                     String itemName, tempPrice;
                     boolean itemInMenu = false;
                     List<List<String>> priceResult = new ArrayList<List<String>>();

                     while(true){
                              try{
                                       System.out.println("Please enter a menu item to order!");
                                       itemName = in.readLine();
                                       break;
                              }catch(Exception e){
                                       System.out.println("Please enter a valid input!");
                                       continue;
                              }
                     }

                     try{

                        String query = String.format("SELECT itemName FROM Menu WHERE itemName = '%s'", itemName);

                        if(esql.executeQuery(query) != 0){
                           itemInMenu = true;
                        }
                        
                     }catch(Exception e){
                        System.err.println(e.getMessage());
                     }

                     if(itemInMenu){
                       
                        try{
                           String query = String.format("SELECT price FROM Menu WHERE itemName = '%s'", itemName);
                           priceResult = esql.executeQueryAndReturnResult(query);
                           
                        }catch(Exception e){
                           System.err.println(e.getMessage());
                        }

                        
                        tempPrice = priceResult.get(0).get(0);
                        tempPrice = tempPrice.replaceAll(" ", "");
                        priceOrder+= Double.parseDouble(tempPrice);
                        itemNames.add(itemName);
                     }
                     else{
                        System.out.println("The item name you typed did not match our records, please either re-try or retry correctly");
                     }

                  } break;
                  case 3: inMenu = false;                                        break;
               default: System.out.println("Please enter a valid choice 1 - 3"); break;
               }

   }while(inMenu);

   price = String.valueOf(priceOrder);

   System.out.printf("Would like to pay for this order now? The total is: %s \n", price);
   System.out.println("1. Yes");
   System.out.println("2. No");
   switch(readChoice()){
      case 1: paidStatus = "t"; break;
      case 2: paidStatus = "f"; break;
      default: System.out.println("Please enter a choice 1 - 2"); break;
   }

   List<List<String>> tempORDERID = new ArrayList<List<String>>();

   long now = System.currentTimeMillis();
   Timestamp time = new Timestamp(now);
   timeStampRecieved = time.toString();
 
   String orderID, orderStatus;
   orderStatus = "Hasn''t started";

   try{
      String query;
      query = String.format("INSERT INTO Orders (login, paid, timeStampRecieved, total) VALUES ('%s', '%s', '%s', '%s')", login, paidStatus, timeStampRecieved, price);
      esql.executeUpdate(query);

      System.out.println("Order created successfully!");

   }catch(Exception e){
         System.out.println("Unable to update order, please re-try or contact IT");
        
         return;
   }

   try{
         String query;
         query = String.format("SELECT orderid FROM Orders WHERE login = '%s' AND timeStampRecieved = '%s'", login, timeStampRecieved);
         tempORDERID = esql.executeQueryAndReturnResult(query);

   }catch(Exception e){
         System.err.println(e.getMessage());
   }

   orderID = tempORDERID.get(0).get(0);
   orderID = orderID.replaceAll(" ", "");

   for(int i = 0; i < itemNames.size(); i++){
   
      try{
         String query = String.format("INSERT INTO ItemStatus (orderid, itemName, lastUpdated, status) VALUES ('%s', '%s', '%s', '%s')", orderID, itemNames.get(i), timeStampRecieved, orderStatus);
         esql.executeUpdate(query);
      }catch(Exception e){
         System.err.println(e.getMessage());
      }
   }

   System.out.println("Items orderd successfully!");

   
}


   public static void UpdateOrder(Cafe esql, String login) {

      String query, loginType;
      List<List<String>> result = new ArrayList<List<String>>();

      // run query to get the type of current user
      try {

         // query to get the typefrom the current user
         query = String.format("SELECT type FROM Users WHERE login = '%s'", login);
         result = esql.executeQueryAndReturnResult(query);

      } catch (Exception e) {
         System.err.println(e.getMessage());
      }

      boolean inMenu = false;

  	   loginType = result.get(0).get(0);
	   loginType = loginType.replaceAll(" ", "");

      if(loginType.equals("Customer")){
         
         do{
            inMenu = true;

            System.out.printf("Welcome to update order user [%s]!\n", login);
            System.out.println("Choose an option");
            System.out.println("1. Modify Order");
            System.out.println("2. See order history (only 5 recent)");
            System.out.println("3. Return to main menu");

            switch(readChoice()){
               case 1: modifyOrder(esql , login);    break;
               case 2: outLast5Order(esql, login);    break;
               case 3: inMenu = false; break;
               default: System.out.println("Option does not exist, choose 1 - 3 !"); break;
            }


         }while(inMenu);


      }
      else if(!loginType.equals("Customer")){

         do{

            inMenu = true;

            System.out.printf("Welcome to employee/manager to update order. Current user ID: [%s]!\n", login);
            System.out.println("Choose an option");
	    System.out.println("1. Modify Order");
	    System.out.println("2. See order history (only 5 recent)");
	    System.out.println("3. See all orders within the last day");
	    System.out.println("4. Change order paid status");
	    System.out.println("5. Return to main menu");
		
            switch(readChoice()){
               case 1: modifyOrder(esql, login);	  break;
               case 2: outLast5Order(esql, login);        break;
               case 3: outputOrderHistroy(esql, login);   break;
	       case 4: changeOrderPaidStatus(esql, login);break;
	       case 5: inMenu = false; 			  break;
               default: System.out.println("Option does not exist, choose 1 - 5!"); break;
            }


         }while(inMenu);


      }


   }//end update order 

   public static void modifyOrder(Cafe esql, String login){

      String orderID, query, paidStatus, itemID, itemName, itemStatus;
      List<List<String>> result = new ArrayList<List<String>>();
      paidStatus = "t";
      boolean itemFound = false;
      while (true) {
         try {
            System.out.print("Enter order ID to modify the order: ");
            orderID = in.readLine();
            // if(nameName.length() > 50){
            // throw new SQLException("Name exceeds limit. Please try a smaller name.");
            // }
            break;
         } catch (Exception e) {
            System.out.println("Please input an orderID");
            continue;
         }
      }

      try {
         query = String.format("SELECT paid FROM Orders WHERE orderid = '%s' AND login = '%s'", orderID, login);
         //result = esql.executeQueryAndReturnResult(query);
         if(esql.executeQuery(query) == 0){
		throw new SQLException();
	 }
	 result = esql.executeQueryAndReturnResult(query);
 	 itemFound = true;
      } catch (Exception e) {
         System.out.println("This order ID either does not exist, or does not belong to you.");
      }
	
	if(itemFound){
		paidStatus = result.get(0).get(0);
     		 paidStatus = paidStatus.replaceAll(" ", "");
     		// System.out.println("paidStatus: " + paidStatus + "test");
	}
      if(!paidStatus.equals("t")){ // if the item status of the orderID is not paid

         //get the orders placed for a specific orderID
         try {
            query = String.format("SELECT itemName, lastUpdated, status FROM ItemStatus WHERE orderid = '%s'", orderID);
            result = esql.executeQueryAndReturnResult(query);
            
         } catch (Exception e) {
            System.out.println("ERROR: Unable to find orderID");
         }

         System.out.println("For this order ID, you ordered the following items below");
         
         for(int i = 0; i < result.size(); i++){
            for(int j = 0; j < result.get(i).size(); j++){
                  System.out.printf(result.get(i).get(j) + " ");
            }
            System.out.println();
         }

         int check; 
         while (true) {
            try {
               System.out.println("Which item would you like to modify?");

               for(int i = 0; i < result.size(); i++){
                  System.out.printf("%d. " + result.get(i).get(0), i);
                  System.out.println();
               }
               System.out.println("Please input the number for item:");
               check = Integer.parseInt(in.readLine());
               if(check < 0 || check > result.size()-1){
                  throw new SQLException();
               }
               break;
            } catch (Exception e) {
               System.out.println("The number you inputted was not in the menu listed above.");
               continue;
            }
         }
         
         itemName = result.get(check).get(0);
         itemName = itemName.replaceAll("\\s{2,}", "");
         //System.out.println("ItemName: " + itemName);
         boolean inMenu = false;
         //see if the itemName the user selected is in the menu
         try {

            query = String.format("SELECT itemName FROM Menu WHERE itemName = '%s'", itemName);

            if(esql.executeQuery(query) == 0){
               throw new SQLException();
            }

            inMenu = true;
            
         } catch (Exception e) {
            System.out.println("ERROR: ItemName you choose is not in menu ... please re-try");

         }



         if(inMenu){

            try {
               query = String.format("SELECT status FROM ItemStatus WHERE orderid = '%s' AND itemName = '%s'", orderID, itemName);

               result = esql.executeQueryAndReturnResult(query);
               
            } catch (Exception e) {
               System.out.println("ERROR: Unable to retreive records, please contact the devs or retry again. Thank you.");
   
            }

            itemStatus = result.get(0).get(0);
            itemStatus = itemStatus.replaceAll("\\s{2,}", "");
	    //System.out.println("itemStatus: " + itemStatus +"test");
            if(itemStatus.equals("Hasn't started")){
               boolean tempMenu = false;

               do{

                  tempMenu = true;

                  System.out.printf("You are now modifying item [%s] for orderID [%s]\n", itemName, orderID);
                  System.out.println("1. Would you like to see the menu?");
                  System.out.println("2. Modify order to a new order");
                  System.out.println("3. I changed my mind, I wanna keep the order!");
                  switch(readChoice()){
                     case 1: outputFullMenu(esql); break;
                     case 2: {

                        String newMenuItem, temp;
                        float previousPrice, newPrice;
                        boolean inMenuTwo = false;

                        while(true){
                           try{

                              System.out.println("Please enter new item name from menu to replace order!");
                              newMenuItem = in.readLine();

                              break;
   
                           }catch(Exception e){
                              System.out.println("Please input an item!");
                              continue;
                           }
                        }

                        try{
                           query = String.format("SELECT itemName FROM Menu WHERE itemName = '%s'", newMenuItem);
                           if(esql.executeQuery(query) == 0){
                              throw new SQLException();
                           }
                           inMenuTwo = true;
                        }catch(Exception e){
                           System.out.println("Item is not in the menu, please re-try!");
                        }
                        
                        if(inMenuTwo){

                           try{
                              query = String.format("SELECT price FROM Menu WHERE itemName = '%s'", itemName);
                              result = esql.executeQueryAndReturnResult(query);

                           }catch(Exception e){
                              System.out.println("Unable to retrive price, please re-try, or contact devs");
			      System.err.println(e.getMessage());
                           }

                           temp = result.get(0).get(0);
                           temp = temp.replaceAll(" ", "");
                           previousPrice = Float.parseFloat(temp);

                           try{
                              query = String.format("SELECT price FROM Menu WHERE itemName = '%s'", newMenuItem);
                              result = esql.executeQueryAndReturnResult(query);

                           }catch(Exception e){
                              System.out.println("Unable to retrieve price, please re-try, or contact devs");
			      System.err.println(e.getMessage());
                           }

                           temp = result.get(0).get(0);
                           temp = temp.replaceAll(" ", "");
                           newPrice = Float.parseFloat(temp);

                           float totalPrice = newPrice - previousPrice;

                           String newOrderPriceUpdate = String.valueOf(totalPrice);
                           
                           try{
                              query = String.format("UPDATE Orders SET total = '%s' WHERE orderid = '%s'", newOrderPriceUpdate, orderID);
                              esql.executeUpdate(query);

                           }catch(Exception e){
                              System.out.println("Unable to update order, please re-try, or contact devs");
			      System.err.println(e.getMessage());
                           }

                           long now = System.currentTimeMillis();
                           Timestamp time = new Timestamp(now);
                           String timestamp = time.toString();

                           try{
                              query = String.format("UPDATE ItemStatus SET itemName = '%s', lastUpdated = '%s' WHERE orderid = '%s' AND itemName = '%s'", newMenuItem, timestamp, orderID, itemName);
                              esql.executeUpdate(query);
				
			      System.out.println("Order successfully updated!");
                           }catch(Exception e){
                              System.out.println("Unable to update item, please re-try, or contact devs");
			      System.err.println(e.getMessage());
                           }

                           tempMenu = false;
                        }

                     } break;
                     case 3: tempMenu = false; break;
                     default: System.out.println("Choose either 1, 2 , or 3"); break;
                  }

               }while(tempMenu);

            }
            else if(!itemStatus.equals("Hasn't started")){
               System.out.println("The item has been started, you may not update this item for this specific ID, please choose another item");
            }

         }
      

      }
      else if(paidStatus.equals("t")){

         System.out.println("You may not modify this order");

      }

   }//end fucntion modify order
   public static void outputOrderHistroy(Cafe esql, String login){
	
	System.out.println("Order histroy from the past 24 hours.");
      List<List<String>> result = new ArrayList<List<String>>();
      String query;
	String timeString, dayBeforeString;

	long now = System.currentTimeMillis();
	Timestamp time = new Timestamp(now);
	timeString = time.toString();
	
	Calendar cal = Calendar.getInstance();
	cal.setTimeInMillis(time.getTime());
	cal.add(Calendar.DAY_OF_MONTH, -1);
	time = new Timestamp(cal.getTime().getTime());

	dayBeforeString = time.toString();
         try{
            
            //query = String.format("SELECT orderid, itemName FROM ItemStatus WHERE '%s' >= dateadd(day, datediff(day, 1, GETDATE()), 0) AND '%s' < dateadd(day, datediff(day,0,GETDATE()),0)", time, time);
            query = String.format("SELECT * FROM ItemStatus WHERE lastUpdated < '%s' AND lastUpdated >= '%s'", timeString, dayBeforeString); 
	    result = esql.executeQueryAndReturnResult(query);

         }catch(Exception e){
            //System.out.println("Unable to process the request, please contact devs");
            System.err.println(e.getMessage());
         }

         for(int i = 0; i < result.size(); i++){
            for(int j = 0; j < result.get(i).size(); j++){
                  System.out.printf(result.get(i).get(j) + " ");
            }
            System.out.println();
         }

      


      

   }

public static void outLast5Order(Cafe esql, String login){

	List<List<String>> result = new ArrayList<List<String>>();
      	String query;
	
	 System.out.println("Last 5 orders are listed below");

         try{

            query = String.format("SELECT I.orderid, I.itemName, I.lastUpdated, I.status, I.comments FROM ItemStatus I, Orders O WHERE I.orderid = O.orderid AND O.login = '%s' ORDER BY I.orderid DESC LIMIT 5", login);
//            result = esql.executeQueryAndReturnResult(query);
		esql.executeQueryAndPrintResult(query);
         }catch(Exception e){
            //System.out.println("Unable to process the request, please contact devs");
            System.err.println(e.getMessage());
         }

         //for(int i = 0; i < result.size(); i++){
         //   for(int j = 0; j < result.get(i).size(); j++){
         //         System.out.printf(result.get(i).get(j) + " ");
         //   } 
         //   System.out.println();
         //}	

	//System.out.println("Test");
}

   public static void changeOrderPaidStatus(Cafe esql, String login){

      String orderID;
      String paidStatus = "false";

      while (true) {
         try {
            System.out.println("What order ID would you like to change?");
            orderID = in.readLine();
            // if(nameName.length() > 50){
            // throw new SQLException("Name exceeds limit. Please try a smaller name.");
            // }
            break;
         } catch (Exception e) {
            System.out.println("Please input an orderID");
            continue;
         }
      }

      System.out.printf("What paid status would you like to give to this order for orderID [%s]?\n", orderID);
      System.out.println("1. Paid");
      System.out.println("2. Non-Paid");
      switch(readChoice()){
         case 1: paidStatus = "t"; break;
         case 2: paidStatus = "f"; break;
         default: System.out.println("Please input 1, 2"); break;

      }

      try{

         String query = String.format("UPDATE Orders SET paid = '%s' WHERE orderid = '%s'", paidStatus, orderID);
         esql.executeUpdate(query);
	 System.out.println("Status updated successfully!");
      }catch(Exception e){
         System.out.println("Order ID does not exist.");
      }

   }

}// end Cafe
