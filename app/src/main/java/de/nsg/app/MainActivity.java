package de.nsg.app;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.webkit.WebSettings;
import android.webkit.WebViewClient;
import android.widget.ImageButton;

public class MainActivity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setTheme(R.style.Theme_AppCompat);
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.main);

		final extendedWebView webview = findViewById(R.id.webview);
		WebSettings websettings = webview.getSettings();
		websettings.setJavaScriptEnabled(true);
		webview.setWebViewClient(new WebViewClient());

		implementedRunnable runnable = new implementedRunnable(this);
		final Thread thread = new Thread(runnable);

		webview.start(thread, this);

		if (runnable.button) {
			final ImageButton imagebutton = findViewById(R.id.button);
			imagebutton.setVisibility(View.VISIBLE);
			imagebutton.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					imagebutton.setVisibility(View.GONE);

					webview.start(thread, MainActivity.this);
				}
			});
		}
	}
}