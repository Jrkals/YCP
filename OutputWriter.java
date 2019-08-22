import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
/*
 * class to write output to file or screen or mySQL database
 */
public class OutputWriter implements IDatabase {
	FileWriter fw;
	String filename;
	Table[] vipTables;
	Table[] tables;

	public OutputWriter(String filename, Table[] vipTables, Table[] tables)  {
		this.filename = filename;
		this.vipTables = vipTables;
		this.tables = tables;
	}

	public void writeToFile() throws IOException {
		fw = new FileWriter(filename);
		fw.write("Table, Name, Chapter, VIP?, Diet, Spouse\n");
		for(Table t: vipTables) {
			if(t.numberOfSpots < 10) {
				fw.write("Table "+t.tableID+"\n");
				for(int i = 0; i < 10; i++) {
					fw.write("seat:"+(i+1)+", ");
					if(t.seats[i] != null) {
						fw.write(t.seats[i]+"\n");
					}
					else {
						fw.write("empty\n");
					}
				}
			}
		} // end outer for
		for(Table t: tables) {
			if(t.numberOfSpots < 10) {
				fw.write("Table "+t.tableID+"\n");
				for(int i = 0; i < 10; i++) {
					fw.write("seat:"+(i+1)+", ");
					if(t.seats[i] != null) {
						fw.write(t.seats[i]+"\n");
					}
					else {
						fw.write("empty\n");
					}
				}
			}
		} // end outer for

	}

	void printArrangementToScreen() {
		for(Table t: vipTables) {
			if(t.numberOfSpots < 10) {
				System.out.println("Table "+t.tableID);
				for(int i = 0; i < 10; i++) {
					System.out.print("seat:"+(i+1)+" ");
					if(t.seats[i] != null) {
						System.out.println(t.seats[i]);
					}
					else {
						System.out.println("empty");
					}
				}
			}
		}
		for(Table t: tables) {
			if(t.numberOfSpots < 10) {
				System.out.println("Table "+t.tableID);
				for(int i = 0; i < 10; i++) {
					System.out.print("seat:"+(i+1)+" ");
					if(t.seats[i] != null) {
						System.out.println(t.seats[i]);
					}
					else {
						System.out.println("empty");
					}
				}
			}
		}
	}

	void writeToDB() {
		Connection connection = connectToDB();
		Statement stmt = null;
		if(connection == null) {
			System.out.println("Can't connect to DB");
			return; 
		}
		else {
			try {
				stmt = connection.createStatement();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			DatabaseReader dr = new DatabaseReader(connection);
			// get a list of rows which can be modified
			ArrayList<Integer> modifiableRows = dr.getModifiableTables();
			// Modify the table
			if(modifiableRows.size() > 0) {
				for(int i = 0; i < vipTables.length; i++) {
					Table t = vipTables[i];
					if(modifiableRows.contains(t.tableID)) {
						int index = modifiableRows.indexOf(t.tableID); // loc of that table
						// delete modifiable rows
						String delete = "Delete from tables where table_number = "
								+modifiableRows.get(index)+";";
						System.out.println(delete);
						try {
							stmt.executeUpdate(delete);
						} catch (SQLException e) {
							e.printStackTrace();
						}
					} // end of if contains
					// re-insert modifiable rows
					String statement = makeInsertStatement(t);
					System.out.println(statement);
					try {
						stmt.executeUpdate(statement);
					} catch (SQLException e) {
						e.printStackTrace();
					}
				} // end of for
				for(int i = 0; i < tables.length; i++) {
					Table t = tables[i];
					if(modifiableRows.contains(t.tableID)) {
						int index = modifiableRows.indexOf(t.tableID); // loc of that table
						// delete modifiable rows
						String delete = "Delete from tables where table_number = "
								+modifiableRows.get(index)+";";
						System.out.println(delete);
						try {
							stmt.executeUpdate(delete);
						} catch (SQLException e) {
							e.printStackTrace();
						}
					} // end of if
						String statement = makeInsertStatement(t);
						System.out.println(statement);
						try {
							stmt.executeUpdate(statement);
						} catch (SQLException e) {
							e.printStackTrace();
						}
					
				} // end of for
			} // end of if
		} // end of else
	} // end of method
	private String makeInsertStatement(Table t) {
		String sql = "INSERT INTO tables (table_number,"; // first line
		for(int j = 1; j <= 10-t.numberOfSpots; j++) {
			sql += "person_"+(j)+",";
		}
		sql += "isVIP, modifyHuh)";
		sql += " Values ("+t.tableID+","; // last line
		for(int j = 0; j <= 9-t.numberOfSpots; j++) {
			sql += "\""+t.seats[j].firstName+" "+t.seats[j].lastName +"\",";
		}
		if(t.isVIPTable)
			sql += "TRUE,";
		else 
			sql += "FALSE,";
		sql += "TRUE);"; // modifyHuh
		return sql;
	}

	Connection connectToDB() {
		return IDatabase.connectToDB(); // calls the static method in the IDatabase interface
	}
}
