package oasis.granola.locker;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ScanActivity extends AppCompatActivity {
    private IntentIntegrator qrScan;
    private SharedPreferences tokenStore;
    private Boolean isEntrust;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);

        Intent intent = getIntent();
        isEntrust = intent.getBooleanExtra("isEntrust", false);
        tokenStore = getSharedPreferences("tokenStore", MODE_PRIVATE);

        qrScan = new IntentIntegrator(this);
        qrScan.setOrientationLocked(false); // default가 세로모드인데 휴대폰 방향에 따라 가로, 세로로 자동 변경됩니다.
        qrScan.setPrompt("사각 테두리 안에 QR 코드를 인식해주세요.");
        qrScan.initiateScan();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if(result != null) {
            if(result.getContents() == null) {
//                qr코드 취소 시
                intentTo();
            } else {
                String token = tokenStore.getString("token", "");
                try {
                    setEntrust(token, result.getContents());
                } catch (JSONException e) {
                    e.printStackTrace();
                    finish();
                }
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    public void intentTo() {
        Intent intent = new Intent(ScanActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void setEntrust(String token, String qrCode) throws JSONException {
        if(AppHelper.requestQueue == null){
            AppHelper.requestQueue = Volley.newRequestQueue(getApplicationContext());
        }
        Map<String, Object> data = new HashMap<>();
        data.put("token", token);
        data.put("qrcode", qrCode);
        data.put("entrust", isEntrust);
        JSONObject jsonObject = new JSONObject(data);
        String url = "http://" + AppHelper.hostUrl + "/locker/entrust";
        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.POST,
                url,
                jsonObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if (response.getBoolean("success")) {
                                intentTo();
                            } else {
                                //올바르지 않은 qrcode
                                qrScan.initiateScan();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            finish();
//                            문제발생
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
        ) {};
        request.setShouldCache(false);
        AppHelper.requestQueue.add(request);
    }
}
