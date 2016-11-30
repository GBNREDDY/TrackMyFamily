package in.skonda.trackmyfamily;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Random;

import static android.R.id.message;

public class MainActivity extends AppCompatActivity {
    TextView tv1, tv2, tv3, tv4;
    EditText et1, et2;
    Button btn;
    String imgstr, user, mobile, deviceid, message;
    private static final int MY_PERMISSIONS_REQUEST_SEND_SMS = 0;
    SharedPreferences sharedPreferences=getSharedPreferences("messege body", Context.MODE_PRIVATE);
    SharedPreferences.Editor editor=sharedPreferences.edit();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tv1 = (TextView) findViewById(R.id.tv1);
        et1 = (EditText) findViewById(R.id.et1);

        tv2 = (TextView) findViewById(R.id.tv2);
        et2 = (EditText) findViewById(R.id.et2);

        tv3 = (TextView) findViewById(R.id.tv3);
        tv4 = (TextView) findViewById(R.id.tv4);
        Typeface myFont = Typeface.createFromAsset(getAssets(), "fonts/fontawesome-webfont.ttf");
        tv1.setTypeface(myFont);
        tv3.setTypeface(myFont);
        tv4.setTypeface(myFont);

        deviceid = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);
        editor.putString("device_id",deviceid);

        btn = (Button) findViewById(R.id.btn);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                user = et1.getText().toString();
                mobile = et2.getText().toString();
                editor.putString("name",user);
                editor.putString("mobile number",mobile);
                Log.d("imgstr", imgstr);
                Insert insert = new Insert();
                insert.execute();
                Intent brintent=new Intent(view.getContext(),Receiver.class);
                startActivity(brintent);
            }
        });
        tv1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(i, 1);
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        ImageView iv = (ImageView) findViewById(R.id.iv);
        Bitmap bmp = (Bitmap) data.getParcelableExtra("data");
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.PNG, 10, baos);
        byte[] img = baos.toByteArray();
        imgstr = Base64.encodeToString(img, Base64.URL_SAFE);
        byte[] barray = Base64.decode(imgstr, Base64.URL_SAFE);
        Bitmap bmp2 = BitmapFactory.decodeByteArray(barray, 0, barray.length);
        iv.setImageBitmap(bmp2);
    }

    public class Insert extends AsyncTask<String, String, String> {
        String result;

        @Override
        protected String doInBackground(String... params) {
            try {
                URL url = new URL("http:trackfamily.co.in/insert.php?deviceid=" + deviceid + "&username=" + user + "&mobile=" + mobile);
                HttpURLConnection connect = (HttpURLConnection) url.openConnection();
                BufferedReader breader = new BufferedReader(new InputStreamReader(connect.getInputStream()));
                result = breader.readLine();
                return result;
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        protected void onPostExecute(String result) {

            Toast.makeText(MainActivity.this, "result is :" + result, Toast.LENGTH_SHORT).show();

        }

    }

    protected void sendingsms() {

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.SEND_SMS)) {
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS}, MY_PERMISSIONS_REQUEST_SEND_SMS);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_SEND_SMS: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    SmsManager smsManager = SmsManager.getDefault();
                    Random randomnumber = new Random();                                                            // generating random number btn 1000 t0 9999
                    int code = randomnumber.nextInt(9999 - 1000) + 1000;
                    message = "verification code from FamilyFriendsTracker" + code;
                    editor.putString("text message", message);
                    editor.commit();
                    Log.d("msg",message);
                    smsManager.sendTextMessage(mobile, null, message, null, null);
                    Toast.makeText(getApplicationContext(), "SMS sent.", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getApplicationContext(),
                            "SMS faild, please try again.", Toast.LENGTH_LONG).show();
                    return;
                }
            }
        }
    }
}
