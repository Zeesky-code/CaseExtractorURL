import java.sql.*;

public class DBConnector {
	static final String DB_URL = "jdbc:mysql://127.0.0.1:3306/rulings";

	static final String USER = "root";
	static final String PASS = "Daddy123";
	static Connection conn = null;
	static PreparedStatement Pstmt;

	public static void createDB (){
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			conn =DriverManager.getConnection(DB_URL,USER,PASS );
			Statement stmt = conn.createStatement();
			String createDB = "CREATE TABLE TC_RULINGS ("+
					"id INTEGER not NULL, "+
					"title VARCHAR(255), "+
					"basvuru_no VARCHAR(255),"+
					"karar_tarihi DATE,"+
					"url TEXT,"+
					"ruling_content LONGTEXT, "+
					"PRIMARY KEY (id))";


			stmt.executeUpdate(createDB);


		}catch(Exception e){
			e.printStackTrace();
		}
	}
	public static PreparedStatement createConnection() throws SQLException {
		Pstmt= conn.prepareStatement("INSERT INTO TC_RULINGS(id,title,basvuru_no,karar_tarihi,url,ruling_content)" + "values(?,?,?,?,?,?)");
		return Pstmt;
	}
}
