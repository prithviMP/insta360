package com.arashivision.sdk.demo.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.afollestad.materialdialogs.MaterialDialog;
import com.arashivision.onecamera.camerarequest.WifiInfo;
import com.arashivision.sdk.demo.R;
import com.arashivision.sdk.demo.util.CameraBindNetworkManager;
import com.arashivision.sdk.demo.util.NetworkManager;
import com.arashivision.sdkcamera.api.bean.SecretInfo;
import com.arashivision.sdkcamera.camera.InstaCameraManager;
import com.arashivision.sdkcamera.camera.callback.ICameraOperateCallback;

public class MainActivity extends BaseObserveCameraActivity {
    public static final String[] NECESSARY_PERMISSIONS = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};
    public static final String[] NECESSARY_PERMISSIONS_13 = new String[]{Manifest.permission.READ_MEDIA_AUDIO, Manifest.permission.READ_MEDIA_IMAGES, Manifest.permission.READ_MEDIA_VIDEO, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};
    private TextView tvWifiInfo;

    private String mAppID = "mmrdyydxlj0e0rwb";
    private String mSecretKey = "ynjy0l7v0ody9d36jbzlf74dfhvlrn5p";

    private MaterialDialog mLoadDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle(R.string.main_toolbar_title);
        tvWifiInfo = findViewById(R.id.tv_wifi_info);

        checkStoragePermission();
        if (InstaCameraManager.getInstance().getCameraConnectedType() != InstaCameraManager.CONNECT_TYPE_NONE) {
            onCameraStatusChanged(true);
        }

        findViewById(R.id.btn_full_demo).setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, FullDemoActivity.class));
        });

        findViewById(R.id.btn_connect_by_wifi).setOnClickListener(v -> {
            CameraBindNetworkManager.getInstance().bindNetwork(errorCode -> {
                InstaCameraManager.getInstance().openCamera(InstaCameraManager.CONNECT_TYPE_WIFI);
            });
        });

        findViewById(R.id.btn_connect_by_usb).setOnClickListener(v -> {
            InstaCameraManager.getInstance().openCamera(InstaCameraManager.CONNECT_TYPE_USB);
        });

        findViewById(R.id.btn_connect_by_ble).setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, BleActivity.class));
        });

        findViewById(R.id.btn_close_camera).setOnClickListener(v -> {
            CameraBindNetworkManager.getInstance().unbindNetwork();
            InstaCameraManager.getInstance().closeCamera();
        });

        findViewById(R.id.btn_capture).setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, CaptureActivity.class));
        });

        findViewById(R.id.btn_preview).setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, PreviewActivity.class));
        });

        findViewById(R.id.btn_preview2).setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, Preview2Activity.class));
        });

        findViewById(R.id.btn_preview3).setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, Preview3Activity.class));
        });

        findViewById(R.id.btn_live).setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, LiveActivity.class));
        });

        findViewById(R.id.btn_osc).setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, OscActivity.class));
        });

        findViewById(R.id.btn_list_camera_file).setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, CameraFilesActivity.class));
        });

        findViewById(R.id.btn_settings).setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, MoreSettingActivity.class));
        });

        findViewById(R.id.btn_play).setOnClickListener(v -> {
            // HDR
//            PlayAndExportActivity.launchActivity(this, StitchActivity.HDR_URLS);
            // PureShot
            PlayAndExportActivity.launchActivity(this, StitchActivity.PURE_SHOT_URLS);
        });

        findViewById(R.id.btn_stitch).setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, StitchActivity.class));
        });

        findViewById(R.id.btn_firmware_upgrade).setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, FwUpgradeActivity.class));
        });

        findViewById(R.id.btn_camera_activate).setOnClickListener(v -> {
            if ("".equals(mAppID) || "".equals(mSecretKey)) {
                Toast.makeText(MainActivity.this, "Secret info is Empty", Toast.LENGTH_SHORT).show();
                return;
            }
            InstaCameraManager.getInstance().activateCamera(new SecretInfo(mAppID, mSecretKey), new InstaCameraManager.IActivateCameraCallback() {
                @Override
                public void onStart() {
                    showLoading();
                    Log.d(TAG, "activateCamera onStart");
                }

                @Override
                public void onSuccess() {
                    hideLoading();
                    Toast.makeText(MainActivity.this, "Activate Success", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "activateCamera onSuccess");
                }

                @Override
                public void onFailed(String message) {
                    hideLoading();
                    Toast.makeText(MainActivity.this, "Activate Failed:" + message, Toast.LENGTH_LONG).show();
                    Log.d(TAG, "activateCamera onFailed:" + message);
                }
            });
        });


        findViewById(R.id.btn_wifi_info).setOnClickListener(v -> {
            InstaCameraManager.getInstance().fetchCameraOptions(new ICameraOperateCallback() {
                @Override
                public void onSuccessful() {
                    updateWifiInfo();
                }

                @Override
                public void onFailed() {

                }

                @Override
                public void onCameraConnectError() {

                }
            });
        });

    }

    private void checkStoragePermission() {
        String[] permissions;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissions = NECESSARY_PERMISSIONS_13;
        } else {
            permissions = NECESSARY_PERMISSIONS;
        }
        if (!checkPermissions(permissions)) {
            ActivityCompat.requestPermissions(this, permissions, 100);
        }
    }

    public boolean checkPermissions(String[] permissions) {
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    private void updateWifiInfo() {
        WifiInfo wifiInfo = InstaCameraManager.getInstance().getWifiInfo();
        if (wifiInfo != null) {
            tvWifiInfo.setText("ssid: "+wifiInfo.getSsid() +"\nPassWord: "+wifiInfo.getPwd());
        }
    }

    @Override
    public void onCameraStatusChanged(boolean enabled) {
        super.onCameraStatusChanged(enabled);
        findViewById(R.id.btn_capture).setEnabled(enabled);
        findViewById(R.id.btn_preview).setEnabled(enabled);
        findViewById(R.id.btn_preview2).setEnabled(enabled);
        findViewById(R.id.btn_preview3).setEnabled(enabled);
        findViewById(R.id.btn_live).setEnabled(enabled);
        findViewById(R.id.btn_osc).setEnabled(enabled);
        findViewById(R.id.btn_list_camera_file).setEnabled(enabled);
        findViewById(R.id.btn_settings).setEnabled(enabled);
        findViewById(R.id.btn_firmware_upgrade).setEnabled(enabled);
        findViewById(R.id.btn_wifi_info).setEnabled(enabled);
        findViewById(R.id.btn_camera_activate).setEnabled(enabled);
        if (enabled) {
            Toast.makeText(this, R.string.main_toast_camera_connected, Toast.LENGTH_SHORT).show();
        } else {
            CameraBindNetworkManager.getInstance().unbindNetwork();
            NetworkManager.getInstance().clearBindProcess();
            Toast.makeText(this, R.string.main_toast_camera_disconnected, Toast.LENGTH_SHORT).show();
        }
        updateWifiInfo();
    }

    @Override
    public void onCameraConnectError(int errorCode) {
        super.onCameraConnectError(errorCode);
        CameraBindNetworkManager.getInstance().unbindNetwork();
        Toast.makeText(this, getResources().getString(R.string.main_toast_camera_connect_error, errorCode), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onCameraSDCardStateChanged(boolean enabled) {
        super.onCameraSDCardStateChanged(enabled);
        if (enabled) {
            Toast.makeText(this, R.string.main_toast_sd_enabled, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, R.string.main_toast_sd_disabled, Toast.LENGTH_SHORT).show();
        }
    }

    private void showLoading() {
        if (mLoadDialog != null && mLoadDialog.isShowing()) return;
        hideLoading();
        mLoadDialog = new MaterialDialog.Builder(this)
                .content("loading...")
                .progress(true, 100).show();
    }

    private void hideLoading() {
        if (mLoadDialog != null) {
            mLoadDialog.dismiss();
        }
        mLoadDialog = null;
    }

}
