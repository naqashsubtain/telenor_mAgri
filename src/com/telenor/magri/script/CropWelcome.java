package com.telenor.magri.script;


import org.apache.log4j.Logger;
import org.asteriskjava.fastagi.AgiChannel;
import org.asteriskjava.fastagi.AgiException;
import org.asteriskjava.fastagi.AgiRequest;

import com.agiserver.helper.DBHelper;
import com.agiserver.helper.DatabaseException;
import com.agiserver.helper.common.AbstractBaseAgiScript;


public class CropWelcome extends AbstractBaseAgiScript{
	private static final Logger logger = Logger.getLogger(CropWelcome.class);
	
	public CropWelcome() throws DatabaseException{
		super(true);
	}
	public void service(AgiRequest request, AgiChannel channel) throws AgiException {
		try {
			String cellno = channel.getVariable("DB_CLI");
			String crop = channel.getVariable("CROP_ID");
			logger.info("DB_CLI is : "+cellno);
			
			String pmp = (String) DBHelper.getInstance().singleResult(
					"SELECT  crop_welcome from crops  WHERE `id`=?",
					super.getConnection(), new Object[] {crop});
			
			logger.info("Welcom prompt is : "+pmp);
			channel.setVariable("CROP_WELCOME", pmp);
			

		} catch (Exception e) {
			logger.error("Exception", e);
		}
	}


}
