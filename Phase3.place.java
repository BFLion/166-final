public static void PlaceOrder(Cafe esql) {

   String login;

   System.out.println("Enter user login: ");
   try{
   login = in.readLine();
}catch (Exception e) {
   System.out.println(e.getMessage ());
   continue;
}

   String ItemName;
   System.out.println("Enter Item's name: ")
   try{

   ItemName = in.readLine();
}catch (Exception e) {
   System.out.println(e.getMessage ());
   continue;
}


   String paid；

      System.out.println("Paid? (true/false): ");
      try{
      paid = in.readLine();
   }catch (Exception e) {
      System.out.println(e.getMessage ());
      continue;
   }



   int ID; //order ID

      System.out.println("Input Your order ID Number: ");
      try {
         ID = Integer.parseInt(in.readLine());
         break;
      }catch (Exception e) {
         System.out.println("Your input is invalid!");
         continue;
      }

   long now = System.currentTimeMillis();
   Timestamp sqlTimestamp = new Timestamp(now);

   
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

   String ordertype;
   System.out.println("What is the order status (Finished/ Started/Hasn't started): ")
   try{
   ItemName = in.readLine();
   }catch (Exception e) {
      System.out.println(e.getMessage ());
      continue;
   }


   try {
      String query = "INSERT INTO ORDERS (orderid，login, Paid，  sqlTimestamp, total) VALUES (" + ID + ", \'" + login +", \'" + paid +  ", \'" + sqlTimestamp + ", \'" + total ");";
      
      esql.executeUpdate(query);
   }catch (Exception e) {
      System.err.println (e.getMessage());
   }


   try{
      String query0 = "INSERt INTO ItemStatus (orderid, itemName, sqlTimestamp, OrderType) VALUES (" + ID + ", \'"  + ItemName + ", \'" + sqlTimestamp + ", \'" + ordertype ");";
      esql.executeUpdate(query);
   }catch (Exception e) {
      System.err.println (e.getMessage());
   }

}//end PlaceOrder
