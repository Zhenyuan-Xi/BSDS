package org.joyinmyzone.wearableDevice.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.joyinmyzone.wearableDevice.model.UserDailySummary;

public class UserDailySummaryDao {

	protected ConnectionManager connectionManager;
	
	public UserDailySummaryDao() {
		this.connectionManager = new ConnectionManager();
	}
	
	/**
	 * Insert a new record to the database
	 */
	public UserDailySummary insert(UserDailySummary record) throws SQLException {
		String insertTemplate = "INSERT INTO UserDailySummaries(UserId,DayId,TotalStepsOfTheDay) VALUES(?,?,?);"; 
		Connection connection = null;
		PreparedStatement insertStatement= null;
		ResultSet resultSet = null;
		try {
			connection = connectionManager.getConnection();
			insertStatement = connection.prepareStatement(insertTemplate);
			insertStatement.setInt(1, record.getUserId());
			insertStatement.setInt(2, record.getDayId());
			insertStatement.setInt(3, record.getSteps());
			insertStatement.executeUpdate();
			return record;
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
	

	/**
	 * Insert a new record to the database
	 */
	public UserDailySummary replace(UserDailySummary record) throws SQLException {
		String replaceTemplate = "REPLACE INTO UserDailySummaries(UserId,DayId,TotalStepsOfTheDay) VALUES(?,?,?);"; 
		Connection connection = null;
		PreparedStatement replaceStatement= null;
		ResultSet resultSet = null;
		try {
			connection = connectionManager.getConnection();
			replaceStatement = connection.prepareStatement(replaceTemplate);
			replaceStatement.setInt(1, record.getUserId());
			replaceStatement.setInt(2, record.getDayId());
			replaceStatement.setInt(3, record.getSteps());
			replaceStatement.executeUpdate();
			return record;
		} catch (SQLException e) {
			e.printStackTrace();
			throw e;
		} finally {
			if(connection != null) {
				connection.close();
			}
			if(replaceStatement != null) {
				replaceStatement.close();
			}
			if(resultSet != null) {
				resultSet.close();
			}
		}
	}
	
	
	
	/**
	 * get the user daily summary given userId and dayId
	 */
	public UserDailySummary getUserDailySummary(int userId, int dayId) throws SQLException{
		
		String selectTemplate = "SELECT * FROM UserDailySummaries WHERE UserId=? AND DayId=?;";
		Connection connection = null;
		PreparedStatement selectStatement = null;
		ResultSet resultSet = null;
		
		try {
			connection = connectionManager.getConnection();
			selectStatement = connection.prepareStatement(selectTemplate);
			selectStatement.setInt(1, userId);
			selectStatement.setInt(2, dayId);
			resultSet = selectStatement.executeQuery();
			int steps = 0;
			if(resultSet.next()) {
				steps = resultSet.getInt("TotalStepsOfTheDay");
			} 
			return new UserDailySummary(userId, dayId, steps);
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
	
	
	/**
	 * Get the steps of the most recent day of a user.
	 */
	public int getCurrentStepsByUserId(int userId) throws SQLException {
		
		String selectTemplate = "SELECT * FROM UserDailySummaries WHERE UserId=? ORDER BY DayId DESC LIMIT 1;";
		Connection connection = null;
		PreparedStatement selectStatement = null;
		ResultSet resultSet = null;
		int totalSteps = 0;
		
		try {
			connection = connectionManager.getConnection();
			selectStatement = connection.prepareStatement(selectTemplate);
			selectStatement.setInt(1, userId);
			resultSet = selectStatement.executeQuery();
			if(resultSet.next()) {
				totalSteps = resultSet.getInt("TotalStepsOfTheDay");
			}
			return totalSteps;
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
	
	/**
	 * get the steps of a user of a specific day
	 */
	public int getStepsByUserIdAndDayId(int userId, int dayId) throws SQLException {
		String selectTemplate = "SELECT * FROM UserDailySummaries WHERE UserId=? AND DayId=?;";
		Connection connection = null;
		PreparedStatement selectStatement = null;
		ResultSet resultSet = null;
		int totalSteps = 0;
		try {
			connection = connectionManager.getConnection();
			selectStatement = connection.prepareStatement(selectTemplate);
			selectStatement.setInt(1, userId);
			selectStatement.setInt(2, dayId);
			resultSet = selectStatement.executeQuery();
			
			if(resultSet.next()) {
				totalSteps = resultSet.getInt("TotalStepsOfTheDay");
			}
			return totalSteps;
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
	
	
	/**
	 * get the steps of a user from start day to (numdays - 1) after that day
	 */
	public int getStepsByUserIdAndDayRange(int userId, int startDay, int numDays) throws SQLException {
		int endDay = startDay + numDays - 1;
		String selectTemplate = "SELECT * FROM UserDailySummaries WHERE UserId=? AND dayId>=? AND dayId<=?;";
		Connection connection = null;
		PreparedStatement selectStatement = null;
		ResultSet resultSet = null;
		int totalSteps = 0;
		try {
			connection = connectionManager.getConnection();
			selectStatement = connection.prepareStatement(selectTemplate);
			selectStatement.setInt(1, userId);
			selectStatement.setInt(2, startDay);
			selectStatement.setInt(3, endDay);
			resultSet = selectStatement.executeQuery();
			
			while(resultSet.next()) {
				int stepCount = resultSet.getInt("TotalStepsOfTheDay");
				totalSteps += stepCount;	
			}
			return totalSteps;
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
