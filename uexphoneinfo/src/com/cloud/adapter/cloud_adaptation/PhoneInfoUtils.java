package com.cloud.adapter.cloud_adaptation;

import android.annotation.TargetApi;
import android.app.AppOpsManager;
import android.content.Context;
import android.os.Binder;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import java.lang.reflect.Method;
import static org.zywx.wbpalmstar.engine.universalex.EUExUtil.mContext;

/**
 * Created by zhang on 2018/5/3.
 */

public class PhoneInfoUtils {
    /**
     * 根据手机类型判断，小米和魅族手机与其他手机不一样
     */
    public static boolean getPermission() {
        if ("Xiaomi".equals(Build.MANUFACTURER)) {//小米手机
            Log.e("TAG","小米手机");
            return isFloatWindowOpAllowed(mContext);
        } else if ("Meizu".equals(Build.MANUFACTURER)) {//魅族手机
            Log.e("TAG","魅族手机");
            return isFloatWindowOpAllowed(mContext);
        }else {
               return Settings.canDrawOverlays(mContext);
        }

    }

    /**
     * 请求用户给予悬浮窗的权限
     */
    public static void requestPermission() {
        if (isFloatWindowOpAllowed(mContext)) {//已经开启
        } else {
            Toast.makeText(mContext,"悬浮框权限未开启",Toast.LENGTH_LONG).show();
        }
    }


    /**
     * 判断悬浮窗权限
     *
     * @param context
     * @return
     */
    @TargetApi(Build.VERSION_CODES.KITKAT)
    public static boolean isFloatWindowOpAllowed(Context context) {
        final int version = Build.VERSION.SDK_INT;
        if (version >= 19) {
            return checkOp(context, 24);  // AppOpsManager.OP_SYSTEM_ALERT_WINDOW
        } else {
            if ((context.getApplicationInfo().flags & 1 << 27) == 1 << 27) {
                return true;
            } else {
                return false;
            }
        }
}


    public static boolean checkOp(Context context, int op) {
        final int version = Build.VERSION.SDK_INT;

        if (version >= 19) {
            AppOpsManager manager = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
            try {
                Class<?> spClazz = Class.forName(manager.getClass().getName());
                Method method = manager.getClass().getDeclaredMethod("checkOp", int.class, int.class, String.class);
                int property = (Integer) method.invoke(manager, op,
                        Binder.getCallingUid(), context.getPackageName());
                Log.e("399", " property: " + property);

                if (AppOpsManager.MODE_ALLOWED == property) {
                    return true;
                } else {
                    return false;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Log.e("399", "Below API 19 cannot invoke!");
        }
        return false;
    }




}
