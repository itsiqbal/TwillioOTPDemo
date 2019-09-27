package com.mlabs.otptwilliodemo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.twilio.Twilio;
import com.twilio.rest.verify.v2.service.Verification;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.IOException;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    public static final String ACCOUNT_SID = "PUT YOUR ACCOUNT SID";
    public static final String AUTH_TOKEN = "PUT YOUR AUTH TOKEN";
    private static final String TAG = "Phone OTP";

    private EditText number,otp;
    private Button send,verify;

    private TextView status;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        //UI components
        number = (EditText) findViewById(R.id.text1);
        otp = (EditText) findViewById(R.id.text2);

        send = (Button) findViewById(R.id.send);
        verify = (Button) findViewById(R.id.verify);


        status = (TextView) findViewById(R.id.text3);

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String numberText = number.getText().toString();
                //check number if not null
                if (number.equals("")){
                    Toast.makeText(getApplicationContext(),"Number is Empty",Toast.LENGTH_LONG).show();
                    return ;
                }

                //making is disabled so user can not request
                number.setEnabled(false);

                //making Request
                sendOTP(numberText,"sms");


            }
        });

        verify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String codeText = otp.getText().toString();
                String numberText = number.getText().toString();
                //check otp if not null
                if (number.equals("")){
                    Toast.makeText(getApplicationContext(),"OTP is Empty",Toast.LENGTH_LONG).show();
                    return ;
                }

                //making is disabled so user can not request
                number.setEnabled(false);

                //making Request
                verifyOTP(numberText,codeText);
            }
        });

        //sendOTP("+923337256796","sms");
    }


    private void sendOTP(String toPhoneNumber, String message){
        OkHttpClient client = new OkHttpClient();
        String url = "https://verify.twilio.com/v2/Services/"+AUTH_TOKEN+"/Verifications";
        String base64EncodedCredentials = "Basic " + Base64.encodeToString((ACCOUNT_SID + ":" + AUTH_TOKEN).getBytes(), Base64.NO_WRAP);

        RequestBody body = new FormBody.Builder()
                .add("To", toPhoneNumber)
                .add("Channel", message)
                .build();

        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .header("Authorization", base64EncodedCredentials)
                .build();
        try {

            Response response = client.newCall(request).execute();

            String responseString = response.body().string();

            JSONObject obj = new JSONObject(responseString);



            Log.d(TAG, "OTP Sent : "+ responseString);

            Context context = getApplicationContext();

            CharSequence text = "Response " +obj.getString("status");

            int duration = Toast.LENGTH_SHORT;

            Toast toast = Toast.makeText(context, text, duration);

            toast.show();

            status.setText("Status :: " +obj.getString("status").toString());

        } catch (IOException e) { e.printStackTrace(); } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    private void verifyOTP (String phoneNumber , String code){

        OkHttpClient client = new OkHttpClient();
        String url = "https://verify.twilio.com/v2/Services/"+AUTH_TOKEN+"/VerificationCheck";
        String base64EncodedCredentials = "Basic " + Base64.encodeToString((ACCOUNT_SID + ":" + AUTH_TOKEN).getBytes(), Base64.NO_WRAP);

        RequestBody body = new FormBody.Builder()
                .add("Code", code)
                .add("To", phoneNumber)
                .build();

        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .header("Authorization", base64EncodedCredentials)
                .build();
        try {

            Response response = client.newCall(request).execute();


            String responseString = response.body().string();

            JSONObject obj = new JSONObject(responseString);



            Log.d(TAG, "OTP Sent : "+ responseString);

            Context context = getApplicationContext();

            CharSequence text = "Response " +obj.getString("status");

            int duration = Toast.LENGTH_SHORT;

            Toast toast = Toast.makeText(context, text, duration);

            toast.show();

            status.setText("Status :: " +obj.getString("status").toString());



            number.setEnabled(true);



        } catch (IOException e) { e.printStackTrace(); } catch (JSONException e) {
            e.printStackTrace();
        }

    }
}
