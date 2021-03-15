package de.nsg.app;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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
        try {
            this.button = false;

            ConnectivityManager connectivitymanager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkinfo = connectivitymanager.getActiveNetworkInfo();

            if (networkinfo != null && networkinfo.isConnected()) {
                URL upstreamurl = new URL("https://raw.githubusercontent.com/nsgtest/references/master/references.json");
                extendedInputStreamReader upstreaminputstreamreader = new extendedInputStreamReader(upstreamurl.openStream());
                JSONArray upstream = new JSONArray(upstreaminputstreamreader.fetch());
                upstreaminputstreamreader.close();

                File referencesfile = new File(context.getFilesDir(), "references.json");
                if (referencesfile.exists()) {
                    FileInputStream fileinputstream = new FileInputStream(referencesfile);
                    extendedInputStreamReader inputstreamreader= new extendedInputStreamReader(fileinputstream);
                    JSONArray references = new JSONArray(inputstreamreader.fetch());
                    fileinputstream.close();
                    inputstreamreader.close();

                    for (int i = 0; i < upstream.length(); i++) {
                        extendedJSONObject upstreamobject = new extendedJSONObject(upstream.getString(i));

                        for (int j = 0; j < references.length(); j++) {
                            extendedJSONObject referencesobject = new extendedJSONObject(references.getString(j));
                            if (upstreamobject.getString("Name").equals(referencesobject.getString("Name"))) {
                                if (upstreamobject.getString("Checksum").equals(referencesobject.getString("Checksum"))) {
                                    break;
                                } else {
                                    upstreamobject.write(context);
                                    this.button = true;
                                    break;
                                }

                            } else if (j == references.length() - 1) {
                                upstreamobject.write(context);
                                break;
                            }
                        }
                    }

                    for (int j = 0; j < references.length(); j++) {
                        extendedJSONObject referencesobject = new extendedJSONObject(references.getString(j));

                        for (int i = 0; i < upstream.length(); i++) {
                            extendedJSONObject upstreamobject = new extendedJSONObject(upstream.getString(i));
                            if (referencesobject.getString("Name").equals(upstreamobject.getString("Name"))) {
                                break;
                            } else if (i == upstream.length() - 1) {
                                referencesobject.delete(context);
                            }
                        }
                    }
                } else {
                    for (int i = 0; i < upstream.length(); i++) {
                        extendedJSONObject object = new extendedJSONObject(upstream.getString(i));
                        object.write(context);
                    }
                }

                FileOutputStream fileoutputstream = new FileOutputStream(referencesfile);
                fileoutputstream.write(upstream.toString().getBytes());
                fileoutputstream.close();
            }
        } catch (JSONException e) {
            Log.d("NSG", "JSONException", e);
        } catch (IOException e) {
            Log.d("NSG", "IOException", e);
        }
    }
}