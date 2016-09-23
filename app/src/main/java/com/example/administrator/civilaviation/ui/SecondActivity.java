package com.example.administrator.civilaviation.ui;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.administrator.civilaviation.R;
import com.example.administrator.civilaviation.tool.AuthHeaderNKG;

import org.apache.http.conn.util.InetAddressUtils;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.kxml2.kdom.Element;
import org.kxml2.kdom.Node;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

/**
 * 第二个界面
 */
public class SecondActivity extends Activity {
    // 命名空间
    private final static String NAME_SPACE = "http://58.213.128.132/";

    // 调用方法名称
    private final static String METHOD_NAME = "ChangePassword";

    // endpoint
    private final static String END_POINT = "http://58.213.128.132:888/TestWBS/NIATAMessage.asmx";

    // SOAP Action
    private final static String SOAP_ACTION = "http://58.213.128.132/ChangePassword";

    private static final String  TAG = "SecondActivity";

    // 新密码
    private String passWorldOne;
    private String passWorldTwo;

    // 从登录界面传递过来的用户名和密码
    private String userName;
    private String userPass;

    private EditText pass1;
    private EditText pass2;
    private  String ErrString = "";
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);
        initView();
    }

    private void initView() {
        Button changePass = (Button) findViewById(R.id.change_btn);
        pass1 = (EditText)findViewById(R.id.change_pass1);
        pass2 = (EditText)findViewById(R.id.change_pass2);

        changePass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userName = getIntent().getStringExtra("userName");
                userPass = getIntent().getStringExtra("userPass");
                passWorldOne = pass1.getText().toString().trim();
                passWorldTwo = pass2.getText().toString().trim();
//                Toast.makeText(SecondActivity.this, passWorld1 + passWorld2, Toast.LENGTH_LONG).show();
                new changePass().execute();
            }
        });
    }

    class changePass extends AsyncTask<Object, Object, String> {

        @Override
        protected void onPostExecute(String request) {
            if(request == null && !ErrString.equals("")){
                Toast.makeText(SecondActivity.this, ErrString, Toast.LENGTH_LONG).show();
            }else if(request.equals("true") && !ErrString.equals("")){
                Toast.makeText(SecondActivity.this, "修改成功:"+ErrString, Toast.LENGTH_LONG).show();
            }else if(request.equals("false") && !ErrString.equals("")){
                Toast.makeText(SecondActivity.this, "修改失败，原因是"+ErrString, Toast.LENGTH_LONG).show();
            }
            super.onPostExecute(request);
        }

        @Override
        protected String doInBackground(Object... params) {
            Log.d(TAG, "doInBackground enter");
            SoapObject object = changePassWord();

            if (object == null) {
                ErrString = "服务器响应失败";
                return null;
            }else {
                String result = object.getProperty(0).toString();
                ErrString = object.getProperty(1).toString();
                return result;
            }
        }

    }

    public SoapObject changePassWord() {
        // 定义SoapHeader,加入4个节点
        Element[] header = new Element[1];
        header[0] = new Element().createElement(NAME_SPACE, "AuthHeaderNKG");

        // userName
        Element userNameE = new Element().createElement(NAME_SPACE, "UserName");
        userNameE.addChild(Node.TEXT, userName);
        header[0].addChild(Node.ELEMENT, userNameE);

        // userPass
        Element userPassE = new Element().createElement(NAME_SPACE, "Password");
        userPassE.addChild(Node.TEXT, userPass);
        header[0].addChild(Node.ELEMENT, userPassE);

        // ip
        String ip = getLocalHostIp();
        Element sendAddressE = new Element().createElement(NAME_SPACE, "SendAddress");
        sendAddressE.addChild(Node.TEXT, ip);
        header[0].addChild(Node.ELEMENT, sendAddressE);

        Element recvAddressE = new Element().createElement(NAME_SPACE, "RecvAddress");
        recvAddressE.addChild(Node.TEXT, NAME_SPACE);
        header[0].addChild(Node.ELEMENT, recvAddressE);

        // 指定WebService的命名空间和调用方法名
        SoapObject rpc = new SoapObject(NAME_SPACE, METHOD_NAME);

        // 设置调用webservice接口需要传入的参数
        rpc.addProperty("newPassword1",passWorldOne);
        rpc.addProperty("newPassword2",passWorldTwo);
        rpc.addProperty("ErrString", ErrString);

        // 生成调用webservice方法的soap请求信息，并指定soap版本
        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);

        envelope.headerOut = header;
        envelope.bodyOut = rpc;

        // 设置是否调用的是donet开发的webservice
        envelope.dotNet = true;
        envelope.setOutputSoapObject(rpc);

        // 发送网络请求
        HttpTransportSE transportSE = new HttpTransportSE(END_POINT);
        try {
            transportSE.call(SOAP_ACTION, envelope);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // 获取返回数据
        SoapObject object = (SoapObject) envelope.bodyIn;
        // 获取返回结果
        if (object != null) {
            String result = object.toString();
            Log.d("tag", result);
        }

        return object;
    }

    public String getLocalHostIp() {
        String ipAddress = "";
        try {
            Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces();

            // 遍历所用的网络接口
            while (en.hasMoreElements()) {
                // 得到每一个网络接口绑定的所有ip
                NetworkInterface nif = en.nextElement();
                Enumeration<InetAddress> inet = nif.getInetAddresses();

                // 遍历每一个接口绑定的所有ip
                while (inet.hasMoreElements()) {
                    InetAddress ip = inet.nextElement();
                    if (!ip.isLoopbackAddress() && InetAddressUtils.isIPv4Address(ip.getHostAddress())) {
                        return ipAddress = "本机的ip地址是" + ":" + ip.getHostAddress();
                    }
                }
            }

        } catch (SocketException e) {
            e.printStackTrace();
            Log.e("feige", "获取IP地址失败");
            e.printStackTrace();
        }
        return ipAddress;
    }
}
