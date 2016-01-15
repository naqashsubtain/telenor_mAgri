package com.telenor.magri.script;

import org.apache.log4j.Logger;
import org.asteriskjava.fastagi.AgiChannel;
import org.asteriskjava.fastagi.AgiException;
import org.asteriskjava.fastagi.AgiRequest;

import com.agiserver.helper.DBHelper;
import com.agiserver.helper.DatabaseException;
import com.agiserver.helper.common.AbstractBaseAgiScript;
import com.agiserver.helper.common.ConfigurationLoader;
import com.telenor.magri.common.SMSHelper;

public class Unsubscribe extends AbstractBaseAgiScript {
	private static final Logger logger = Logger.getLogger(Unsubscribe.class);

	public Unsubscribe() throws DatabaseException {
		super(true);
	}

	public void service(AgiRequest request, AgiChannel channel)
			throws AgiException {
		try {
			String cellno = channel.getVariable("DB_CLI");
			String msisdn = "92"
					+ channel.getVariable("DB_CLI").toString().substring(1);

			logger.info("DB_CLI is : " + cellno);
			logger.info(DBHelper.getInstance());

			int rs = DBHelper
					.getInstance()
					.executeDml(
							"insert into subscriber_unsub select null,cellno,sub_dt,now(),lang,last_call_dt,is_active,sub_from,last_billed_date,expiry_date,charging_attempt,charging_dt,response,sub_name,crop_focus_id,location_id,crop_mix,channel from subscriber WHERE cellno=?",
							super.getConnection(), new Object[] { cellno });
			logger.info("Record has been inserted in subscriber_unsub ");
			int rsd = DBHelper.getInstance().executeDml(
					"DELETE from subscriber where cellno=?",
					super.getConnection(), new Object[] { cellno });

			//////////////////////////////////////////////////////////////////
			rsd = DBHelper.getInstance().executeDml(
					"DELETE from help_requests where cellno=?",
					super.getConnection(), new Object[] { cellno });
			/////////////////////////////////////////////////////////////////----20160115--mateen


			SMSHelper.sendSMS(ConfigurationLoader.getProperty("SENT_SMS_URL"),
					msisdn, ConfigurationLoader.getProperty("SMS_FROM"),
					ConfigurationLoader.getProperty("SMSC"),
					ConfigurationLoader.getProperty("UNSUB_SMS"));
			logger.info("Record has been deleted from subscriber ");

		} catch (Exception e) {
			logger.error("Exception", e);
		}
	}

}
