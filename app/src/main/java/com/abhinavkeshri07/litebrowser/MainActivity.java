package com.abhinavkeshri07.litebrowser;

import android.app.DownloadManager;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.PersistableBundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.webkit.DownloadListener;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    ProgressBar  progressBar;

    WebView webView;
    LinearLayout linearLayout;
    SwipeRefreshLayout swipeRefreshLayout;
    String urlToShare;
    String currentUri = "https://www.google.com";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        progressBar = findViewById(R.id.myProgressBar);

        webView = findViewById(R.id.myWebView);
        linearLayout = findViewById(R.id.progress_bar_linear_layout);
        swipeRefreshLayout = findViewById(R.id.swiperefresher);





        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewClient(){
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                linearLayout.setVisibility(View.VISIBLE);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                linearLayout.setVisibility(View.GONE);
                swipeRefreshLayout.setRefreshing(false);
                urlToShare = url;
            }
        });
        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                progressBar.setProgress(newProgress);
            }

            @Override
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);
                getSupportActionBar().setTitle(title);
            }

            @Override
            public void onReceivedIcon(WebView view, Bitmap icon) {
                super.onReceivedIcon(view, icon);

            }
        });

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                webView.reload();
                webView.loadUrl("https://www.google.com/");
            }
        });
        webView.setDownloadListener(new DownloadListener() {
            @Override
            public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {
                DownloadManager.Request myRequest = new DownloadManager.Request(Uri.parse(url));
                myRequest.allowScanningByMediaScanner();
                myRequest.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                DownloadManager myManager = (DownloadManager)getSystemService(DOWNLOAD_SERVICE);
                myManager.enqueue(myRequest);



            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.browser_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch(item.getItemId())
        {
            case R.id.back:
                webView.goBack();
                break;
            case R.id.forward:
                if(webView.canGoForward())
                {
                    webView.goForward();
                }
                else
                {
                    Toast.makeText(this,"Can't go forward! ", Toast.LENGTH_LONG ).show();
                }
                break;
            case R.id.refresh:
                webView.reload();
                break;
            case R.id.share:
                Intent shareIntent  = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                shareIntent.putExtra(Intent.EXTRA_TEXT, urlToShare);
                shareIntent.putExtra(Intent.EXTRA_SUBJECT, "The title");
                startActivity(Intent.createChooser(shareIntent, "share.."));

        }
            return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        outState.putString("currentUri", currentUri);
        Toast.makeText(getBaseContext(),currentUri,Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        currentUri = savedInstanceState.getString("currentUri");
        webView.loadUrl(currentUri);
        Toast.makeText(getApplicationContext(),currentUri,Toast.LENGTH_LONG).show();

    }

    @Override
    public void onBackPressed()
    {
        if(webView.canGoBack())
        {
            webView.goBack();
        }
        else
        {
            finish();
        }
    }


}
