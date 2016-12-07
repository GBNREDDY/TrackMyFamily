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

public class MessageReciever extends BroadcastReceiver {
    String number, mobile, deviceid,user, message;
    SharedPreferences sharedPreferences;
    public MessageReciever() {
        this.deviceid = sharedPreferences.getString("device_id", null);
        this.user = sharedPreferences.getString("name", null);
        this.mobile = sharedPreferences.getString("mobile_number", null);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
        //throw new UnsupportedOperationException("Not yet implemented");
        sharedPreferences = context.getSharedPreferences("in.skonda.trackfamily.registration", Context.MODE_PRIVATE);
        // Toast.makeText(context, "receiver on", Toast.LENGTH_SHORT).show();
        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            Object[] pdus = (Object[]) bundle.get("pdus");
            final SmsMessage[] messages = new SmsMessage[pdus.length];
            //  Toast.makeText(context, "reading sms", Toast.LENGTH_SHORT).show();
            for (int i = 0; i < pdus.length; i++) {
                messages[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
                // Toast.makeText(context, "in for loop", Toast.LENGTH_SHORT).show();
                number = messages[i].getOriginatingAddress();
            }
            if(mobile.equalsIgnoreCase(number)){
                if (messages.length > -1) {
                    message = sharedPreferences.getString("text message", message);
                    // Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                    if (messages[0].getDisplayMessageBody().equals(message)) {
                        Log.d("text", "Message recieved: " + messages[0].getMessageBody());
                        DataUpload dataUpload = new DataUpload(deviceid, user, mobile);
                        dataUpload.execute();
                        Intent intent1 = new Intent(context,Map.class);
                        intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(intent1);
                    } else
                        Toast.makeText(context, "verification failed", Toast.LENGTH_SHORT).show();
                    // TODO: This method is called when the BroadcastReceiver is receiving
                    // an Intent broadcast.
                    //throw new UnsupportedOperationException("Not yet implemented");
                }
            }

        }
    }
}
