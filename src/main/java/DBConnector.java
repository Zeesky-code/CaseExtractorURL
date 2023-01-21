import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;

public class DBConnector {
	static final String JDBC_Driver = "org.h2.Driver";
	static final String DB_URL = "jdbc:mysql://127.0.0.1:3306/rulings";

	static final String USER = "root";
	static final String PASS = "Daddy123";
	static Connection conn = null;
	static PreparedStatement Pstmt;

	public static PreparedStatement createConnection (){
		try {
			Class.forName("com.mysql.cj.jdbc.Driver").newInstance();
			conn =DriverManager.getConnection(DB_URL,USER,PASS );

			Pstmt= conn.prepareStatement("INSERT INTO judgements(id,title,basvuru_no,karar_tarihi,ruling_content)" + "values(?,?,?,?,?)");



		}catch(Exception e){
			e.printStackTrace();
		}
		return Pstmt;
	}
	public static void main(String[] args) {

	}
}
