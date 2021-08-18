package oasis.granola.locker;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;


import com.google.android.material.bottomnavigation.BottomNavigationView;

import oasis.granola.locker.fragment.Fragment1;
import oasis.granola.locker.fragment.Fragment2;
import oasis.granola.locker.fragment.Fragment3;

public class MainActivity extends AppCompatActivity {

    // FrameLayout에 각 메뉴의 Fragment를 바꿔 줌
        private FragmentManager fragmentManager = getSupportFragmentManager();
        private FragmentTransaction transaction;
        // 4개의 메뉴에 들어갈 Fragment들
        private Fragment1 frag1 = new Fragment1();
        private Fragment2 frag2 = new Fragment2();
        private Fragment3 frag3 = new Fragment3();
        private BottomNavigationView bottomNavigationView;
        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main);

            Intent intent = getIntent();
            int fragNum = intent.getIntExtra("fragment", 1);
            bottomNavigationView = findViewById(R.id.bnv_main);

            doNavigate(fragNum);
        }
        private void doNavigate(int fragNum) {
            transaction = fragmentManager.beginTransaction();
            switch(fragNum){
                case 1:
                    bottomNavigationView.setSelectedItemId(R.id.first);
                    transaction.replace(R.id.frame_layout, frag1).commitAllowingStateLoss();
                    break;
                case 2:
                    bottomNavigationView.setSelectedItemId(R.id.second);
                    transaction.replace(R.id.frame_layout, frag2).commitAllowingStateLoss();
                    break;
                case 3:
                    bottomNavigationView.setSelectedItemId(R.id.third);
                    transaction.replace(R.id.frame_layout, frag3).commitAllowingStateLoss();
                    break;
            }

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
}
