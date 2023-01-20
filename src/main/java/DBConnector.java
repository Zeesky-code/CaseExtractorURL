import java.sql.Connection;
import java.sql.DriverManager;

public class DBConnector {
	static final String JDBC_Driver = "org.h2.Driver";
	static final String DB_URL = "jdbc:h2:mem:test";

	static final String USER = "zai";
	static final String PASS = "";
	static Connection conn = null;
	public static void main(String[] args) {
		try {
			Class.forName("com.mysql.cj.jdbc.Driver").newInstance();
			conn =DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/Rulings" +
							"user=root&password=Daddy123");

			System.out.println("Connection is successful");
		}catch(Exception e){
			e.printStackTrace();
		}
	}
}
