package oasis.granola.locker;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

    public class MainActivity extends AppCompatActivity {

        //    private final String initialUrl = "http://dev.userinsight.co.kr/milk-t";
        private final String initialUrl = "http://172.30.1.17:8080/";

        private WebView webView;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main);

            webView = (WebView) findViewById(R.id.webView);

            WebSettings webSettings = webView.getSettings();
            webSettings.setUserAgentString("Mozilla/5.0 AppleWebKit/535.19 Chrome/56.0.0 Mobile Safari/535.19");
            webSettings.setDomStorageEnabled(true);
            webSettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
            webSettings.setJavaScriptEnabled(true);
            webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
            webSettings.setAllowFileAccessFromFileURLs(true);
            webSettings.setUseWideViewPort(true);
            webSettings.setSupportMultipleWindows(true);

            webView.setWebViewClient(new WebViewClient() {
                @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                @Override
                public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                    super.onReceivedError(view, request, error);

                    if (request.isForMainFrame() && error != null) {
                        webView.loadUrl("about:blank");

                        new AlertDialog.Builder(view.getContext())
                                .setTitle(R.string.app_name)
                                .setMessage("서버와의 통신이 원활하지 않습니다.")
                                .setPositiveButton(android.R.string.ok,
                                        new AlertDialog.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {
                                                webView.loadUrl(initialUrl);
                                            }
                                        })
                                .setCancelable(false)
                                .create()
                                .show();
                    }
                }
            });
            webView.setWebChromeClient(new WebChromeClient() {
                @Override
                public boolean onJsAlert(WebView view, String url, String message, final JsResult result) {
                    new AlertDialog.Builder(view.getContext())
                            .setTitle(R.string.app_name)
                            .setMessage(message)
                            .setPositiveButton(android.R.string.ok,
                                    new AlertDialog.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            result.confirm();
                                        }
                                    })
                            .setCancelable(false)
                            .create()
                            .show();
                    return true;
                }

                @Override
                public boolean onJsConfirm(WebView view, String url, String message,
                                           final JsResult result) {
                    new AlertDialog.Builder(view.getContext())
                            .setTitle(R.string.app_name)
                            .setMessage(message)
                            .setPositiveButton("예",
                                    new AlertDialog.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            result.confirm();
                                        }
                                    })
                            .setNegativeButton("아니오",
                                    new AlertDialog.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            result.cancel();
                                        }
                                    })
                            .setCancelable(false)
                            .create()
                            .show();
                    return true;
                }

            });
            webView.setLayerType(View.LAYER_TYPE_HARDWARE, null);

            webView.loadUrl(initialUrl);
        }

        public void onBackPressed() {
            if(webView.canGoBack()) webView.goBack();
            else finish();
        }
    }