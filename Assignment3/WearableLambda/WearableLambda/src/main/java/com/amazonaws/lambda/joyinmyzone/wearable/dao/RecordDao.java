package com.amazonaws.lambda.joyinmyzone.wearable.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.amazonaws.lambda.joyinmyzone.wearable.model.Record;

public class RecordDao {
	
	protected ConnectionManager connectionManager;
	
	public RecordDao() {
		this.connectionManager = new ConnectionManager();
	}
	
	/**
	 * Insert a new record to the database
	 */
	public Record insert(Record record) throws SQLException {
		String insertTemplate = "INSERT INTO Records(UserId,DayId,HourId,Steps) VALUES(?,?,?,?);"; 
		Connection connection = null;
		PreparedStatement insertStatement= null;
		ResultSet resultSet = null;
		try {
			connection = connectionManager.getConnection();
			insertStatement = connection.prepareStatement(insertTemplate);
			insertStatement.setInt(1, record.getUserId());
			insertStatement.setInt(2, record.getDayId());
			insertStatement.setInt(3, record.getHourId());
			insertStatement.setInt(4, record.getSteps());
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
	 * Replace a new record to the database
	 */
	public Record replace(Record record) throws SQLException {
		String replaceTemplate = "REPLACE INTO Records(UserId,DayId,HourId,Steps) VALUES(?,?,?,?);"; 
		Connection connection = null;
		PreparedStatement replaceStatement= null;
		ResultSet resultSet = null;
		try {
			connection = this.connectionManager.getConnection();
			replaceStatement = connection.prepareStatement(replaceTemplate);
			replaceStatement.setInt(1, record.getUserId());
			replaceStatement.setInt(2, record.getDayId());
			replaceStatement.setInt(3, record.getHourId());
			replaceStatement.setInt(4, record.getSteps());
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
	 * Get the steps of the most recent day of a user.
	 */
	public int getCurrentStepsByUserId(int userId) throws SQLException{
		String selectTemplate = "SELECT * FROM Records WHERE UserId=? ORDER BY DayId DESC;";
		Connection connection = null;
		PreparedStatement selectStatement = null;
		ResultSet resultSet = null;
		int currentDayId = -1;
		int totalSteps = 0;
		try {
			connection = connectionManager.getConnection();
			selectStatement = connection.prepareStatement(selectTemplate);
			selectStatement.setInt(1, userId);
			resultSet = selectStatement.executeQuery();
			
			while(resultSet.next()) {
				int day = resultSet.getInt("DayId");
				if (currentDayId == -1) {
					currentDayId = day;
				}
				if (day == currentDayId) {
					int stepCount = resultSet.getInt("Steps");
					totalSteps += stepCount;
				} else {
					break;
				}
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
	 * Get the steps of a specific day of a user.
	 */
	public int getStepsByUserIdAndDayId(int userId, int dayId) throws SQLException{
		String selectTemplate = "SELECT * FROM Records WHERE UserId=? AND dayId=?;";
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
			
			while(resultSet.next()) {
				int stepCount = resultSet.getInt("Steps");
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
	
	/**
	 * Get the steps of a range of days of a user.
	 */
	public int getStepsByUserIdAndDayRange(int userId, int startDay, int numDays) throws SQLException{
		int endDay = startDay + numDays - 1;
		String selectTemplate = "SELECT * FROM Records WHERE UserId=? AND dayId>=? AND dayId<=?;";
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
				int stepCount = resultSet.getInt("Steps");
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
