package oasis.granola.locker;
import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.MenuItem;

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

        // FrameLayout에 각 메뉴의 Fragment를 바꿔 줌
        private FragmentManager fragmentManager = getSupportFragmentManager();
        // 4개의 메뉴에 들어갈 Fragment들
        private Fragment1 frag1 = new Fragment1();
        private Fragment2 frag2 = new Fragment2();
        private Fragment3 frag3 = new Fragment3();

        private BottomNavigationView bottomNavigationView;
        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main);

//            LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

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
