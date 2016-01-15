package com.telenor.magri.script;

import org.apache.log4j.Logger;
import org.asteriskjava.fastagi.AgiChannel;
import org.asteriskjava.fastagi.AgiException;
import org.asteriskjava.fastagi.AgiRequest;

import com.agiserver.helper.DBHelper;
import com.agiserver.helper.common.AbstractBaseAgiScript;
import com.agiserver.helper.common.ConfigurationLoader;
import com.telenor.magri.common.SMSHelper;

public class saveSubConfirmation extends AbstractBaseAgiScript {
	private static final Logger logger = Logger
			.getLogger(saveSubConfirmation.class);

	public void service(AgiRequest request, AgiChannel channel)
			throws AgiException {
		try {

			String cellno = channel.getVariable("DB_CLI");
			String sub_from = "IVR";
			if (channel.getVariable("SUB_FROM") != null) {
				sub_from = channel.getVariable("SUB_FROM");

			}
			String msisdn = "92"
					+ channel.getVariable("DB_CLI").toString().substring(1);
			String status = channel.getVariable("STATUS");
			if (status.equals("-100")) {
				DBHelper.getInstance()
						.executeDml(
								"INSERT INTO subscription_requests (cellno, dt, sub_through, status,dial_count) VALUES(?, NOW(), ?, 'PENDING',0) ON DUPLICATE KEY UPDATE status='PENDING', dt=now(),sub_through=?,dial_count=0",
								super.getConnection(),
								new Object[] { cellno, sub_from, sub_from });
				channel.setVariable("IS_SUB", "YES");
				channel.verbose("Customer: " + cellno + ", has subscribed ", 0);
				SMSHelper.sendSMS(
						ConfigurationLoader.getProperty("SENT_SMS_URL"),
						msisdn, ConfigurationLoader.getProperty("SMS_FROM"),
						ConfigurationLoader.getProperty("SMSC"),
						ConfigurationLoader.getProperty("SUB_SMS"));
			} else if (status.equals("200")) {
				int rs = DBHelper
						.getInstance()
						.executeDml(
								"INSERT INTO subscriber  SELECT cellno,sub_dt,lang,last_call_dt,is_active,sub_from,last_billed_date,expiry_date,charging_attempt,charging_dt,response,sub_name,crop_focus_id,location_id,crop_mix,channel FROM subscriber_unsub WHERE cellno=? ORDER BY id DESC LIMIT 1",
								super.getConnection(), new Object[] { cellno });
				logger.info("Record has been inserted in sub_unsub ");
				channel.setVariable("IS_SUB", "YES");
				channel.verbose("Customer: " + cellno + ", has subscribed ", 0);
				SMSHelper.sendSMS(
						ConfigurationLoader.getProperty("SENT_SMS_URL"),
						msisdn, ConfigurationLoader.getProperty("SMS_FROM"),
						ConfigurationLoader.getProperty("SMSC"),
						ConfigurationLoader.getProperty("ALREADY_SUB_SMS"));
			}
			// channel.setVariable("IS_SUB", "YES");
			SMSHelper.sendSMS(
					ConfigurationLoader.getProperty("SENT_SMS_URL"),
					msisdn, ConfigurationLoader.getProperty("SMS_FROM"),
					ConfigurationLoader.getProperty("SMSC"),
					ConfigurationLoader.getProperty("MENU_SMS"));

		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
	}
}
