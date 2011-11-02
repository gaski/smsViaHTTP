package com.smsViaHTTP;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.gsm.SmsMessage;

public class smsReceiver extends BroadcastReceiver
{
    @Override
    public void onReceive(Context context, Intent intent) 
    {
    	serverService.is_changed = true;
        Bundle bundle = intent.getExtras();        
        SmsMessage[] msgs = null;       
        if (bundle != null)
        {
            Object[] pdus = (Object[]) bundle.get("pdus");
            msgs = new SmsMessage[pdus.length];            
            for (int i=0; i<msgs.length; i++){
                msgs[i] = SmsMessage.createFromPdu((byte[])pdus[i]);
                
                serverService.new_content += "<message from='" + msgs[i].getOriginatingAddress() + "' time='" + msgs[i].getTimestampMillis() + "' >" + msgs[i].getMessageBody().toString() + "</message>\n";   
            }
        }                         
    }
}