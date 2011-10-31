package com.salami.awkward.mobile.control.experiment.tracking;

import com.loopj.android.http.*;

public class ServerClient {
  private static final String BASE_URL = "http://www.awkwardsalamigames.com/_calls/";

  private static AsyncHttpClient client = new AsyncHttpClient();

  public static void post(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
      //add in the validation key
	  params.put("validation_key",ServerValidationKey.get_key());
	  client.post(getAbsoluteUrl(url), params, responseHandler);
  }

  private static String getAbsoluteUrl(String relativeUrl) {
      return BASE_URL + relativeUrl;
  }
}