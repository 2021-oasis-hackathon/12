package oasis.granola.locker;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.webkit.JsResult;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    //        private final String initialUrl = "https://www.google.co.kr/maps/@35.7924554,127.1365699,14z";
    private final String initialUrl = AppHelper.hostUrl + "/login";
    private WebView webView;
    Handler handler = new Handler();
    SharedPreferences tokenStore;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        webView = (WebView) findViewById(R.id.webView);

        tokenStore = getSharedPreferences("tokenStore", MODE_PRIVATE);
        editor = tokenStore.edit();

        initWebView();
    }

        private void initWebView(){
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

            webView.setWebViewClient(new WebViewClient() {
                @Override
                public void onLoadResource(WebView view, String url) {
                    js(webView,
                            "setInterval(map, 3000);"
                    );
                }

                @Override
                // Notify the host application that a page has finished loading.
                public void onPageFinished(WebView view, String url)
                {
                    js(webView,
                            "setInterval(map, 3000);"
                    );
                }
            });

            webView.setWebChromeClient(new WebChromeClient() {
                @Override
                public boolean onJsAlert(WebView view, String url, String message, final JsResult result) {
                    new AlertDialog.Builder(view.getContext())
                            .setTitle(R.string.app_name)
                            .setMessage(message)
                            .setPositiveButton(android.R.string.ok, new AlertDialog.OnClickListener() {
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
        webView.addJavascriptInterface(new JavascriptInterface(),"myJSInterfaceName");
    }
    public void js(WebView view, String code)
    {
        String javascriptCode = "javascript:" + code;
        if (Build.VERSION.SDK_INT >= 19) {
            view.evaluateJavascript(javascriptCode, new ValueCallback<String>() {

                @Override
                public void onReceiveValue(String response) {
                    Log.i("debug_log", response);
                }
            });
        } else {
            view.loadUrl(javascriptCode);
        }
    }
    private void intentTo() {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    final class JavascriptInterface {
        @android.webkit.JavascriptInterface
        public void callAndroid(String token) {
            handler.post(new Runnable() {
                public void run() {
                    editor.putString("token", token);
                    editor.commit();
                    intentTo();
                }
            });

        }
    }

}
