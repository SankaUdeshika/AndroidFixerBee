package lk.sankaudeshika.androidfixerbee.ui.myprofile;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import lk.sankaudeshika.androidfixerbee.R;
import lk.sankaudeshika.androidfixerbee.databinding.FragmentHomeBinding;
import lk.sankaudeshika.androidfixerbee.ui.home.HomeViewModel;


public class MyProfileFragment extends Fragment {


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_my_profile, container, false);




        return  view;


    }

}