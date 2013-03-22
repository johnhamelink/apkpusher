package com.farmgeek.apkpusher;

import android.app.Activity;
import android.app.Notification;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import org.apache.http.conn.util.InetAddressUtils;

import java.io.*;
import java.io.FileOutputStream;
import java.lang.String;
import java.net.*;
import java.util.Enumeration;

import static com.farmgeek.apkpusher.R.drawable.icon;

public class HelloAndroidActivity extends Activity {

    public static final String TAG = "apkpusher";
    public static final Integer SERVER_PORT = 8080;
    public static final String filename = "apk" + (System.currentTimeMillis() / 1000L) + ".apk";
    private ServerSocket server;

    /**
     * Called when the activity is first created.
     *
     * @param savedInstanceState If the activity is being re-initialized after
     *                           previously being shut down then this Bundle contains the data it most
     *                           recently supplied in onSaveInstanceState(Bundle). <b>Note: Otherwise it is null.</b>
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.main);
        new Thread(conn).start();
        showLaunched();
    }

    private void showLaunched()
    {
        Context ctx = getApplicationContext();
        Toast toast = Toast.makeText(
                ctx,
                "APKPusher is now running in the background with IP " + getLocalIpAddress(),
                Toast.LENGTH_LONG
        );
        toast.show();
    }

    private void toastReceived()
    {
        runOnUiThread(new Runnable() {
            public void run() {
                Toast.makeText(HelloAndroidActivity.this, "Received new APK", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void promptInstall()
    {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.fromFile(new File(Environment.getExternalStorageDirectory() + "/" + this.filename)), "application/vnd.android.package-archive");
        startActivity(intent);
    }


    public String getLocalIpAddress() {
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); ) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress() && InetAddressUtils.isIPv4Address(inetAddress.getHostAddress().toString())) {
                        return inetAddress.getHostAddress().toString();
                    }
                }
            }
        } catch (SocketException ex) {
            Log.e(TAG, ex.toString());
        }
        return "";
    }


    Runnable conn = new Runnable() {

        public void run() {
            // Set up the server
            try {
                server = new ServerSocket(SERVER_PORT);
            } catch (IOException e) {
                e.printStackTrace();
            }

            File file = new File(Environment.getExternalStorageDirectory() + "/" + HelloAndroidActivity.filename);
            FileOutputStream fis = null;
            InputStream os = null;
            Socket sock = null;

            // Wait for a response
            try {
                while (true) {
                    // Accept the data connection
                    sock = server.accept();
                    // Load the byteArray with bytes the size of the file.
                    byte[] byteArray = new byte[1024 * 1024];
                    fis = new FileOutputStream(file);
                    os = sock.getInputStream();

                    int count;
                    // Write the contents to the file
                    while ((count = os.read(byteArray)) >= 0) {
                        fis.write(byteArray, 0, count);
                    }

                    toastReceived();
                    // Run the installation
                    promptInstall();
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                // Cleanup.
                try {
                    fis.close();
                    os.close();
                    sock.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    };

}

