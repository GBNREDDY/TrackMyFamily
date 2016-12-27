package in.skonda.trackmyfamily;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.net.Uri;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Random;


public class MainActivity extends AppCompatActivity {
    private static final int MY_PERMISSIONS_REQUEST = 0;
    public String user, mobile, deviceid, message, image;
    TextView tv1, tv2, tv3, tv4;
    EditText et1, et2;
    Button btn;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    File f;
    Bitmap bitmap;
    Boolean isRegistered;
    private int PICK_IMAGE_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPreferences = getSharedPreferences("in.skonda.trackmyfamily.registration", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        isRegistered = sharedPreferences.getBoolean("registered", false);
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED)   // sms permission selfcheck
        {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.SEND_SMS}, MY_PERMISSIONS_REQUEST);
        }
        if (isRegistered) {
            Intent map = new Intent(this, TabbedActivity.class);
            map.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(map);
            finish();
        } else {
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
            editor.putString("device_id", deviceid);
            editor.commit();

            btn = (Button) findViewById(R.id.btn);
            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    user = et1.getText().toString();
                    mobile = et2.getText().toString();
                    if (TextUtils.isEmpty(user) || TextUtils.isEmpty(mobile)) {
                        if (TextUtils.isEmpty(user)) {
                            et1.setError("Should not be empty");
                        }
                        if (TextUtils.isEmpty(mobile)) {
                            et2.setError("should not be empty");
                        }
                        return;
                    } else {
                        //Toast.makeText(MainActivity.this, user+"\n"+mobile, Toast.LENGTH_SHORT).show();
                        editor.putString("name", user);
                        editor.putString("mobile_number", mobile);
                        editor.commit();
                        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED)   // sms permission selfcheck
                        {
                            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.SEND_SMS}, MY_PERMISSIONS_REQUEST);
                        } else {
                            sendingSms();
                        }
                        if (bitmap != null) {
                            ImageUpload imageUpload = new ImageUpload(f.getName(), getFilesDir());
                            imageUpload.execute();
                            editor.putString("image", f.getName() + ".png");
                            editor.commit();
                        } else {
                            editor.putString("image", "default.png");
                            editor.commit();
                        }
                    }
                }
            });
            tv1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent();
                    intent.setType("image/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
                }
            });
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri filePath = data.getData();
            try {
                //Getting the Bitmap from Gallery
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);

                f = new File(getFilesDir(), deviceid + ".png");
                f.createNewFile();
                //ImageView iv=(ImageView) findViewById(R.id.iv);
                //iv.setImageBitmap(bitmap);
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 0 /*ignored for PNG*/, bos);
                byte[] bitmapdata = bos.toByteArray();
                FileOutputStream fos = new FileOutputStream(f);
                fos.write(bitmapdata);
                fos.flush();
                fos.close();


            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                TextView tvpic = (TextView) findViewById(R.id.tv2);
                tvpic.setText(f.getName());
            }
        }


    }

    protected void sendingSms() {
        SmsManager smsManager = SmsManager.getDefault();
        Random randomnumber = new Random();                   // generating random number btn 1000 t0 9999
        int code = randomnumber.nextInt(9999 - 1000) + 1000;
        message = "verification code from FamilyFriendsTracker " + code;
        editor.putString("text_message", message);
        editor.commit();
        //Toast.makeText(this, sharedPreferences.getString("text_message",null).toString(), Toast.LENGTH_SHORT).show();
        Log.d("msg", message);
        smsManager.sendTextMessage(mobile, null, message, null, null);
        Toast.makeText(getApplicationContext(), "SMS sent.", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED || grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    if (grantResults[0] == PackageManager.PERMISSION_DENIED || grantResults[1] == PackageManager.PERMISSION_DENIED){
                        ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.SEND_SMS}, MY_PERMISSIONS_REQUEST);

                    }

                } else if ( grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_DENIED && grantResults[1] == PackageManager.PERMISSION_DENIED) {
                    //ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.SEND_SMS}, MY_PERMISSIONS_REQUEST);
                    finish();
                }
            break;
        }
    }
}
