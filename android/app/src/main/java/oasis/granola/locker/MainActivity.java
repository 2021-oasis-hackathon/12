package oasis.granola.locker;
import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.MenuItem;
import android.webkit.WebView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;


import com.google.android.material.bottomnavigation.BottomNavigationView;

import oasis.granola.locker.fragment.Fragment1;
import oasis.granola.locker.fragment.Fragment2;
import oasis.granola.locker.fragment.Fragment3;

public class MainActivity extends AppCompatActivity {
        private final int PERMISSIONS_REQUEST_RESULT = 1;
//        private final String initialUrl = "https://www.google.co.kr/maps/@35.7924554,127.1365699,14z";
        private final String initialUrl = "172.30.1.17:8080";

        // FrameLayout에 각 메뉴의 Fragment를 바꿔 줌
        private FragmentManager fragmentManager = getSupportFragmentManager();
        // 4개의 메뉴에 들어갈 Fragment들
        private Fragment1 frag1 = new Fragment1();
        private Fragment2 frag2 = new Fragment2();
        private Fragment3 frag3 = new Fragment3();

        private WebView webView;
        private BottomNavigationView bottomNavigationView;
        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main);

//            LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
//            webView = (WebView) findViewById(R.id.webView);

            bottomNavigationView = findViewById(R.id.bnv_main);

            permissionCheck();
            doNavigate();
        }
        private void doNavigate() {
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.replace(R.id.frame_layout, frag1).commitAllowingStateLoss();

            bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    int id = item.getItemId();
                    switch(id){
                        case R.id.first:
                            getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout, frag1).commit();
                            break;
                        case R.id.second:
                            getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout, frag2).commit();
                            break;
                        case R.id.third:
                            getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout, frag3).commit();
                            break;

                    }
                    return true;
                }

            });
        }
//        private void initWebView(){
//            WebSettings webSettings = webView.getSettings();
//            webSettings.setUserAgentString("Mozilla/5.0 AppleWebKit/535.19 Chrome/56.0.0 Mobile Safari/535.19");
//            webSettings.setDomStorageEnabled(true);
//            webSettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
//            webSettings.setJavaScriptEnabled(true);
//            webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
//            webSettings.setAllowFileAccessFromFileURLs(true);
//            webSettings.setUseWideViewPort(true);
//            webSettings.setSupportMultipleWindows(true);
//            webSettings.setGeolocationEnabled(true);
//            webView.setWebViewClient(new WebViewClient() {
//                @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
//                @Override
//                public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
//                    super.onReceivedError(view, request, error);
//
//                    if (request.isForMainFrame() && error != null) {
//                        webView.loadUrl("about:blank");
//
//                        new AlertDialog.Builder(view.getContext())
//                                .setTitle(R.string.app_name)
//                                .setMessage("서버와의 통신이 원활하지 않습니다.")
//                                .setPositiveButton(android.R.string.ok,
//                                        new AlertDialog.OnClickListener() {
//                                            public void onClick(DialogInterface dialog, int which) {
//                                                webView.loadUrl(initialUrl);
//                                            }
//                                        })
//                                .setCancelable(false)
//                                .create()
//                                .show();
//                    }
//
//                }
//            });
//
//            webView.setWebViewClient(new WebViewClient() {
//                @Override
//                public void onLoadResource(WebView view, String url) {
//                    js(webView,
//                            "setInterval(map, 3000);"
//                    );
//                }
//
//                @Override
//                                           // Notify the host application that a page has finished loading.
//                                           public void onPageFinished(WebView view, String url)
//                                           {
//                                               js(webView,
//                                                       "setInterval(map, 3000);"
//                                           );
//                                           }
//                                       });
//
//            webView.setWebChromeClient(new WebChromeClient() {
//                @Override
//                public void onGeolocationPermissionsShowPrompt(String origin, GeolocationPermissions.Callback callback) {
//                    super.onGeolocationPermissionsShowPrompt(origin, callback);
//                    callback.invoke(origin, true, false);
//                }
//                @Override
//                public boolean onJsAlert(WebView view, String url, String message, final JsResult result) {
//                    new AlertDialog.Builder(view.getContext())
//                            .setTitle(R.string.app_name)
//                            .setMessage(message)
//                            .setPositiveButton(android.R.string.ok, new AlertDialog.OnClickListener() {
//                                public void onClick(DialogInterface dialog, int which) {
//                                    result.confirm();
//                                }
//                            })
//                            .setCancelable(false)
//                            .create()
//                            .show();
//                    return true;
//                }
//
//                @Override
//                public boolean onJsConfirm(WebView view, String url, String message,
//                                           final JsResult result) {
//                    new AlertDialog.Builder(view.getContext())
//                            .setTitle(R.string.app_name)
//                            .setMessage(message)
//                            .setPositiveButton("예",
//                                    new AlertDialog.OnClickListener() {
//                                public void onClick(DialogInterface dialog, int which) {
//                                    result.confirm();
//                                }
//                            })
//                            .setNegativeButton("아니오",
//                                    new AlertDialog.OnClickListener() {
//                                public void onClick(DialogInterface dialog, int which) {
//                                    result.cancel();
//                                }
//                            })
//                            .setCancelable(false)
//                            .create()
//                            .show();
//                    return true;
//                }
//
//            });
//            webView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
//
//        webView.loadUrl(initialUrl);
//    }
//    public void js(WebView view, String code)
//    {
//        String javascriptCode = "javascript:" + code;
//        if (Build.VERSION.SDK_INT >= 19) {
//            view.evaluateJavascript(javascriptCode, new ValueCallback<String>() {
//
//                @Override
//                public void onReceiveValue(String response) {
//                    Log.i("debug_log", response);
//                }
//            });
//        } else {
//            view.loadUrl(javascriptCode);
//        }
//    }
    private void permissionCheck(){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            //Manifest.permission.ACCESS_FINE_LOCATION 접근 승낙 상태 일때
//            initWebView();
        } else{
            //Manifest.permission.ACCESS_FINE_LOCATION 접근 거절 상태 일때
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},PERMISSIONS_REQUEST_RESULT);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,  int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode == PERMISSIONS_REQUEST_RESULT){
//            initWebView();
        }
    }
}
