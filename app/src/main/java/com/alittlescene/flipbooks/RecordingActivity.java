package com.alittlescene.flipbooks;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.FieldPosition;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;
import android.widget.TextView;
import android.os.AsyncTask;
import android.os.SystemClock;
import android.widget.ProgressBar;

@SuppressWarnings( "deprecation" )
public class RecordingActivity extends Activity {
    private static final String TAG = "RecordingActivity";
    private Camera mCamera;
    private CameraPreview mPreview;
    private MediaRecorder mediaRecorder;
    private Button capture, switchCamera;
    private Context myContext;
    private LinearLayout cameraPreview;
    private boolean cameraFront = false;
    private ProgressBar customProgress;
    private TextView progressDisplay;
    private String currPath;
    private String currPathVid;
    private String filename;
    private boolean internal_flag = false;
    private boolean success_flag = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_recording);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        myContext = this;
        initialize();
    }

    private int findFrontFacingCamera() {
        int cameraId = -1;
        // Search for the front facing camera
        int numberOfCameras = Camera.getNumberOfCameras();
        for (int i = 0; i < numberOfCameras; i++) {
            CameraInfo info = new CameraInfo();
            Camera.getCameraInfo(i, info);
            if (info.facing == CameraInfo.CAMERA_FACING_FRONT) {
                cameraId = i;
                cameraFront = true;
                break;
            }
        }
        return cameraId;
    }

    private int findBackFacingCamera() {
        int cameraId = -1;
        // Search for the back facing camera
        // get the number of cameras
        int numberOfCameras = Camera.getNumberOfCameras();
        // for every camera check
        for (int i = 0; i < numberOfCameras; i++) {
            CameraInfo info = new CameraInfo();
            Camera.getCameraInfo(i, info);
            if (info.facing == CameraInfo.CAMERA_FACING_BACK) {
                cameraId = i;
                cameraFront = false;
                break;
            }
        }
        return cameraId;
    }

    public void onResume() {
        super.onResume();
        if (!hasCamera(myContext)) {
            Toast toast = Toast.makeText(myContext, "Sorry, your phone does not have a camera!", Toast.LENGTH_LONG);
            toast.show();
            finish();
        }
        if (mCamera == null) {
            // if the front facing camera does not exist
            if (findFrontFacingCamera() < 0) {
                Toast.makeText(this, "No front facing camera found.", Toast.LENGTH_LONG).show();
                switchCamera.setVisibility(View.GONE);
            }
            mCamera = Camera.open(findBackFacingCamera());
            Camera.Parameters params = mCamera.getParameters();
            // auto-focus
            if (params.getSupportedFocusModes().contains(
                    Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO)) {
                params.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);
            } else {
                params.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
            }

            // stabilization
            if (params.getVideoStabilization()) {
                params.setVideoStabilization(true);
            }

            // auto exposure
            if (params.getAutoExposureLock()) {
                params.setAutoExposureLock(false);
            }

            // auto whitebalance
            if (params.getAutoWhiteBalanceLock()) {
                params.setAutoWhiteBalanceLock(false);
            }

            mCamera.setParameters(params);
            mPreview.refreshCamera(mCamera);

        }
    }

    public void initialize() {
        cameraPreview = (LinearLayout) findViewById(R.id.camera_preview);

        mPreview = new CameraPreview(myContext, mCamera);
        cameraPreview.addView(mPreview);

        capture = (Button) findViewById(R.id.button_capture);
        capture.setOnClickListener(captrureListener);

        switchCamera = (Button) findViewById(R.id.button_ChangeCamera);
        switchCamera.setOnClickListener(switchCameraListener);

        customProgress = (ProgressBar)findViewById(R.id.customProgress);
        progressDisplay = (TextView)findViewById(R.id.percentTextView);
    }

    OnClickListener switchCameraListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            // get the number of cameras
            if (!recording) {
                int camerasNumber = Camera.getNumberOfCameras();
                if (camerasNumber > 1) {
                    // release the old camera instance
                    // switch camera, from the front and the back and vice versa

                    releaseCamera();
                    chooseCamera();
                } else {
                    Toast toast = Toast.makeText(myContext, "Sorry, your phone has only one camera!", Toast.LENGTH_LONG);
                    toast.show();
                }
            }
        }
    };

    public void chooseCamera() {
        // if the camera preview is the front
        if (cameraFront) {
            int cameraId = findBackFacingCamera();
            if (cameraId >= 0) {
                // open the backFacingCamera
                // set a picture callback
                // refresh the preview

                mCamera = Camera.open(cameraId);
                Camera.Parameters params = mCamera.getParameters();
                if (params.getSupportedFocusModes().contains(
                        Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO)) {
                    params.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);
                } else {
                    params.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
                }
                mCamera.setParameters(params);

                // mPicture = getPictureCallback();
                mPreview.refreshCamera(mCamera);
            }
        } else {
            int cameraId = findFrontFacingCamera();
            if (cameraId >= 0) {
                // open the backFacingCamera
                // set a picture callback
                // refresh the preview

                mCamera = Camera.open(cameraId);
                Camera.Parameters params = mCamera.getParameters();
                if (params.getSupportedFocusModes().contains(
                        Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO)) {
                    params.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);
                } else {
                    params.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
                }
                mCamera.setParameters(params);
                // mPicture = getPictureCallback();
                mPreview.refreshCamera(mCamera);
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        // when on Pause, release camera in order to be used from other
        // applications
        releaseCamera();
    }

    private boolean hasCamera(Context context) {
        // check if the device has camera
        if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
            return true;
        } else {
            return false;
        }
    }

    boolean recording = false;
    OnClickListener captrureListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            if (recording) {
                // stop recording and release camera
                mediaRecorder.stop(); // stop the recording
                releaseMediaRecorder(); // release the MediaRecorder object
                Toast.makeText(RecordingActivity.this, "Video captured!", Toast.LENGTH_LONG).show();
                recording = false;
            } else {
                if (!prepareMediaRecorder()) {
                    Toast.makeText(RecordingActivity.this, "Fail in prepareMediaRecorder()!\n - Ended -", Toast.LENGTH_LONG).show();
                    finish();
                }
                // work on UiThread for better performance
                runOnUiThread(new Runnable() {
                    public void run() {
                        // If there are stories, add them to the table

                        try {
                            mediaRecorder.start();
                            initData();
                        } catch (final Exception ex) {
                            // Log.i("---","Exception in thread");
                        }
                    }
                });

                recording = true;
            }
        }
    };

    private void releaseMediaRecorder() {
        if (mediaRecorder != null) {
            mediaRecorder.reset(); // clear recorder configuration
            mediaRecorder.release(); // release the recorder object
            mediaRecorder = null;
            mCamera.lock(); // lock camera for later use
        }
    }

    private boolean prepareMediaRecorder() {

        mediaRecorder = new MediaRecorder();

        mCamera.unlock();
        mediaRecorder.setCamera(mCamera);

        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.CAMCORDER);
        mediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);

        mediaRecorder.setProfile(CamcorderProfile.get(CamcorderProfile.QUALITY_720P));

        Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
        Date currentDate = calendar.getTime();
        SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd_kkmmss");
        String currDate = df.format(currentDate);
        Log.d(TAG, "Current date and time: "+currDate);
        filename = "flipbooks_" + currDate;

        File folder1 = new File(Environment.getExternalStorageDirectory() + "/Flipbooks/" + currDate + "/");
        boolean success = true;
        if (!folder1.exists()) {
            success = folder1.mkdir();
        }
        if (success) {
            mediaRecorder.setOutputFile(Environment.getExternalStorageDirectory() + "/Flipbooks/" + currDate + "/temp.mp4");
            currPath = Environment.getExternalStorageDirectory() + "/Flipbooks/" + currDate + "/";
            currPathVid = Environment.getExternalStorageDirectory() + "/Flipbooks/" + currDate + "/temp.mp4";
            internal_flag = false;
            success_flag = true;
            Log.d(TAG,"Success to make path: "+ Environment.getExternalStorageDirectory() + "/Flipbooks/" + currDate + "/temp.mp4");
        } else {
            File folder2 =new File(getFilesDir().getAbsolutePath() + "/" + currDate + "/");
            boolean success2 = true;
            if (!folder2.exists()) {
                success2 = folder2.mkdir();
            }

            if (success2) {
                mediaRecorder.setOutputFile(getFilesDir().getAbsolutePath() + "/" + currDate + "/temp.mp4");
                currPath = getFilesDir().getAbsolutePath() + "/" + currDate + "/";
                currPathVid = getFilesDir().getAbsolutePath() + "/" + currDate + "/temp.mp4";
                internal_flag = true;
                success_flag = true;
             }

            else {
                success_flag = false;
                Log.d(TAG, "Failed to make path: " + getFilesDir().getAbsolutePath() + "/" + currDate + "/temp.mp4");
            }
        }

        //mediaRecorder.setOutputFile("/sdcard/Flipbooks/" + filename + ".mp4");
        mediaRecorder.setMaxDuration(70000); // Set max duration 7 sec.
        mediaRecorder.setMaxFileSize(50000000); // Set max file size 50M
        mediaRecorder.setOrientationHint(90);

        try {
            mediaRecorder.prepare();
        } catch (IllegalStateException e) {
            releaseMediaRecorder();
            return false;
        } catch (IOException e) {
            releaseMediaRecorder();
            return false;
        }
        return true;

    }

    private void releaseCamera() {
        // stop and release camera
        if (mCamera != null) {
            mCamera.release();
            mCamera = null;
        }
    }

    private void initData() {
        new ShowCustomProgressBarAsyncTask().execute();
    }

    /**
     * Progress bar increment and display current state
     */
    public class ShowCustomProgressBarAsyncTask extends AsyncTask<Void, Integer, Void> {

        int myProgress;

        @Override
        protected void onPreExecute() {
            myProgress = 0;
        }

        @Override
        protected Void doInBackground(Void... params) {
            while(myProgress<7){
                myProgress++;
                publishProgress(myProgress);
                SystemClock.sleep(1000);
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            customProgress.setProgress(values[0]);
            customProgress.setSecondaryProgress(values[0] + 1);
            progressDisplay.setText(String.valueOf(myProgress)+"s");
        }

        @Override
        protected void onPostExecute(Void result) {
            // stop recording and release camera
            mediaRecorder.stop(); // stop the recording
            releaseMediaRecorder(); // release the MediaRecorder object
            Toast.makeText(RecordingActivity.this, "Video captured!", Toast.LENGTH_LONG).show();
            recording = false;
            myProgress = 0;

            if (success_flag) {
                if (internal_flag) {
                    File video = new File(currPathVid);
                    video.setReadable(true, false);
                }
            }

            Intent intent = new Intent(RecordingActivity.this,PlayActivity.class);
            intent.putExtra("PATH_TO_VIDEO", currPath);
            intent.putExtra("PATH_VIDEO", currPathVid);
            intent.putExtra("FILENAME", filename);
            RecordingActivity.this.startActivity(intent);
            finish();
        }
    }
}