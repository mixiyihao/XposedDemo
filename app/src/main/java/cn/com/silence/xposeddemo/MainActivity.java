package cn.com.silence.xposeddemo;

import android.app.Application;
import android.content.Intent;
import android.os.Build;
import android.os.Process;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.Toast;

import java.io.IOException;
import java.security.Security;

import de.robv.android.xposed.XposedHelpers;

public class MainActivity extends AppCompatActivity {
    EditText mUserName,mPassword;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }

/*
        public void check(String name,String password){
        Toast.makeText(getApplicationContext(),name+","+password,Toast.LENGTH_SHORT).show();
        String anroid_id = Settings.Secure.getString(getContentResolver(), "anroid_id");
        Toast.makeText(getApplicationContext(),""+anroid_id,Toast.LENGTH_SHORT);

        try {
            String [] command = new String[2];
            command[0]="/system/bin/cat";
            command[1]="";
            ProcessBuilder builder = new ProcessBuilder(command);
            builder.start();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }*/

}
