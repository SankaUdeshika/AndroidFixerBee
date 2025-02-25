package lk.sankaudeshika.androidfixerbee.ui.home;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.models.SlideModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

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

//        Video 1 Load
        VideoView videoView1 = root.findViewById(R.id.videoView1);
        MediaController mediaController = new MediaController(root.getContext());
        mediaController.setMediaPlayer(videoView1);
        mediaController.setAnchorView(videoView1);

        videoView1.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                Log.i("appout", "onCompletion: Video1 added");
            }
        });

        Uri uri = Uri.parse("https://fitnessfirst.lk/Resources/Videos/Cleaning.mp4");
        videoView1.setVideoURI(uri);
        videoView1.start();



//        Sensor Initlaizd
        sensorManager = (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);
        if(sensorManager != null){
            accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        }

//        set Image Sliders
        ImageSlider imageSlider = root.findViewById(R.id.ImageSlider);
        ArrayList<SlideModel> slideModels = new ArrayList<>();

        FirebaseFirestore firestore  = FirebaseFirestore.getInstance();
        firestore.collection("cardimages").get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                String banner1 ;
                                String banner2 ;
                                String banner3 ;
                                String banner4 ;

                                List<DocumentSnapshot> documentSnapshots = task.getResult().getDocuments();
                                for (DocumentSnapshot documentItem: documentSnapshots) {
                                  banner1=  documentItem.getString("banner1");
                                  banner2= documentItem.getString("banner2");
                                  banner3= documentItem.getString("banner3");
                                  banner4= documentItem.getString("banner4");
                                }
                            }
                        });

        slideModels.add(new SlideModel(R.drawable.banner2, ScaleTypes.FIT));
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