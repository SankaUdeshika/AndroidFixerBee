package lk.sankaudeshika.androidfixerbee;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.sql.Time;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Date;
import java.util.List;
import java.util.SimpleTimeZone;
import java.util.Timer;

import lk.sankaudeshika.androidfixerbee.model.SqlHelper;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


//      Connect Load Sub Compoenent
        View view = getLayoutInflater().inflate(R.layout.login_sub_component, null);
        LinearLayout login_lLinearLayout = findViewById(R.id.registerLienarLayout);
        login_lLinearLayout.addView(view);

        //        Check Shared Preferences part is existing
        SharedPreferences sp = getSharedPreferences("lk.sankaudeshika.androidfixerbee", Context.MODE_PRIVATE);


        // remember swithch on/Off
        Switch rememberMeSwitch = view.findViewById(R.id.loginRememberMeSwitch);
        if(sp.getString("switch",null) == null){
            Log.i("appout", "switch is null");
        }else if(sp.getString("switch",null).equals("true")){
            Log.i("appout", "switch is true");
            rememberMeSwitch.setChecked(true);
        }else if(sp.getString("switch",null).equals("false")){
            Log.i("appout", "switch is false");
            rememberMeSwitch.setChecked(false);
        }

//       set mobile numbet SP to text field
        String logedMobile = sp.getString("mobile", null);
        if (logedMobile == null) {
            Log.i("appout", "null");
        } else {
            Log.i("appout", logedMobile);
            EditText mobileText = view.findViewById(R.id.loginMobile);
            mobileText.setText(logedMobile);
        }

        //      Check if Register Activity has a value
        Intent i = getIntent();
        int message = i.getIntExtra("RegisterMessage", 0);
        if (message == 1) {
            new AlertDialog.Builder(LoginActivity.this)
                    .setTitle("Resgtration Complete")
                    .setMessage("Please Sign into you Account")
                    .show();
            SharedPreferences.Editor editor = sp.edit();
            editor.putString("mobile", logedMobile);
            editor.apply();
        }


//      Link Regsiter Activity
        TextView goRegisterText = view.findViewById(R.id.textView8);
        goRegisterText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(i);
            }
        });

//       Sign in Process
        Button loginButton = view.findViewById(R.id.loginButton);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View buttonView) {
                EditText mobile = view.findViewById(R.id.loginMobile);
                EditText password = view.findViewById(R.id.loginPassword);
                Switch rememberMeSwitch = view.findViewById(R.id.loginRememberMeSwitch);

//                Validation
                if (mobile.getText().toString().isEmpty()) {
                    Toast.makeText(LoginActivity.this, "Please Enter Your Mobile Number", Toast.LENGTH_SHORT).show();
                } else if (mobile.getText().toString().length() != 10) {
                    Toast.makeText(LoginActivity.this, "Mobile Number must have less than 10 characters", Toast.LENGTH_SHORT).show();
                } else if (!mobile.getText().toString().matches("^(?:\\+94|0)7[1245678]\\d{7}$")) {
                    Toast.makeText(LoginActivity.this, "Please Enter Valid Mobile Number", Toast.LENGTH_SHORT).show();
                } else if (password.getText().toString().isEmpty()) {
                    Toast.makeText(LoginActivity.this, "Please Enter a password", Toast.LENGTH_SHORT).show();
                } else {

                    FirebaseFirestore firestore = FirebaseFirestore.getInstance();
                    firestore.collection("user")
                            .whereEqualTo("mobile", mobile.getText().toString())
                            .whereEqualTo("password", password.getText().toString())
                            .get()
                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if (task.isSuccessful() && task.getResult().getDocuments().size() != 0) {

                                        List<DocumentSnapshot> documentList = task.getResult().getDocuments();

                                        for (DocumentSnapshot documentItem : documentList ) {
                                            SharedPreferences.Editor editor = sp.edit();
                                            editor.putString("Logged_mobile",String.valueOf(documentItem.getString("mobile")));
                                            editor.putString("Logged_user_id",documentItem.getId());
                                            if(rememberMeSwitch.isChecked() == true){
                                                Log.i("appout","Go Dashabord");
                                                editor.putString("mobile",String.valueOf(documentItem.get("mobile")));
                                                editor.putString("name",String.valueOf(documentItem.get("name")));
                                                editor.putString("status",String.valueOf(documentItem.get("status")));
                                                editor.putString("latitude",String.valueOf(documentItem.get("latitude")));
                                                editor.putString("longitude",String.valueOf(documentItem.get("longitude")));
                                                editor.putString("switch","true");
                                                editor.apply();

                                            }else if(rememberMeSwitch.isChecked() == false){
                                                sp.edit().remove("mobile").apply();
                                                sp.edit().remove("name").apply();
                                                sp.edit().remove("status").apply();
                                                sp.edit().remove("latitude").apply();
                                                sp.edit().remove("longitude").apply();
                                                editor.putString("switch","false").apply();

                                            }

//                                            SQL Insert Login
                                            new Thread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    try {
                                                        SimpleDateFormat simpleDate = new SimpleDateFormat("yyyy-MM-dd");
                                                        String currunt_date  = simpleDate.format(new Date());
                                                        SimpleDateFormat simpleTime = new SimpleDateFormat("HH:mm:ss");
                                                        String currunt_time = simpleTime.format(new Date());

                                                        SqlHelper sqlHelper = new SqlHelper(LoginActivity.this,"activity.db",null,1);
                                                        SQLiteDatabase sqLiteDatabase1 = sqlHelper.getWritableDatabase();
                                                        sqLiteDatabase1.execSQL("INSERT INTO `actions` (`action_name`,`action_date`,`action_time`) VALUES('User Login','"+currunt_date+"','"+currunt_time+"')");
                                                    } catch (Exception e) {
                                                        Log.i("appout", e.toString());
                                                        throw new RuntimeException(e);
                                                    }
                                                }
                                            }).start();

                                            if(String.valueOf(documentItem.get("status")).equals("active")){
                                                Log.i("appout","Go Dashabord");
                                                Intent intent = new Intent(LoginActivity.this,HomeActivity.class);
                                                startActivity(intent);

                                            }else if(String.valueOf(documentItem.get("status")).equals("deactive")){
                                                new AlertDialog.Builder(LoginActivity.this).setTitle("Your Account is Restriced,").setMessage("Please Try Contact Us and Get Solution for this").show();
                                            }

                                        }

                                    } else {
                                        new AlertDialog.Builder(LoginActivity.this).setTitle("Invalid User Details.").setMessage("Please Enter Valid User Details").show();
                                    }
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    new AlertDialog.Builder(LoginActivity.this).setTitle("Error").setMessage("Unable to do this Process");
                                }
                            });
                }

            }
        });


    }
}