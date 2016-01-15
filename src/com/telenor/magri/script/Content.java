package com.telenor.magri.script;


import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.asteriskjava.fastagi.AgiChannel;
import org.asteriskjava.fastagi.AgiException;
import org.asteriskjava.fastagi.AgiRequest;

import com.agiserver.helper.DBHelper;
import com.agiserver.helper.DatabaseException;
import com.agiserver.helper.common.AbstractBaseAgiScript;


public class Content extends AbstractBaseAgiScript{
	private static final Logger logger = Logger.getLogger(Content.class);
	
	public Content() throws DatabaseException{
		super(true);
	}
	public void service(AgiRequest request, AgiChannel channel) throws AgiException {
		try {
			String cellno = channel.getVariable("DB_CLI");
			String crop = channel.getVariable("CROP_ID");
			String category = channel.getVariable("OPT");
			
			logger.info("DB_CLI is : "+cellno+ " and OPT is : "+ category);
			
			List<Map<String, Object>> contentHashMap = null;
			
			contentHashMap= DBHelper.getInstance().query(
					"SELECT  file_name from crops_content  WHERE crop_id =? and category=? ORDER BY dt DESC",
					super.getConnection(), new Object[] {crop,category});
			logger.info("FILES FOUND : "+contentHashMap);
			logger.info("Total FILES Found are : "+Integer.toString(contentHashMap.size()));
			channel.setVariable("TOTAL_FILES", Integer.toString(contentHashMap.size()));
			String prompt =null;
			
				int i =1;
				for(Map<String, Object> cellNumberMap : contentHashMap){
					
					prompt = cellNumberMap.get("file_name").toString();	
					channel.setVariable("FILE_NAME_"+Integer.toString(i), prompt);
					i = i+1;
				}
			

		} catch (Exception e) {
			logger.error("Exception", e);
		}
	}


}
