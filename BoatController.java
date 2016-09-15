

import java.sql.*; //driver manager exists in here
import java.util.*;

public class BoatController {

	static final String JDBC_DRIVER = "org.postgresql.Driver"; //name of folder of downloaded driver
															// needs to be in class path
	String boat = "mburke16.boat";
	String reservation = "mburke16.reservation"; 
	String sailor = "mburke16.sailor";
	String places = "places";
	String places_staging = "places_staging";
	String tbl_public = "tbl_public";
	String tbl_test = "tbl_test";
	Connection db_conn = null;
	Statement stm = null;
	String myName;
	Calendar calendar = new GregorianCalendar();

	public BoatController() {this("gaz:");}
	public BoatController(String name){
		myName = "gaz";
		try  {	Class.forName(JDBC_DRIVER); }
		catch (ClassNotFoundException e) {	e.printStackTrace(); return; }
		try	{
				System.out.println(myName+" Loading Sailor Reservation Program...");	
				db_conn = DriverManager.getConnection( 
					"jdbc:postgresql://postgres.cs.amherst.edu:5432/cosc251",
					"mburke16", // System.getenv("USER"),
					"silve22r13577" // System.getenv("JDBC_PASSWORD")
					);
				stm = db_conn.createStatement(
                                      ResultSet.TYPE_SCROLL_INSENSITIVE, 
                                      ResultSet.CONCUR_UPDATABLE); //statement object 
		}
		catch (SQLException e) { 
				System.out.println(myName+" Failed to connect to mburke database");
				e.printStackTrace(); return; }
	}

	public void close(){   
	 try { db_conn.close(); }  catch (SQLException e) { e.printStackTrace(); }
	}


	public void act(String question, int sid, String name, double age,
				int rating, int bid, int year, int month, int day) {
		
		try{
		if(question.equals("newSailor")) newSailor(name, age, rating);
		else if(question.equals("removeSailor")) removeSailor(sid);
		else if(question.equals("newReservation")) newReservation(sid,bid, year, month, day);
		else if(question.equals("removeReservation")) removeReservation(sid,bid,year,month,day);
		else if(question.equals("allSailors") || question.equals("allBoats") 
			 || question.equals("allReservations")) print(question, 0,null,0,0,0,null);
		else System.out.println("You typed: "+question+", which is not an option");
		}
		catch(SQLException e){System.out.println("Oops, database failed to perform ["+question+"]"); 
								e.printStackTrace();}
	}
	
	public void printAll() throws SQLException{
		print("allSailors",0,null,0,0,0,null );
		print("allBoats",0,null,0,0,0,null );
		print("allReservations",0,null,0,0,0,null );
	}

	public void print(String question, int sid, String name, double age,
								int rating, int bid, java.util.Date date) throws SQLException {

		ResultSet rst = SQLBuilder(question,sid,name,age,rating,bid,date );
		
		while(rst.next())
			if(question.equals("allSailors") || question.equals("oneSailor")) 
				System.out.println("Name: "+ rst.getString("sname") + "\t ID: "+rst.getInt("sid")+
				"\t\t Age: "+rst.getDouble("age")+ "\t Rating: "+rst.getInt("rating"));
			else if (question.equals("allBoats")) 
				System.out.println("Name: "+rst.getString("bname") + "\t ID: "+rst.getInt("bid")+
				"\t Color: "+rst.getString("color"));
			else if (question.equals("allReservations") || question.equals("oneReservation"))
				System.out.println("SID: "+rst.getInt("sid")+"\t\t BID: "+rst.getInt("bid")+
				"\t Date: "+rst.getDate("rdate"));
		System.out.println("");
	}

	public void newSailor(String name, double age, int rating) throws SQLException
	{
		SQLBuilder("newSailor", 0, name, age, rating, 0, null);
	}

	public void removeSailor(int sid) throws SQLException{
		ResultSet toRemove = SQLBuilder("oneSailor", sid, null, 0, 0, 0, null); toRemove.next();
		toRemove.deleteRow();
		toRemove.close();
	}

	public void newReservation(int sid, int bid, int year, int month, int day) throws SQLException{
		calendar.set(year,month,day);
		SQLBuilder("newReservation", sid, null, 0, 0, bid, calendar.getTime());
	}

	public void removeReservation(int sid, int bid, int year, int month, int day)throws SQLException{
		calendar.set(year,month,day);
		ResultSet toRemove = SQLBuilder("oneReservation",sid, null, 0, 0, bid, calendar.getTime());
		toRemove.next(); toRemove.deleteRow(); toRemove.close();
	}
	

	public ResultSet SQLBuilder(String question, int sid, String name, double age,
								int rating, int bid, java.util.Date date) throws SQLException {

		String query; 
		String allSailors = "SELECT sid, sname, rating, age FROM "+sailor;
		String oneSailor = "SELECT sid, sname, rating, age FROM "+sailor+ " WHERE sid="+sid;
		String newSailor = "INSERT INTO "+sailor+ " (sname,age,rating)\n"+
			"VALUES ('"+name+"','"+age+"','"+rating+"')";
		String allBoats = "SELECT bid, bname, color FROM "+boat;
		String allReservations = "SELECT * FROM "+reservation;
		String oneReservation = "SELECT * FROM "+reservation+" WHERE sid="+sid+
								" AND bid="+bid+" AND date="+date;
		String newReservation = "INSERT INTO "+reservation+ " (sid,bid,date)\n"+
			"VALUES ('"+sid+"','"+bid+"','"+date+"')"; 
		String sailorReservation = "SELECT * FROM "+reservation+ "WHERE sid="+(sid);
		String boatReservation = "SELECT * FROM "+reservation+ "WHERE bid="+(bid);

		if(question.equals("allSailors")) query = allSailors;
		else if(question.equals("oneSailor")) query = oneSailor;
		else if(question.equals("newSailor")) query = newSailor;
		else if(question.equals("allBoats")) query = allBoats;
		else if(question.equals("allReservations")) query = allReservations;
		else if(question.equals("oneReservation")) query = oneReservation;
		else if(question.equals("newReservation")) query = newReservation;
		else if(question.equals("sailorReservation")) query = sailorReservation;
		else if(question.equals("boatReservation")) query = boatReservation;
		else throw new SQLException();

		return stm.executeQuery(query); 
	}

}