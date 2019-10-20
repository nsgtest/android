package de.nsg.app;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.webkit.WebView;
import java.io.File;
import java.net.MalformedURLException;

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
                thread.start();
                thread.join();
            } else {
                thread.start();
                thread.join();
                if (html.exists()) {
                    this.loadUrl(url);
                } else {
                    this.exception();
                }
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