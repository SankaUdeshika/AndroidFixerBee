package lk.sankaudeshika.androidfixerbee;

import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.picasso.Picasso;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import lk.sankaudeshika.androidfixerbee.model.ServerURL;
import lk.sankaudeshika.androidfixerbee.model.Service;

public class ServiceSearchActivity extends AppCompatActivity {


    private ServiceAdapter serviceAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_service_search);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        //          get if Search Text Have Value
        Intent intent = getIntent();
        String searchText = intent.getExtras().getString("SearchText", "no_value");
        Log.i("appout", "search test is : "+searchText);

        if(!searchText.isEmpty()){
           EditText SearchBox = findViewById(R.id.servicesMainSerch);
           SearchBox.setText(searchText);
        }

//        Search again Button
        ImageButton searchButton = findViewById(R.id.searchButton);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText text = findViewById(R.id.servicesMainSerch);
                Intent i = new Intent(ServiceSearchActivity.this, ServiceSearchActivity.class);
                i.putExtra("SearchText",text.getText().toString());
                startActivity(i);
            }
        });

//        Back Button
        ImageButton backButton = findViewById(R.id.BackButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText text = findViewById(R.id.servicesMainSerch);
                Intent i = new Intent(ServiceSearchActivity.this, HomeActivity.class);
                i.putExtra("SearchText",text.getText().toString());
                startActivity(i);
            }
        });


//        load Recycle View
        EditText getCurruntSeartchText = findViewById(R.id.servicesMainSerch);
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        firestore.collection("vendor")
                .whereEqualTo("sub_category",getCurruntSeartchText.getText().toString())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        List<DocumentSnapshot> documentList = task.getResult().getDocuments();
                        ArrayList<Service> serviceList = new ArrayList<>() ;

                        for (DocumentSnapshot documentItem : documentList) {

//                            Service childService  = documentItem.toObject(Service.class);
                            Service childService = new Service();
                            childService.setId(documentItem.getId());
                            childService.setEmail(documentItem.getString("email"));
                            childService.setLocation(documentItem.getString("locaiton"));
                            childService.setMobile_1(documentItem.getString("mobile_1"));
                            childService.setMobile_2(documentItem.getString("mobile_2"));
                            childService.setSeller_company(documentItem.getString("seller_company"));
                            childService.setSeller_name(documentItem.getString("seller_name"));
                            childService.setStatus(documentItem.getString("status"));
                            childService.setSub_category(documentItem.getString("sub_category"));

                            serviceList.add(childService);

                        }
                        Log.i("appout", "ServiceAdapter: service list" + serviceList.size());

                        serviceAdapter = new ServiceAdapter(serviceList,ServiceSearchActivity.this);
                        RecyclerView recyclerView = findViewById(R.id.recycleView);
                        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(ServiceSearchActivity.this,LinearLayoutManager.VERTICAL,false);
                        recyclerView.setLayoutManager(linearLayoutManager);

                        recyclerView.setAdapter(serviceAdapter);

                    }
                });

    }
}




class ServiceAdapter extends RecyclerView.Adapter<ServiceAdapter.ServiceViewHolder>{

    class ServiceViewHolder extends RecyclerView.ViewHolder{
        TextView CompanyName;
        TextView CompanyMobile;
        TextView status;
        Button ViewServiceBtn;
        ImageView profileImageView;
        public ServiceViewHolder(@NonNull View itemView){
            super(itemView);
            CompanyName =  itemView.findViewById(R.id.textView14);
            CompanyMobile = itemView.findViewById(R.id.textView18);
            status = itemView.findViewById(R.id.textView20);
            ViewServiceBtn = itemView.findViewById(R.id.ViewServiceBtn);
            profileImageView = itemView.findViewById(R.id.profileImageView);
        }
    }

    ArrayList<Service> ServiceArraylist;
    Context context;

    public ServiceAdapter(ArrayList serviceArraylist1, Context context) {
        ServiceArraylist = serviceArraylist1;
        this.context = context;
    }

    @NonNull
    @Override
    public ServiceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View serviceView = layoutInflater.inflate(R.layout.service_item,parent,false);
        ServiceViewHolder serviceViewHolder = new ServiceViewHolder(serviceView);
        return serviceViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ServiceViewHolder holder, int position) {
        Service service = ServiceArraylist.get(position);
        holder.CompanyName.setText(service.getSeller_company());
        holder.CompanyMobile.setText(service.getMobile_1());
        holder.status.setText(service.getStatus());

        holder.ViewServiceBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(context,  service.getMobile_1(), Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(view.getContext(),ServiceViewActivity.class);
                intent.putExtra("CompanyMobile",service.getMobile_1());
                intent.putExtra("CompanyName",service.getSeller_company());
                ContextCompat.startActivity(context,intent,null);
            }
        });
        Picasso.get()
                .load(ServerURL.ServerImages+service.getId()+"seller_profileImage.jpg")
                .resize(500, 500)
                .centerCrop()
                .into(holder.profileImageView);
//        Log.i("appout", "onBindViewHolder: "+service.getMobile_1());
    }

    @Override
    public int getItemCount() {
        return ServiceArraylist.size();
    }
}
























