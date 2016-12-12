package in.skonda.trackmyfamily;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

/**
 * Created by GBNREDDY on 07-12-2016.
 */

public class DataUpload extends AsyncTask<String, String, String> {
    private final String deviceid;
    private  final  String user;
    private  final  String mobile;
    private final String imagepath;
    String result;
    public DataUpload(String deviceid, String user, String mobile, String image) {
        this.deviceid=deviceid;
        this.user=user;
        this.mobile=mobile;
        this.imagepath=image;
    }

    @Override
    protected String doInBackground(String... strings) {

        // build RequestBody
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
// multiple images can be uploaded by repeating this
                .addFormDataPart("deviceid",deviceid)
                .addFormDataPart("username",user)
                .addFormDataPart("mobile",mobile)
                .addFormDataPart("image",imagepath)
                .build();

        Request request = new Request.Builder()
                .url("http://trackfamily.co.in/insert.php")
                .post(requestBody)
                .build();

        OkHttpClient client = new OkHttpClient();

        client.newCall(request).enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(okhttp3.Call call, IOException e) {
                Log.d("skondad ", "Failed to upload: data " + e.getMessage() );
            }
            @Override
            public void onResponse(okhttp3.Call call, okhttp3.Response response) throws IOException {
                Log.d("skondad ", "onResponse: " + response.body().string() );
            }
        });
        return "Sucessfully uploaded";
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
    }
}
