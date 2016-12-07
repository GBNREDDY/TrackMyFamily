package in.skonda.trackmyfamily;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by GBNREDDY on 07-12-2016.
 */

public class DataUpload extends AsyncTask<String, String, String> {
private final String deviceid,user,mobile;
    String result;
    public DataUpload(String deviceid, String user, String mobile) {
        this.deviceid=deviceid;
        this.user=user;
        this.mobile=mobile;
    }

    @Override
    protected String doInBackground(String... strings) {
        try {
            Log.d("insert",deviceid +" "+ user +" "+mobile);// name and device id from main activity
            URL url = new URL("http:trackfamily.co.in/insert.php?deviceid=" + deviceid + "&username=" + user + "&mobile=" + mobile);
            HttpURLConnection connect = (HttpURLConnection) url.openConnection();
            BufferedReader breader = new BufferedReader(new InputStreamReader(connect.getInputStream()));
            result = breader.readLine();
            Log.d("code",result);
            return result;
        } catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }
}
