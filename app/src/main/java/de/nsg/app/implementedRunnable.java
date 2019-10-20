package de.nsg.app;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Base64;
import android.util.Log;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;

class implementedRunnable implements Runnable {
    private Context context;
    Boolean button;

    implementedRunnable(Context context) {
        this.context = context;
    }

    @Override
    public void run() {
        this.button = false;

        try {
            ConnectivityManager connectivitymanager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkinfo = connectivitymanager.getActiveNetworkInfo();

            if (networkinfo != null && networkinfo.isConnected()) {
                URL upstreamurl = new URL("https://api.github.com/repos/nsgtest/refs/contents/references.json?ref=master");
                extendedInputStreamReader upstreaminputstreamreader = new extendedInputStreamReader(upstreamurl.openStream());
                String upstream = upstreaminputstreamreader.fetch();
                upstreaminputstreamreader.close();

                JSONObject response = new JSONObject(upstream);
                byte[] base64 = Base64.decode(response.getString("content"), Base64.DEFAULT);
                JSONArray upstreamarray = new JSONArray(new String(base64));

                File references = new File(context.getFilesDir(), "references.json");

                this.button = false;
                if (references.exists()) {
                    FileInputStream fileinputstream = new FileInputStream(references);
                    extendedInputStreamReader inputstreamreader= new extendedInputStreamReader(fileinputstream);
                    JSONArray referencesarray = new JSONArray(inputstreamreader.fetch());
                    fileinputstream.close();
                    inputstreamreader.close();

                    for (int i = 0; i < upstreamarray.length(); i++) {
                        extendedJSONObject upstreamobject = new extendedJSONObject(upstreamarray.getString(i));

                        for (int j = 0; j < referencesarray.length(); j++) {
                            extendedJSONObject referencesobject = new extendedJSONObject(referencesarray.getString(j));
                            if (upstreamobject.getString("Name").equals(referencesobject.getString("Name"))) {
                                if (upstreamobject.getString("Checksum").equals(referencesobject.getString("Checksum"))) {
                                    break;
                                } else {
                                    upstreamobject.write(context);

                                    if (upstreamobject.getString("Name").equals("index.html") || upstreamobject.getString("Name").equals("index.css") || upstreamobject.getString("Name").equals("index.js")) {
                                        this.button = true;
                                    }
                                    break;
                                }

                            } else if (j == referencesarray.length() - 1) {
                                upstreamobject.write(context);
                                break;
                            }
                        }
                    }

                    for (int j = 0; j < referencesarray.length(); j++) {
                        extendedJSONObject referencesobject = new extendedJSONObject(referencesarray.getString(j));

                        for (int i = 0; i < upstreamarray.length(); i++) {
                            extendedJSONObject upstreamobject = new extendedJSONObject(upstreamarray.getString(i));
                            if (referencesobject.getString("Name").equals(upstreamobject.getString("Name"))) {
                                break;
                            } else if (i == upstreamarray.length() - 1) {
                                referencesobject.delete(context);
                            }
                        }
                    }
                } else {
                    for (int i = 0; i < upstreamarray.length(); i++) {
                        extendedJSONObject object = new extendedJSONObject(upstreamarray.getString(i));
                        object.write(context);
                    }
                }

                FileOutputStream fileoutputstream = new FileOutputStream(references);
                fileoutputstream.write(new String(base64).getBytes());
                fileoutputstream.close();
            }
        } catch (JSONException e) {
            Log.d("NSG", "JSONException", e);
        } catch (IOException e) {
            Log.d("NSG", "IOException", e);
        }
    }
}