package de.nsg.app;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;
import android.webkit.WebView;
import android.webkit.WebSettings;
import android.net.ConnectivityManager;
import android.content.Context;
import android.net.NetworkInfo;
import android.webkit.WebViewClient;

public class MainActivity extends Activity{
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		WebView wv = new WebView(this);
		setContentView(wv);
		WebSettings ws = wv.getSettings();
		ws.setJavaScriptEnabled(true);
		wv.setWebViewClient(new WebViewClient());
		ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo an = cm.getActiveNetworkInfo();

		if (an != null && an.isConnected()) {

		}

		wv.loadUrl("file://" + getFilesDir().getAbsolutePath() + "/index.html");
	}
}
