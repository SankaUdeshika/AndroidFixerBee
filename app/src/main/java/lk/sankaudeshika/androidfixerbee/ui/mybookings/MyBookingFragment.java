package lk.sankaudeshika.androidfixerbee.ui.mybookings;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import lk.sankaudeshika.androidfixerbee.R;

import lk.sankaudeshika.androidfixerbee.ServiceSearchActivity;
import lk.sankaudeshika.androidfixerbee.ServiceViewActivity;
import lk.sankaudeshika.androidfixerbee.model.Books;
import lk.sankaudeshika.androidfixerbee.model.ServerURL;
import lk.sankaudeshika.androidfixerbee.model.Service;

public class MyBookingFragment extends Fragment {

    private  ArrayList<Books>IterateBooksArrayList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View inflateView =  inflater.inflate(R.layout.fragment_my_booking, container, false);

//        get Data
        SharedPreferences sp = getActivity().getSharedPreferences("lk.sankaudeshika.androidfixerbee", Context.MODE_PRIVATE);
        String customerMobile = sp.getString("Logged_mobile","null");

        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        firestore.collection("booking")
                .whereEqualTo("cusomier_id",customerMobile)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        List<DocumentSnapshot> documentList = queryDocumentSnapshots.getDocuments();
                        IterateBooksArrayList = new ArrayList<>();
                        for (DocumentSnapshot documentItem: documentList) {
                            Books books = new Books(
                                    documentItem.getString("description"),
                                    documentItem.getString("status"),
                                    documentItem.getString("time"),
                                    documentItem.getString("date"),
                                    documentItem.getString("vendor_id"),
                                    documentItem.getString("vendor_mobile")
                                    );

                            IterateBooksArrayList.add(books);
                        }
                        BookingAdapter bookingAdapter = new BookingAdapter(IterateBooksArrayList,inflateView.getContext() );
                        RecyclerView recyclerView = getActivity().findViewById(R.id.bookingStatusRecycleView);
                        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(inflater.getContext(),LinearLayoutManager.VERTICAL,false);
                        recyclerView.setLayoutManager(linearLayoutManager);

                        recyclerView.setAdapter(bookingAdapter);
                    }
                });

        return  inflateView;

    }

}



class BookingAdapter extends RecyclerView.Adapter<BookingAdapter.BookingViewHolder>{

    class BookingViewHolder extends RecyclerView.ViewHolder{
        TextView CompanyName;
        TextView vendorMobile;
        TextView bookdate;
        TextView booktime;
        TextView bookstatus;
        public BookingViewHolder(@NonNull View itemView){
            super(itemView);
            CompanyName =  itemView.findViewById(R.id.vendorCompany);
            vendorMobile = itemView.findViewById(R.id.vendorMobile);
            bookdate = itemView.findViewById(R.id.bookdate);
            booktime = itemView.findViewById(R.id.booktime);
            bookstatus = itemView.findViewById(R.id.bookstatus);
        }
    }

    ArrayList<Books> BookingArraylist;
    Context context;

    public BookingAdapter(ArrayList bookingArraylist1, Context context) {
        BookingArraylist = bookingArraylist1;
        this.context = context;
    }


    @NonNull
    @Override
    public BookingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View bookingView = layoutInflater.inflate(R.layout.booking_recycle_view,parent,false);
        BookingAdapter.BookingViewHolder bookingViewHolder = new BookingAdapter.BookingViewHolder(bookingView);
        return bookingViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull BookingViewHolder holder, int position) {
        Books books = BookingArraylist.get(position);
        holder.CompanyName.setText(books.getDescription());
        holder.vendorMobile.setText(books.getVendorMobile());
        holder.bookdate.setText(books.getDate());
        holder.booktime.setText(books.getTime());
        holder.bookstatus.setText(books.getStatus());
    }

    @Override
    public int getItemCount() {
        return BookingArraylist.size();
    }

}