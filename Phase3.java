

public static void PlaceOrder(Cafe esql) {
   int ID; //order ID
   
   do {
      System.out.print("Input Your order ID Number: ");
      try {
         ID = Integer.parseInt(in.readLine());
         break;
      }catch (Exception e) {
         System.out.println("Your input is invalid!");
         continue;
      }
   }while (true);
   
   String timestampReceived;
   
   do {
      System.out.print("Input time stamp Received: ");
      try {
         make = in.readLine();
      }catch (Exception e) {
         System.out.println(e);
         continue;
      }
   }while (true);
   
   String paid;
   
   do {
      System.out.print("Input paid or not (Yes/No): ");
      try {
         paid = in.readLine();
      }catch (Exception e) {
         System.out.println(e);
         continue;
      }
   }while (true);
   
   int total;
   
   do {
      System.out.print("Input total price: ");
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
         System.out.println(e);
         continue;
      }
   }while (true);
   

   
   try {
      String query = "INSERT INTO Order (id, timestampReceived, paid, total) VALUES (" + id + ", \'" + timestampReceived + "\', \'" + model + "\', " + paid + ", " + total + ");";
      
      esql.executeUpdate(query);
   }catch (Exception e) {
      System.err.println (e.getMessage());
   }
}
