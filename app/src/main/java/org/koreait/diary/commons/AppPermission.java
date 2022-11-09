package org.koreait.diary.commons;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import org.koreait.diary.MainActivity;

import java.util.ArrayList;

public class AppPermission {
    private static String[] permissions = {
            Manifest.permission.CAMERA,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    public static void check(Context context) {
        check(permissions, context);
    }

    public static void check(String[] permissions, Context context) {
        ArrayList<String> targetList = new ArrayList<String>();
        for(String permission : permissions) {
            int permissionCheck = ContextCompat.checkSelfPermission(context, permission);
            if (permissionCheck != PackageManager.PERMISSION_GRANTED && !ActivityCompat.shouldShowRequestPermissionRationale((MainActivity)context, permission)) {
                targetList.add(permission);
            }
        }
        if (targetList.size() > 0) {
            String[] targets = new String[targetList.size()];
            targetList.toArray(targets);

            // 위험 권한 부여 요청하기
            ActivityCompat.requestPermissions((MainActivity)context, targets, 101);
        }
    }
}
