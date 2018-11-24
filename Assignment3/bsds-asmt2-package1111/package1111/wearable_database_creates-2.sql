-- Schema Wearable Device Database
CREATE SCHEMA IF NOT EXISTS wearable;
USE wearable;

-- Drop existing Tables
DROP TABLE IF EXISTS UserDailySummaries;
DROP TABLE IF EXISTS Records;

-- Create Tables
CREATE TABLE Records (
	UserId INT,
    DayId INT,
    HourId TINYINT,
    Steps INT,
    CONSTRAINT PK_Records PRIMARY KEY (UserId, DayId, HourId)
);

CREATE TABLE UserDailySummaries (
	UserId INT,
    DayId INT,
    TotalStepsOfTheDay INT,
    CONSTRAINT PK_Records PRIMARY KEY (UserId, DayId)
);
