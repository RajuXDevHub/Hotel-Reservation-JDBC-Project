import javax.swing.*;
import javax.swing.plaf.nimbus.State;
import java.sql.*;
import java.util.Scanner;


public class HotelReservationSystem {

    public static final String url = "jdbc:mysql://localhost:3306/hotel_db";

    public static final String username = "root";

    public static final String password = "Raju@123";

    public static void main(String[] args) throws ClassNotFoundException , SQLException{

        //* For Loading The Driver
        try{
            Class.forName("com.mysql.cj.jdbc.Driver");
            System.out.println("driver Loaded successfully");

        }catch ( ClassNotFoundException e ){
            System.out.println(e.getMessage());

        }

        //? For connection establishing purpose
        try{
            Connection connection = DriverManager.getConnection(url,username,password);
            Statement statement = connection.createStatement();

            while (true){
                System.out.println();
                System.out.println("^^ HOTEL RESERVATION SYSTEM ^^");
                Scanner snc = new Scanner(System.in);
                System.out.println("1. Reserve a room ");
                System.out.println("2. View Reservation ");
                System.out.println("3. Get Room Number ");
                System.out.println("4. Update Reservations ");
                System.out.println("5. Delete Reservations ");
                System.out.println("0. Exit ");
                System.out.print("Choose an option: ");
                int choice = snc.nextInt();
                switch (choice){
                    case 1 :
                        reserveRoom( snc,statement);
                        break;
                    case 2 :
                        viewReservation(statement);
                        break;
                    case 3 :
                        getRoomNumber( snc , statement);
                        break;
                    case 4 :
                        updateReservation( connection , snc ,statement);
                        break;
                    case 5 :
                        deleteDeservation( connection , snc , statement);
                        break;
                    case 0 :
                        exit();
                        snc.close();
                        return;
                    default:
                        System.out.println("Invalid choice , Please Try again .");
                }

            }
        }catch (SQLException e){
            System.out.println(e.getMessage());
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private static void reserveRoom( Scanner snc , Statement statement){
        System.out.print("Enter guest name: ");
        String guestName = snc.next();
        snc.nextLine();
        System.out.print("Enter room number: ");
        int roomNumber = snc.nextInt();
        System.out.print("Enter contact number: ");
        String contactNumber = snc.next();


        String sql = "INSERT INTO reservation (guest_name, room_number, contact_number) " +
                "VALUES ('" + guestName + "', " + roomNumber + ", '" + contactNumber + "');";

        try{
            int affectRows = statement.executeUpdate(sql);

            if ( affectRows >0 ){
                System.out.println("Reservation Successful!");
            }else{
                System.out.println("Reservation Failed!");
            }
        }catch (SQLException e ){
            e.printStackTrace();
        }
    }

    private static void viewReservation(Statement statement ){
        String sql = "SELECT reservation_id , guest_name , room_number , contact_number , reservation_date FROM reservation ; ";

        try{
            ResultSet resultSet = statement.executeQuery(sql);

            System.out.println("Current Reservations: ");
            System.out.println("+----------------+----------------+----------------+----------------+------------------------+");
            System.out.println("|Reservation ID  | Guest Name     | Room Number    | Contact Number | Reservation Date       |");
            System.out.println("+----------------+----------------+----------------+----------------+------------------------+");

            while( resultSet.next()){
                int reservationId = resultSet.getInt("reservation_id");
                String guestName  = resultSet.getString("guest_name");
                int roomNumber  = resultSet.getInt("room_number");
                String contactNumber = resultSet.getString("contact_number");
                String reservationDate = resultSet.getTimestamp("reservation_date").toString();

                // Fornat and display the reservation data in a table-like formate
                System.out.printf("| %-14d | %-15s | %-13d | %-15s | %-15s  |\n", reservationId, guestName, roomNumber, contactNumber,reservationDate );
            }
            System.out.println("+----------------+----------------+----------------+----------------+------------------------+");

        }catch (SQLException e ){
            e.printStackTrace();
        }
    }

    public static void getRoomNumber( Scanner snc ,Statement statement){
        System.out.print("Enter Reservation Id: ");
        int reservationId = snc.nextInt();
        System.out.print("Enter Guest Name : ");
        String guestName = snc.next();

        String sql = "SELECT room_number FROM reservation WHERE reservation_id = "
                + reservationId + " AND guest_name = '" + guestName + "';";

        try {
            ResultSet resultSet = statement.executeQuery(sql);
            if (resultSet.next()){
                int roomNumber = resultSet.getInt("room_number");
                System.out.println("Room Number for Reservation Id :"+reservationId +" and Guest: "+guestName + " is : "+roomNumber);
            }else {
                System.out.println("Reservation not found for the given Id and Name ....");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private static void updateReservation( Connection connection , Scanner snc , Statement statement){
        try {
            System.out.print("Enter reservation Id to Update :");
            int reservationId = snc.nextInt();
            snc.nextLine(); //* Consume the next Line Character

            if (!reservationExists(connection, reservationId , statement)) {
                System.out.println("Reservation not found for the given Id ...");
                return;
            }

            System.out.print("Enter new guest name : ");
            String newGuestName = snc.next();
            System.out.print("Enter new room Number : ");
            int newRoomNumber = snc.nextInt();
            System.out.print("Enter new contact Number : ");
            String newContactNumber = snc.next();


            String sql = "UPDATE reservation SET guest_name = '" + newGuestName +
                    "', room_number = " + newRoomNumber +
                    ", contact_number = '" + newContactNumber +
                    "' WHERE reservation_id = " + reservationId + ";";

            try{
                int affectedRows = statement.executeUpdate(sql);
                if( affectedRows > 0){
                    System.out.println("Reservation Updated Successfully ");
                }else {
                    System.out.println("Reservation Updated Faild ");
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }

        } catch (RuntimeException e) {  //* Parent type exception
            throw new RuntimeException(e);
        }
    }
    private static void deleteDeservation( Connection connection , Scanner snc , Statement statement ){
        try{
            System.out.print("Enter Reservation Id to Delete : ");
            int reservationId = snc.nextInt();
            if ( !reservationExists(connection , reservationId , statement)){
                System.out.println("Reservation not found for the Given Id ");
                return;
            }

            String sql = "DELETE FROM reservation WHERE reservation_id = "+ reservationId+";";

            try{
                int rowsAffects = statement.executeUpdate(sql);
                if ( rowsAffects >0 ){
                    System.out.println("Reservation Deleted Successfully ...");
                }else {
                    System.out.println("Reservation Deletion Failed ..");
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }

        } catch (RuntimeException e) {
            throw new RuntimeException(e);
        }
    }
    private static boolean reservationExists( Connection connection , int reservationId , Statement statement ){
        try {
            String sql = "SELECT reservation_id FROM reservation WHERE reservation_id = "+reservationId+ ";";
            try{
                ResultSet resultSet = statement.executeQuery(sql);
                return resultSet.next(); //? if there's a result, the reservation id exists
            } catch (SQLException e) {
                e.printStackTrace();
                return false;
            }
        } catch (RuntimeException e) {
            e.printStackTrace();
            return false; //? Handel database error as needed
        }

    }
    public static void exit() throws InterruptedException{
        System.out.print("Exiting System");
        int i = 5 ;
        while ( i != 0 ){
            System.out.print(".");
            Thread.sleep(450);
            i--;
        }
        System.out.println();
        System.out.println("ThankYou For Using Hotel Reservation System!!!");



    }

}