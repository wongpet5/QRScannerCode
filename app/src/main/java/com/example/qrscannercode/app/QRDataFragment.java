package com.example.qrscannercode.app;

import android.app.Fragment;
import android.os.Bundle;
import android.util.JsonReader;
import android.util.Log;
import android.view.View;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import android.os.Handler;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;

public class QRDataFragment extends Fragment
{
    private Handler handler;
    private View rootView;
    private String error;

    public QRDataFragment() {

        handler = new Handler();
        error = "";
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.drawer_list_item_data, container, false);

        if (getArguments() != null) {

            if (getArguments().containsKey("CONTENT")) {
                String content = getArguments().getString("CONTENT");

                if (content != null && !content.isEmpty()) {
                    TextView text = (TextView) rootView.findViewById(R.id.content);
                    text.setText(content);
                }

                // DO JSON Parsing
                ParseJSONThread(content);
            }
        }

        return rootView;
    }

    // content is URL
    protected void ParseJSONThread(String content){

        final String _content = content;

        Thread t = new Thread() {

            public void run() {

                WebService(_content);

                final String ERROR = error;


                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        SetJSONData(ERROR);
                    }
                });
            }
        };
        t.start();
    }

    private void SetJSONData(String data) {
        TextView text = (TextView) rootView.findViewById(R.id.json);
        text.setText(data);
    }

    private void WebService(String content) {

        HashMap <String, String> retValue = new HashMap<String, String>();

        InputStream in = null;
        try {
            in = OpenHTTPConnection(content);
        } catch (IOException e) {
            Log.d("Networking", e.getLocalizedMessage());
        }

        try {
            JsonReader reader = new JsonReader(new InputStreamReader(in, "UTF-8"));

            try {
                reader.beginObject();

                // This JSON object "{"error":"Invalid or no provided token"}"
                // Very basic JSON so just look for "ERROR"

                while (reader.hasNext()) {

                    String name = reader.nextName();

                    if (name.equals("error")) {
                        error = reader.nextString();
                    }
                }

                reader.endObject();
            }
            catch (IOException ex) {
                Log.d("JSON Parse Error", ex.getLocalizedMessage());
            }
        }
        catch (java.io.UnsupportedEncodingException e) {
            Log.d("Networking", e.getLocalizedMessage());
        }

    }

    private InputStream OpenHTTPConnection(String content) throws IOException
    {
        InputStream in = null;
        int response = -1;

        URL url = new URL(content);

        URLConnection conn = url.openConnection();

        if (!(conn instanceof HttpURLConnection))
            throw new IOException("Not an HTTP connection");
        try {
            HttpURLConnection httpConn = (HttpURLConnection) conn;
            httpConn.setAllowUserInteraction(false);
            httpConn.setInstanceFollowRedirects(true);
            httpConn.setRequestMethod("GET");
            httpConn.connect();
            response = httpConn.getResponseCode();
            if (response == HttpURLConnection.HTTP_OK) {
                in = httpConn.getInputStream();
            }
            else if (response == HttpURLConnection.HTTP_FORBIDDEN) { // 403
                in = httpConn.getErrorStream();
            }

        }
        catch (Exception ex) {
            Log.d("Networking", ex.getLocalizedMessage());
        }


        return in;
    }


}
