package com.gramup.lead.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.google.android.material.tabs.TabLayout;
import com.gramup.lead.R;
import com.gramup.lead.adapter.CorporateViewAdapter;
import com.gramup.lead.adapter.ViewPagerAdapter;

import static com.gramup.lead.activity.CorporateRegistrationActivity.USERINFO;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;


public class LeadsMainActivity extends AppCompatActivity {

    TabLayout tabLayout;
    ViewPager viewPager;
    ViewPagerAdapter viewPagerAdapter;
    CorporateViewAdapter corporateViewAdapter;
    private String  code="";
    private SharedPreferences userpreferences;
    String  Role="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tab_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        userpreferences = getSharedPreferences(USERINFO,
                Context.MODE_PRIVATE);
        code=userpreferences.getString("code","");
        toolbar.setTitle("User : " + code);
        setSupportActionBar(toolbar);
        Role=userpreferences.getString("Role","");




        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimary, this.getTheme()));
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimary));
        }

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        viewPager = (ViewPager) findViewById(R.id.viewPager);
        corporateViewAdapter = new CorporateViewAdapter(getSupportFragmentManager());
        viewPager.setAdapter(corporateViewAdapter);
        tabLayout.setupWithViewPager(viewPager);
        /*if (Role.equals("4")){
            corporateViewAdapter = new CorporateViewAdapter(getSupportFragmentManager());
            viewPager.setAdapter(corporateViewAdapter);
            tabLayout.setupWithViewPager(viewPager);
        }else {
            viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
            viewPager.setAdapter(viewPagerAdapter);
            tabLayout.setupWithViewPager(viewPager);
        }
*/
    }


    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.login_menu, menu);
        return true;
    }
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();

        }

        if (item.getItemId() == R.id.logout) {

            SharedPreferences.Editor editor = userpreferences.edit();
            editor.remove("Token");
            editor.remove("Type");
            editor.remove("Login");
            editor.remove("Role");
            editor.remove("code");
            editor.commit();
            startActivity(new Intent(LeadsMainActivity.this,MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
            finish();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
