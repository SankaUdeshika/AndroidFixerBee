package lk.sankaudeshika.androidfixerbee;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.ValueCallback;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import lk.sankaudeshika.androidfixerbee.model.SqlHelper;

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

                //                Register Validaiton
                EditText mobile = RegisterView.findViewById(R.id.RegisterMobile);
                EditText name = RegisterView.findViewById(R.id.RegisterName);
                EditText email = RegisterView.findViewById(R.id.RegisterEmail);
                EditText password = RegisterView.findViewById(R.id.RegisterPasswrod);


                if (mobile.getText().toString().length() != 10) {
                    Toast.makeText(RegisterActivity.this, "mobile Number must have 10 characters", Toast.LENGTH_SHORT).show();
                } else if (mobile.getText().toString().isEmpty()) {
                    Toast.makeText(RegisterActivity.this, "Please Mobile Number", Toast.LENGTH_SHORT).show();
                } else if (!mobile.getText().toString().matches("^(?:\\+94|0)7[1245678]\\d{7}$")) {
                    Toast.makeText(RegisterActivity.this, "Please Enter Valid Mobile Number ", Toast.LENGTH_SHORT).show();
                } else if (name.getText().toString().isEmpty()) {
                    Toast.makeText(RegisterActivity.this, "Please Enter Your Name", Toast.LENGTH_SHORT).show();
                } else if (email.getText().toString().isEmpty()) {
                    Toast.makeText(RegisterActivity.this, "Please Enter Your Email", Toast.LENGTH_SHORT).show();
                } else if (!email.getText().toString().matches("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$")) {
                    Toast.makeText(RegisterActivity.this, "Please Enter Valid Email Address ", Toast.LENGTH_SHORT).show();
                } else if (password.getText().toString().isEmpty()) {
                    Toast.makeText(RegisterActivity.this, " The Password must have 5-8 characters ", Toast.LENGTH_SHORT).show();
                } else {

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

//                                  View Map
                                    String url = mapView.getUrl();
                                    String regex = "@([0-9.-]+),([0-9.-]+)";
                                    Pattern pattern = Pattern.compile(regex);
                                    Matcher matcher = pattern.matcher(url);
                                    mapView.setBackgroundColor(getColor(R.color.SecondColor));
//                                    mapView.evaluateJavascript("document.querySelector('.xiQnY').value;",
//                                            new ValueCallback<String>() {
//                                                @Override
//                                                public void onReceiveValue(String s) {
//                                                    Log.i("appout", s);
//                                                }
//                                            });
                                    if (matcher.find()) {
                                        FirebaseFirestore firestore  = FirebaseFirestore.getInstance();
                                        firestore.collection("user")
                                                .whereEqualTo("mobile",mobile.getText().toString())
                                                .get()
                                                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                                    @Override
                                                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                                        Log.i("appout", String.valueOf(queryDocumentSnapshots.size()));
                                                        int member_rs = queryDocumentSnapshots.size();
                                                        if(member_rs == 0){

                                                            String latitude = matcher.group(1);
                                                            String longitude = matcher.group(2);

//                                                          Insert into Database
                                                            HashMap<String, Object> document = new HashMap<>();
                                                            document.put("mobile",mobile.getText().toString());
                                                            document.put("name",name.getText().toString());
                                                            document.put("email",email.getText().toString());
                                                            document.put("password",password.getText().toString());
                                                            document.put("status","active");
                                                            document.put("address","");
                                                            document.put("latitude",latitude);
                                                            document.put("longitude",longitude);

                                                            firestore.collection("user")
                                                                    .add(document)
                                                                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                                                        @Override
                                                                        public void onSuccess(DocumentReference documentReference) {
                                                                            Log.i("appout", "onSuccess: Success"+documentReference.get());
                                                                            //                                            SQL Insert Login
                                                                            new Thread(new Runnable() {
                                                                                @Override
                                                                                public void run() {
                                                                                    try {
                                                                                        SimpleDateFormat simpleDate = new SimpleDateFormat("yyyy-MM-dd");
                                                                                        String currunt_date  = simpleDate.format(new Date());
                                                                                        SimpleDateFormat simpleTime = new SimpleDateFormat("HH:mm:ss");
                                                                                        String currunt_time = simpleTime.format(new Date());

                                                                                        SqlHelper sqlHelper = new SqlHelper(RegisterActivity.this,"activity.db",null,1);
                                                                                        SQLiteDatabase sqLiteDatabase1 = sqlHelper.getWritableDatabase();
                                                                                        sqLiteDatabase1.execSQL("INSERT INTO `actions` (`action_name`,`action_date`,`action_time`) VALUES('User Registerd','"+currunt_date+"','"+currunt_time+"')");
                                                                                    } catch (Exception e) {
                                                                                        Log.i("appout", e.toString());
                                                                                        throw new RuntimeException(e);
                                                                                    }
                                                                                }
                                                                            }).start();
                                                                            Intent i = new Intent(RegisterActivity.this,LoginActivity.class);
                                                                            i.putExtra("RegisterMessage",1);
                                                                            startActivity(i);
                                                                        }
                                                                    });




//



                                                        }else{
                                                            new AlertDialog.Builder(RegisterActivity.this)
                                                                    .setTitle("Already Registerd Person")
                                                                    .setMessage("You Already have a Account, Please Sign in")
                                                                    .show();
                                                        }


                                                    }
                                                })
                                                .addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        Log.i("appout", "Error Search User");
                                                        new AlertDialog.Builder(RegisterActivity.this)
                                                                .setTitle("Error")
                                                                .setMessage("Something Wrong, Please Try again later")
                                                                .show();
                                                    }
                                                });
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


            }
        });


    }
}