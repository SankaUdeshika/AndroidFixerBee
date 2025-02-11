package lk.sankaudeshika.androidfixerbee;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

//        Loading PreLoader Page
        ProgressBar progressBar = findViewById(R.id.progressBar);

        new Thread(new Runnable() {


            int x=0;
            @Override
            public void run() {
                for( int i = 0;i<100; i++){
                    x = i;
                    try {
                      Thread.sleep(50);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            progressBar.setProgress(x,true);
                        }
                    });
                }
                Intent i = new Intent(MainActivity.this,LoginActivity.class);
                startActivity(i);
            }
        }).start();




    }
}