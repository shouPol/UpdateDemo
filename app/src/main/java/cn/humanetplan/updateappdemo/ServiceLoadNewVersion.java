package cn.humanetplan.updateappdemo;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.widget.RemoteViews;

import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.HttpHandler;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;

import java.io.File;

/**
 * 下载 新 版本 的 服务
 */
public class ServiceLoadNewVersion extends Service {


    private RemoteViews remoteViews = null;
    private Notification notification = null;
    private NotificationManager notificationManager = null;
    private PendingIntent pReDownLoadIntent = null;

    private Handler myHandler;

    // notification id
    private final int NOTIFICATION_ID = 1000;
    private final int START = 1001;
    private final int LOADING = 1002;
    private final int FINISHED = 1003;
    private final int LOAD_ERROR = 1004;
    private String url;
    private String filePath;

    String apkName = "updateTest.apk";
    String loagPath = "";

    public ServiceLoadNewVersion() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        try {
            String path = intent.getStringExtra("path");
            url = path;
            myHandler = new MyHandler();
            initUrls();

            initNotification();

            // 开始 下载
            new DownLoadThread(url, filePath).start();
        } catch (Exception e) {

        }
        return super.onStartCommand(intent, flags, startId);
    }

    /**
     * 开始 下载
     */
    private void initUrls() {
        File file = new File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                , apkName);
        if (file.exists()) {
            file.delete();
        }
        filePath = file.getAbsolutePath();
    }

    NotificationCompat.Builder builder = null;

    /**
     * 配置 通知栏显示 样式
     */
    private void initNotification() {
        String id = "chanel_update";
        String name = "水务集团";
        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            //如果是8以上的系统。需要传一个channelId.
            builder = new NotificationCompat.Builder(this, id);
        } else {
            builder = new NotificationCompat.Builder(this);
        }

        builder.setContentTitle(getResources().getString(R.string.app_name) + "新版本下载").
                setContentText("下载进行中...")
                .setVibrate(new long[]{0})
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.applogo))
                .setSmallIcon(R.mipmap.applogo)
                .setProgress(100, 0, false);


        // 下载 失败 重新 下载 的 PendingIntent
        Intent reDownLoadIntent = new Intent(this, this.getClass());
        reDownLoadIntent.putExtra("path", loagPath);
        pReDownLoadIntent = PendingIntent.getService(this, 200, reDownLoadIntent, PendingIntent.FLAG_CANCEL_CURRENT);


        remoteViews = new RemoteViews(getPackageName(), R.layout.view_download_notification);
        remoteViews.setTextViewText(R.id.textViewTitle, "正在下载");
        remoteViews.setTextViewText(R.id.textViewProgress, "进度0%");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {//android8.0以上通知的适配
            NotificationChannel mChannel = new NotificationChannel(id, name, NotificationManager.IMPORTANCE_LOW);
            mChannel.enableVibration(false);
            mChannel.setVibrationPattern(new long[]{0});

            notificationManager.createNotificationChannel(mChannel);
            notification = builder.build();
        } else {

            notification = builder.build();
        }
    }


    class MyHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case START:
                    notificationManager.notify(NOTIFICATION_ID, notification);

                    break;
                case LOADING:
                    builder.setProgress(100, msg.arg1, false);
                    builder.setContentText("下载进行中" + msg.arg1 + "/100");
                    notification = builder.build();
                    notificationManager.notify(NOTIFICATION_ID, notification);
                    //关键部分，如果你不重新更新通知，进度条是不会更新的
                    break;
                case FINISHED:

                    notification.flags |= Notification.FLAG_AUTO_CANCEL;
//                    //关键部分，如果你不重新更新通知，进度条是不会更新的
                    notificationManager.notify(NOTIFICATION_ID, notification);
                    notificationManager.cancel(NOTIFICATION_ID);
                    installApkNew(null);

                    break;
                case LOAD_ERROR:
                    builder.setContentTitle("新版本下载失败");
                    builder.setContentText("下载失败,点击重新下载！");
                    notification.contentIntent = pReDownLoadIntent;
//                    notification.flags = Notification.FLAG_NO_CLEAR; // 点击通知 不消失
                    //关键部分，如果你不重新更新通知，进度条是不会更新的
                    notificationManager.notify(NOTIFICATION_ID, notification);
                    break;
            }
        }
    }

    //安装apk

    protected void installApkNew(Uri uri) {

        try {
            File file = new File(filePath);
            Intent intent = new Intent(Intent.ACTION_VIEW);
            // 由于没有在Activity环境下启动Activity,设置下面的标签
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            if (Build.VERSION.SDK_INT >= 24) { //判读版本是否在7.0以上
                //参数1 上下文, 参数2 Provider主机地址 和配置文件中保持一致   参数3  共享的文件
                Uri apkUri =
                        FileProvider.
                                getUriForFile(getApplicationContext(),
                                        BuildConfig.APPLICATION_ID + ".provider",
                                        file);

                //添加这一句表示对目标应用临时授权该Uri所代表的文件
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                intent.setDataAndType(apkUri, "application/vnd.android.package-archive");
            } else {
                Uri mUri = Uri.fromFile(file);
                mUri = Uri.parse(mUri.toString().replace("content", "file"));
                intent.setDataAndType(mUri,
                        "application/vnd.android.package-archive");
            }


            startActivity(intent);
        } catch (Exception e) {

            e.printStackTrace();
            Log.i("ServiceLoadNewVersion", "exc  " + e.toString());
        }
    }


    class DownLoadThread extends Thread {
        private String url;
        private String filePath;

        public DownLoadThread(String url, String filePath) {
            this.url = url;
            this.filePath = filePath;
        }

        @Override
        public void run() {

            HttpUtils http = new HttpUtils();
            RequestParams params = new RequestParams();
            HttpHandler handler = http.download(
                    url,//url
                    filePath, // 文件保存路径，
                    params,
                    true, // 如果目标文件存在，接着未完成的部分继续下载。服务器不支持RANGE时将从新下载。
                    false, // 如果从请求返回信息中获取到文件名，下载完成后自动重命名。
                    new RequestCallBack<File>() {
                        @Override
                        public void onStart() {
                            myHandler.sendEmptyMessage(START);
                        }

                        @Override
                        public void onLoading(long total, long current, boolean isUploading) {
                            Message message = myHandler.obtainMessage();
                            message.what = LOADING;
                            message.arg1 = (int) ((float) current / (float) total * 100);
                            myHandler.sendMessage(message);


                        }

                        @Override
                        public void onSuccess(ResponseInfo<File> responseInfo) {
                            myHandler.sendEmptyMessage(FINISHED);
                        }


                        @Override
                        public void onFailure(HttpException error, String msg) {
                            myHandler.sendEmptyMessage(LOAD_ERROR);
                        }
                    });
        }
    }
}
