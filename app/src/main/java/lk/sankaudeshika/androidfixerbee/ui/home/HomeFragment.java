package lk.sankaudeshika.androidfixerbee.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.models.SlideModel;

import java.util.ArrayList;

import lk.sankaudeshika.androidfixerbee.R;
import lk.sankaudeshika.androidfixerbee.ServiceSearchActivity;
import lk.sankaudeshika.androidfixerbee.databinding.FragmentHomeBinding;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();



//        set Image Sliders
        ImageSlider imageSlider = root.findViewById(R.id.ImageSlider);
        ArrayList<SlideModel> slideModels = new ArrayList<>();

        slideModels.add(new SlideModel(R.drawable.banner1, ScaleTypes.FIT));
        slideModels.add(new SlideModel(R.drawable.banner2, ScaleTypes.FIT));
        slideModels.add(new SlideModel(R.drawable.banner3, ScaleTypes.FIT));
        slideModels.add(new SlideModel(R.drawable.banner4, ScaleTypes.FIT));




        imageSlider.setImageList(slideModels,ScaleTypes.FIT);

//       Transer Search Service Activity
        EditText editText = root.findViewById(R.id.servicesMainSerch);
        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

                if (actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_GO ||
                        actionId == EditorInfo.IME_ACTION_SEND || actionId == EditorInfo.IME_ACTION_NEXT ||
                        (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {

//                    set Search
                    String text = editText.getText().toString();
                    Intent i = new Intent(root.getContext(), ServiceSearchActivity.class);
                    i.putExtra("SearchText",text);
                    startActivity(i);

                    return true;
                }
                return false;
            }
        });


        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}