package com.junglezhang.dragimageviewlib.utils;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.StringDef;
import android.support.v7.app.AlertDialog;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by Jungle on 2019/1/8 0008.
 *
 * @desc 验证是否有权限的帮助类
 */
public class PermissionHelper {

    private ForcePermissionCallbacks mForceCallBack;
    private NormalPermissionCallbacks mNormalCallBack;

    private Activity mActivity;
    private String[] permission;

    public static final int REQUEST_CODE = 10000;
    public static final int SETTINGS_CODE = 10001;

    //需要申请的权限，同组权限原则上只需要申请一个，多个同组权限申请对应一个组权限，一个权限通过全组权限通过，
    //如果有特殊需要，可以修改提示dialog和提示msg对应不同权限
    //日历组
    public static final String READ_CALENDAR = "android.permission.READ_CALENDAR";//(读取日历)
    public static final String WRITE_CALENDAR = "android.permission.WRITE_CALENDAR";//(修改日历)
    //相机组
    public static final String CAMERA = "android.permission.CAMERA";//(获取拍照权限)
    //联系人组
    public static final String READ_CONTACTS = "android.permission.READ_CONTACTS";//(读取联系人)
    public static final String WRITE_CONTACTS = "android.permission.WRITE_CONTACTS";//(写入联系人)
    public static final String GET_ACCOUNTS = "android.permission.GET_ACCOUNTS";//(查找设备上的帐户)
    //位置组
    public static final String ACCESS_COARSE_LOCATION = "android.permission.ACCESS_COARSE_LOCATION";//(通过wifi和移动基站获取定位权限)
    public static final String ACCESS_FINE_LOCATION = "android.permission.ACCESS_FINE_LOCATION";//(通过gps获取定位权限)
    //麦克风组
    public static final String RECORD_AUDIO = "android.permission.RECORD_AUDIO";//(录音权限)
    //电话组
    public static final String READ_PHONE_STATE = "android.permission.READ_PHONE_STATE";//(读取电话状态)
    public static final String CALL_PHONE = "android.permission.CALL_PHONE";//(拨打电话)
    public static final String READ_CALL_LOG = "android.permission.READ_CALL_LOG";//(读取通话记录)
    public static final String WRITE_CALL_LOG = "android.permission.WRITE_CALL_LOG";//(修改通话记录)
    public static final String ADD_VOICEMAIL = "com.android.voicemail.permission.ADD_VOICEMAIL";//(允许应用程序添加系统中的语音邮件)
    public static final String USE_SIP = "android.permission.USE_SIP";//(SIP视频服务)
    public static final String PROCESS_OUTGOING_CALLS = "android.permission.PROCESS_OUTGOING_CALLS";//(程序监视，修改或放弃拨出电话)
    //传感器组
    public static final String BODY_SENSORS = "android.permission.BODY_SENSORS";//(获取传感器权限)
    //短信组
    public static final String SEND_SMS = "android.permission.SEND_SMS";//(发送短信)
    public static final String RECEIVE_SMS = "android.permission.RECEIVE_SMS";//(接收短信权限)
    public static final String READ_SMS = "android.permission.READ_SMS";//(读取短信)
    public static final String RECEIVE_WAP_PUSH = "android.permission.RECEIVE_WAP_PUSH";//(接收WAP PUSH信息)
    public static final String RECEIVE_MMS = "android.permission.RECEIVE_MMS";//(允许一个程序监控将收到MMS彩信)
    //读写内存组
    public static final String READ_EXTERNAL_STORAGE = "android.permission.READ_EXTERNAL_STORAGE";//(读取内存卡)
    public static final String WRITE_EXTERNAL_STORAGE = "android.permission.WRITE_EXTERNAL_STORAGE";//(写内存卡)

    @Retention(RetentionPolicy.SOURCE)
    @StringDef({READ_CALENDAR, WRITE_CALENDAR, CAMERA, READ_CONTACTS, WRITE_CONTACTS, GET_ACCOUNTS,
            ACCESS_COARSE_LOCATION, ACCESS_FINE_LOCATION, RECORD_AUDIO, READ_PHONE_STATE, CALL_PHONE,
            READ_CALL_LOG, WRITE_CALL_LOG, ADD_VOICEMAIL, USE_SIP, PROCESS_OUTGOING_CALLS, BODY_SENSORS,
            SEND_SMS, RECEIVE_SMS, READ_SMS, RECEIVE_WAP_PUSH, RECEIVE_MMS, READ_EXTERNAL_STORAGE,
            WRITE_EXTERNAL_STORAGE})
    public @interface PermissionGroup {

    }

    /**
     * 验证权限的帮助类，
     * 使用帮助
     * 1.先调用{@link #checkPermissionForce(ForcePermissionCallbacks)}，或者{@link #checkPermissionNormal(NormalPermissionCallbacks)}激活方法
     * 2.重写 onRequestPermissionsResult 方法调用 {@link #bindRequestPermissionsResult(int, String[], int[])}，监听权限申请的返回数据并处理
     * 3.(强制申请权限需要)重写 onActivityResult 方法调用 {@link #bindActivityResult(int, int, Intent)}，在用户拒绝权限申请时，申请去设置界面打开权限，监听返回的结果
     *
     * @param mActivity
     * @param permission 这里申请的权限应该是权限组
     */
    public PermissionHelper(Activity mActivity, @PermissionGroup String... permission) {
        this.mActivity = mActivity;
        this.permission = permission;
    }

