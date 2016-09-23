package com.example.administrator.civilaviation.tool;

import android.util.Log;

import org.ksoap2.serialization.KvmSerializable;
import org.ksoap2.serialization.PropertyInfo;

import java.util.Hashtable;

public class AuthHeaderNKG implements KvmSerializable {
    public String Username;
    public String Password;
    public AuthHeaderNKG(){

    }
    public String getUsername() {
        return Password;
    }

    public void setUsername(String Username) {
        this.Username = Username;
    }

    public String getPassword() {
        return Password;
    }


    public void setPassword(String Password) {
        this.Password = Password;
    }

    @Override
    public Object getProperty(int arg0) {
        switch(arg0){
            case 0:
                return Username;
            case 1:
                return Password;
        }
        return null;
    }

    @Override
    public int getPropertyCount() {
        return 2;
    }

    @Override
    public void getPropertyInfo(int index, Hashtable arg1, PropertyInfo info) {
        switch(index){
            case 0:
                info.type=PropertyInfo.STRING_CLASS;//设置info type的类型
                info.name="Username";
                break;
            case 1:
                info.type=PropertyInfo.STRING_CLASS;
                info.name="Password";
                break;
            default:
                break;
        }
    }

    @Override
    public void setProperty(int index, Object value) {
        switch(index){
            case 0:
                Username=value.toString();

                Log.d("NKG", "Username = " + Username);
                break;
            case 1:
                Password = value.toString();
                Log.d("NKG", "Password = " + Password);
                break;
            default:
                break;
        }
    }

}
