package lk.sankaudeshika.androidfixerbee;

import android.app.Activity;
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
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.UUID;

import lk.payhere.androidsdk.PHConfigs;
import lk.payhere.androidsdk.PHConstants;
import lk.payhere.androidsdk.PHMainActivity;
import lk.payhere.androidsdk.PHResponse;
import lk.payhere.androidsdk.model.InitRequest;
import lk.payhere.androidsdk.model.Item;
import lk.payhere.androidsdk.model.StatusResponse;
import lk.sankaudeshika.androidfixerbee.model.SqlHelper;
import lk.sankaudeshika.androidfixerbee.ui.mybookings.MyBookingFragment;

public class BookNowActivity extends AppCompatActivity {

    private static final int PAYHERE_REQUEST = 11010;
    private static final int ADVANCEDPAYMENT = 500;

      View pickupDate_View;
     View pickupTime_View;
    private String Customer_mobile;
    private String Customer_user_id;
    String vendor_id;
    String vendor_mobile;

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
         Customer_mobile = intent.getStringExtra("Customer_mobile_1");
         Customer_user_id = intent.getStringExtra("Logged_user_id");
         vendor_id = intent.getStringExtra("vendor_id");
         vendor_mobile = intent.getStringExtra("vendor_mobile");


//        Link Pickup Date Layout
         LayoutInflater inflater = LayoutInflater.from(BookNowActivity.this);
         pickupDate_View = inflater.inflate(R.layout.pickup_date_view, null, false);

//        Link Pickup Time Layout
          pickupTime_View = inflater.inflate(R.layout.pickup_time, null, false);


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
        Button addBookingBtn = findViewById(R.id.addBookingBtn);
        addBookingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Payhere Initialize
                try {
                    InitRequest req = new InitRequest();
                    req.setMerchantId("1221534");       // Merchant ID
                    req.setCurrency("LKR");             // Currency code LKR/USD/GBP/EUR/AUD
                    req.setAmount(ADVANCEDPAYMENT);             // Final Amount to be charged
                    req.setOrderId("230000123");        // Unique Reference ID
                    req.setItemsDescription("Door bell wireless");  // Item description title
                    req.setCustom1("This is the custom message 1");
                    req.setCustom2("This is the custom message 2");
                    req.getCustomer().setFirstName("Saman");
                    req.getCustomer().setLastName("Perera");
                    req.getCustomer().setEmail("samanp@gmail.com");
                    req.getCustomer().setPhone("+94771234567");
                    req.getCustomer().getAddress().setAddress("No.1, Galle Road");
                    req.getCustomer().getAddress().setCity("Colombo");
                    req.getCustomer().getAddress().setCountry("Sri Lanka");

//Optional Params
//                req.setNotifyUrl(“xxxx”);           // Notifiy Url
//                req.getCustomer().getDeliveryAddress().setAddress("No.2, Kandy Road");
//                req.getCustomer().getDeliveryAddress().setCity("Kadawatha");
//                req.getCustomer().getDeliveryAddress().setCountry("Sri Lanka");
//                req.getItems().add(new Item(null, "Door bell wireless", 1, 1000.0));


                    Intent intent = new Intent(BookNowActivity.this, PHMainActivity.class);
                    intent.putExtra(PHConstants.INTENT_EXTRA_DATA, req);
                    PHConfigs.setBaseUrl(PHConfigs.SANDBOX_URL);
                    startActivityForResult(intent, PAYHERE_REQUEST); //unique request ID e.g. "11001"


                } catch (Exception e) {
                    Log.d("appout", "onClick: " + e);
                    throw new RuntimeException(e);
                }

            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if (requestCode == PAYHERE_REQUEST && data != null && data.hasExtra(PHConstants.INTENT_EXTRA_RESULT)) {

            PHResponse<StatusResponse> response = null;
            if (requestCode == PAYHERE_REQUEST && data != null && data.hasExtra(PHConstants.INTENT_EXTRA_RESULT)) {
                response = (PHResponse<StatusResponse>) data.getSerializableExtra(PHConstants.INTENT_EXTRA_RESULT);
                if (resultCode == Activity.RESULT_OK) {

                    Log.i("appout", "ResultCode is = "+ resultCode);

                    //               Booking Now process
//               validation
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

                                        //  SQL Insert Login
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



                    // Inside the fragment:
                    Intent intent = new Intent(BookNowActivity.this, HomeActivity.class);
                    startActivity(intent);

                } else {
                    new AlertDialog.Builder(this).setTitle("cancel booking?").setMessage("if you wanna get this Service you have to pay 500 advance payment for this").show();
                    String msg = "Result: " + (response != null ? response.toString() : "no response");
                    Log.i("appout", msg);
                }
            } else if (resultCode == Activity.RESULT_CANCELED) {

                Log.i("appout", response.toString());
            }
        }

    }
}