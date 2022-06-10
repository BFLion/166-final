public static void PlaceOrder(Cafe esql) {

   String login;
   System.out.println("Enter user login: ");
   login = in.readLine();


   int ID; //order ID

      System.out.println("Input Your order ID Number: ");
      try {
         ID = Integer.parseInt(in.readLine());
         break;
      }catch (Exception e) {
         System.out.println("Your input is invalid!");
         continue;
      }

   String timestampReceived;
   
      System.out.println("Input time stamp Received: ");
      try {
         timestampReceived = in.readLine();
      }catch (Exception e) {
         System.out.println(e.getMessage ());
         continue;
      }

   
   int total;

      System.out.println("Input total price: ");
      try {
         total = Integer.parseInt(in.readLine());
         if(total < 0) {
            throw new RuntimeException("Price cannot be negative");
         }
         break;
      }catch (NumberFormatException e) {
         System.out.println("Your input is invalid!");
         continue;
      }catch (Exception e) {
         System.out.println(e.getMessage ());
         continue;
      }

   try {
      String query = "INSERT INTO Order (login, id , timestampReceived, total) VALUES (" + login + ", \'" + ID + "\', \'" + timestampReceived + "\', " + total ");";
      
      esql.executeUpdate(query);
   }catch (Exception e) {
      System.err.println (e.getMessage());
   }
}

