//
// jdbc sample for postgres.cs
// 
// Amherst College
// Steffen Plotner, 2016
//
import java.sql.*; //driver manager exists in here
import java.util.*;

	// Get URL : https://jdbc.postgresql.org/download/postgresql-9.4.1208.jar
	// Get main class name
	// loadClass(name, true)

public class Boating
{
	


	public static void main(String[] argv) throws SQLException
	{

			BoatController gaz = new BoatController();
			print("Presenting... The Sailors:");
			gaz.printAll();
			runScript(gaz);
			
	}

	public static void runScript(BoatController gaz){
		Scanner reader = new Scanner(System.in);
		String question;
		while(true){
				print("What would you like to do?\n");
				print("To make a new sailor, type \"newSailor\"");
				print("To delete a sailor, type \"removeSailor\"");
				print("To make a reservation, type newReservation <sailorID> <boatID> ");
				print("To cancel a reservation,\n\t type removeReservation <sid> <bid> <year> <month> <day>");

				print("To see all sailors, boats, and reservations, type \"printAll\"" );
				print(" ");

				question = reader.nextLine();
				int sid = 0; String name = null; double age = 0; int rating = 0;
				int bid = 0; int year = 0; int month = 0; int day = 0; 
				String response;	String[] responses;
				if(question.equals("newSailor")) { 
					print("type your sailor's <name> <age(double)> <rating>");
					response = reader.nextLine();
					responses = response.split("\\s+");
					try{
							name = responses[0]; age = Double.parseDouble(responses[1]); 
							rating = Integer.parseInt(responses[2]);
					}	
						catch (IllegalArgumentException a) {
					   		print("You typed: "+response +". At least one answer is the incorrect type.");
					   		continue;
					   	}
				}
				else if(question.equals("removeSailor")){
					print("type the <sid> of the sailor to remove.");
					response = reader.nextLine();
					try{sid = Integer.parseInt(response);} 
					catch(IllegalArgumentException b){ print("SID is an integer."); continue; }
				}
				else if(question.equals("newReservation") || question.equals("removeReservation")){
					print("type the <sailorid> <boatid> <year> <month> <day> of reservation.");
					response = reader.nextLine();
					responses = response.split("\\s+");
					try{sid = Integer.parseInt(responses[0]); bid = Integer.parseInt(responses[1]);
						year = Integer.parseInt(responses[2]); month = Integer.parseInt(responses[3]);
						day = Integer.parseInt(responses[4]);}
					catch(IllegalArgumentException c) {
						print("You typed: "+response +". At least one answer is the incorrect type.");
						continue;
					}
				}
				else if(question.equals("printAll")) {
					try {gaz.printAll();} 
					catch(SQLException d) { print("failed to print all from database"); continue;}
				}
				else {print(question + " is not a valid command!"); continue;} 

				gaz.act(question, sid, name, age, rating, bid, year, month, day); // made it
				print("You command had been submitted");
				print("");

			}

	}

	public static void print(String s) {System.out.println(s);}
	
	
}

