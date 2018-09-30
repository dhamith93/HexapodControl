package com.example.dhamith93.hexapodcontrol;

import android.content.pm.ActivityInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.Switch;
import android.widget.Toast;
import com.android.volley.*;
import com.android.volley.toolbox.*;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private Switch powerSwitch;
    private String serverAddress;
    private String currentOp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
        );
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        powerSwitch = findViewById(R.id.powerSwitch);

        serverAddress = "http://192.168.4.1:8081";
        currentOp = "halt";

        powerSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String op = (powerSwitch.isChecked()) ? "start" : "stop";
                sendOperationRequest(op);
            }
        });

        Button btnLeft = findViewById(R.id.btnLeft);
        Button btnRight = findViewById(R.id.btnRight);


        btnLeft.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        if (powerSwitch.isChecked())
                            sendOperationRequest("left");
                        return true;
                    case MotionEvent.ACTION_UP:
                        if (powerSwitch.isChecked())
                            sendOperationRequest(currentOp);
                        return true;
                }
                return false;
            }
        });

        btnRight.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        if (powerSwitch.isChecked())
                            sendOperationRequest("right");
                        return true;
                    case MotionEvent.ACTION_UP:
                        if (powerSwitch.isChecked())
                            sendOperationRequest(currentOp);
                        return true;
                }
                return false;
            }
        });
    }

    public void onClick(View view) {
        if (powerSwitch.isChecked()) {
            switch (view.getId()) {
                case R.id.btnForward:
                    sendOperationRequest("forward");
                    currentOp = "forward";
                    break;
                case R.id.btnBackward:
                    sendOperationRequest("backward");
                    currentOp = "backward";
                    break;
                case R.id.btnHalt:
                    sendOperationRequest("halt");
                    currentOp = "halt";
                    break;
                case R.id.btnStream:
                    startStream();
                    break;
                case R.id.btnCapture:
                    new Downloader().execute();
                    break;
                default:
                    break;
            }
        }
    }

    private void sendOperationRequest(final String operation) {
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = this.serverAddress;

        if (url.isEmpty())
            return;

        try {
            StringRequest stringRequest = new StringRequest(
                    Request.Method.POST,
                    url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            handleResponse(response);
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            System.out.println(error.toString());
                            displayErrors("Error sending request...");
                        }
                    }
            ) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<>();
                    params.put("type", "op");
                    params.put("op", operation);
                    params.put("auth", "KEY_123");

                    return params;
                }
            };
            queue.add(stringRequest);
        } catch (Exception ex) {
            displayErrors("Can't setup request...");
        }
    }

    private void startStream() {
        WebView webView = findViewById(R.id.webView);
        webView.loadUrl("http://192.168.4.1:8080/?action=stream");
    }

    private void handleResponse(String response) {
        try {
            JSONObject jsonObj = new JSONObject(response);
            String status = jsonObj.getString("status");
            if (status.equals("error"))
                displayErrors(status);
        } catch (Exception ex) {
            displayErrors("Can't parse response...");
        }
    }

    private void displayErrors(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }
}
