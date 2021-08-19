package oasis.granola.locker;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.JavascriptInterface;
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

public class ChatRoomActivity extends AppCompatActivity {
    private WebView webView;
    private final String initialUrl = AppHelper.hostUrl + "/chat/room/enter";
    private String queryString;
    private Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        Intent intent = getIntent();
        int roomId = intent.getIntExtra("roomId", -1);
        int userId = intent.getIntExtra("userId", -1);
        webView = (WebView) findViewById(R.id.webView);

        queryString = "?id=" + roomId + "&userId=" + userId;
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

        webView.setOnKeyListener(new View.OnKeyListener(){

            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() != KeyEvent.ACTION_DOWN)
                    return true;

                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    if (webView.canGoBack()) {
                        webView.goBack();
                    } else {
                        webView.destroy();
                        intentTo();
                    }

                    return true;
                }
                return false;
            }
        });
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
                                            webView.loadUrl(initialUrl + queryString);
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

        webView.loadUrl(initialUrl + queryString);
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
        Intent intent = new Intent(ChatRoomActivity.this, MainActivity.class);
        intent.putExtra("fragment", 2);
        startActivity(intent);
        finish();
    }
    final class JavascriptInterface {
        @android.webkit.JavascriptInterface
        public void closeChatRoom() {
            handler.post(new Runnable() {
                public void run() {
                    webView.destroy();
                    intentTo();
                }
            });

        }
    }
}
