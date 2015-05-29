package com.alittlescene.flipbooks;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.graphics.Typeface;
import android.view.Window;

public class LoginScreen extends ActionBarActivity {
    private TempSession session = new TempSession(null);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_login_screen);

        TextView txt = (TextView) findViewById(R.id.welcomeTextView);
        Typeface font = Typeface.createFromAsset(getAssets(), "cabinmedium.otf");
        txt.setTextSize(40);
        txt.setTypeface(font);

        final EditText usrname = (EditText) findViewById(R.id.userEditText);
        final EditText pswd = (EditText) findViewById(R.id.passEditText);

        final TextView errorMsg = (TextView) findViewById(R.id.loginErrorMsg);

        usrname.setOnKeyListener(new View.OnKeyListener() {

            public boolean onKey(View v, int keyCode, KeyEvent event) {

                if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                    pswd.requestFocus();
                    //if the enter key was pressed, then hide the keyboard and do whatever needs doing.
                    //InputMethodManager imm = (InputMethodManager) LoginScreen.this.getSystemService(Context.INPUT_METHOD_SERVICE);
                    //imm.hideSoftInputFromWindow(usrname.getApplicationWindowToken(), 0);

                    //do what you need on your enter key press here

                    return true;
                }

                return false;
            }
        });



        pswd.setOnKeyListener(new View.OnKeyListener() {

            public boolean onKey(View v, int keyCode, KeyEvent event) {

                if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                    //if the enter key was pressed, then hide the keyboard and do whatever needs doing.
                    InputMethodManager imm = (InputMethodManager) LoginScreen.this.getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(pswd.getApplicationWindowToken(), 0);

                    if (usrname.getText().toString().toLowerCase().equals("admin") && pswd.getText().toString().equals("admin")) {
                        errorMsg.setVisibility(View.INVISIBLE);
                        session.setUsername(usrname.getText().toString());
                        LoginScreen.this.loginUser();
                    }

                    // login error handling
                    else {
                        errorMsg.setVisibility(View.VISIBLE);
                    }

                    return true;
                }

                return false;
            }
        });

        final Button loginBtn = (Button) findViewById(R.id.loginConfirmBtn);

        TextView t = (TextView) findViewById(R.id.lostIDPWTextView);
        t.setMovementMethod(LinkMovementMethod.getInstance());

        // Login Up button behavior
        loginBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // check ID & PW. This is temporary method
                if (usrname.getText().toString().toLowerCase().equals("admin") && pswd.getText().toString().equals("admin")) {
                    errorMsg.setVisibility(View.INVISIBLE);
                    session.setUsername(usrname.getText().toString());
                    LoginScreen.this.loginUser();
                }

                // login error handling
                else {
                    errorMsg.setVisibility(View.VISIBLE);
                }
            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_login_screen, menu);
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

    private void loginUser() {
        session.setLoggedIn(true);
        Intent loginIntent = new Intent(LoginScreen.this,MainActivity.class);
        LoginScreen.this.startActivity(loginIntent);
    }
}
