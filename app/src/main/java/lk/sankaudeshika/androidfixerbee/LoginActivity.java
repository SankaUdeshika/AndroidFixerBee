package lk.sankaudeshika.androidfixerbee;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

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
        View view = getLayoutInflater().inflate(R.layout.login_sub_component,null);
        LinearLayout login_lLinearLayout =  findViewById(R.id.registerLienarLayout);
        login_lLinearLayout.addView(view);

        //        Check Shared Preferences part is existing
        SharedPreferences sp = getSharedPreferences("lk.sankaudeshika.androidfixerbee", Context.MODE_PRIVATE);

        String logedMobile = sp.getString("mobile",null);
        if(logedMobile == null){
            Log.i("appout", "null");
        }else{
            Log.i("appout", logedMobile);
           EditText mobileText =  view.findViewById(R.id.loginMobile);
           mobileText.setText(logedMobile);
        }

        //      Check if Register Activity has a value
        Intent i = getIntent();
        int message = i.getIntExtra("RegisterMessage",0);
        if(message == 1){
            new  AlertDialog.Builder(LoginActivity.this)
                    .setTitle("Resgtration Complete")
                    .setMessage("Please Sign into you Account")
                    .show();
            SharedPreferences.Editor editor = sp.edit();
            editor.putString("mobile","0764213724");
            editor.apply();
        }


//      Link Regsiter Activity
        TextView goRegisterText = findViewById(R.id.textView8);
        goRegisterText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(LoginActivity.this,RegisterActivity.class);
                startActivity(i);
            }
        });




    }
}