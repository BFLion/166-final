public static void UpdateOrder(Cafe esql){
   try{
      boolean updateorder = true;

      while(updateorder){
         System.out.print("which order you want to change(ID): ");
         String changedid = in.readLine();

         String query0 = String.format("SELECT * FROM ORDERS WHERE orderid = '%s' AND type = Hasn’t started ", changedid);
         int HasntStart = esql.executeQuery(query0);
          if (HasntStart > 0){
             while(updateorder){
                System.out.println("What do you want to do with your order?");
                System.out.println("---------------------------------------");
                System.out.println("1. OrderID");
                System.out.println("2. ItemName");
                System.out.println("3. Type");
                System.out.println("4. Timestamp Received");
                System.out.println(".........................");
                System.out.println("9. < Exit");

                switch (readChoice()){
                   case 1: System.out.println("Enter new OrderID: ");
                         String neworderid = in.readLine();
                         String query1 = String.format("UPDATE ORDERS SET orderid = '%s' orderid = '%s'" , neworderid, changedid);
                         esql.executeUpdate(query1);
                         System.out.println ("Successfully updated!");
                         break;
                   case 2: System.out.println("What is new Item?:");
                         String newItemName = in.readLine();
                         String query2 = String.format("UPDATE ORDERS SET login = '%s' orderid = '%s'" , newItemName, changedid);
                         esql.executeUpdate(query2);
                         System.out.println ("Successfully updated!");
                         break;
                   case 3: System.out.println ("Are you a manager? ");
                         System.out.print("\tEnter user login: ");
                         String login = in.readLine();

                         String query3 = String.format("SELECT * FROM USERS WHERE login = '%s'", login);
                         int loginCheck = esql.executeQuery(query3);
                         if (loginCheck > 0){
                            System.out.println("You are not a manager or an employee.");
                            break;
                         }//end if
                         else{
                            String query4 = String.format("UPDATE ORDERS SET type = Finished WHERE orderid = '%s'", changedid);
                            esql.executeQuery(query4);
                            System.out.println("Order finished!");
                            break;
                         }//end else
                   case 4: System.out.println("Enter new Timestamp: ");
                         String newtimestamp = in.readLine();
                         String query5 = String.format("UPDATE ORDERS SET timeStampReceived = '%s' WHERE orderid = '%s'" , newtimestamp, changedid);
                         esql.executeUpdate(query);
                         System.out.println ("Timestamp successfully updated!");
                         break;
                   case 9: updateorder = false; break;
                   default: System.out.println("Unrecognized choice!"); break;
                }//end switch
             }//end while
             
          }//end if

    System.out.println("This order is already Finished.");
    break;
      }//end while
   }catch(Exception e){
       System.err.println (e.getMessage ());
       }//end try and catch
}//end UpdateOrder function
