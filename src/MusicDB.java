import java.sql.*;
import java.util.Scanner;

public class MusicDB {
	static final String ARTIST_TABLE = "Artist_akter";
	static final String SONG_TABLE = "Song_akter";

	static final Scanner scanner = new Scanner(System.in);

	public static void main(String[] args) throws Exception {

		Connection con = null;
		try {
			
			System.out.println("Connecting to database...");

			DriverManager.registerDriver(new oracle.jdbc.driver.OracleDriver());
			con = DriverManager.getConnection("jdbc:oracle:thin:@sabzevi2.homeip.net:1521:orcl", "csus", "student");

			Statement st = con.createStatement();
			
			createTables();

			boolean option = false;
			while (!option) {

				// Display menu
				System.out.println("Please select an option:");
				System.out.println("1. Insert");
				System.out.println("2. Delete");
				System.out.println("3. Update");
				System.out.println("4. View");
				System.out.println("5. Quit");

				int choice = scanner.nextInt();
				scanner.nextLine();
				switch (choice) {
				case 1:
					insertRecord();
					break;
				case 2:
					deleteRecord();
					break;
				case 3:
					updateRecord();
					break;
				case 4:
					viewRecord();
					break;
				case 5:
					option = true;
					quit();
					break;
				default:
					System.out.println("Invalid choice. Please try again.");
				}
			} 
			
			
			try {
			    Statement dropStatement = con.createStatement();
			    dropStatement.executeUpdate("drop table " + SONG_TABLE + " cascade constraints");
			    dropStatement.executeUpdate("drop table " + ARTIST_TABLE + " cascade constraints");
			    dropStatement.executeUpdate("drop sequence artistadi");
			    dropStatement.executeUpdate("drop sequence songadi");
			} catch (SQLException e) {
			   
			}

		}
		
		catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (con != null)
					con.close();
				System.out.println("Disconnected from database.");
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	private static void createTables() throws SQLException {

		Connection con = DriverManager.getConnection("jdbc:oracle:thin:@sabzevi2.homeip.net:1521:orcl", "csus",
				"student");
		Statement st = con.createStatement();

		String sql = "CREATE TABLE " + ARTIST_TABLE + "(ARTIST_ID INT, NAME VARCHAR(300), PRIMARY KEY(ARTIST_ID))";
		st.executeQuery(sql);

		sql = "CREATE TABLE " + SONG_TABLE + "(SONG_ID INT, NAME VARCHAR(255), ARTIST_ID INT, PRIMARY KEY(SONG_ID), FOREIGN KEY(ARTIST_ID) REFERENCES Artist_akter(ARTIST_ID))"; 
		st.executeQuery(sql);
		st.executeQuery("CREATE SEQUENCE artistadi");
		st.executeQuery("CREATE SEQUENCE songadi");
		st.close();
		con.close();
	}

	private static void insertRecord() throws SQLException {
		Connection con = DriverManager.getConnection("jdbc:oracle:thin:@sabzevi2.homeip.net:1521:orcl", "csus",
				"student");
		Statement st = con.createStatement();
		System.out.println("Enter artist name:");
		String artistName = scanner.nextLine();
		String sql = "INSERT INTO " + ARTIST_TABLE + " VALUES (artistadi.nextval, '" + artistName + "')";
		st.executeUpdate(sql, Statement.RETURN_GENERATED_KEYS);
		
		System.out.println("Enter song name:");
		String songName = scanner.nextLine();
		sql = "INSERT INTO " + SONG_TABLE + " VALUES (songadi.nextval, '" + songName + "', artistadi.currval )";
		st.executeUpdate(sql);
		st.close();
		con.close();
	}

	private static void deleteRecord() {
	    try (Connection con = DriverManager.getConnection("jdbc:oracle:thin:@sabzevi2.homeip.net:1521:orcl", "csus", "student");
	        PreparedStatement selectStmt = con.prepareStatement("SELECT * FROM " + SONG_TABLE + " WHERE UPPER(NAME) = ?");
	        PreparedStatement deleteStmt1 = con.prepareStatement("DELETE FROM " + SONG_TABLE + " WHERE UPPER(NAME) = ?");
	        PreparedStatement selectStmt2 = con.prepareStatement("SELECT * FROM " + ARTIST_TABLE + " WHERE UPPER(NAME) = ?");
	        PreparedStatement deleteStmt2 = con.prepareStatement("DELETE FROM " + ARTIST_TABLE + " WHERE UPPER(NAME) = ?")) {
	      
	        System.out.println("Enter the name of the song you want to delete: ");
	        String name = scanner.nextLine().trim().toUpperCase();

	        selectStmt.setString(1, name);
	        ResultSet selectResult = selectStmt.executeQuery();
	        if (selectResult.next()) {
	            int songId = selectResult.getInt("SONG_ID");
	            deleteStmt1.setString(1, name);
	            deleteStmt1.executeUpdate();

	            int artistId = selectResult.getInt("ARTIST_ID");
	            selectStmt2.setString(1, name);
	            ResultSet selectResult2 = selectStmt2.executeQuery();
	            if (selectResult2.next()) {
	                deleteStmt2.setString(1, selectResult2.getString("NAME"));
	                deleteStmt2.executeUpdate();
	            }
	            System.out.println("Record deleted successfully.");
	        } else {
	            System.out.println("Record not found.");
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	}


	private static void updateRecord() {
	    try {
	        Connection con = DriverManager.getConnection("jdbc:oracle:thin:@sabzevi2.homeip.net:1521:orcl", "csus", "student");
	        Scanner scanner = new Scanner(System.in);

	      
	        System.out.println("Enter the name of the artist you want to update: ");
	        String oldArtistName = scanner.nextLine().trim();

	        PreparedStatement selectArtistStmt = con.prepareStatement("SELECT * FROM " + ARTIST_TABLE + " WHERE NAME = ?");
	        selectArtistStmt.setString(1, oldArtistName);
	        ResultSet selectArtistResult = selectArtistStmt.executeQuery();
	        if (selectArtistResult.next()) {
	            int artistId = selectArtistResult.getInt("ARTIST_ID");
	            
	            System.out.println("Enter the new name of the artist: ");
	            String newArtistName = scanner.nextLine().trim();
	            
	       
	            PreparedStatement updateArtistStmt = con.prepareStatement("UPDATE " + ARTIST_TABLE + " SET NAME = ? WHERE ARTIST_ID = ?");
	            updateArtistStmt.setString(1, newArtistName);
	            updateArtistStmt.setInt(2, artistId);
	            updateArtistStmt.executeUpdate();
	            System.out.println("Updated artist record.");
	            
	          
	            System.out.println("Enter the new name of the song for the updated artist: ");
	            String newSongName = scanner.nextLine().trim();
	            
	        
	            PreparedStatement updateSongStmt = con.prepareStatement("UPDATE " + SONG_TABLE + " SET NAME = ? WHERE ARTIST_ID = ?");
	            updateSongStmt.setString(1, newSongName);
	            updateSongStmt.setInt(2, artistId);
	            updateSongStmt.executeUpdate();
	            System.out.println("Updated song record for the updated artist.");
	        } else {
	            System.out.println("No artist found with the name " + oldArtistName);
	        }

	        con.close();
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	}


	private static void viewRecord() {
		try (Connection con = DriverManager.getConnection("jdbc:oracle:thin:@sabzevi2.homeip.net:1521:orcl", "csus", "student");
				PreparedStatement selectStmt = con.prepareStatement("SELECT " + SONG_TABLE + ".SONG_ID, " + SONG_TABLE + ".NAME, " + ARTIST_TABLE + ".NAME FROM " + SONG_TABLE + " JOIN " + ARTIST_TABLE + " ON " + SONG_TABLE + ".ARTIST_ID = " + ARTIST_TABLE + ".ARTIST_ID ORDER BY " + ARTIST_TABLE + ".NAME, " + SONG_TABLE + ".NAME");
				ResultSet selectResult = selectStmt.executeQuery()) {

			System.out.println("Song ID\t\tArtist Name\tSong Name");
			System.out.println("---------------------------------------------------");
			while (selectResult.next()) {
				int songId = selectResult.getInt(1);
				String songName = selectResult.getString(2);
				String artistName = selectResult.getString(3);
				System.out.println(songId + "\t\t" + artistName + "\t\t" + songName);
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public static void quit() throws SQLException {
		Connection con = DriverManager.getConnection("jdbc:oracle:thin:@sabzevi2.homeip.net:1521:orcl", "csus",
				"student");
		Statement st = con.createStatement();
		try {
			con.close();
		} catch (SQLException e) {
			System.out.println("Error: " + e.getMessage());
		}
		System.out.println("Program ended.");
		st.close();
		con.close();
	}

}
