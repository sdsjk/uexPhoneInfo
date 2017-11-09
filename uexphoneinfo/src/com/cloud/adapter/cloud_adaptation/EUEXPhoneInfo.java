package com.cloud.adapter.cloud_adaptation;
import android.annotation.TargetApi;
import android.app.AppOpsManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Binder;
import android.os.Build;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;
import org.zywx.wbpalmstar.engine.DataHelper;
import org.zywx.wbpalmstar.engine.EBrowserView;
import org.zywx.wbpalmstar.engine.universalex.EUExBase;
import org.zywx.wbpalmstar.engine.universalex.EUExUtil;

import java.lang.reflect.Method;
/**
 * Created by zhang on 2017/9/19.
 */

public class EUEXPhoneInfo extends EUExBase  {
    private static WindowManager wm;
    private View phoneView;
    private static boolean flag=true;
    public static PhoneInfo phoneInfo=new PhoneInfo();
    public EUEXPhoneInfo(Context context, EBrowserView eBrowserView) {
        super(context, eBrowserView);
    }
    @Override
    protected boolean clean() {
        return false;
    }
    public static void onApplicationCreate(final Context context){

    }
    public  void callBackPluginJs(String methodName, String jsonData){
        String js = SCRIPT_HEADER + "if(" + methodName + "){"
                + methodName + "('" + jsonData + "');}";
        onCallback(js);
    }
    public void initPhoneData(String[] parm) {
        if(parm.length<1){
           return;
        }

        phoneInfo=DataHelper.gson.fromJson(parm[0], PhoneInfo.class);
        //判断是否开启悬浮框权限
        getPermission();

    }

    /**
     * 根据手机类型判断，小米和魅族手机与其他手机不一样
     */
    private void getPermission() {
        if ("Xiaomi".equals(Build.MANUFACTURER)) {//小米手机
            Log.e("TAG","小米手机");
            requestPermission();
        } else if ("Meizu".equals(Build.MANUFACTURER)) {//魅族手机
            Log.e("TAG","魅族手机");
            requestPermission();
        }

    }

