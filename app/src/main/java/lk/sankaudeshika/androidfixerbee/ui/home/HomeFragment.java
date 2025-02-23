package lk.sankaudeshika.androidfixerbee.ui.home;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
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

import lk.sankaudeshika.androidfixerbee.LoginActivity;
import lk.sankaudeshika.androidfixerbee.R;
import lk.sankaudeshika.androidfixerbee.ServiceSearchActivity;
import lk.sankaudeshika.androidfixerbee.databinding.FragmentHomeBinding;
import lk.sankaudeshika.androidfixerbee.model.SqlHelper;

public class HomeFragment extends Fragment implements SensorEventListener {

    private FragmentHomeBinding binding;

    private SensorManager sensorManager;
    private Sensor accelerometer;
    private long lastShakeTime = 0;
    private static final long SHAKE_COOLDOWN = 2000;
    private static final float SHAKE_THRESHOLD = 10f;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

//        Sensor Initlaizd
        sensorManager = (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);
        if(sensorManager != null){
            accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        }

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
    public void onPause() {
        super.onPause();
        if(sensorManager != null){
            sensorManager.unregisterListener(this);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if(sensorManager != null && accelerometer !=null){
            sensorManager.registerListener(this,accelerometer,SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
        if(sensorManager != null){
            sensorManager.unregisterListener(this);
        }
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        if(sensorEvent.sensor.getType() == Sensor.TYPE_ACCELEROMETER){

            long curruntTime = System.currentTimeMillis();
            if((curruntTime - lastShakeTime) > SHAKE_COOLDOWN){
                float x = sensorEvent.values[0];
                float y = sensorEvent.values[1];
                float z = sensorEvent.values[2];

                float acceleration = (float) Math.sqrt(x * x + y * y + z * z) - SensorManager.GRAVITY_EARTH;

                if(acceleration > SHAKE_THRESHOLD){
                    Log.e("appout", "Goto Login");

                    lastShakeTime = curruntTime;

                    Intent intent = new Intent(getContext(),LoginActivity.class);
                    startActivity(intent);
                }

            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }
}