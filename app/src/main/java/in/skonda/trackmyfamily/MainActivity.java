package in.skonda.trackmyfamily;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

public class MainActivity extends AppCompatActivity {
    TextView tv1,tv2,tv3,tv4;
    EditText et1,et2;
    Button btn;
    String imgstr,user,mobile,deviceid;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tv1=(TextView)findViewById(R.id.tv1);
        et1=(EditText)findViewById(R.id.et1);

        tv2=(TextView) findViewById(R.id.tv2);
        et2=(EditText)findViewById(R.id.et2);

        tv3=(TextView)findViewById(R.id.tv3);
        tv4=(TextView) findViewById(R.id.tv4);
        Typeface myFont=Typeface.createFromAsset(getAssets(),"fonts/fontawesome-webfont.ttf");
        tv1.setTypeface(myFont);
        tv3.setTypeface(myFont);
        tv4.setTypeface(myFont);

        deviceid=Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);

        btn=(Button)findViewById(R.id.btn);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                user=et1.getText().toString();
                mobile=et2.getText().toString();
                Log.d("imgstr",imgstr);
                Insert insert=new Insert();
                insert.execute();
            }
        });
        tv1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(i,1);
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        ImageView iv=(ImageView)findViewById(R.id.iv);
        Bitmap bmp=(Bitmap) data.getParcelableExtra("data");
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.PNG, 10 , baos);
        byte[] img = baos.toByteArray();
        imgstr= Base64.encodeToString(img,Base64.URL_SAFE);
        byte[] barray=Base64.decode(imgstr,Base64.URL_SAFE);
        Bitmap bmp2= BitmapFactory.decodeByteArray(barray,0,barray.length);
        iv.setImageBitmap(bmp2);
    }
    public class Insert extends AsyncTask<String,String,String> {
        String result;
        @Override
        protected String doInBackground(String... params) {
            try {
                URL url = new URL("http:trackfamily.co.in/insert.php?deviceid="+deviceid+"&username="+user+"&mobile="+mobile);
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
        protected  void onPostExecute(String result){

            Toast.makeText(MainActivity.this, "result is :"+ result, Toast.LENGTH_SHORT).show();

        }

    }
}
