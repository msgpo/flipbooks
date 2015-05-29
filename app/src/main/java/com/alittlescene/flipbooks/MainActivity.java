package com.alittlescene.flipbooks;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Typeface;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;


public class MainActivity extends ActionBarActivity {
    private TempSession session;

    private Button startProjBtn;
    private Button myProjBtn;
    private Button myAccountBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_main);

        startProjBtn = (Button) findViewById(R.id.main_start_project_btn);
        myProjBtn = (Button) findViewById(R.id.main_my_projects_btn);
        myAccountBtn = (Button) findViewById(R.id.main_my_account_btn);

        final TextView mainMsg = (TextView) findViewById(R.id.mainMenuTextView);
        Typeface font = Typeface.createFromAsset(getAssets(), "cabinmedium.otf");
        mainMsg.setTextSize(30);
        mainMsg.setTypeface(font);

        mainMsg.setText("Hello, " + session.getUsername());


        startProjBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, StartProject.class);
                MainActivity.this.startActivity(intent);
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
