package lk.sankaudeshika.androidfixerbee;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import lk.sankaudeshika.androidfixerbee.model.SqlHelper;
import lk.sankaudeshika.androidfixerbee.ui.mybookings.MyBookingFragment;

public class BookNowActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_book_now);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
//        Get Intent Details
        Intent intent = getIntent();
        String Customer_mobile = intent.getStringExtra("Customer_mobile_1");
        String Customer_user_id = intent.getStringExtra("Logged_user_id");
        String vendor_id = intent.getStringExtra("vendor_id");
        String vendor_mobile = intent.getStringExtra("vendor_mobile");



//        Link Pickup Date Layout
        LayoutInflater inflater = LayoutInflater.from(BookNowActivity.this);
        View pickupDate_View = inflater.inflate(R.layout.pickup_date_view,null,false);

//        Link Pickup Time Layout
        View pickupTime_View = inflater.inflate(R.layout.pickup_time,null,false);


        //        Popup DateBtn
        Button dateBtn = findViewById(R.id.DateBtn);
        dateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(BookNowActivity.this).setView(pickupDate_View).show();
            }
        });

        //        Popup TimeBtn
        Button timebtn = findViewById(R.id.timebtn);
        timebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(BookNowActivity.this).setView(pickupTime_View).show();
            }
        });


        //        Firebase Book Now
        Button addBookingBtn =  findViewById(R.id.addBookingBtn);
        addBookingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // validation
                CalendarView calendarView = pickupDate_View.findViewById(R.id.calendarView);
                TimePicker Time = pickupTime_View.findViewById(R.id.pickupTime);
                EditText description = findViewById(R.id.descriptionText);
//
                long selectedDateInMillis = calendarView.getDate();
                int hour = Time.getHour();
                int minute = Time.getMinute();

                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

                String formattedDate = sdf.format(new Date(selectedDateInMillis));
                String formattedTime = String.format(Locale.getDefault(), "%02d:%02d", hour, minute);

                if(description.getText().toString().isEmpty()){
                    Toast.makeText(BookNowActivity.this, "Please Enter You desciption", Toast.LENGTH_SHORT).show();
                }else{
                    FirebaseFirestore firestore = FirebaseFirestore.getInstance();
                    HashMap<String,Object> BookingMap = new HashMap<>();

                    BookingMap.put("cusomier_id",Customer_mobile);
                    BookingMap.put("date",formattedDate);
                    BookingMap.put("description",description.getText().toString());
                    BookingMap.put("status","pending");
                    BookingMap.put("time",formattedTime);
                    BookingMap.put("vendor_id",vendor_id);
                    BookingMap.put("vendor_mobile",vendor_mobile);

                    firestore.collection("booking").add(BookingMap)
                            .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                @Override
                                public void onSuccess(DocumentReference documentReference) {
                                    MyBookingFragment myBookingFragment = new MyBookingFragment();

                                    //                                            SQL Insert Login
                                    new Thread(new Runnable() {
                                        @Override
                                        public void run() {
                                            try {
                                                SimpleDateFormat simpleDate = new SimpleDateFormat("yyyy-MM-dd");
                                                String currunt_date  = simpleDate.format(new Date());
                                                SimpleDateFormat simpleTime = new SimpleDateFormat("HH:mm:ss");
                                                String currunt_time = simpleTime.format(new Date());

                                                SqlHelper sqlHelper = new SqlHelper(BookNowActivity.this,"activity.db",null,1);
                                                SQLiteDatabase sqLiteDatabase1 = sqlHelper.getWritableDatabase();
                                                sqLiteDatabase1.execSQL("INSERT INTO `actions` (`action_name`,`action_date`,`action_time`) VALUES('User Booked','"+currunt_date+"','"+currunt_time+"')");

                                            } catch (Exception e) {
                                                Log.i("appout", e.toString());
                                                throw new RuntimeException(e);
                                            }
                                        }
                                    }).start();

                                    Intent intent1 = new Intent(BookNowActivity.this, HomeActivity.class);
                                    intent1.putExtra("bookingresult","Yes");
                                    startActivity(intent1);

                                }
                            })

                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    new AlertDialog.Builder(BookNowActivity.this).setTitle("Error").setMessage("Something Wrong, Please Try again later").show();
                                }
                            });



                }


            }
        });



    }
}