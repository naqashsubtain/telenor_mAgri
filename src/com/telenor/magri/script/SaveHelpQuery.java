package com.telenor.magri.script;

import org.apache.log4j.Logger;
import org.asteriskjava.fastagi.AgiChannel;
import org.asteriskjava.fastagi.AgiException;
import org.asteriskjava.fastagi.AgiRequest;

import com.agiserver.helper.DBHelper;
import com.agiserver.helper.common.AbstractBaseAgiScript;
import com.agiserver.helper.common.ConfigurationLoader;
import com.telenor.magri.common.SMSHelper;

public class SaveHelpQuery extends AbstractBaseAgiScript {
	private static final Logger logger = Logger.getLogger(SaveHelpQuery.class);

	public void service(AgiRequest request, AgiChannel channel)
			throws AgiException {
		try {

			String cellno = channel.getVariable("DB_CLI");
			String msisdn = "92" + channel.getVariable("DB_CLI").toString().substring(1);
			channel.setVariable("IS_HELP", "NO");

			DBHelper.getInstance()
					.executeDml(
							"INSERT INTO help_requests (cellno, dt, ask_through, status) VALUES(?, NOW(), 'IVR', 'PENDING') ON DUPLICATE KEY UPDATE status='PENDING', dt=now()",
							super.getConnection(), new Object[] { cellno });
			channel.setVariable("IS_HELP", "YES");
			SMSHelper.sendSMS(ConfigurationLoader.getProperty("SENT_SMS_URL"),
					msisdn, ConfigurationLoader.getProperty("SMS_FROM"),
					ConfigurationLoader.getProperty("SMSC"),
					ConfigurationLoader.getProperty("HELP_SMS"));
			channel.verbose("Customer: " + cellno + ", has subscribed ", 0);

		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
	}
}
