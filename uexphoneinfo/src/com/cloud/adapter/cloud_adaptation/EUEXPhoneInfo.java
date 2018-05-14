package com.cloud.adapter.cloud_adaptation;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AppOpsManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PixelFormat;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;
import org.zywx.wbpalmstar.engine.DataHelper;
import org.zywx.wbpalmstar.engine.EBrowserView;
import org.zywx.wbpalmstar.engine.universalex.EUExBase;
import org.zywx.wbpalmstar.engine.universalex.EUExUtil;
import org.zywx.wbpalmstar.widgetone.uexphoneinfo.R;

import java.lang.reflect.Method;
/**
 * Created by zhang on 2017/9/19.
 */

public class EUEXPhoneInfo extends EUExBase implements View.OnClickListener {
    private static WindowManager wm;
    public static Context mcontext;
    public static View phoneView=null;
    private static boolean flag=true;
    public static PhoneInfo phoneInfo=new PhoneInfo();
    public static SharedPreferences sharedPreferences=null;
    public int functionId=-1;
    public static Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 1:
                    Log.e("TAG", "=======================初始化数据成功");
                    //判断是否开启悬浮框权限
                    break;
                case 2:
                    PhoneInfo.LinksBean linksBean = (PhoneInfo.LinksBean) msg.obj;
//                    Log.e("TAG", "=======================数据查找成功"+linksBean.toString());
                    showPhoneWindow(linksBean);
                    flag=true;
                    break;
            }

        }
    };
    private int requestOverlaysPermissionCode=1;

    private static void showPhoneWindow(PhoneInfo.LinksBean linksBean) {
        String name=linksBean.getName();
        String positioninfo=linksBean.getPositioninfo();
        String messageinfo=linksBean.getMessageinfo();
        String companyinfo=linksBean.getCompanyinfo();
        TextView nameview,positioninfoview,messageinfoview,companyinfoview;

        if(wm==null){
            wm = (WindowManager)EUExUtil.mContext.getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
        }
        final WindowManager.LayoutParams params = new WindowManager.LayoutParams();
        int type;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            type = WindowManager.LayoutParams.TYPE_PHONE;
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
//        Log.e("TAG", "phoneinf0==============="+phoneInfo);
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
//            Log.e("TAG", "========================phoneView不等同于空");
        }else {
//            Log.e("TAG", "========================phoneView等同于空");
            wm.addView(phoneView, params);
        }
    }

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

    /**
     * 检查数据是否缓存
     * @param param
     */
    public void checkDataCache(String[] param){
        if(param.length<1) {
            return;
        }
        try {
//            回调函数
            functionId=Integer.parseInt(param[0]);
//            检查是否有缓存数据
            if(sharedPreferences==null){
                sharedPreferences=mContext.getSharedPreferences("phoneinfo",Context.MODE_PRIVATE);
            }
            String userinfo = sharedPreferences.getString("userinfo", "");
            if("".equals(userinfo)){
//                没有缓存数据
                callbackToJs(functionId,false,false,PhoneInfoUtils.getPermission());
            }else {
//                有缓存数据
                callbackToJs(functionId,false,true,PhoneInfoUtils.getPermission());
            }
//            没有悬浮框权限弹出打开悬浮框提示框
            if(!PhoneInfoUtils.getPermission()) {
                showToastDialog();
//                Toast.makeText(mContext,"悬浮框权限未开启",Toast.LENGTH_LONG).show();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    //显示提示框
    PopupWindow popupWindow=null;
    private void showToastDialog() {

        if(popupWindow==null){
            popupWindow=new PopupWindow(mContext);
        }
        if(popupWindow.isShowing()){
            return;
        }
        View contentView=LayoutInflater.from(mContext).inflate(EUExUtil.getResLayoutID("poupup_toast"),null);
        popupWindow.setContentView(contentView);
        popupWindow.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        popupWindow.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);

        //设置popupwindow弹出动画
        popupWindow.setAnimationStyle(EUExUtil.getResStyleID("popupwindow_anim_style"));
        //设置popupwindow背景
        popupWindow.setBackgroundDrawable(new ColorDrawable());
        popupWindow.showAtLocation(contentView, Gravity.BOTTOM, 0, 0);

        //处理popupwindow
        popupwindowselectphoto(contentView);
        setBackgroundAlpha(0.5f);
    }
    //设置屏幕背景透明效果
    public void setBackgroundAlpha(float alpha) {
        WindowManager.LayoutParams lp = ((Activity)mContext).getWindow().getAttributes();
        lp.alpha = alpha;
        ((Activity)mContext).getWindow().setAttributes(lp);
    }
    private void popupwindowselectphoto(View contentView) {
        TextView popupwindow_cancle= (TextView) contentView.findViewById(EUExUtil.getResIdID("popupwindow_cancle"));
        TextView popupwindow_setting= (TextView) contentView.findViewById(EUExUtil.getResIdID("popupwindow_setting"));
        popupwindow_cancle.setOnClickListener(this);
        popupwindow_setting.setOnClickListener(this);
    }

    public void initPhoneData(final String[] parm) {
        if(parm.length<1){
           return;
        }
        new Thread(){
            public void run(){
                phoneInfo=DataHelper.gson.fromJson(parm[0], PhoneInfo.class);
                sharedPreferences=mContext.getSharedPreferences("phoneinfo",Context.MODE_PRIVATE);
                sharedPreferences.edit().putString("userinfo",parm[0]).commit();
                Message message=Message.obtain();
                message.what=1;
                message.obj=phoneInfo;
                handler.sendMessage(message);

            }
        }.start();
        //判断悬浮框权限
//        callBackPluginJs("uexCallKit.checkPerssions",PhoneInfoUtils.getPermission()+"");
    }

    @Override
    public void onClick(View v) {
        if(v.getId()==EUExUtil.getResIdID("popupwindow_setting")){
            //确认跳转申请
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
            intent.setData(Uri.parse("package:" + mContext.getPackageName()));
            startActivityForResult(intent, requestOverlaysPermissionCode);
        }else if(v.getId()==EUExUtil.getResIdID("popupwindow_cancle")){
            setBackgroundAlpha(1.0f);
            popupWindow.dismiss();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if(requestCode==requestOverlaysPermissionCode){
            if(PhoneInfoUtils.getPermission()) {
                if(popupWindow!=null){
                    popupWindow.dismiss();
                    setBackgroundAlpha(1);
                    Toast.makeText(mContext,"悬浮框权限开启",Toast.LENGTH_LONG).show();
                }

            }
        }

    }

    public static  class PhoneReceiver extends BroadcastReceiver {

        int layoutId = EUExUtil.getResLayoutID("phone_alert");

        @Override
        public void onReceive(Context context, Intent intent){
            mcontext= context;
            if(intent.getAction().equals(Intent.ACTION_NEW_OUTGOING_CALL)){
                //如果是去电（拨出）
                Log.e("TAG","拨出");
            }else{
                //查了下android文档，貌似没有专门用于接收来电的action,所以，非去电即来电
//                Log.e("TAG", "======================"+flag);
                if(flag) {
//                  Log.e("TAG", "=====================来电来电来电来电");
                    TelephonyManager tm = (TelephonyManager)context.getSystemService(Service.TELEPHONY_SERVICE);
                    tm.listen(listener, PhoneStateListener.LISTEN_CALL_STATE);
                    flag=false;
                }

            }
        }


        private PhoneStateListener listener=new PhoneStateListener(){

            @Override
            public void onCallStateChanged(int state, final String incomingNumber) {
                //state 当前状态 incomingNumber,貌似没有去电的API
                super.onCallStateChanged(state, incomingNumber);
                if(phoneView==null){
                    phoneView= LayoutInflater.from(EUExUtil.mContext).inflate(layoutId, null);
                }
//                Log.e("TAG", "========================"+state);
                switch(state){
                    case TelephonyManager.CALL_STATE_IDLE:
//                        Log.e("TAG", "===================CALL_STATE_IDLE======="+phoneView.getParent());
                        flag=true;
                        if(phoneView.getParent()!=null){
                            wm.removeView(phoneView);
                        }

                        break;
                    case TelephonyManager.CALL_STATE_OFFHOOK:
//                        Log.e("TAG", "===================CALL_STATE_OFFHOOK======="+phoneView.getParent());
                        flag=true;
                        if(phoneView.getParent()!=null){
                            wm.removeView(phoneView);
                        }
                        break;
                    case TelephonyManager.CALL_STATE_RINGING:

                        new Thread(){
                            public void run(){
                                int len=0;
                                try {
                                    len=phoneInfo.getLinks().size();
                                    if(len==0) {
                                        sharedPreferences=mcontext.getSharedPreferences("phoneinfo",Context.MODE_PRIVATE);
                                        String userinfo = sharedPreferences.getString("userinfo", "");
                                        phoneInfo=DataHelper.gson.fromJson(userinfo, PhoneInfo.class);
                                        len=phoneInfo.getLinks().size();
                                    }
                                }catch (Exception e){
                                    e.printStackTrace();
                                    Log.e("TAG", "缓存中获取数据");
                                    sharedPreferences=mcontext.getSharedPreferences("phoneinfo",Context.MODE_PRIVATE);
                                    String userinfo = sharedPreferences.getString("userinfo", "");
                                    phoneInfo=DataHelper.gson.fromJson(userinfo, PhoneInfo.class);
                                    len=phoneInfo.getLinks().size();
//                                    Toast.makeText(mcontext,"没有初始化数据",Toast.LENGTH_LONG).show();
                                }
                                for(int i=0;i<len;i++){
                                    PhoneInfo.LinksBean linksBean = phoneInfo.getLinks().get(i);
                                    String num = linksBean.getNum();
                                    if(incomingNumber.equals(num)){
                                        Message message=Message.obtain();
                                        message.what=2;
                                        message.obj=linksBean;
                                        handler.sendMessage(message);
                                        break;
                                    }
                                    if(i==len-1){
                                        Message message=Message.obtain();
                                        message.what=2;
                                        linksBean.setCompanyinfo("未知");
                                        linksBean.setNum(incomingNumber);
                                        linksBean.setPositioninfo("未知");
                                        linksBean.setName("未知");
                                        linksBean.setMessageinfo("未知");
                                        message.obj=linksBean;
                                        handler.sendMessage(message);
                                    }
                                }
                            }
                        }.start();
                        Log.e("TAG","响铃:来电号码"+incomingNumber);
                        break;
                }
            }
        };
    };
}
