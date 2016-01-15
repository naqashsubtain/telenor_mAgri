package com.telenor.magri.script;




import org.apache.log4j.Logger;
import org.asteriskjava.fastagi.AgiChannel;
import org.asteriskjava.fastagi.AgiException;
import org.asteriskjava.fastagi.AgiRequest;

import com.agiserver.helper.DBHelper;
import com.agiserver.helper.DatabaseException;
import com.agiserver.helper.common.AbstractBaseAgiScript;


public class OBD_Call extends AbstractBaseAgiScript{
	private static final Logger logger = Logger.getLogger(OBD_Call.class);
	
	public OBD_Call() throws DatabaseException{
		super(true);
	}
	public void service(AgiRequest request, AgiChannel channel) throws AgiException {
		try {
			String cellno = channel.getVariable("DB_CLI");
			
			logger.info("DB_CLI is : "+cellno);
			logger.info(DBHelper.getInstance());
			
			int rs= DBHelper.getInstance().executeDml(
					"INSERT INTO help_call (id,cellno,dt ,status) VALUES(null,?,NOW(), ?)",
					super.getConnection(), new Object[] {cellno,0});
		
		} catch (Exception e) {
			logger.error("Exception", e);
		}
	}


}
