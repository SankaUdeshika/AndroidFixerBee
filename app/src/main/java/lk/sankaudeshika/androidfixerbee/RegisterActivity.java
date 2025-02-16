package lk.sankaudeshika.androidfixerbee;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.maps.SupportMapFragment;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegisterActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
//      Add register_sub_component
        LayoutInflater inflater = LayoutInflater.from(RegisterActivity.this);
        View RegisterView = inflater.inflate(R.layout.register_sub_component, null, false);

//        Add Liner Layouts
        LinearLayout linearLayout = findViewById(R.id.registerLienarLayout);
        linearLayout.addView(RegisterView);

//      Open Popup Notificaiton
        Button button = RegisterView.findViewById(R.id.register_next_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LayoutInflater inflater = LayoutInflater.from(RegisterActivity.this);
                View address_input_popup_view = inflater.inflate(R.layout.address_input_popup, linearLayout, false);
                AlertDialog alertDialog = new AlertDialog.Builder(RegisterActivity.this).setView(address_input_popup_view).show();

                //               popup Ok BUtton Listner
                Button OkBtn = address_input_popup_view.findViewById(R.id.alertOk);
                OkBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        alertDialog.cancel();

//                        load Map to get User Location
                        View register_map_view = inflater.inflate(R.layout.register_map, null, false);
                        new AlertDialog.Builder(RegisterActivity.this).setView(register_map_view).show();

                        WebView mapView = register_map_view.findViewById(R.id.MapView);
                        mapView.getSettings().setJavaScriptEnabled(true);
                        WebViewClient client = new WebViewClient();
                        mapView.setWebViewClient(client);
                        mapView.loadUrl("https://www.google.com/maps/place/Colombo/@6.9218315,79.7736315,12z/data=!3m1!4b1!4m6!3m5!1s0x3ae253d10f7a7003:0x320b2e4d32d3838d!8m2!3d6.9270786!4d79.861243!16zL20vMGZuN3I?entry=ttu&g_ep=EgoyMDI1MDIxMi4wIKXMDSoASAFQAw%3D%3D");

//                          Save Button
                        Button saveButton = register_map_view.findViewById(R.id.saveButton);
                        saveButton.setOnClickListener(new View.OnClickListener() {

                            @Override
                            public void onClick(View view) {

                                String url = mapView.getUrl();
                                String regex = "@([0-9.-]+),([0-9.-]+)";
                                Pattern pattern = Pattern.compile(regex);
                                Matcher matcher = pattern.matcher(url);

                                if (matcher.find()) {

                                    String latitude = matcher.group(1);
                                    String longitude = matcher.group(2);


                                } else {
                                    Log.i("appout", "onClick: waradi");
                                    Toast.makeText(RegisterActivity.this, "Somethig Wrong, Please Try again", Toast.LENGTH_SHORT).show();
                                }


                            }
                        });

                    }
                });

                //               popup Cancel BUtton Listner
                Button cancelBtn = address_input_popup_view.findViewById(R.id.alertCancel);
                cancelBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent i = new Intent(RegisterActivity.this, LoginActivity.class);
                        startActivity(i);
                    }
                });

            }
        });


    }
}