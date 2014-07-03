package com.kevintcoughlin.smodr;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

/**
 * An AsyncTask implementation for performing GETs on the Hypothetical REST APIs.
 */
public class GetTask extends AsyncTask<String, String, String> {
    private Context mContext;
    private String mRestUrl;
    private RestTaskCallback mCallback;

    /**
     * Creates a new instance of GetTask with the specified URL and callback.
     *
     * @param restUrl The URL for the REST API.
     * @param callback The callback to be invoked when the HTTP request
     *            completes.
     */
    public GetTask(Context context, String restUrl, RestTaskCallback callback) {
        this.mContext = context;
        this.mRestUrl = restUrl;
        this.mCallback = callback;
    }

    protected void onPreExecute() {
        ((Activity) mContext).setProgressBarIndeterminateVisibility(true);
    }

    @Override
    protected String doInBackground(String... params) {
        StringBuffer output = new StringBuffer("");

        try {
            InputStream stream = null;
            URL url = new URL("http://smodcast.com/channels/smodcast/feed/");
            URLConnection connection = url.openConnection();

            HttpURLConnection httpConnection = (HttpURLConnection) connection;
            httpConnection.setRequestMethod("GET");
            httpConnection.connect();

            if (httpConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {

                stream = httpConnection.getInputStream();
                BufferedReader buffer = new BufferedReader(new InputStreamReader(stream));
                String s = "";

                while ((s = buffer.readLine()) != null) {
                    output.append(s);
                }

            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return output.toString();
    }

    @Override
    protected void onPostExecute(String result) {
        ((Activity) mContext).setProgressBarIndeterminateVisibility(false);
        mCallback.onTaskComplete(result);
        super.onPostExecute(result);
    }
}