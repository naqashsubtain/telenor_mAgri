package com.telenor.magri.common;

public class SMSSender {

	public SMSSender() {

	}

	public boolean sendSMS(String msisdn, String shortcode, String message_key,
			String language) {
		boolean isSend = false;
		try {
			String response = Util.makeWebServiceRequest("send_message?msisdn="
					+ "msisdn" + "&shortcode=" + shortcode + "&message_key="
					+ message_key + "&language=" + language);

			if (response != null) {

				String[] params = response.split(",");
				if (params.length > 0) {

					isSend = params[0].equals("1") ? true : false;
				}

			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return isSend;
	}
}
