package lk.sankaudeshika.androidfixerbee;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;

public class ServiceViewActivity extends AppCompatActivity {

    String vendor_id;
    String mobile ;
    double locaiton_latitude;
    double locaiton_longitude;
    LatLng latlanObject = new LatLng(locaiton_latitude,locaiton_longitude);



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_service_view);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        //    set Static Text
        TextView service_email = findViewById(R.id.textView32);
        TextView service_locaiton =findViewById(R.id.textView33);
        TextView service_mobile_1 =findViewById(R.id.textView34);
        TextView service_mobile_2 =findViewById(R.id.textView35);
        TextView service_seller_company =findViewById(R.id.textView37);
        TextView service_seller_name =findViewById(R.id.textView38);
        TextView service_status =findViewById(R.id.textView36);
        TextView service_sub_category =findViewById(R.id.textView39);


//        get Intent Details
        Intent i = getIntent();
        String Coompan_mobile = i.getStringExtra("CompanyMobile");
        String CompanyName = i.getStringExtra("CompanyName");

//        Search Seller Firebase
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        firestore.collection("vendor")
                .whereEqualTo("mobile_1",Coompan_mobile)
                .whereEqualTo("seller_company",CompanyName)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        List<DocumentSnapshot> documentSnapshots = task.getResult().getDocuments();

                        for (DocumentSnapshot documentItem: documentSnapshots ) {
                               vendor_id = documentItem.getId();
                               service_mobile_1.setText(documentItem.getString("mobile_1"));
                               mobile = documentItem.getString("mobile_1");
                               service_mobile_2.setText(documentItem.getString("mobile_2"));
                               service_seller_company.setText(documentItem.getString("seller_company"));
                               service_seller_name.setText(documentItem.getString("seller_name"));
                               service_status.setText(documentItem.getString("status"));
                               service_sub_category.setText(documentItem.getString("sub_category"));
                               service_email.setText(documentItem.getString("email"));
                               service_locaiton.setText(documentItem.getString("locaiton"));
                               locaiton_latitude= Double.parseDouble(documentItem.getString("locaiton_latitude")) ;
                               locaiton_longitude=Double.parseDouble(documentItem.getString("locaiton_longitude"));
                               Log.i("appout",String.valueOf(locaiton_longitude));
                        }

                    }
                });


        SupportMapFragment supportMapFragment = new SupportMapFragment();
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.googleMapView,supportMapFragment);
        fragmentTransaction.commit();

//        MakeCall Btn
        Button callBtn = findViewById(R.id.callbtn);
        callBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(checkSelfPermission(Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED){
                    Log.i("appout", "onCreate: Permission yes");
                    Intent i = new Intent(Intent.ACTION_CALL);
                    Uri uri = Uri.parse("tel:"+mobile);
                    i.setData(uri);
                    startActivity(i);
                }else{

                    String permissionArray [] = new String[1];
                    permissionArray[0] = Manifest.permission.CALL_PHONE;
                    requestPermissions(permissionArray,100);
                }
            }
        });

//        Send BookNow Activity
        Button BookNowBtn = findViewById(R.id.BookNow);
        BookNowBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences sp = getSharedPreferences("lk.sankaudeshika.androidfixerbee", Context.MODE_PRIVATE);
                String LoggedUser_id = sp.getString("Logged_user_id","null");
                Intent i  = new Intent(ServiceViewActivity.this,BookNowActivity.class);
                i.putExtra("Customer_mobile_1",Coompan_mobile);
                i.putExtra("Logged_user_id",LoggedUser_id);
                i.putExtra("vendor_id",vendor_id);
                i.putExtra("vendor_mobile",mobile);

                startActivity(i);
            }
        });





        supportMapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(@NonNull GoogleMap googleMap) {
//                zoom locaiton
                googleMap
                        .animateCamera(CameraUpdateFactory
                                .newCameraPosition(
                                        new CameraPosition.Builder()
                                        .target(new LatLng(locaiton_latitude,locaiton_longitude))
                                                .zoom(15)
                                                .build()));

//                set Marker
                googleMap.addMarker(
                        new MarkerOptions()
                                .position(new LatLng(locaiton_latitude,locaiton_longitude))
                                .title(CompanyName)
                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.location_icon))
                );
            }
        });

    }
    // Handle Permission Request Result
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 100) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.i("appout", "Permission granted");

            } else {
                Log.i("appout", "Permission denied");
            }
        }
    }

}