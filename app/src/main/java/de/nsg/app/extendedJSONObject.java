package de.nsg.app;

import android.content.Context;
import android.util.Log;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

class extendedJSONObject extends JSONObject {
    extendedJSONObject(String json) throws JSONException {
        super(json);
    }

    void write(Context context) throws JSONException, IOException {
        InputStream inputstream = new URL(this.getString("Url")).openStream();
        extendedInputStreamReader inputstreamreader = new extendedInputStreamReader(inputstream);
        String upstream = inputstreamreader.fetch();
        inputstream.close();
        inputstreamreader.close();

        File file = new File(context.getFilesDir(), this.getString("Name"));
        FileOutputStream fileoutputstream = new FileOutputStream(file);
        fileoutputstream.write(upstream.getBytes());
        fileoutputstream.close();
    }

    void delete(Context context) throws JSONException {
        File file = new File(context.getFilesDir(), this.getString("Name"));
        if (file.delete()) {
            Log.d("NSG", "FileNotFound");
        }
    }
}