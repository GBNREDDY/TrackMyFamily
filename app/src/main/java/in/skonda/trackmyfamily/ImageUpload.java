package in.skonda.trackmyfamily;

import android.os.AsyncTask;
import android.util.Log;

import java.io.File;
import java.io.IOException;

import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

/**
 * Created by GBNREDDY on 07-12-2016.
 */

public class ImageUpload extends AsyncTask<String, String, String> {
    private final String fName;
    private final File fDir;
    public ImageUpload(String fileName, File filesDir) {
        this.fName=fileName;
        this.fDir=filesDir;

    }

    @Override
    protected String doInBackground(String... strings) {
        // build RequestBody
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
// multiple images can be uploaded by repeating this
                .addFormDataPart("photo",fName, RequestBody.create(MultipartBody.FORM, new File(fDir+"/"+fName)))
                .build();

        Request request = new Request.Builder()
                .url("http://trackfamily.co.in/uploadImage.php")
                .post(requestBody)
                .build();

        OkHttpClient client = new OkHttpClient();

        client.newCall(request).enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(okhttp3.Call call, IOException e) {
                Log.d("skondad ", "Failed to upload:  " + e.getMessage() );
            }
            @Override
            public void onResponse(okhttp3.Call call, okhttp3.Response response) throws IOException {
                Log.d("skondad ", "onResponse: " + response.body().string() );
            }
        });

        return "Sucessfully uploaded";
    }

}
