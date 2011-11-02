package com.smsViaHTTP;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class smsViaHTTPActivity extends Activity {
	
	public static TextView serverStatus;
	
	public boolean isRunning = false;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        serverStatus = (TextView) findViewById(R.id.statusLbl);
        
        Button startBtn = (Button)findViewById(R.id.startBtn);
    	Button stopBtn = (Button)findViewById(R.id.stopBtn);
    	
    	startBtn.setOnClickListener(startBtnListener);
        stopBtn.setOnClickListener(stopBtnListener);
    }
   

    private OnClickListener startBtnListener = new OnClickListener() {
    	public void onClick(View v){
    		if (!isRunning)
    			startService(new Intent(smsViaHTTPActivity.this,serverService.class));
    		isRunning = true;
    	}          
    };

    private OnClickListener stopBtnListener = new OnClickListener() {
    	public void onClick(View v){
    		stopService(new Intent(smsViaHTTPActivity.this,serverService.class));
    		isRunning = false;
    	}          
    };
    
}