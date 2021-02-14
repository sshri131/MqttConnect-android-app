package com.example.mqttconnect;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.io.UnsupportedEncodingException;

public class MainActivity extends AppCompatActivity {




    Button btnConnect,btnPublish;
    EditText etPublishedData;
    final String topicTemperature = "Wearable/Temperature";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnConnect = findViewById(R.id.btnConnect);
        btnPublish = findViewById(R.id.btnPublish);
        etPublishedData = findViewById(R.id.etPublishedData);

        ActivityCompat.requestPermissions(MainActivity.this,
                new String[]{Manifest.permission.WAKE_LOCK, Manifest.permission.INTERNET,
                        Manifest.permission.READ_PHONE_STATE, Manifest.permission.ACCESS_NETWORK_STATE},
                101);

        String clientId = MqttClient.generateClientId();
        final MqttAndroidClient client =
                new MqttAndroidClient(this.getApplicationContext(), "tcp://broker.hivemq.com:1883",
                        clientId);


        btnConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    IMqttToken token = client.connect();
                    token.setActionCallback(new IMqttActionListener() {
                        @Override
                        public void onSuccess(IMqttToken asyncActionToken) {
                            // We are connected
                            Log.d("MQTT", "onSuccess");
                            Toast.makeText(MainActivity.this, "Connected", Toast.LENGTH_LONG).show();
                        }

                        @Override
                        public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                            // Something went wrong e.g. connection timeout or firewall problems
                            Log.d("MQTT", "onFailure");
                            Toast.makeText(MainActivity.this,"Failed",Toast.LENGTH_LONG).show();

                        }
                    });
                } catch (MqttException e) {
                    e.printStackTrace();
                }

            }
        });



        btnPublish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String payload = etPublishedData.getText().toString();


                if (client.isConnected()) {
                    byte[] encodedPayload;
                    try {
                        encodedPayload = payload.getBytes("UTF-8");
                        MqttMessage message = new MqttMessage(encodedPayload);
                        client.publish(topicTemperature, message);
                        etPublishedData.setText("");
                        Toast.makeText(MainActivity.this,"Message Published ",Toast.LENGTH_SHORT).show();
                    } catch (UnsupportedEncodingException | MqttException e) {
                        e.printStackTrace();
                    }
                }

                else
                    Toast.makeText(MainActivity.this,"Kindly Connect first",Toast.LENGTH_LONG).show();
            }

        });


    }
}