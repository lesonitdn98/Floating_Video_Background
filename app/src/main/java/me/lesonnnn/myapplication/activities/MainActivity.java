package me.lesonnnn.myapplication.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import me.lesonnnn.myapplication.FloatViewManager;
import me.lesonnnn.myapplication.R;

public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_CODE_DRAW_OVERLAY_PERMISSION = 5;
    private FloatViewManager mFloatViewManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mFloatViewManager = new FloatViewManager(MainActivity.this);
        Button button = findViewById(R.id.mainButton);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkDrawOverlayPermission()) {
                    mFloatViewManager.showFloatView();
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_DRAW_OVERLAY_PERMISSION) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && Settings.canDrawOverlays(this)) {
                mFloatViewManager.showFloatView();
            }
        }
    }

    private boolean checkDrawOverlayPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(this)) {
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:" + getPackageName()));
            startActivityForResult(intent, REQUEST_CODE_DRAW_OVERLAY_PERMISSION);
            return false;
        } else {
            return true;
        }
    }

    //    public void checkPermission() {
    //        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
    //            if (!Settings.canDrawOverlays(this)) {
    //                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
    //                        Uri.parse("package:" + getPackageName()));
    //                startActivity(intent);
    //            }
    //        }
    //    }
}
