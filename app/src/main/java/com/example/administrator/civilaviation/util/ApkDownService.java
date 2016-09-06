package com.example.administrator.civilaviation.util;

import android.app.DownloadManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Environment;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.widget.Toast;

import java.io.File;

/**
 * 安装下载的service
 */
public class ApkDownService extends Service {

    private DownloadManager dm;
    private long downloadId;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String apkUrl = intent.getStringExtra("apkUrl");
        registerReceiver(receiver, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
        startDownload(apkUrl);
        return super.onStartCommand(intent, flags, startId);
    }


    public void startDownload(String apkUrl) {
        dm = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
        if (!StringUtils.isNetUrl(apkUrl)) {
            Toast.makeText(ApkDownService.this, "不是一个正确的网址", Toast.LENGTH_SHORT).show();
            return;
        }
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(apkUrl));
        request.setMimeType("application/vnd.android.package-archive");
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE | DownloadManager.Request.NETWORK_WIFI);
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
//        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, getFileName(apkUrl));
        request.setDestinationInExternalPublicDir("feijichang", getFileName(apkUrl));
        downloadId = dm.enqueue(request);
    }

    private BroadcastReceiver receiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            long id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
            if (downloadId == id) {
                File file = new File(dm.getUriForDownloadedFile(downloadId).toString());
                installApk(file, context);
            }
            stopSelf();
        }
    };

    private String getFileName(String path) {
        return path.substring(path.lastIndexOf("/") + 1);
    }

    private void installApk(File file, Context context) {
        Intent intent = new Intent();
        intent.setAction("android.intent.action.VIEW");
//        intent.setAction(Intent.ACTION_VIEW);
        intent.setData(Uri.fromFile(file));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
        context.startActivity(intent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
    }

}
