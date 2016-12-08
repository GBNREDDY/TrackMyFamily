package in.skonda.trackmyfamily;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Toast;

import java.util.Map;

import static java.lang.Boolean.TRUE;

public class MessageReciever extends BroadcastReceiver {
    String  mobile, deviceid, user, message;
    String number;
    SharedPreferences sharedPreferences;

    public MessageReciever(){

    }

    @Override
    public void onReceive(Context context, Intent intent) {
        sharedPreferences = context.getSharedPreferences("in.skonda.trackmyfamily.registration", Context.MODE_PRIVATE);
        deviceid=sharedPreferences.getString("device_id","defaut");
        user=sharedPreferences.getString("name","default");
        mobile=sharedPreferences.getString("mobile_number","default");
         //Toast.makeText(context, "receiver on", Toast.LENGTH_SHORT).show();
        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            Object[] pdus = (Object[]) bundle.get("pdus");
            final SmsMessage[] messages = new SmsMessage[pdus.length];
              //Toast.makeText(context, "reading sms", Toast.LENGTH_SHORT).show();
            for (int i = 0; i < pdus.length; i++) {
                messages[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
               //  Toast.makeText(context, "in for loop", Toast.LENGTH_SHORT).show();
                number = messages[i].getOriginatingAddress();
            }
            if(sharedPreferences.getString("mobile_number","123456789").equalsIgnoreCase(number)){
                if (messages.length > -1) {
                    message = sharedPreferences.getString("text_message", message);
                     //Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                    if (messages[0].getDisplayMessageBody().equals(message)) {
                        Log.d("text", "Message recieved: " + messages[0].getMessageBody());
                        DataUpload dataUpload=new DataUpload(deviceid, user, mobile);
                        dataUpload.execute();
                        SharedPreferences.Editor editor=sharedPreferences.edit();
                        editor.putBoolean("registered",TRUE);
                        editor.commit();
                        Intent mapIntent = new Intent(context,MapsActivity.class);
                        mapIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(mapIntent);
                    }else {
                        Toast.makeText(context, "verification failed", Toast.LENGTH_SHORT).show();
                    }
                }
            }

        }
    }
}