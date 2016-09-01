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
 * 测试天气的城市
 */
public class CityActivity extends Activity{
    // WSDL文档中的命名空间
    private static final String targetNameSpace = "http://WebXml.com.cn/";
    // WSDL文档中的URL
    private static final String WSDL = "http://www.webxml.com.cn/WebServices/WeatherWebService.asmx";

    // 需要调用的方法名(获得本天气预报Web Services支持的城市信息,根据省份查询城市集合：带参数)
    private static final String getSupportCity = "getSupportCity";
    private List<Map<String,String>> listItems;
    private ListView mListView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_city_main);
        listItems = new ArrayList<Map<String,String>>();
        mListView = (ListView) findViewById(R.id.city_list);
        new NetAsyncTask().execute();
        //列表单击事件监听
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                String mCityName = listItems.get(position).get("city");
                String cityName = getCityName(mCityName);
                Log.d("CityName", cityName);
                Intent intent = new Intent();
                //存储选择的城市名
                intent.putExtra("Cname", cityName);
                intent.setClass(CityActivity.this, WeatherActivity.class);
                startActivity(intent);
            }

        });
    }
    /**
     * 拆分“城市 （代码）”字符串，将“城市”字符串分离
     * @param name
     * @return
     */
    public String getCityName(String name) {
        String city = "";
        int position = name.indexOf(' ');
        city = name.substring(0, position);
        return city;
    }

    class NetAsyncTask extends AsyncTask<Object, Object, String> {

        @Override
        protected void onPostExecute(String result) {
            if (result.equals("success")) {
                //列表适配器
                SimpleAdapter simpleAdapter = new SimpleAdapter(CityActivity.this, listItems, R.layout.city_item,
                        new String[] {"city"}, new int[]{R.id.city_tv});
                mListView.setAdapter(simpleAdapter);
            }
            super.onPostExecute(result);
        }

        @Override
        protected String doInBackground(Object... params) {
            // 根据命名空间和方法得到SoapObject对象
            SoapObject soapObject = new SoapObject(targetNameSpace,getSupportCity);
            //参数输入
            String name = getIntent().getExtras().getString("Pname");
            soapObject.addProperty("byProvinceName", name);
            // 通过SOAP1.1协议得到envelop对象
            SoapSerializationEnvelope envelop = new SoapSerializationEnvelope(
                    SoapEnvelope.VER11);
            // 将soapObject对象设置为envelop对象，传出消息
            envelop.dotNet = true;
            envelop.setOutputSoapObject(soapObject);
            HttpTransportSE httpSE = new HttpTransportSE(WSDL);
            // 开始调用远程方法
            try {
                httpSE.call(targetNameSpace + getSupportCity, envelop);
                // 得到远程方法返回的SOAP对象
                SoapObject resultObj = (SoapObject) envelop.getResponse();
                // 得到服务器传回的数据
                int count = resultObj.getPropertyCount();
                for (int i = 0; i < count; i++) {
                    Map<String,String> listItem = new HashMap<String, String>();
                    listItem.put("city", resultObj.getProperty(i).toString());
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

