package com.example.administrator.civilaviation.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.example.administrator.civilaviation.R;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 测试天气用的activity
 */
public class WeatherActivity extends Activity{
    // WSDL文档中的命名空间
    private static final String targetNameSpace = "http://WebXml.com.cn/";
    // WSDL文档中的URL
    private static final String WSDL = "http://ws.webxml.com.cn/WebServices/WeatherWS.asmx";

    // 需要调用的方法名(获得本天气预报Web Services支持的洲、国内外省份和城市信息)
    private static final String getSupportProvince = "getRegionProvince";
    private List<Map<String,String>> listItems;
    private ListView mListView;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather_main);
        listItems = new ArrayList<Map<String,String>>();
        mListView = (ListView) findViewById(R.id.province_list);
        new NetAsyncTask().execute();
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                String mProvinceName = listItems.get(position).get("province");
                Log.d("ProvinceName", mProvinceName);
                Intent intent = new Intent();
                intent.putExtra("Pname", mProvinceName);
                intent.setClass(WeatherActivity.this, CityActivity.class);
                startActivity(intent);
            }

        });
    }

    class NetAsyncTask extends AsyncTask<Object, Object, String> {

        @Override
        protected void onPostExecute(String result) {
            if (result.equals("success")) {
                //列表适配器
                SimpleAdapter simpleAdapter = new SimpleAdapter(WeatherActivity.this, listItems, R.layout.weather_item,
                        new String[] {"province"}, new int[]{R.id.weather_tv});
                mListView.setAdapter(simpleAdapter);
            }
            super.onPostExecute(result);
        }

        @Override
        protected String doInBackground(Object... params) {
            // 根据命名空间和方法得到SoapObject对象
            SoapObject soapObject = new SoapObject(targetNameSpace,
                    getSupportProvince);
            // 通过SOAP1.1协议得到envelop对象
            SoapSerializationEnvelope envelop = new SoapSerializationEnvelope(
                    SoapEnvelope.VER11);
            // 将soapObject对象设置为envelop对象，传出消息

            envelop.dotNet = true;
            envelop.setOutputSoapObject(soapObject);
            // 或者envelop.bodyOut = soapObject;
            HttpTransportSE httpSE = new HttpTransportSE(WSDL);
            // 开始调用远程方法
            try {
                httpSE.call(targetNameSpace + getSupportProvince, envelop);
                // 得到远程方法返回的SOAP对象
                SoapObject resultObj = (SoapObject) envelop.getResponse();
                // 得到服务器传回的数据
                int count = resultObj.getPropertyCount();
                for (int i = 0; i < count; i++) {
                    Map<String,String> listItem = new HashMap<String, String>();
                    listItem.put("province", resultObj.getProperty(i).toString());
                    listItems.add(listItem);
                }
            } catch (IOException e) {
                e.printStackTrace();
                return "IOException";
            } catch (XmlPullParserException e) {
                e.printStackTrace();
                return "XmlPullParserException";
            }
            return "success";
        }
    }
}
