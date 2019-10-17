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

		webview.start(thread, this);
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
		File file = new File(context.getFilesDir(), this.getString("Name"));
        FileOutputStream fileoutputstream = new FileOutputStream(file);
        fileoutputstream.write(new String(base64).getBytes());
        fileoutputstream.close();
	}

	void delete(Context context) throws JSONException {
		File file = new File(context.getFilesDir(), this.getString("Name"));
		if (file.delete()) {
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

	public void start(Thread thread, Context context) {
		try {
            File html = new File(context.getFilesDir(), "index.html");
            String url = html.toURI().toURL().toString();

            if (html.exists()) {
                this.loadUrl(url);
            } else {
                thread.join();
                this.loadUrl(url);
            }
        } catch (MalformedURLException e) {
            Log.d("NSG", "MalformedURLException", e);
            this.exception();
        } catch (InterruptedException e) {
            Log.d("NSG", "InterruptedException", e);
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

				File references = new File(context.getFilesDir(), "references.json");

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