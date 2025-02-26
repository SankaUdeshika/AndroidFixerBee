package lk.sankaudeshika.androidfixerbee.model;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import lk.sankaudeshika.androidfixerbee.MainActivity;
import lk.sankaudeshika.androidfixerbee.NoInternetActivity;
import lk.sankaudeshika.androidfixerbee.RegisterActivity;

public class InternetBroadCast extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        if (ConnectivityManager.CONNECTIVITY_ACTION.equals(intent.getAction())) {
            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

            if (activeNetwork != null) {
                if (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE) {
                    if (activeNetwork.isConnected()) {
                        Log.i("appout", "onReceive: ON");
                        Toast.makeText(context, "Mobile Data is ON", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Log.i("appout", "onReceive: ON");
                    Toast.makeText(context, "Mobile Data is ON", Toast.LENGTH_SHORT).show();
                }
            } else {
                Log.i("appout", "onReceive: NO CONNECTION");
                Toast.makeText(context, "No Internet Connection", Toast.LENGTH_SHORT).show();
                Intent i = new Intent(context,NoInternetActivity.class);
                context.startActivity(i);
            }
        }
       
    }
}