    /**
     * 请求用户给予悬浮窗的权限
     */
    public void requestPermission() {
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




    public static  class PhoneReceiver extends BroadcastReceiver {
        public Context mcontext;
        public View phoneView=null;
        int layoutId = EUExUtil.getResLayoutID("phone_alert");


        @Override
        public void onReceive(Context context, Intent intent){
            mcontext=context;


            if(intent.getAction().equals(Intent.ACTION_NEW_OUTGOING_CALL)){
                //如果是去电（拨出）
                Log.e("TAG","拨出");
            }else{
                //查了下android文档，貌似没有专门用于接收来电的action,所以，非去电即来电
                if(flag) {
//                    Log.e("TAG", "=====================来电来电来电来电");
                    TelephonyManager tm = (TelephonyManager)context.getSystemService(Service.TELEPHONY_SERVICE);
                    tm.listen(listener, PhoneStateListener.LISTEN_CALL_STATE);
                    flag=false;
                }

            }
        }


        private PhoneStateListener listener=new PhoneStateListener(){

            @Override
            public void onCallStateChanged(int state, String incomingNumber) {
                if(phoneView==null){
                    phoneView= LayoutInflater.from(EUExUtil.mContext).inflate(layoutId, null);
                }
                //state 当前状态 incomingNumber,貌似没有去电的API
                super.onCallStateChanged(state, incomingNumber);
                switch(state){
                    case TelephonyManager.CALL_STATE_IDLE:
                        if(phoneView.getParent()!=null){
                            wm.removeView(phoneView);
                        }

                        break;
                    case TelephonyManager.CALL_STATE_OFFHOOK:
                        if(phoneView.getParent()!=null){
                            wm.removeView(phoneView);
                        }
                        break;
                    case TelephonyManager.CALL_STATE_RINGING:
                        int len=0;
                        try {
                             len=phoneInfo.getLinks().size();
                        }catch (Exception e){
                        Toast.makeText(mcontext,"没有初始化数据",Toast.LENGTH_LONG).show();
                    }
                         String nums;
                         String name="陌生人";
                         String positioninfo="未知";
                         String messageinfo="未知";
                         String companyinfo="未知";
                        TextView nameview,positioninfoview,messageinfoview,companyinfoview;

                        for(int i=0;i<len;i++){
                            PhoneInfo.LinksBean linksBean = phoneInfo.getLinks().get(i);
                            String num = linksBean.getNum();
                            if(incomingNumber.equals(num)){
                                nums=incomingNumber;
                                name=linksBean.getName();
                                positioninfo=linksBean.getPositioninfo();
                                messageinfo=linksBean.getMessageinfo();
                                companyinfo=linksBean.getCompanyinfo();
                                break;
                            }

                        }
                        if(wm==null){
                            wm = (WindowManager)EUExUtil.mContext.getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
                        }
                        final WindowManager.LayoutParams params = new WindowManager.LayoutParams();
                        int type;
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                            type = WindowManager.LayoutParams.TYPE_TOAST;
                        } else {
                            type = WindowManager.LayoutParams.TYPE_PHONE;
                        }
                        params.type = type;
                        params.flags = WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM
                                | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                                | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL;                        params.gravity= Gravity.CENTER;
                        params.width = WindowManager.LayoutParams.WRAP_CONTENT;
                        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
                        params.x = 0;
                        params.y = 0;
                        params.gravity= Gravity.CENTER;
                        params.format = PixelFormat.RGBA_8888;
//                        int layoutId = EUExUtil.getResLayoutID("phone_alert");
//                        phoneView= LayoutInflater.from(EUExUtil.mContext).inflate(layoutId, null);
                        nameview= (TextView) phoneView.findViewById(EUExUtil.getResIdID("name"));
                        positioninfoview= (TextView) phoneView.findViewById(EUExUtil.getResIdID("positioninfo"));
                        messageinfoview= (TextView) phoneView.findViewById(EUExUtil.getResIdID("messageinfo"));
                        companyinfoview= (TextView) phoneView.findViewById(EUExUtil.getResIdID("companyinfo"));
                        nameview.setText(name);
                        positioninfoview.setText(positioninfo);
                        messageinfoview.setText(messageinfo);
                        companyinfoview.setText(companyinfo);
                        phoneView.setOnTouchListener(new View.OnTouchListener() {
                            float lastX, lastY;
                            int oldOffsetX, oldOffsetY;
                            int tag = 0;
                            @Override
                            public boolean onTouch(View view, MotionEvent event) {

                                final int action = event.getAction();
                                float x = event.getX();
                                float y = event.getY();
                                if (tag == 0) {
                                    oldOffsetX = params.x;
                                    oldOffsetY = params.y;
                                }
                                if (action == MotionEvent.ACTION_DOWN) {
                                    lastX = x;
                                    lastY = y;
                                } else if (action == MotionEvent.ACTION_MOVE) {
                                    // 减小偏移量,防止过度抖动
                                    params.x += (int) (x - lastX) / 3;
                                    params.y += (int) (y - lastY) / 3;
                                    tag = 1;
                                    if (phoneView != null)
                                        wm.updateViewLayout(phoneView, params);
                                } else if (action == MotionEvent.ACTION_UP) {
                                    int newOffsetX = params.x;
                                    int newOffsetY = params.y;
                                    if (Math.abs(oldOffsetX - newOffsetX) <= 20 && Math.abs(oldOffsetY - newOffsetY) <= 20) {

                                    } else {
                                        tag = 0;
                                    }
                                }
                                return true;
                            }
                        });
                        if(phoneView.getParent()!=null){

                        }else {
                            wm.addView(phoneView, params);
                        }




                        Log.e("TAG","响铃:来电号码"+incomingNumber);
                        break;
                }
            }
        };
    };
}
