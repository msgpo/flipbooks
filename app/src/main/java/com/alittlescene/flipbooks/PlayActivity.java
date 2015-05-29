package com.alittlescene.flipbooks;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.media.MediaPlayer;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.github.hiteshsondhi88.libffmpeg.ExecuteBinaryResponseHandler;
import com.github.hiteshsondhi88.libffmpeg.FFmpeg;
import com.github.hiteshsondhi88.libffmpeg.LoadBinaryResponseHandler;
import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegCommandAlreadyRunningException;
import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegNotSupportedException;

import java.io.File;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import dagger.Module;
import dagger.ObjectGraph;

public class PlayActivity extends ActionBarActivity implements View.OnClickListener {
    private ImageButton undoBtn;
    private boolean touchFlag;
    private String currPath;
    private String currPathVid;
    private String filename;
    private VideoView videoView;

    /**
     * Called when the activity is first created.
     */
    private static final String TAG = "PlayActivity";

    @Inject
    FFmpeg ffmpeg;

    @InjectView(R.id.play_check_button)
    ImageButton acceptBtn;

    private ProgressDialog progressDialog;

    public void onCreate(Bundle savedInstanceState) {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_play);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            currPath = extras.getString("PATH_TO_VIDEO");
            currPathVid = extras.getString("PATH_VIDEO");
            filename = extras.getString("FILENAME");
        }

        undoBtn = (ImageButton) findViewById(R.id.play_edit_button);
        acceptBtn = (ImageButton) findViewById(R.id.play_check_button);
        touchFlag = true;

        ButterKnife.inject(this);
        ObjectGraph.create(new DaggerDependencyModule(this)).inject(this);

        videoView = (VideoView) findViewById(R.id.videoView);
        videoView.setVideoPath(currPathVid);
        videoView.setMediaController(null);
        videoView.requestFocus();
        videoView.start();
        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mp.setLooping(true);
            }
        });

        loadFFMpegBinary();
        initUI();

        undoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmExit();
            }
        });

    }

    private void initUI() {
        acceptBtn.setOnClickListener(this);

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle(null);
    }

    private void loadFFMpegBinary() {
        try {
            ffmpeg.loadBinary(new LoadBinaryResponseHandler() {
                @Override
                public void onFailure() {
                    showUnsupportedExceptionDialog();
                }
            });
        } catch (FFmpegNotSupportedException e) {
            showUnsupportedExceptionDialog();
        }
    }

    private void execFFmpegBinary(final String command) {
        try {
            ffmpeg.execute(command, new ExecuteBinaryResponseHandler() {
                @Override
                public void onFailure(String s) {
                    //addTextViewToLayout("FAILED with output : " + s);
                }

                @Override
                public void onSuccess(String s) {
                    //addTextViewToLayout("SUCCESS with output : " + s);
                }

                @Override
                public void onProgress(String s) {
                    Log.d(TAG, "Started command : ffmpeg " + command);
                    //addTextViewToLayout("progress : " + s);
                    progressDialog.setMessage("Processing\n" + s);
                }

                @Override
                public void onStart() {
                    touchFlag = false;
                    Log.d(TAG, "Started command : ffmpeg " + command);
                    progressDialog.setMessage("Processing...");
                    progressDialog.show();
                }

                @Override
                public void onFinish() {
                    touchFlag = true;
                    Log.d(TAG, "Finished command : ffmpeg " + command);
                    progressDialog.dismiss();
                    videoView.resume();

                    File tempvideo = new File(currPath+"temp.mp4");
                    tempvideo.delete();

                    File video = new File(currPath+filename+".mp4");
                    video.setReadable(true, false);

                    Intent intent = new Intent(PlayActivity.this,LogoActivity.class);
                    PlayActivity.this.startActivity(intent);
                }
            });
        } catch (FFmpegCommandAlreadyRunningException e) {
            // do nothing for now
        }
    }

    private void showUnsupportedExceptionDialog() {
        new AlertDialog.Builder(PlayActivity.this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle(getString(R.string.device_not_supported))
                .setMessage(getString(R.string.device_not_supported_message))
                .setCancelable(false)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        PlayActivity.this.finish();
                    }
                })
                .create()
                .show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_play, menu);
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

    @Override
    public void onBackPressed() {
        confirmExit();
    }

    @Override
    public void onClick(View v) {
        confirmAccept();
    }

    private void confirmExit() {
        new AlertDialog.Builder(this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("Deleting video")
                .setMessage("Are you sure you want to delete the video?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteRecursive(new File(currPath));
                        Intent intent = new Intent(PlayActivity.this, RecordingActivity.class);
                        PlayActivity.this.startActivity(intent);
                        finish();
                    }

                })
                .setNegativeButton("No", null)
                .show();
    }

    private void confirmAccept() {
        videoView.pause();
        new AlertDialog.Builder(this)
                .setIcon(android.R.drawable.ic_menu_save)
                .setTitle("Confirm")
                .setMessage("Processing will take up to a minute.")
                .setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String command = "-y -i "+ currPathVid + " -vf crop=720:720:157.5:0 -an "+ currPath + filename +".mp4";
                        Log.d(TAG, command);
                        if (!TextUtils.isEmpty(command)) {
                            execFFmpegBinary(command);
                        } else {
                            Toast.makeText(PlayActivity.this, getString(R.string.empty_command_toast), Toast.LENGTH_LONG).show();
                        }
                    }

                })
                .setNegativeButton("Not Yet!", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        videoView.resume();
                    }
                })
                .show();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!touchFlag)
            return false;

        else
            return true;
    }


    void deleteRecursive(File fileOrDirectory) {

        if (fileOrDirectory.isDirectory())
            for (File child : fileOrDirectory.listFiles())
                deleteRecursive(child);

        fileOrDirectory.delete();
    }
}