    /**
     * 强制申请权限，用户拒绝就提示用户去打开
     *
     * @param mForceCallBack
     */
    public void checkPermissionForce(ForcePermissionCallbacks mForceCallBack) {
        this.mForceCallBack = mForceCallBack;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int state = checkP(permission);
            if (state == PackageManager.PERMISSION_GRANTED) {//都已经有权限了
                mForceCallBack.onPermissionsAllGranted();
            } else {
                mActivity.requestPermissions(permission, REQUEST_CODE);
            }
        } else {
            //无需申请直接返回成功
            mForceCallBack.onPermissionsAllGranted();
        }
    }

    /**
     * 一般申请权限，用户拒绝就拒绝了
     *
     * @param mNormalCallBack
     */
    public void checkPermissionNormal(NormalPermissionCallbacks mNormalCallBack) {
        this.mNormalCallBack = mNormalCallBack;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int state = checkP(permission);
            if (state == PackageManager.PERMISSION_GRANTED) {//都已经有权限了
                mNormalCallBack.onPermissionsResult();
            } else {
                mActivity.requestPermissions(permission, REQUEST_CODE);
            }
        } else {
            mNormalCallBack.onPermissionsResult();
        }
    }

    /**
     * 检查要申请的权限
     *
     * @param permissions
     * @return
     */
    @TargetApi(23)
    private int checkP(String... permissions) {
        for (String p : permissions) {
            //只要有一个权限不存在就返回缺少权限
            if (mActivity.checkSelfPermission(p) == PackageManager.PERMISSION_DENIED) {
                return PackageManager.PERMISSION_DENIED;
            }
        }
        return PackageManager.PERMISSION_GRANTED;
    }

    /**
     * 绑定 onRequestPermissionsResult() 使用
     *
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    public void bindRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CODE) {
            if (mNormalCallBack != null) {
                mNormalCallBack.onPermissionsResult();

            } else if (mForceCallBack != null) {
                StringBuilder sbMsg = new StringBuilder();
                for (int i = 0; i < grantResults.length; i++) {
                    //把所有拒绝了的权限整理出来，写入sbMsg
                    if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                        sbMsg.append(tableOfComparisons4Permission(permissions[i]));
                        sbMsg.append("，");
                    }
                }
                if (sbMsg.toString().isEmpty()) {
                    //用户同意了申请的全部权限
                    mForceCallBack.onPermissionsAllGranted();
                } else {
                    //去掉尾部逗号
                    String msg = sbMsg.substring(0, sbMsg.length() - 1);
                    //用户拒绝了申请的权限之一
                    AlertDialog dialog = new AlertDialog.Builder(mActivity)
                            .setTitle("当前应用缺少必要权限")
                            .setMessage("请点击[确定] - [权限] - 打开所需[" + msg + "]权限。最后点击两次后退按钮，即可返回。")
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                    Uri uri = Uri.fromParts("package", mActivity.getPackageName(), null);
                                    intent.setData(uri);
                                    mActivity.startActivityForResult(intent, SETTINGS_CODE);
                                }
                            })
                            .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    mForceCallBack.onPermissionsDenied();
                                }
                            })
                            .create();
                    dialog.show();
                    dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.BLACK);
                }
            }
        }
    }

    public void bindActivityResult(int requestCode, int resultCode, Intent data) {
        if (SETTINGS_CODE == requestCode) {
            //返回后再次检查申请的权限是否被用户打开了
            checkPermissionForce(mForceCallBack);
        }
    }


    public interface ForcePermissionCallbacks {

        void onPermissionsAllGranted();

        void onPermissionsDenied();
    }

    public interface NormalPermissionCallbacks {

        void onPermissionsResult();
    }

    private String tableOfComparisons4Permission(String permission) {
        String result;
        switch (permission) {
            case READ_CALENDAR:
            case WRITE_CALENDAR:
                result = "日历";
                break;
            case CAMERA:
                result = "相机";
                break;
            case READ_CONTACTS:
            case WRITE_CONTACTS:
            case GET_ACCOUNTS:
                result = "联系人";
                break;
            case ACCESS_COARSE_LOCATION:
            case ACCESS_FINE_LOCATION:
                result = "位置";
                break;
            case RECORD_AUDIO:
                result = "麦克风";
                break;
            case READ_PHONE_STATE:
            case CALL_PHONE:
            case READ_CALL_LOG:
            case WRITE_CALL_LOG:
            case ADD_VOICEMAIL:
            case USE_SIP:
            case PROCESS_OUTGOING_CALLS:
                result = "电话";
                break;
            case BODY_SENSORS:
                result = "传感器";
                break;
            case SEND_SMS:
            case RECEIVE_SMS:
            case READ_SMS:
            case RECEIVE_WAP_PUSH:
            case RECEIVE_MMS:
                result = "短信";
                break;
            case READ_EXTERNAL_STORAGE:
            case WRITE_EXTERNAL_STORAGE:
                result = "读写内存";
                break;
            default:
                result = "相应";
                break;
        }
        return result;
    }
}


