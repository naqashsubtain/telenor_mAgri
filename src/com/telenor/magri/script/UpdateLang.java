package com.telenor.magri.script;

import org.apache.log4j.Logger;
import org.asteriskjava.fastagi.AgiChannel;
import org.asteriskjava.fastagi.AgiException;
import org.asteriskjava.fastagi.AgiRequest;

import com.agiserver.helper.DBHelper;
import com.agiserver.helper.common.AbstractBaseAgiScript;
import com.agiserver.helper.common.ConfigurationLoader;
import com.telenor.magri.common.SMSHelper;

public class UpdateLang extends AbstractBaseAgiScript {
	private static final Logger logger = Logger.getLogger(UpdateLang.class);

	public void service(AgiRequest request, AgiChannel channel)
			throws AgiException {
		try {

			String cellno = channel.getVariable("DB_CLI");
			
			int rs = DBHelper.getInstance().executeDml(
					"UPDATE  subscription_requests SET sub_lang=2 where cellno=?",
					super.getConnection(), new Object[] { cellno });
			logger.info("Record has been update in subscriber ");
			channel.setVariable("LANG_CHANGED", "YES");
			channel.setVariable("LANG", "2");
			channel.verbose("Customer: " + cellno + ", language changed", 0);

		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
	}
}
