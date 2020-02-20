package com.example.mynewinstagram;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.mynewinstagram.ui.NewPost.NewPostFragment;
import com.example.mynewinstagram.ui.Quiz_feed.Feed;
import com.example.mynewinstagram.ui.UserProfile.UserProfileFragment;
import com.example.mynewinstagram.ui.add_quiz.AddQuiz;
import com.example.mynewinstagram.ui.home.HomeFragment;
import com.example.mynewinstagram.ui.notifications.NotificationsFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class HomeActivity extends AppCompatActivity {

    BottomNavigationView navigationView;
    static int flag = 0;
    Fragment currentFragment = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        navigationView = findViewById(R.id.nav_view);

        loadFragment(new HomeFragment());


    }

    @Override
    protected void onResume() {
        super.onResume();

        navigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch(item.getItemId()){
                    case R.id.navigation_home:
                        currentFragment = new HomeFragment();
                        break;

                    case R.id.navigation_notifications:
                        currentFragment = new NotificationsFragment();
                        break;
                    case R.id.navigation_userprofile:
                        currentFragment = new UserProfileFragment();
                        break;
                    case R.id.new_post_icon:
                        currentFragment = new NewPostFragment();
                        break;
                    case R.id.navigation_feed:
                        currentFragment = new Feed();
                        break;
                    case R.id.navigation_quiz:
                        currentFragment = new AddQuiz();
                        break;

                }
                return loadFragment(currentFragment);

            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if(currentFragment!=null){
            currentFragment.onActivityResult(requestCode,resultCode,data);
        }
    }

    private boolean loadFragment(Fragment fragment) {
          if(fragment!=null){
              if(flag==0){
                  getSupportFragmentManager()
                          .beginTransaction()
                          .replace(R.id.nav_host_fragment,fragment)
                          .commit();

                  flag = 1;
              }else{
                  getSupportFragmentManager()
                          .beginTransaction()
                          .replace(R.id.nav_host_fragment,fragment)
                          .addToBackStack(null)
                          .commit();

              }
              return true;
          }
          return false;
    }

}
