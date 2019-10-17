package de.nsg.app;

import android.app.Activity;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Base64;
import android.view.Window;
import android.webkit.WebView;
import android.webkit.WebSettings;
import android.net.ConnectivityManager;
import android.content.Context;
import android.net.NetworkInfo;
import android.webkit.WebViewClient;
import java.io.BufferedReader;
import java.io.File;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import android.util.Log;

public class MainActivity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.main);

		extendedWebView webview = findViewById(R.id.webview);
		WebSettings websettings = webview.getSettings();
		websettings.setJavaScriptEnabled(true);
		webview.setWebViewClient(new WebViewClient());

		implementedRunnable runnable = new implementedRunnable(this);
		Thread thread = new Thread(runnable);
		thread.start();

		try {
			webview.start(this);
		} catch (MalformedURLException e) {
			Log.d("NSG", "MalformedURLException", e);
			webview.exception();
		}
	}
}

class extendedFile extends File {
	extendedFile(File file, String string) {
		super(file, string);
	}

	@Override
	public URL toURL() throws MalformedURLException {
		return this.toURI().toURL();
	}

	void write(String content) throws IOException {
		FileOutputStream fileoutputstream = new FileOutputStream(this);
		fileoutputstream.write(content.getBytes());
		fileoutputstream.close();
	}
}

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

		JSONObject response = new JSONObject(upstream);
		byte[] base64 = Base64.decode(response.getString("content"), Base64.DEFAULT);
		extendedFile file = new extendedFile(context.getFilesDir(), this.getString("Name"));
		file.write(new String(base64));
	}

	void delete(Context context) throws JSONException {
		File file = new File(context.getFilesDir(), this.getString("Name"));
		Boolean deleted = file.delete();
		if (!deleted) {
            Log.d("NSG", "FileNotFound");
		}
	}
}

class extendedInputStreamReader extends InputStreamReader {
	extendedInputStreamReader(InputStream inputstream) {
		super(inputstream);
	}

	String fetch() throws IOException{
		BufferedReader bufferedreader = new BufferedReader(this);
		StringBuilder stringbuilder = new StringBuilder();
		String string;

		while ((string = bufferedreader.readLine()) != null) {
			stringbuilder.append(string);
		}

		bufferedreader.close();

		return stringbuilder.toString();
	}
}

class extendedWebView extends WebView {
	public extendedWebView(Context context) {
		super(context);
	}

	public extendedWebView(Context context, AttributeSet attributeset) {
		super(context, attributeset);
	}

	public void exception() {
		this.loadData("Etwas ist schief gelaufen, bitte verbinde das Gerät mit dem Internet und starte die App neu!", "text/html; charset=UTF-8", null);
	}

	public void start(Context context) throws MalformedURLException {
		extendedFile html = new extendedFile(context.getFilesDir(), "index.html");
		String url = html.toURL().toString();

		if (html.exists()) {
			this.loadUrl(url);
		} else {
			this.exception();
		}
	}
}

class implementedRunnable implements Runnable {
	private Context context;

	implementedRunnable(Context context) {
		this.context = context;
	}

	@Override
	public void run() {
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

				extendedFile references = new extendedFile(context.getFilesDir(), "references.json");

				if (references.exists()) {
					FileInputStream fileinputstream = new FileInputStream(references);
					extendedInputStreamReader inputstreamreader= new extendedInputStreamReader(fileinputstream);
					JSONArray referencesarray = new JSONArray(inputstreamreader.fetch());
                    fileinputstream.close();
                    inputstreamreader.close();

					Boolean notexist;
					for (int i = 0; i < upstreamarray.length(); i++) {
						extendedJSONObject upstreamobject = new extendedJSONObject(upstreamarray.getString(i));

						notexist = true;
						for (int j = 0; j < referencesarray.length(); j++) {
							extendedJSONObject referencesobject = new extendedJSONObject(referencesarray.getString(j));
							if (upstreamobject.getString("Name").equals(referencesobject.getString("Name")) && !upstreamobject.getString("Checksum").equals(referencesobject.getString("Checksum"))) {
								upstreamobject.write(context);
								notexist = false;
								break;
							}
						}

						if (notexist) {
							upstreamobject.write(context);
						}
					}

					for (int j = 0; j < referencesarray.length(); j++) {
						extendedJSONObject referencesobject = new extendedJSONObject(referencesarray.getString(j));

						notexist = true;
						for (int i = 0; i < upstreamarray.length(); i++) {
							extendedJSONObject upstreamobject = new extendedJSONObject(upstreamarray.getString(i));
							if (referencesobject.getString("Name").equals(upstreamobject.getString("Name"))) {
								notexist = false;
								break;
							}
						}

						if (notexist) {
							referencesobject.delete(context);
						}
					}
				} else {
					for (int i = 0; i < upstreamarray.length(); i++) {
						extendedJSONObject object = new extendedJSONObject(upstreamarray.getString(i));
						object.write(context);
					}
				}

				references.write(new String(base64));
			}
		} catch (JSONException e) {
			Log.d("NSG", "JSONException", e);
		} catch (IOException e) {
			Log.d("NSG", "IOException", e);
		} catch (NullPointerException e) {
			Log.d("NSG", "NullPointerException");
		}
	}
}