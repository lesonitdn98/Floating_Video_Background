package me.lesonnnn.myapplication;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.os.Build;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import static android.content.Context.WINDOW_SERVICE;

/**
 * Created by leson on 3/28/20
 */
public class FloatViewManager {
    private static boolean mIsFloatViewShowing;

    private Activity mActivity;
    private WindowManager mWindowManager;
    private View mFloatView;
    private WindowManager.LayoutParams mFloatViewLayoutParams;
    private boolean mFloatViewTouchConsumedByMove = false;
    private int mFloatViewLastX;
    private int mFloatViewLastY;
    private int mFloatViewFirstX;
    private int mFloatViewFirstY;

    @SuppressLint("InflateParams")
    public FloatViewManager(Activity activity) {
        mActivity = activity;
        LayoutInflater inflater = LayoutInflater.from(activity);
        mFloatView = inflater.inflate(R.layout.float_view_layout, null);
        VideoView videoView = mFloatView.findViewById(R.id.textView);

        MediaController mediaController = new MediaController(mActivity);
        videoView.setMediaController(mediaController);
        mediaController.setAnchorView(videoView);

        videoView.setVideoURI(Uri.parse("android.resource://" + mActivity.getPackageName() + "/" + R.raw.videoplayback));
        videoView.start();

        ImageButton imageButton = mFloatView.findViewById(R.id.imageButton);
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismissFloatView();
            }
        });

        mFloatView.setOnClickListener(mFloatViewOnClickListener);
        mFloatView.setOnTouchListener(mFloatViewOnTouchListener);

        mFloatViewLayoutParams = new WindowManager.LayoutParams();
        mFloatViewLayoutParams.format = PixelFormat.TRANSLUCENT;
        mFloatViewLayoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;

        mFloatViewLayoutParams.type = Build.VERSION.SDK_INT >= Build.VERSION_CODES.O
                ? WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
                : WindowManager.LayoutParams.TYPE_TOAST;

        mFloatViewLayoutParams.gravity = Gravity.CENTER;
        mFloatViewLayoutParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        mFloatViewLayoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;

    }

    private void dismissFloatView() {
        if (mIsFloatViewShowing) {
            mIsFloatViewShowing = false;
            mActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (mWindowManager != null) {
                        mWindowManager.removeViewImmediate(mFloatView);
                    }
                }
            });
        }
    }

    public void showFloatView() {
        if (!mIsFloatViewShowing) {
            mIsFloatViewShowing = true;
            mActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (!mActivity.isFinishing()) {
                        mWindowManager = (WindowManager) mActivity.getSystemService(WINDOW_SERVICE);
                        if (mWindowManager != null) {
                            mWindowManager.addView(mFloatView, mFloatViewLayoutParams);
                        }
                    }
                }
            });
        }
    }

    @SuppressWarnings("FieldCanBeLocal")
    private View.OnClickListener mFloatViewOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            mActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(mActivity, "Float view is clicked!", Toast.LENGTH_SHORT).show();
                }
            });
        }
    };

    @SuppressWarnings("FieldCanBeLocal")
    private View.OnTouchListener mFloatViewOnTouchListener = new View.OnTouchListener() {

        @SuppressLint("ClickableViewAccessibility")
        @TargetApi(Build.VERSION_CODES.FROYO)
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            WindowManager.LayoutParams prm = mFloatViewLayoutParams;
            int totalDeltaX = mFloatViewLastX - mFloatViewFirstX;
            int totalDeltaY = mFloatViewLastY - mFloatViewFirstY;

            switch (event.getActionMasked()) {
                case MotionEvent.ACTION_DOWN:
                    mFloatViewLastX = (int) event.getRawX();
                    mFloatViewLastY = (int) event.getRawY();
                    mFloatViewFirstX = mFloatViewLastX;
                    mFloatViewFirstY = mFloatViewLastY;
                    break;
                case MotionEvent.ACTION_MOVE:
                    int deltaX = (int) event.getRawX() - mFloatViewLastX;
                    int deltaY = (int) event.getRawY() - mFloatViewLastY;
                    mFloatViewLastX = (int) event.getRawX();
                    mFloatViewLastY = (int) event.getRawY();
                    if (Math.abs(totalDeltaX) >= 5 || Math.abs(totalDeltaY) >= 5) {
                        if (event.getPointerCount() == 1) {
                            prm.x += deltaX;
                            prm.y += deltaY;
                            mFloatViewTouchConsumedByMove = true;
                            if (mWindowManager != null) {
                                mWindowManager.updateViewLayout(mFloatView, prm);
                            }
                        } else {
                            mFloatViewTouchConsumedByMove = false;
                        }
                    } else {
                        mFloatViewTouchConsumedByMove = false;
                    }
                    break;
                default:
                    break;
            }
            return mFloatViewTouchConsumedByMove;
        }
    };
}
