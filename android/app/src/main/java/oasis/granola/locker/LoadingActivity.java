package oasis.granola.locker;

import android.Manifest;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class LoadingActivity extends AppCompatActivity {
    private final int PERMISSIONS_REQUEST_RESULT = 1;
    SharedPreferences tokenStore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);
        ImageView loading = findViewById(R.id.imageView);
        Glide.with(this).load(R.raw.loading).into(loading);
        tokenStore = getSharedPreferences("tokenStore", MODE_PRIVATE);
//        권한 체크 -> 로그인 체크
        new Handler().postDelayed(new Runnable()
        {
            @Override
            public void run()
            {
                permissionCheck();
            }
        }, 3000);
    }
    private void loginCheck() {
        String token = tokenStore.getString("token", null);
        if (token == null) {
            intentToLogin();
        } else {
            try {
                checkToken(token);
            } catch (JSONException e) {
                e.printStackTrace();
//                            문제발생 종료
            }
        }
    }
    private void intentToLogin() {
        Intent intent = new Intent(LoadingActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
    private void intentToMain() {
        Intent intent = new Intent(LoadingActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
    private void checkToken(String token) throws JSONException {
        if(AppHelper.requestQueue == null){
            AppHelper.requestQueue = Volley.newRequestQueue(getApplicationContext());
        }
        String url = "http://" + AppHelper.hostUrl + "/api/token-login";
        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.POST,
                url,
                new JSONObject(),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if (response.getJSONObject("data").getBoolean("success")) {
                                intentToMain();
                            } else {
                                intentToLogin();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            finish();
//                            문제발생 종료
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        finish();
//                            문제발생 종료
                    }
                }
        ) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> header = new HashMap<>();
                header.put("token", token);
                return header;
            }
        };
        request.setShouldCache(false);
        AppHelper.requestQueue.add(request);
    }
    private void permissionCheck(){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
        && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
        && ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED){
            //Manifest.permission.ACCESS_FINE_LOCATION 접근 승낙 상태 일때
            loginCheck();
        } else{
            //Manifest.permission.ACCESS_FINE_LOCATION 접근 거절 상태 일때
            ActivityCompat.requestPermissions(
                    this,new String[]{
                            Manifest.permission.ACCESS_FINE_LOCATION
                            , Manifest.permission.ACCESS_COARSE_LOCATION
                            , Manifest.permission.CAMERA},PERMISSIONS_REQUEST_RESULT);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,  int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSIONS_REQUEST_RESULT: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    loginCheck();
                } else {
                    AlertDialog.Builder localBuilder = new AlertDialog.Builder(this);
                    localBuilder.setTitle("권한 설정")
                            .setMessage("권한 거절로 인해 앱 사용이 제한됩니다.")
                            .setPositiveButton("권한 설정하러 가기", new DialogInterface.OnClickListener(){
                                public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt){
                                    try {
                                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                                                .setData(Uri.parse("package:" + getPackageName()));
                                        startActivity(intent);
                                    } catch (ActivityNotFoundException e) {
                                        e.printStackTrace();
                                        Intent intent = new Intent(Settings.ACTION_MANAGE_APPLICATIONS_SETTINGS);
                                        startActivity(intent);
                                    }
                                }})
                            .setNegativeButton("취소하기", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt) {
                                    finish();
                                }})
                            .create()
                            .show();
                }
            }
        }
    }
}
