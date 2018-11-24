package org.joyinmyzone.wearableDevice.tester;

import java.sql.SQLException;

import org.joyinmyzone.wearableDevice.dao.RecordDao;
import org.joyinmyzone.wearableDevice.model.Record;
import org.joyinmyzone.wearableDevice.servlet.PostRecordHandler;

public class Test {
	
	public static void main(String[] args) throws SQLException {
		PostRecordHandler handler = new PostRecordHandler();
		String result = handler.postRecord(1, 1, 1, 1024);
		System.out.println(result);
	}
	
}
