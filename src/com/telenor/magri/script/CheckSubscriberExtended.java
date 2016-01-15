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

public class CheckSubscriberExtended extends AbstractBaseAgiScript {
	private static final Logger logger = Logger
			.getLogger(CheckSubscriberExtended.class);

	public CheckSubscriberExtended() throws DatabaseException {
		super(true);
	}

	public void service(AgiRequest request, AgiChannel channel)
			throws AgiException {
		try {
			String cellno = channel.getVariable("DB_CLI");
			String cid = channel.getVariable("CROP_ID");
			channel.setVariable("LANG_CHANGED", "NO");
			int dialcount = -1;
			logger.info("DB_CLI is : " + cellno);

			List<Map<String, Object>> cellnoHashMap = null;
			// List<Map<String, Object>> cropIdHashMap = null;

			cellnoHashMap = DBHelper
					.getInstance()
					.query("SELECT  * from subscriber  WHERE `cellno`=? and is_active=?",
							super.getConnection(), new Object[] { cellno, 1 });
			logger.info(cellnoHashMap);
			if (!(cellnoHashMap.isEmpty())) {
				channel.setVariable("STATUS", "100");

				String sub_name = null;
				String loc_name = null;
				String cname = null;
				String location = null;
				String lang = null;

				for (Map<String, Object> cellNumberMap : cellnoHashMap) {

					sub_name = cellNumberMap.get("sub_name").toString();
					location = cellNumberMap.get("location_id").toString();
					lang = cellNumberMap.get("lang").toString();
				}
				cname = (String) DBHelper.getInstance().singleResult(
						"SELECT  crop_name from crops  WHERE id=?",
						super.getConnection(),
						new Object[] { Integer.parseInt(cid) });
				loc_name = (String) DBHelper.getInstance().singleResult(
						"SELECT  location_name from location  WHERE id=?",
						super.getConnection(),
						new Object[] { Integer.parseInt(location) });
				channel.setVariable("CROP_ID", cid);
				channel.setVariable("CROP_NAME", cname);
				channel.setVariable("SUB_NAME", sub_name);
				channel.setVariable("LOC_NAME", loc_name);
				channel.setVariable("LOC_ID", location);
				channel.setVariable("LANG", lang);

				logger.info("Caller name is : " + sub_name
						+ " and selected crop of user is : " + cname);
			} else {
				cellnoHashMap = DBHelper.getInstance().query(
						"SELECT  * from subscriber_unsub  WHERE `cellno`=?",
						super.getConnection(), new Object[] { cellno });
				if (!(cellnoHashMap.isEmpty())) {
					channel.setVariable("STATUS", "200");
					String sub_name = null;
					String loc_name = null;
					String cname = null;
					String location = null;
					String lang = null;

					for (Map<String, Object> cellNumberMap : cellnoHashMap) {
						cid = cellNumberMap.get("crop_focus_id").toString();
						sub_name = cellNumberMap.get("sub_name").toString();
						location = cellNumberMap.get("location_id").toString();
						lang = cellNumberMap.get("lang").toString();
					}
					cname = (String) DBHelper.getInstance().singleResult(
							"SELECT  crop_name from crops  WHERE id=?",
							super.getConnection(),
							new Object[] { Integer.parseInt(cid) });
					loc_name = (String) DBHelper.getInstance().singleResult(
							"SELECT  location_name from location  WHERE id=?",
							super.getConnection(),
							new Object[] { Integer.parseInt(location) });
					channel.setVariable("CROP_ID", cid);
					channel.setVariable("CROP_NAME", cname);
					channel.setVariable("SUB_NAME", sub_name);
					channel.setVariable("LOC_NAME", loc_name);
					channel.setVariable("LOC_ID", location);
					channel.setVariable("LANG", lang);

					logger.info("Caller name is : " + sub_name
							+ " and selected crop of user is : " + cname);
				} else {
					String lang = null;
					cellnoHashMap = DBHelper
							.getInstance()
							.query("SELECT  * from subscription_requests  WHERE `cellno`=? and status='PENDING'",
									super.getConnection(),
									new Object[] { cellno });
					if (!(cellnoHashMap.isEmpty())) {
						channel.setVariable("STATUS", "300");

						for (Map<String, Object> cellNumberMap : cellnoHashMap) {
							dialcount = Integer.parseInt(cellNumberMap.get(
									"dial_count").toString());
							lang = cellNumberMap.get("sub_lang").toString();
							channel.setVariable("LANG", lang);
							if (lang.equals("2")) {
								channel.setVariable("LANG_CHANGED", "YES");
							}
							channel.setVariable("DIAL_COUNT", dialcount + "");
						}
					}
					dialcount = dialcount + 1;
					DBHelper.getInstance()
							.executeDml(
									"UPDATE subscription_requests set dial_count=? where cellno=?",
									super.getConnection(),
									new Object[] { dialcount, cellno });
				}

			}

		} catch (Exception e) {
			logger.error("Exception", e);
		}
	}

}
