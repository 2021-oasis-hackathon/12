package oasis.granola.locker;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.webkit.WebView;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.toolbox.Volley;

public class LoadingActivity extends AppCompatActivity {
    SharedPreferences tokenStore = getSharedPreferences("tokenStore", MODE_PRIVATE);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.loading_activity);

//        권한 체크
//        로그인 체크
        String token = tokenStore.getString("token", "");
        if (token == "") {
            intentToLogin();
        } else {
            if (checkToken(token)) {
                intentToMain();
            }
            else {
                intentToLogin();
            }
        }
    }

    private void intentToLogin() {
        Intent intent = new Intent(LoadingActivity.this, LoginActivity.class);
        startActivity(intent);
    }
    private void intentToMain() {
        Intent intent = new Intent(LoadingActivity.this, MainActivity.class);
        startActivity(intent);
    }
    private boolean checkToken(String token) {
        if(AppHelper.requestQueue == null){
            AppHelper.requestQueue = Volley.newRequestQueue(getApplicationContext());
        }
        String url = "https://www.google.co.kr";
        StringRequest request = new StringRequest(
                Request.Method.GET,
                url,
                new Response.Listener<String>() { //응답을 잘 받았을 때 이 메소드가 자동으로 호출
                    @Override
                    public void onResponse(String response) {
                        println("응답 -> " + response);
                    }
                },
                new Response.ErrorListener() { //에러 발생시 호출될 리스너 객체
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        println("에러 -> " + error.getMessage());
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<String,String>();

                return params;
            }
        };
        request.setShouldCache(false); //이전 결과 있어도 새로 요청하여 응답을 보여준다.
        AppHelper.requestQueue = Volley.newRequestQueue(this); // requestQueue 초기화 필수
        AppHelper.requestQueue.add(request);
        println("요청 보냄.");
    }
}
