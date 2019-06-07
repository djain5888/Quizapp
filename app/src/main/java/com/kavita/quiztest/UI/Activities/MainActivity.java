package com.kavita.quiztest.UI.Activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.kavita.quiztest.R;
import com.kavita.quiztest.UI.Fragments.AboutUsFragment;
import com.kavita.quiztest.UI.Fragments.DashboardFragment;
import com.kavita.quiztest.UI.Fragments.FeedbackFragment;
import com.kavita.quiztest.UI.Fragments.InstructionFragment;
import com.kavita.quiztest.UI.Fragments.ProfileFragment;
import com.kavita.quiztest.UI.Fragments.QuizFragment;

import java.util.List;

import java.util.Random;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_main);
        drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        SharedPreferences sharedPreferences = getSharedPreferences("user", Context.MODE_PRIVATE);
        String email = sharedPreferences.getString("email", "");
        String name = sharedPreferences.getString("name", "");
        if (name.isEmpty() || email.isEmpty()){
            name = "User";
            email = "user@gmail.com";
        }

        //random int generation
        Random random = new Random();
        int x = random.nextInt(2000);
        //random color generation
        ColorGenerator generator = ColorGenerator.MATERIAL;
        int color = generator.getColor(x);
        //text drawable generation
        TextDrawable.IBuilder builder = TextDrawable.builder()
                .beginConfig()
                .withBorder(4)
                .endConfig().round();
        TextDrawable drawable = builder.build(String.valueOf(name.charAt(0)), color);

        //inflating navigation header programmatically
        View hView =  navigationView.inflateHeaderView(R.layout.nav_header_main);
        ImageView userimage = hView.findViewById(R.id.textimageview);
        TextView nameview = hView.findViewById(R.id.nav_name);
        TextView emailview = hView.findViewById(R.id.nav_email);

        Typeface custom_font = Typeface.createFromAsset(getAssets(),  "fonts/Roboto-Regular.ttf");
        nameview.setTypeface(custom_font);
        emailview.setTypeface(custom_font);

        nameview.setText(name);
        emailview.setText(email);
        userimage.setImageDrawable(drawable);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open,
                R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

       /* if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    new DashboardFragment()).commit();
            navigationView.setCheckedItem(R.id.nav_dashboard);
        }*/
        Fragment frag = new DashboardFragment();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, frag)
                .commit();


    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // clear stack
        getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        switch (item.getItemId()) {

            case R.id.nav_dashboard:

                Fragment frag = new DashboardFragment();
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, frag)
                        .add(new DashboardFragment(), "dash")
                        .addToBackStack("dash")
                        .commit();

                break;
            case R.id.nav_profile:
                Fragment frag1 = new ProfileFragment();
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, frag1)
                        .add(new ProfileFragment(), "dash")
                        .addToBackStack("dash")
                        .commit();
                break;

            case R.id.nav_instructions:

                Fragment frag2 = new InstructionFragment();
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, frag2)
                        .add(new InstructionFragment(), "dash")
                        .addToBackStack("dash")
                        .commit();
                break;

            case R.id.nav_feedback:
                Fragment frag3 = new FeedbackFragment();
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, frag3)
                        .add(new FeedbackFragment(), "dash")
                        .addToBackStack("dash")
                        .commit();
                break;

            case R.id.nav_about_us:

                Fragment frag4 = new AboutUsFragment();
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, frag4)
                        .add(new AboutUsFragment(), "dash")
                        .addToBackStack("dash")
                        .commit();
                break;
            case R.id.nav_logout:
                showPopup();
                break;


        }
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    // first step helper function
    private void showPopup() {
        AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this);
        alert.setMessage("Are you sure?")
                .setPositiveButton("Logout", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {

                        logout(); // Last step. Logout function

                    }
                }).setNegativeButton("Cancel", null);

        AlertDialog alert1 = alert.create();
        alert1.show();
    }

    private void logout() {
        SharedPreferences sharedPreferences = getSharedPreferences("user", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("email", "");
        editor.putString("name", "");
        editor.apply();
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }


    boolean doubleBackToExitPressedOnce = false;

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {

            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {

                doubleBackToExitPressedOnce = false;
            }
        }, 2000);
    }

    /**  public void onBackPressed() {


     if (drawer.isDrawerOpen(GravityCompat.START)) {
     drawer.closeDrawer(GravityCompat.START);
     } else {

     super.onBackPressed();

     }
     }  **/



        }


//	#24B574