package com.telenor.magri.script;

import java.util.List;
import java.util.Map;

import org.asteriskjava.fastagi.AgiChannel;
import org.asteriskjava.fastagi.AgiException;
import org.asteriskjava.fastagi.AgiRequest;

import com.agiserver.helper.DBHelper;
import com.agiserver.helper.common.AbstractBaseAgiScript;

public class GetLatestAgricultureNews extends AbstractBaseAgiScript {

	@Override
	public void service(AgiRequest request, AgiChannel channel)
			throws AgiException {
		// TODO Auto-generated method stub

		try {
			String cellno = channel.getVariable("DB_CLI");
			String crop = channel.getVariable("CROP_ID");

			logger.info("DB_CLI is : " + cellno + " and CROP_ID is : " + crop);

			List<Map<String, Object>> contentHashMap = null;

			contentHashMap = DBHelper.getInstance().query(
					"SELECT  file_name from latest_agri_news where DATE(dt)<=CURDATE() ORDER BY dt DESC",
					super.getConnection(), new Object[] {});
			logger.info("FILES FOUND : " + contentHashMap);
			logger.info("Total FILES Found are : "
					+ Integer.toString(contentHashMap.size()));
			channel.setVariable("TOTAL_FILES",
					Integer.toString(contentHashMap.size()));
			String prompt = null;

			int i = 1;
			for (Map<String, Object> cellNumberMap : contentHashMap) {

				prompt = cellNumberMap.get("file_name").toString();
				channel.setVariable("FILE_NAME_" + Integer.toString(i), prompt);
				i = i + 1;
			}

		} catch (Exception e) {
			logger.error("Exception", e);
		}

	}

}
