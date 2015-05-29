package com.alittlescene.flipbooks;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Typeface;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class SignUpScreen extends ActionBarActivity {
    private Pattern pattern;
    private Matcher matcher;

    private static final String PASSWORD_PATTERN =
            "((?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?!.*\\s).{8,20})";

    private boolean validUsername = false;
    private boolean validPw = false;
    private boolean validPwConf = false;
    private boolean validEmail = false;

    private TempSession session = new TempSession(null);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_sign_up_screen);

        pattern = Pattern.compile(PASSWORD_PATTERN);

        TextView txt = (TextView) findViewById(R.id.signupWelcome);
        Typeface font = Typeface.createFromAsset(getAssets(), "cabinmedium.otf");
        txt.setTextSize(25);
        txt.setTypeface(font);



        final EditText usrname = (EditText) findViewById(R.id.signupUsrname);
        final EditText pwd = (EditText) findViewById(R.id.signupPw);
        final EditText pwdConf = (EditText) findViewById(R.id.signupPwConf);
        final EditText email = (EditText) findViewById(R.id.signupEmail);

        final TextView errorMsg = (TextView) findViewById(R.id.signupErrorTextView);
        final Button signupBtn = (Button) findViewById(R.id.signUpButton);

        TextView tos = (TextView) findViewById(R.id.tosTextView);
        tos.setMovementMethod(LinkMovementMethod.getInstance());

        // username EditText OnKeyListener
        usrname.setOnKeyListener(new View.OnKeyListener() {

            public boolean onKey(View v, int keyCode, KeyEvent event) {

                if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                    String usrnameStr = usrname.getText().toString();

                    if(usrnameStr != null && usrnameStr.length() > 5) {
                        pwd.requestFocus();
                        usrname.setError(null);
                        validUsername = true;
                    }

                    else {
                        //if the enter key was pressed, then hide the keyboard and do whatever needs doing.
                        InputMethodManager imm = (InputMethodManager) SignUpScreen.this.getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(usrname.getApplicationWindowToken(), 0);

                        usrname.setError("Invalid username");
                        errorMsg.setText("Username must be at least 6 characters");
                        errorMsg.setVisibility(View.VISIBLE);
                        signupBtn.setEnabled(false);
                        validUsername = false;
                    }

                    //do what you need on your enter key press here

                    return true;
                }

                return false;
            }
        });

        // usrname focusListner
        usrname.setOnFocusChangeListener(new View.OnFocusChangeListener() {

            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    String usrnameStr = usrname.getText().toString();

                    if (usrnameStr != null && usrnameStr.length() > 5) {
                        pwd.requestFocus();
                        usrname.setError(null);
                        validUsername = true;
                    } else {
                        //if the enter key was pressed, then hide the keyboard and do whatever needs doing.
                        InputMethodManager imm = (InputMethodManager) SignUpScreen.this.getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(usrname.getApplicationWindowToken(), 0);

                        usrname.setError("Invalid username");
                        errorMsg.setText("Username must be at least 6 characters");
                        errorMsg.setVisibility(View.VISIBLE);
                        signupBtn.setEnabled(false);
                        validUsername = false;
                    }
                }
            }
        });

        // pwd EditText OnKeyListener
        pwd.setOnKeyListener(new View.OnKeyListener() {

            public boolean onKey(View v, int keyCode, KeyEvent event) {

                if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                    final String pwdStr = pwd.getText().toString();
                    // validate password
                    if (pwdValidation(pwdStr)) {
                        pwdConf.requestFocus();
                        errorMsg.setVisibility(View.INVISIBLE);
                        pwd.setError(null);
                        validPw = true;
                    }

                    // password is not validated
                    else {
                        //if the enter key was pressed, then hide the keyboard and do whatever needs doing.
                        InputMethodManager imm = (InputMethodManager) SignUpScreen.this.getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(pwd.getApplicationWindowToken(), 0);

                        pwd.setError("Invalid password");
                        errorMsg.setText("Password must be 8-20 characters containing at least \n" +
                                         "one lowercase, one uppercase, and one digit.");
                        errorMsg.setVisibility(View.VISIBLE);
                        signupBtn.setEnabled(false);
                        validPw = false;
                    }

                    return true;
                }

                return false;
            }
        });

        // pwd focusListener
        pwd.setOnFocusChangeListener(new View.OnFocusChangeListener() {

            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    final String pwdStr = pwd.getText().toString();
                    // validate password
                    if (pwdValidation(pwdStr)) {
                        pwdConf.requestFocus();
                        errorMsg.setVisibility(View.INVISIBLE);
                        pwd.setError(null);
                        validPw = true;
                    }

                    // password is not validated
                    else {
                        //if the enter key was pressed, then hide the keyboard and do whatever needs doing.
                        InputMethodManager imm = (InputMethodManager) SignUpScreen.this.getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(pwd.getApplicationWindowToken(), 0);

                        pwd.setError("Invalid password");
                        errorMsg.setText("Password must be 8-20 characters containing at least \n" +
                                "one lowercase, one uppercase, and one digit.");
                        errorMsg.setVisibility(View.VISIBLE);
                        signupBtn.setEnabled(false);
                        validPw = false;
                    }
                }
            }
        });

        // pwdConf EditText OnKeyListener
        pwdConf.setOnKeyListener(new View.OnKeyListener() {

            public boolean onKey(View v, int keyCode, KeyEvent event) {

                if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                    // check if pwd == pwdConf
                    if(pwd.getText().toString().equals(pwdConf.getText().toString())) {
                        email.requestFocus();
                        errorMsg.setVisibility(View.INVISIBLE);
                        pwdConf.setError(null);
                        validPwConf = true;
                    }

                    // password does not match
                    else {
                        //if the enter key was pressed, then hide the keyboard and do whatever needs doing.
                        InputMethodManager imm = (InputMethodManager) SignUpScreen.this.getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(pwdConf.getApplicationWindowToken(), 0);
                        pwdConf.setError("Does not match");
                        errorMsg.setText("Your confirmation does not match with what /n" +
                                         "you entered above. Please try again.");
                        errorMsg.setVisibility(View.VISIBLE);
                        signupBtn.setEnabled(false);
                        validPwConf = false;
                    }
                    return true;
                }

                return false;
            }
        });

        // pwdConf focusListener
        pwdConf.setOnFocusChangeListener(new View.OnFocusChangeListener() {

            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    // check if pwd == pwdConf
                    if(pwd.getText().toString().equals(pwdConf.getText().toString())) {
                        email.requestFocus();
                        errorMsg.setVisibility(View.INVISIBLE);
                        pwdConf.setError(null);
                        validPwConf = true;
                    }

                    // password does not match
                    else {
                        //if the enter key was pressed, then hide the keyboard and do whatever needs doing.
                        InputMethodManager imm = (InputMethodManager) SignUpScreen.this.getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(pwdConf.getApplicationWindowToken(), 0);
                        pwdConf.setError("Does not match");
                        errorMsg.setText("Your confirmation does not match with what /n" +
                                "you entered above. Please try again.");
                        errorMsg.setVisibility(View.VISIBLE);
                        signupBtn.setEnabled(false);
                        validPwConf = false;
                    }
                }
            }
        });

        // email EditText OnKeyListener
        email.setOnKeyListener(new View.OnKeyListener() {

            public boolean onKey(View v, int keyCode, KeyEvent event) {

                if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                    String emailStr = email.getText().toString();
                    if (emailStr != null && emailStr.length() > 3 && emailStr.contains("@")) {
                        //if the enter key was pressed, then hide the keyboard and do whatever needs doing.
                        InputMethodManager imm = (InputMethodManager) SignUpScreen.this.getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(email.getApplicationWindowToken(), 0);

                        signupBtn.setEnabled(true);
                        email.setError(null);
                        validEmail = true;
                    }

                    else {
                        email.setError("Invalid email address");
                        signupBtn.setEnabled(false);
                        validEmail = false;
                    }

                    return true;
                }

                return false;
            }
        });

        // email focusListener
        email.setOnFocusChangeListener(new View.OnFocusChangeListener() {

            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    String emailStr = email.getText().toString();
                    if (emailStr != null && emailStr.length() > 3 && emailStr.contains("@")) {
                        //if the enter key was pressed, then hide the keyboard and do whatever needs doing.
                        InputMethodManager imm = (InputMethodManager) SignUpScreen.this.getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(email.getApplicationWindowToken(), 0);

                        signupBtn.setEnabled(true);
                        email.setError(null);
                        validEmail = true;
                    }

                    else {
                        email.setError("Invalid email address");
                        signupBtn.setEnabled(false);
                        validEmail = false;
                    }
                }
            }
        });


        // Sign up button listener
        signupBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // check ID & PW. This is temporary method
                if (validUsername && validPw && validPwConf && validEmail) {
                    errorMsg.setVisibility(View.INVISIBLE);
                    SignUpScreen.this.loginUser();
                    session.setUsername(usrname.getText().toString());
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
        getMenuInflater().inflate(R.menu.menu_sign_up_screen, menu);
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

    // validating password with retype password
    private boolean pwdValidation(String pwd) {
        matcher = pattern.matcher(pwd);
        return matcher.matches();
    }


    private void loginUser() {
        session.setLoggedIn(true);
        Intent loginIntent = new Intent(SignUpScreen.this,MainActivity.class);
        SignUpScreen.this.startActivity(loginIntent);
    }
}
