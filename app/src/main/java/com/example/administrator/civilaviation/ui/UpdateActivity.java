package com.example.administrator.civilaviation.ui;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.example.administrator.civilaviation.R;
import com.example.administrator.civilaviation.util.UpdateInfo;
import com.example.administrator.civilaviation.util.UpdateInfoService;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HttpContext;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * 版本更新activity
 */
public class UpdateActivity extends Activity{
    // 更新版本信息要用到的基本信息
    private UpdateInfo info;
    private ProgressDialog pBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update);

        Toast.makeText(UpdateActivity.this, "正在检查版本更新", Toast.LENGTH_LONG).show();

        // 自动检查有没有新版本，如果有新版本就提示更新
        new Thread(){
            @Override
            public void run() {
                super.run();
                try {
                    UpdateInfoService updateInfoService = new UpdateInfoService(UpdateActivity.this);
                    info = updateInfoService.getUpDateInfo();
                    handler1.sendEmptyMessage(0);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }
    @SuppressLint("HandlerLeak")
    private Handler handler1 = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            // 如果有更新就提示
            if (isNeedUpdate()) {
                showUpdateDialog();
            }
        }
    };

    // 判断是否是最新版本，如果不是，跳出对话框选择是否更新
    private void showUpdateDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setIcon(android.R.drawable.ic_dialog_info);
        builder.setTitle("请升级APP至版本" + info.getVersion());
        builder.setMessage(info.getDescription());
        builder.setCancelable(false);

        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                    downFile(info.getUrl());
                } else {
                    Toast.makeText(UpdateActivity.this, "SD卡不可用", Toast.LENGTH_LONG).show();
                }
            }
        });

        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.create().show();
    }

    private boolean isNeedUpdate() {
        // 获取最新版本号
        String v = info.getVersion();
        Log.d("version", v);
        Toast.makeText(UpdateActivity.this, v, Toast.LENGTH_LONG).show();

        if (v.equals(getVersion())) {
            return false;
        } else {
            return true;
        }
    }

    // 获取当前版本号
    private String getVersion() {
        try {
            PackageManager packageManager = getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(getPackageName(), 0);
            return packageInfo.versionName;
        } catch (Exception e) {
            e.printStackTrace();
            return "版本号未知";
        }
    }


    void downFile(final String url) {
        // 进度条，在下载的时候实时更新进度，提高用户友好度
        pBar = new ProgressDialog(UpdateActivity.this);
        pBar.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        pBar.setTitle("正在下载");
        pBar.setMessage("请稍后......");
        pBar.setProgress(0);
        pBar.show();
        new Thread() {
            @Override
            public void run() {
                super.run();
                HttpClient client = new CloseableHttpClient() {
                    @Override
                    protected CloseableHttpResponse doExecute(HttpHost target, HttpRequest request, HttpContext context) throws IOException, ClientProtocolException {
                        return null;
                    }

                    @Override
                    public void close() throws IOException {

                    }

                    @Override
                    public HttpParams getParams() {
                        return null;
                    }

                    @Override
                    public ClientConnectionManager getConnectionManager() {
                        return null;
                    }
                };
                HttpGet get = new HttpGet(url);
                HttpResponse response;
                try {
                    response = client.execute(get);
                    HttpEntity entity = response.getEntity();
                    // 获取文件大小
                    int length = (int)entity.getContentLength();

                    // 设置进度条的总长度
                    pBar.setMax(length);

                    InputStream is = entity.getContent();
                    FileOutputStream fileOutputStream = null;
                    if (is != null) {
                        File file = new File(Environment.getExternalStorageDirectory(), "Test.apk");
                        fileOutputStream = new FileOutputStream(file);

                        //这个是缓冲区，即一次读取10个比特，我弄的小了点，
                        // 因为在本地，所以数值太大一 下就下载完了，看不出progressbar的效果。
                        byte[] buf = new byte[10];
                        int ch = -1;
                        int process = 0;
                        while ((ch = is.read(buf)) != -1) {
                            fileOutputStream.write(buf, 0, ch);
                            process += ch;
                            // 这里就是关键的实时更新进度
                            pBar.setProgress(process);
                        }
                    }
                    fileOutputStream.flush();
                    if (fileOutputStream != null) {
                        fileOutputStream.close();
                    }
                    down();
                } catch (Exception e){
                    e.printStackTrace();
                }

            }
        }.start();
    }

    void down() {
        handler1.post(new Runnable() {
            @Override
            public void run() {
                pBar.cancel();
                update();
            }
        });
    }

    void update() {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.fromFile(new File(Environment.getExternalStorageDirectory(), "Test.apk")),
                "application/vnd.android.package-archive");
        startActivity(intent);
    }

}
