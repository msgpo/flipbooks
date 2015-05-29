package com.alittlescene.flipbooks;

import android.content.pm.ActivityInfo;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.content.Intent;

public class Title_1 extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        View view = findViewById(android.R.id.content);
        Animation mLoadAnimation = AnimationUtils.loadAnimation(getApplicationContext(), android.R.anim.fade_in);
        mLoadAnimation.setDuration(2000);
        view.startAnimation(mLoadAnimation);

        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_title_1);


        final Button signUpBtn = (Button) findViewById(R.id.signupBtn);
        final Button loginBtn = (Button) findViewById(R.id.loginBtn);

        // Sign Up button behavior
        signUpBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent signUpIntent = new Intent(Title_1.this,SignUpScreen.class);
                Title_1.this.startActivity(signUpIntent);
            }
        });

        // Login Up button behavior
        loginBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent loginIntent = new Intent(Title_1.this,LoginScreen.class);
                Title_1.this.startActivity(loginIntent);
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_title_1, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
