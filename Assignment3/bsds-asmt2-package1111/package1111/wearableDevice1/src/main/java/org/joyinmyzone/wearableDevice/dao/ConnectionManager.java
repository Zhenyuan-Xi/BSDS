package org.joyinmyzone.wearableDevice.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class ConnectionManager {
	
	private final String username = "linzisheng1995";
	private final String password = "12345678";
	private final String hostName = "35.233.251.213";
	private final int port = 3306;
	private final String schema = "wearable";
	
	/**
	 * Connect to database
	 */
	public Connection getConnection() throws SQLException {
		Connection connection = null;
		Properties connProperties = new Properties();
		connProperties.put("user", username);
		connProperties.put("password", password);
		
		try {
			Class.forName("com.mysql.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			System.out.println("MySQL JDBC Driver NOT Registered!");
			throw new SQLException(e);
		}
		
		connection = DriverManager.getConnection( 
				"jdbc:mysql://" + this.hostName + ":" + this.port + "/" + this.schema + "?useSSL=false",
				connProperties);
		
		return connection;
	}
	
	/**
	 * Close connection to database
	 */
	public void closeConnection(Connection connection) throws SQLException {
		try {
			connection.close();
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("------ Connection close failure------");
			throw e;
		}
	}
	
}
