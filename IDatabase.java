import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public interface IDatabase {
	
	//static final String JDBC_DRIVER = "com.mysql.jdbc.Driver"; //deprecated
	static final String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";
	static final String DB_URL = "jdbc:mysql://localhost/confSeating?serverTimezone=UTC&useSSL=false";
	static final String USER = "root";
	static final String PASS = "AGRmarc6";
	
	static Connection connectToDB() {
		Connection conn = null;
		try{
			Class.forName(JDBC_DRIVER);
			//Open a connection
			System.out.println("Connecting to database...");
			conn = DriverManager.getConnection(DB_URL, USER, PASS);
		}catch(SQLException se){
			se.printStackTrace();
		}catch(Exception e){
			e.printStackTrace();
		}
		return  conn;
	}
}
