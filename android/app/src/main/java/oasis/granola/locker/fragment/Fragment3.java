package oasis.granola.locker.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JsResult;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import java.util.HashMap;
import java.util.Map;

import oasis.granola.locker.AppHelper;
import oasis.granola.locker.LoginActivity;
import oasis.granola.locker.MainActivity;
import oasis.granola.locker.R;

import static android.content.Context.MODE_PRIVATE;

public class Fragment3 extends Fragment {

    //        private final String initialUrl = "https://www.google.co.kr/maps/@35.7924554,127.1365699,14z";
    private final String initialUrl = AppHelper.hostUrl + "/mypage/dispatcher";
    private String queryString;
    private WebView webView;
    SharedPreferences tokenStore;
    Handler handler = new Handler();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment3, container, false);

        webView = (WebView) view.findViewById(R.id.webView);
        tokenStore = getActivity().getSharedPreferences("tokenStore", MODE_PRIVATE);

        String token = tokenStore.getString("token", null);
        if (token != null) {
            queryString = "?token=" + token;
        }
        initWebView();
        return view;
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
                        ((MainActivity) getActivity()).onBackPressed();
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

    final class JavascriptInterface {
        @android.webkit.JavascriptInterface
        public void callLogout() {
            handler.post(new Runnable() {
                public void run() {
                    intentTo();
                }
            });

        }
    }

    private void intentTo() {
        Intent intent = new Intent(getActivity(), LoginActivity.class);
        startActivity(intent);
        getActivity().finish();
    }
}
