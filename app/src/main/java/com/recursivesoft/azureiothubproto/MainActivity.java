package com.recursivesoft.azureiothubproto;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.microsoft.azure.iothub.DeviceClient;
import com.microsoft.azure.iothub.IotHubClientProtocol;
import com.microsoft.azure.iothub.IotHubEventCallback;
import com.microsoft.azure.iothub.IotHubMessageResult;
import com.microsoft.azure.iothub.IotHubStatusCode;
import com.microsoft.azure.iothub.Message;

import java.io.IOException;
import java.net.URISyntaxException;

public class MainActivity extends AppCompatActivity {

    String connString = "Put your IoT device's connection string here!";
    IotHubClientProtocol protocol = IotHubClientProtocol.MQTT;
    DeviceClient client = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }

    @Override
    protected void onResume() {
        super.onResume();

        try {
            client = new DeviceClient(connString, protocol);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        try {
            client.open();
        } catch(IOException e1) {
            System.out.println("Exception while opening IoTHub connection: " + e1.toString());
        } catch(Exception e2) {
            System.out.println("Exception while opening IoTHub connection: " + e2.toString());
        }
    }

    @Override
    protected void onStop() {
        try {
            client.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        super.onStop();
    }

    public void sendData(View v) {

        String b = "\"value\"";
        String c = String.valueOf((int)(Math.random()*20));   // 0~19

        String m = "\"time\"";
        String n = Long.toString(System.currentTimeMillis());

        String x = String.valueOf((char)((int)(Math.random()*26 + 65)));
        String y = "\"type\"";
        String z = "\"" + x + " Type\"";


        String a = "{";
        String d = "}";
        String e = ",";

        String msgStr = a+ b+":"+c +e+ m+":"+n +e+ y+":"+z +d;
        Log.d("Test json String: ",msgStr);

        Message msg = new Message(msgStr);
        msg.setProperty("messageCount", Long.toString(System.currentTimeMillis()));

        EventCallback eventCallback = new EventCallback();
        client.sendEventAsync(msg, eventCallback, System.currentTimeMillis());
    }

    // Azure Inner static Class Begin ----------------
    // Our MQTT doesn't support abandon/reject, so we will only display the messaged received from IoTHub and return COMPLETE
    protected static class MessageCallbackMqtt implements com.microsoft.azure.iothub.MessageCallback {
        public IotHubMessageResult execute(Message msg, Object context) {
            Counter counter = (Counter) context;
            System.out.println(
                    "Received message " + counter.toString()
                            + " with content: " + new String(msg.getBytes(), Message.DEFAULT_IOTHUB_MESSAGE_CHARSET));
            counter.increment();
            return IotHubMessageResult.COMPLETE;
        }
    }

    protected static class EventCallback implements IotHubEventCallback {
        public void execute(IotHubStatusCode status, Object context){
            Long i = (Long) context;
            System.out.println("IoT Hub responded to message "+i.toString()
                    + " with status " + status.name());
        }
    }

    protected static class MessageCallback implements com.microsoft.azure.iothub.MessageCallback {
        public IotHubMessageResult execute(Message msg, Object context) {
            Counter counter = (Counter) context;
            //System.out.println("Received message " + counter.toString() + " with content: " + new String(msg.getBytes(), Message.DEFAULT_IOTHUB_MESSAGE_CHARSET));
            Log.d("Test", "Received message " + counter.toString()
                    + " with content: " + new String(msg.getBytes(), Message.DEFAULT_IOTHUB_MESSAGE_CHARSET));

            int switchVal = counter.get() % 3;
            IotHubMessageResult res;
            switch (switchVal) {
                case 0:
                    res = IotHubMessageResult.COMPLETE;
                    break;
                case 1:
                    res = IotHubMessageResult.ABANDON;
                    break;
                case 2:
                    res = IotHubMessageResult.REJECT;
                    break;
                default:
                    // should never happen.
                    throw new IllegalStateException("Invalid message result specified.");
            }

            //System.out.println("Responding to message " + counter.toString() + " with " + res.name());
            Log.d("Test", "Responding to message " + counter.toString() + " with " + res.name());
            counter.increment();
            return res;
        }
    }

    /** Used as a counter in the message callback. */
    protected static class Counter {
        protected int num;
        public Counter(int num) {
            this.num = num;
        }
        public int get() {
            return this.num;
        }
        public void increment(){
            this.num++;
        }
        @Override
        public String toString() {
            return Integer.toString(this.num);
        }
    }
    // Azure Inner static Class End ----------------


}   // End of MainActivity
