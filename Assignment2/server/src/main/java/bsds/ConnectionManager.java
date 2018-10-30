package bsds;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class ConnectionManager {
	
	private final String username = "root";
	private final String password = "1235";
//	private final String hostName = "bsds.c8xtyxxs61uv.us-east-2.rds.amazonaws.com";
	private final String hostName = "localhost";
	private final int port = 3306;
	private final String schema = "dataframe";

	public Connection getConnection() throws SQLException {
		Connection connection = null;
		Properties connProperties = new Properties();
		connProperties.put("user", username);
		connProperties.put("password", password);

		try {
			Class.forName("com.mysql.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			throw new SQLException(e);
		}
		
		connection = DriverManager.getConnection( 
				"jdbc:mysql://" + this.hostName + ":" + this.port + "/" + this.schema + "?useSSL=false",
				connProperties);
		
		return connection;
	}
}
