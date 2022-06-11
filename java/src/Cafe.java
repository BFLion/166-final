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
   }//end Cafe

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
      stmt.close ();
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
            Cafe.class.getName () +
            " <dbname> <port> <user>");
         return;
      }//end if

      Greeting();
      Cafe esql = null;
      try{
         // use postgres JDBC driver.
         Class.forName ("org.postgresql.Driver").newInstance ();
         // instantiate the Cafe object and creates a physical
         // connection.
         String dbname = args[0];
         String dbport = args[1];
         String user = args[2];
         esql = new Cafe (dbname, dbport, user, "");

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
                System.out.println("1. Goto Menu");
                System.out.println("2. Update Profile");
                System.out.println("3. Place a Order");
                System.out.println("4. Update a Order");
                System.out.println(".........................");
                System.out.println("9. Log out");
                switch (readChoice()){
                   case 1: Menu(esql, authorisedUser); break;
                   case 2: UpdateProfile(esql, authorisedUser); break;
                   case 3: PlaceOrder(esql, authorisedUser); break;
                   case 4: UpdateOrder(esql, authorisedUser); break;
                   case 9: usermenu = false; break;
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
   }//end CreateUser


   /*
    * Check log in credentials for an existing user
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
   }//end

// Rest of the functions definition go in here

  public static void Menu(Cafe esql, String login){
     
     String query, result;

     //run query to get the type of current user
     try{

         //query to get the typefrom the current user
         query = "SELECT type FROM Users WHERE login = '" + login + "';";
         //result = executeQueryAndReturnResult(query);

      }catch(Exception e){
         System.err.println(e.getMessage());
      }

     boolean inMenu = false;

      result = "Customer";
     if(result != "Manager"){

        do{
            inMenu = true;
            System.out.println("Welcome to the menu. How would you like to proceed?");
            System.out.println("1. Show full menu");
            System.out.println("2. Search item name");
            System.out.println("3. Search item type");
            System.out.println("4. Go back to main menu");
            switch(readChoice()){
               case 1: outputFullMenu(esql); break;
               case 2: searchItemName(esql); break;
               case 3: searchItemType(esql); break;
               case 4: inMenu = false; break;
               default: System.out.println("Choice not recognized!"); break;
            }

        }while(inMenu);
       
     }//end if result != manager
     else if(result == "Manager"){

        do{
           inMenu = true;

           System.out.println("Welcome to the menu. How would you like to proceed?");
           System.out.println("1. Show full menu");
           System.out.println("2. Search item name");
           System.out.println("3. Search item type");
           System.out.println("4. Add item");
           System.out.println("5. Delete item");
           System.out.println("6. Update item");
           System.out.println("7. Go back to main menu");
           switch(readChoice()){
              case 1: outputFullMenu(esql); break;
              case 2: searchItemName(esql); break;
              case 3: searchItemType(esql); break;
              case 4: addItem(esql);        break;
              case 5: deleteItem(esql);     break;
              case 6: updateItem(esql);     break;
              case 7: inMenu = false;       break;
              default: System.out.println("Choice not recognized!"); break;
           }
        }while(inMenu);  

     }//end if result == manager

      //end function menu
  }

  public static void outputFullMenu(Cafe esql){

  }

  public static void searchItemName(Cafe esql){

     boolean searchItem = false;
     String itemName;
     do{
        searchItem = true;
        while(true){
            try{
               System.out.print("Please input the name of item you would like to search: ");
               itemName = in.readLine();
               break;
            }catch(Exception e){
               System.out.println("Please input a valid input");
               continue;
            }
        }
        
         try{
            String query;
            query = String.format("SELECT itemName, type, price, description FROM Users WHERE itemName = '%s'", itemName);
            if(esql.executeQuery(query) < 0){
               System.out.println("This item does not exist in the menu.");
            }
            else{
               //execute the query to print out nicely into output stream
            }
         }catch(Exception e){
            System.err.println (e.getMessage ());
         }

        System.out.println("Would you like to search another item?");
        System.out.println("1. YES");
        System.out.println("2. NO");
        switch(readChoice()){
           case 1: break;
           case 2: searchItem = false; break;
           default: System.out.println("Please input a valid choice."); break;
        }
       
     }while(searchItem);

  }

  public static void searchItemType(Cafe esql){

     boolean searchType = false;
     String itemType;
     do{
        searchType = true;
        
        if(itemType == null || itemType.length() == 0){
           System.out.println("Invalid input.");
        }

         while(true){
            try{
               System.out.print("Please input the type of item you would like to search: ");
               itemType = in.readLine();
               break;
            }catch(Exception e){
               System.out.println("Please input a valid input");
               continue;
            }
        }

         try{
            String query;
            query = String.format("SELECT itemName, type, price, description FROM Users WHERE type = '%s'", itemType);
            if(esql.executeQuery(query) < 0){
               System.out.println("This item does not exist in the menu.");
            }
            else{
               //execute the query again to print out nicely into output stream
            }
         }catch(Exception e){
            System.err.println (e.getMessage ());
         }

      

        System.out.println("Would you like to search another item?");
        System.out.println("1. YES");
        System.out.println("2. NO");
        switch(readChoice()){
           case 1: break;
           case 2: searchType = false; break;
           default: System.out.println("Please input a valid choice."); break;
        }
       
     }while(searchType);

  }
  public static void addItem(Cafe esql){

     boolean addItem = false;
     String itemName, itemType, itemPrice, itemDescription, itemURL;

     do{
        addItem = true;
        while(true){
           try{
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
           }catch(Exception e){
              System.out.println("Input error. Please input corret values");
              continue;
           }
        }

       

       try{
         
          String query;
          query = String.format("SELECT itemName FROM Menu WHERE itemName = '%s'", itemName);
          
         if(esql.executeQuery(query) > 0){
            //check if item is in menu
            System.out.println("Item is already in menu! Please retry");
         }
         else{

            query = String.format("INSERT INTO Menu (itemName, type, price, description, imageURL) VALUES ('%s', '%s', '%s', '%s', '%s')", itemName, itemType, itemPrice, itemDescription, itemURL);
            esql.executeUpdate(query);
            System.out.println("New item added successfully!");
         }

       }catch(Exception e){
          System.err.println(e.getMessage());
       }


      System.out.println("Would you like to add another item?");
      System.out.println("1. YES");
      System.out.println("2. NO");
      switch(readChoice()){
         case 1: break;
         case 2: addItem = false; break;
         default: System.out.println("Please input a valid choice."); break;
      }

     }while(addItem);


  }
  public static void deleteItem(Cafe esql){

     boolean delItem = false;
     String itemName;
     do{
        delItem = true;

         while(true){
            try{
               System.out.println("Which item from the menu would you like to delete");
               itemName = in.readLine();
               break;
            }catch(Exception e){
               System.out.println("Please input a valid input");
               continue;
            }
        }

        try{
           String query;

           query = String.format("DELETE FROM Menu WHERE itemName = '%s'", itemName);
           esql.executeUpdate(query);
           System.out.println("Item deleted successfully!");

        }catch(Exception e){
           System.out.println("Some error occured. Please re-try, or either the item does not exist in Menu ");
        }

      System.out.println("Would you like to delete another item?");
      System.out.println("1. YES");
      System.out.println("2. NO");
      switch(readChoice()){
         case 1: break;
         case 2: delItem = false; break;
         default: System.out.println("Please input a valid choice."); break;
      }

     }while(delItem);

  }
public static void updateItem(Cafe esql){

   boolean upItem = false;
   String itemName;
   int ifExists = 0;

   do{
      upItem = true;

      while(true){
            try{
               System.out.println("What item would you like to update (itemName)");
               itemName = in.readLine();
               break;
            }catch(Exception e){
               System.out.println("Please input a valid input");
               continue;
            }
        }

      try{
         String query = String.format("SELECT itemName FROM Menu WHERE itemName = '%s'", itemName);
         ifExists = esql.executeQuery(query);
      }catch(Exception e){
         System.out.println("Error getting item, please input an exisitng item, or item does not exist");
      }
      
      if(ifExists > 0){
         boolean currentItem = false;
         String newItemName = itemName;

         do{
            currentItem = true;
            
            System.out.printf("What item [%s] would you like to update?\n", itemName);
            System.out.println("1. Name");
            System.out.println("2. Type");
            System.out.println("3. Price");
            System.out.println("4. Description");
            System.out.println("5. imageURL");
            System.out.println("6. Exit updating current item");
            switch(readChoice()){
               case 1: newItemName = updateMenuName(esql, itemName); break;
               case 2: updateMenuType(esql, newItemName); break;
               case 3: updateMenuPrice(esql, newItemName); break;
               case 4: updateMenuDescription(esql, newItemName); break;
               case 5: updateMenuImageURL(esql, newItemName); break;
               case 6: currentItem = false; break;
               default: System.out.println("Please input a valid choice."); break;
            
            }
         }while(currentItem);

      }
      else if(ifExists < 0){
         System.out.println("Item does not exist in Menu, try adding instead of updating. ");
      }

      System.out.println("Would you like to update another item?");
      System.out.println("1. YES");
      System.out.println("2. NO");
      switch(readChoice()){
         case 1: break;
         case 2: upItem = false; break;
         default: System.out.println("Please input a valid choice."); break;
      }

     }while(upItem);

  }

public static String updateMenuName(Cafe esql, String itemName){

   String newName;
   while(true){
         try{
            System.out.printf("Enter new name to be updated for item [%s]\n", itemName);
            newName = in.readLine();

            // if(nameName.length() > 50){
            //    throw new SQLException("Name exceeds limit. Please try a smaller name.");
            // }
            break;
         }catch(Exception e){
            System.out.println("ERROR: Please input a name");
            continue;
         }
   }

   try{
      String query;
      query =  String.format("SELECT itemName FROM Menu WHERE itemName = '%s'", newName);

      if(esql.executeQuery(query) > 0){
         System.out.println("The name you choose already exists, please re-try and choose a new one.");

         return itemName;
      }
      else{

         query =  String.format("UPDATE Menu SET itemName = '%s' WHERE itemName = '%s'", newName, itemName);
         esql.executeUpdate(query);
         System.out.println("Name updated successfully!");
      }

   }catch(Exception e){
      System.out.println("Error when updating, please re-try or contact IT.");
   }

   return newName;
}

public static void updateMenuType(Cafe esql, String itemName){

   String newType;
   while(true){
         try{
            System.out.printf("Enter new type to be updated for item [%s]\n", itemName);
            newType = in.readLine();

            // if(nameName.length() > 50){
            //    throw new SQLException("Name exceeds limit. Please try a smaller name.");
            // }
            break;
         }catch(Exception e){
            System.out.println("ERROR: Please input a type");
            continue;
         }
   }

   try{
      String query;
      query =  String.format("UPDATE Menu SET type = '%s' WHERE itemName = '%s'", newType, itemName);
      esql.executeUpdate(query);
      System.out.printf("Typed updated successfully for item [%s]\n", itemName);

   }catch(Exception e){
      System.out.println("Error when updating, please re-try or contact IT.");
   }

}
public static void updateMenuPrice(Cafe esql, String itemName){

   String newPrice;
   while(true){
         try{
            System.out.printf("Enter new price to be updated for item [%s] (do not include $)\n", itemName);
            newPrice = in.readLine();

            // if(nameName.length() > 50){
            //    throw new SQLException("Name exceeds limit. Please try a smaller name.");
            // }
            break;
         }catch(Exception e){
            System.out.println("ERROR: Please input a type");
            continue;
         }
   }

   try{
      String query;
      query =  String.format("UPDATE Menu SET price = '%s' WHERE itemName = '%s'", newPrice, itemName);
      esql.executeUpdate(query);
      System.out.printf("Price updated successfully for item [%s]\n", itemName);

   }catch(Exception e){
      System.out.println("Error when updating, please re-try or contact IT.");
   }

}
public static void updateMenuDescription(Cafe esql, String itemName){

   String newDes;

   while(true){
      try{
         System.out.printf("Enter new description to be updated for item [%s]\n", itemName);
         newDes = in.readLine();
         break;
      }catch(Exception e){
         System.out.println("Please input a valid input");
         continue;
      }
   }
   
   try{
      String query;
      query =  String.format("UPDATE Menu SET description = '%s' WHERE itemName = '%s'", newDes, itemName);
      esql.executeUpdate(query);
      System.out.printf("Description updated successfully for item [%s]\n", itemName);

   }catch(Exception e){
      System.out.println("Error when updating, please re-try or contact IT.");
   }


}
public static void updateMenuImageURL(Cafe esql, String itemName){

   String newURL;

   while(true){
      try{
         System.out.printf("Enter new description to be updated for item [%s]\n", itemName);
         newURL = in.readLine();
         break;
      }catch(Exception e){
         System.out.println("Please input a valid input");
         continue;
      }
   }
   
   try{
      String query;
      query =  String.format("UPDATE Menu SET imageURL = '%s' WHERE itemName = '%s'", newURL, itemName);
      esql.executeUpdate(query);
      System.out.printf("imageURL updated successfully for item [%s]\n", itemName);

   }catch(Exception e){
      System.out.println("Error when updating, please re-try or contact IT.");
   }


}

public static void UpdateProfile(Cafe esql, String login){


   String query, result;

   result = "Customer";

   //run query to get the type of current user
   try{

      //query to get the typefrom the current user
      query = "SELECT type FROM Users WHERE login = '" + login + "';";
      //result = executeQueryAndReturnResult(query);

   }catch(Exception e){
      System.err.println(e.getMessage());
   }

   boolean inMenu = false;

   if(result != "Manager"){
      
      String userName =  login;
      do{
         inMenu = true;
         System.out.println("Which information would you like to update?");
         System.out.println("1. Login");
         System.out.println("2. Phone Number");
         System.out.println("3. Password");
         System.out.println("4. Favorite Items");
         System.out.println("5. Go back to Main Menu");
         switch(readChoice()){
            case 1: userName = updateLogin(esql, login); break;
            case 2: updatePhoneNumber(esql, userName);      break;
            case 3: updatePassword(esql, userName);         break;
            case 4: updateFavItems(esql, userName);         break;
            case 5: inMenu = false;                      break;
            default: System.out.println("Choice not recognized!"); break;
         }

      }while(inMenu);
      
   }//end if result != manager
   else if(result == "Manager"){

      String userName = login;

      do{
         inMenu = true;

         System.out.println("Which information would you like to update?");
         System.out.println("1. Login");
         System.out.println("2. Phone Number");
         System.out.println("3. Password");
         System.out.println("4. Favorite Items");
         System.out.println("5. Change type");
         System.out.println("6. Modify other user");
         System.out.println("7. Go back to Main Menu");
         switch(readChoice()){
            case 1: userName = updateLogin(esql, login); break;
            case 2: updatePhoneNumber(esql, userName);      break;
            case 3: updatePassword(esql, userName);         break;
            case 4: updateFavItems(esql, userName);         break;
            case 5: updateType(esql, userName);             break;
            case 6: managerUpdateUser(esql, userName);      break;
            case 7: inMenu = false;                      break;
            default: System.out.println("Choice not recognized!"); break;
         }
      }while(inMenu);  

   }//end if result == manager

   //end function updateUser

}

public static String updateLogin(Cafe esql, String login){

   String newLogin;
   while(true){
      try{

         System.out.printf("Please enter new user login for [%s]\n", login);
         newLogin = in.readLine();
         break;
      }catch(Exception e){
         System.out.println("Please enter a valid login. ");
         continue;
      }
   }

   try{
      String query;
      query =  String.format("SELECT login FROM Users WHERE login = '%s'", newLogin);

      if(esql.executeQuery(query) > 0){
         System.out.println("The login you choose already exists, please re-try and choose a new one.");

         return login;
      }
      else{
         query =  String.format("UPDATE Users SET login = '%s' WHERE login = '%s'", newLogin, login);
         esql.executeUpdate(query);
         System.out.println("Login updated successfully!");
      }
   }catch(Exception e){
      System.out.println("Error updating User, please re-try or contact IT");
   }
   
   return newLogin;

}
public static void updatePhoneNumber(Cafe esql, String login){

   String newPhoneNumber;
   while(true){
      try{

         System.out.printf("Please enter new user phone number for [%s]\n", login);

         //TODO add line where it shows the user current phone number
         newPhoneNumber = in.readLine();
         break;
      }catch(Exception e){
         System.out.println("Please enter a valid phone number.");
         continue;
      }
   }

   try{
      String query;
      query =  String.format("SELECT phoneNum FROM Users WHERE phoneNum = '%s'", newPhoneNumber);

      if(esql.executeQuery(query) > 0){
         System.out.println("The phone number you choose already exists, please re-try and choose a new one.");

      }
      else{
         query =  String.format("UPDATE Users SET phoneNum = '%s' WHERE login = '%s'", newPhoneNumber, login);
         esql.executeUpdate(query);
         System.out.printf("Phone number updated successfully for user [%s]!", login);
      }
   }catch(Exception e){
      System.out.println("Error updating User, please re-try or contact IT");
   }


}

public static void updatePassword(Cafe esql, String login){

   String newPassword, passwordTwo;
   while(true){
      try{

         System.out.printf("Please enter new user password for [%s]\n", login);
         //TODO add line where it shows the user current phone number
         newPassword = in.readLine();

         System.out.println("Please re-type password");
         passwordTwo = in.readLine();

         if(!passwordTwo.equals(newPassword)){
            throw new SQLException("Passwords do not match, please re-type!");
         }

         break;
      }catch(Exception e){
         System.out.println("Please enter a valid password.");
         continue;
      }
   }

   try{

      String query;
      query =  String.format("UPDATE Users SET password = '%s' WHERE login = '%s'", newPassword, login);
      esql.executeUpdate(query);
      System.out.printf("Password updated successfully for user [%s]!", login);
   
   }catch(Exception e){
      System.out.println("Error updating User, please re-try or contact IT");
   }
}

public static void updateFavItems(Cafe esql, String login){

   String newFavItems;

   while(true){
      try{
         System.out.printf("Please enter new favorite items for [%s]\n", login);
         //TODO add line where it shows the user current phone number
         newFavItems = in.readLine();
         break;
      }catch(Exception e){
         System.out.println("Please input a valid input");
         continue;
      }
   }

   try{

      String query;
      query =  String.format("UPDATE Users SET favItems = '%s' WHERE login = '%s'", newFavItems, login);
      esql.executeUpdate(query);
      System.out.printf("Favorite items updated successfully for user [%s]!", login);
   
   }catch(Exception e){
      System.out.println("Error updating User, please re-try or contact IT");
   }

}
public static void updateType(Cafe esql, String login){

   String newType = null;
   String input;

   System.out.println("DANGEROUS ACTION! If you are admin and remove admin status for yourself, you will not have admin access anymore.");

   

   while(true){
      try{
         System.out.println("Proceed!???? 1 for yes 0 for no");
         input = in.readLine();
         break;
      }catch(Exception e){
         System.out.println("Please input a valid input");
         continue;
      }
   }

   if(!input.equals("1")){
      return;
   }

   while(true){
      try{

         System.out.printf("Please enter choice for new user type for [%s]\n", login);
         System.out.println("1. Employee");
         System.out.println("2. Customer");
         switch(readChoice()){
            case 1: newType = "Employee"; break;
            case 2: newType = "Customer"; break;
            default: System.out.println("Please enter valid choice"); break;
         }

         if(newType == null){
            throw new SQLException("Re-trying choice ...");
         }

         break;
      }catch(Exception e){
         continue;
      }
   }

   try{

      String query;
      query =  String.format("UPDATE Users SET type = '%s' WHERE login = '%s'", newType, login);
      esql.executeUpdate(query);
      System.out.printf("User type updated successfully for user [%s]!", login);
   
   }catch(Exception e){
      System.out.println("Error updating User, please re-try or contact IT");
   }

}
public static void managerUpdateUser(Cafe esql, String login){

   String editUserLogin;

   //get user to edit from manager
   while(true){
      try{
         System.out.println("Please enter a user login to edit");
         editUserLogin = in.readLine();

         break;
      }catch(Exception e){
         System.out.println("Please enter an input");
         continue;
      }
   }

   //once have user login, check if the user exists
   int userExists = 0;

   try{
      String query;
      query = String.format("SELECT login FROM Users WHERE login = '%s'", editUserLogin);

      userExists = esql.executeQuery(query);
       
   }catch(Exception e){
      System.out.println("ERROR processing user find, please re-try");
   }
   
   boolean inMenu = false;

   if(userExists > 0){

      String userName = editUserLogin;

      do{
         inMenu = true;

         System.out.printf("Which information would you like to update for user [%s]?", editUserLogin);
         System.out.println("1. Login");
         System.out.println("2. Phone Number");
         System.out.println("3. Password");
         System.out.println("4. Favorite Items");
         System.out.println("5. Change type");
         System.out.println("6. Go back to Manager Menu");
         switch(readChoice()){
            case 1: userName = updateLogin(esql, editUserLogin); break;
            case 2: updatePhoneNumber(esql, userName);      break;
            case 3: updatePassword(esql, userName);         break;
            case 4: updateFavItems(esql, userName);         break;
            case 5: updateType(esql, userName);             break;
            case 6: inMenu = false;                              break;
            default: System.out.println("Choice not recognized!"); break;
         }
      }while(inMenu);  

   }
   else{
      System.out.println("Could not find user, please re-try");
   }

}




  public static void PlaceOrder(Cafe esql, String login){}

  public static void UpdateOrder(Cafe esql, String login){}

}//end Cafe

