package bsds;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DataDao {
	
	private ConnectionManager connectionManager;
	private static DataDao dataDao = null;
	
	private DataDao() {
		this.connectionManager = new ConnectionManager();
	}

	public static DataDao getInstance() {
		if (dataDao == null) {
			dataDao = new DataDao();
		}
		return dataDao;
	}

	public Data insert(Data data) throws SQLException {
		String insertTemplate = "INSERT INTO data(userId,day,hour,stepCount) VALUES(?,?,?,?);";
		Connection connection = null;
		PreparedStatement insertStatement= null;
		ResultSet resultSet = null;
		int id = -1;
		try {
			connection = connectionManager.getConnection();
			insertStatement = connection.prepareStatement(insertTemplate,Statement.RETURN_GENERATED_KEYS);
			insertStatement.setInt(1, data.getUserId());
			insertStatement.setInt(2, data.getDay());
			insertStatement.setInt(3, data.getHour());
			insertStatement.setInt(4, data.getStepCount());
			insertStatement.executeUpdate();
			resultSet = insertStatement.getGeneratedKeys();
			if (resultSet.next()) {
				id = resultSet.getInt(1);
			} else {
				throw new SQLException("Unable to retrieve auto-generated key.");
			}
			data.setId(id);
			return data;
		} catch (SQLException e) {
			e.printStackTrace();
			throw e;
		} finally {
			if(connection != null) {
				connection.close();
			}
			if(insertStatement != null) {
				insertStatement.close();
			}
			if(resultSet != null) {
				resultSet.close();
			}
		}
	}

	public int getStepCountByUserId(int userId) throws SQLException{
		String selectTemplate = "SELECT * FROM data WHERE userId=? ORDER BY day DESC";
		Connection connection = null;
		PreparedStatement selectStatement = null;
		ResultSet resultSet = null;
		int currentDay = -1;
		int currentStepCount = 0;
		try {
			connection = connectionManager.getConnection();
			selectStatement = connection.prepareStatement(selectTemplate);
			selectStatement.setInt(1, userId);
			resultSet = selectStatement.executeQuery();
			
			while(resultSet.next()) {
				int day = resultSet.getInt("day");
				if (currentDay == -1) {
          currentDay = day;
				}
				if (day == currentDay) {
					int stepCount = resultSet.getInt("stepCount");
          currentStepCount += stepCount;
				} else {
					break;
				}
			}
			return currentStepCount;
		} catch (SQLException e) {
			e.printStackTrace();
			throw e;
		} finally {
			if(connection != null) {
				connection.close();
			}
			if(selectStatement != null) {
				selectStatement.close();
			}
			if(resultSet != null) {
				resultSet.close();
			}
		}
	}

	public int getStepCountByUserIdAndDay(int userId, int day) throws SQLException{
		String selectTemplate = "SELECT * FROM data WHERE userId=? AND day=?";
		Connection connection = null;
		PreparedStatement selectStatement = null;
		ResultSet resultSet = null;
		int currentStepCount = 0;
		try {
			connection = connectionManager.getConnection();
			selectStatement = connection.prepareStatement(selectTemplate);
			selectStatement.setInt(1, userId);
			selectStatement.setInt(2, day);
			resultSet = selectStatement.executeQuery();
			
			while(resultSet.next()) {
				int stepCount = resultSet.getInt("stepCount");
        currentStepCount += stepCount;
			}
			return currentStepCount;
		} catch (SQLException e) {
			e.printStackTrace();
			throw e;
		} finally {
			if(connection != null) {
				connection.close();
			}
			if(selectStatement != null) {
				selectStatement.close();
			}
			if(resultSet != null) {
				resultSet.close();
			}
		}
	}

	public int getStepCountByUserIdAndDayRange(int userId, int startDay, int numDays) throws SQLException{
		int endDay = startDay + numDays - 1;
		String selectTemplate = "SELECT * FROM data WHERE userId=? AND day>=? AND day<=?";
		Connection connection = null;
		PreparedStatement selectStatement = null;
		ResultSet resultSet = null;
		int currentStepCount = 0;
		try {
			connection = connectionManager.getConnection();
			selectStatement = connection.prepareStatement(selectTemplate);
			selectStatement.setInt(1, userId);
			selectStatement.setInt(2, startDay);
			selectStatement.setInt(3, endDay);
			resultSet = selectStatement.executeQuery();
			
			while(resultSet.next()) {
				int stepCount = resultSet.getInt("stepCount");
        currentStepCount += stepCount;
			}
			return currentStepCount;
		} catch (SQLException e) {
			e.printStackTrace();
			throw e;
		} finally {
			if(connection != null) {
				connection.close();
			}
			if(selectStatement != null) {
				selectStatement.close();
			}
			if(resultSet != null) {
				resultSet.close();
			}
		}
	}
}
