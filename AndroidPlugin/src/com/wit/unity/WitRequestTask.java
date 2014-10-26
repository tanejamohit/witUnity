package com.wit.unity;

import android.os.AsyncTask;
import android.util.Log;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class WitRequestTask extends AsyncTask<String, String, String>
{
  private final String WIT_URL = "https://api.wit.ai/message?q=";
  private final String AUTHORIZATION_HEADER = "Authorization";
  private final String BEARER_FORMAT = "Bearer %s";
  private final String ACCEPT_HEADER = "Accept";
  private final String ACCEPT_VERSION = "application/vnd.wit.20140501";
  private String _accessToken;

  public WitRequestTask(String accessToken)
  {
    this._accessToken = accessToken;
  }

  public static String convertStreamToString(InputStream is) throws Exception {
    BufferedReader reader = new BufferedReader(new InputStreamReader(is));
    StringBuilder sb = new StringBuilder();
    String line;
    while ((line = reader.readLine()) != null) {
      sb.append(line);
    }

    is.close();

    return sb.toString();
  }

  protected String doInBackground(String[] text)
  {
    String response = null;
    try {
      Log.d("Wit", new StringBuilder().append("Requesting ....").append(text[0]).toString());
      String getUrl = String.format("%s%s", new Object[] { WIT_URL, URLEncoder.encode(text[0], "utf-8") });
      URL url = new URL(getUrl);
      HttpURLConnection urlConnection = (HttpURLConnection)url.openConnection();
      urlConnection.addRequestProperty(AUTHORIZATION_HEADER, String.format(BEARER_FORMAT, new Object[] { this._accessToken }));
      urlConnection.addRequestProperty(ACCEPT_HEADER, ACCEPT_VERSION);
      try {
        InputStream in = new BufferedInputStream(urlConnection.getInputStream());
        response = convertStreamToString(in);
        in.close();
      } finally {
        urlConnection.disconnect();
      }
    } catch (Exception e) {
      Log.e("Wit", "An error occurred during the request, did you set your token correctly?", e);
    }
    return response;
  }

  protected void onPostExecute(String result)
  {
  }
}