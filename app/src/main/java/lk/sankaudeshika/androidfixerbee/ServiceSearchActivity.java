package lk.sankaudeshika.androidfixerbee;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
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

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import lk.sankaudeshika.androidfixerbee.model.Service;

public class ServiceSearchActivity extends AppCompatActivity {

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

                            Service childService  = documentItem.toObject(Service.class);
                            serviceList.add(childService);
//                            Log.i("appout", "onComplete: "+documentItem.getString("locaiton"));
                            Log.i("appout", "onComplete: "+childService.getLocation());

                        }

                        RecyclerView recyclerView = findViewById(R.id.recycleView);
                        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(ServiceSearchActivity.this,LinearLayoutManager.VERTICAL,false);
                        recyclerView.setLayoutManager(linearLayoutManager);
                        recyclerView.setAdapter(new ServiceAdapter(serviceList, ServiceSearchActivity.this));

                    }
                });

    }
}




class ServiceAdapter extends RecyclerView.Adapter<ServiceAdapter.ServiceViewHolder>{

    class ServiceViewHolder extends RecyclerView.ViewHolder{
        TextView CompanyName;
        TextView CompanyAddress;
        TextView CompanyMobile;
        TextView status;
        Button ViewServiceBtn;


        public ServiceViewHolder(@NonNull View itemView){
            super(itemView);
            CompanyName =  itemView.findViewById(R.id.textView14);
            CompanyAddress = itemView.findViewById(R.id.textView21);
            CompanyMobile = itemView.findViewById(R.id.textView18);
            status = itemView.findViewById(R.id.textView20);
            ViewServiceBtn = itemView.findViewById(R.id.ViewServiceBtn);
        }

    }

    ArrayList<Service> ServiceArraylist;
    Context context;

    public ServiceAdapter(ArrayList serviceArraylist, Context context) {
        ServiceArraylist = serviceArraylist;
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
        holder.CompanyAddress.setText(service.getLocation());
        holder.CompanyMobile.setText(service.getMobile_1());
        holder.status.setText(service.getStatus());

        holder.ViewServiceBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(context,  service.getMobile_1(), Toast.LENGTH_SHORT).show();
//                Intent intent = new Intent(context.this,)

            }
        });
    }

    @Override
    public int getItemCount() {
        return ServiceArraylist.size();
    }
}
























