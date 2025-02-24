package lk.sankaudeshika.androidfixerbee.ui.myactivity;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import lk.sankaudeshika.androidfixerbee.R;
import lk.sankaudeshika.androidfixerbee.model.Action;
import lk.sankaudeshika.androidfixerbee.model.SqlHelper;


public class ActionActivityFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View ActionView = inflater.inflate(R.layout.fragment_action_activity, container, false);

        //        Get Action From SQLite
        SqlHelper sqlHelper = new SqlHelper(ActionView.getContext(), "activity.db",null,1);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    SQLiteDatabase sqLiteDatabase = sqlHelper.getReadableDatabase();
                    Cursor cursor = sqLiteDatabase.rawQuery("SELECT * FROM `actions`",new String[]{});

                    ArrayList<Action> actionArrayList = new ArrayList<>();

                    while(cursor.moveToNext()){
                        actionArrayList.add(new Action(cursor.getString(1),cursor.getString(2),cursor.getString(3)));
                        Log.d("appout", "run: "+cursor.getString(1)+" "+ cursor.getString(0));
                    }

                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            RecyclerView recyclerView = ActionView.findViewById(R.id.ActionRecycleView);
                            recyclerView.setLayoutManager(new LinearLayoutManager(ActionView.getContext(),LinearLayoutManager.VERTICAL,false));
                            recyclerView.setAdapter(new ActionAdapter(actionArrayList));
                        }
                    });



                } catch (Exception e) {
                    Log.e("appout", e.toString());
                    throw new RuntimeException(e);
                }
            }
        }).start();

        return  ActionView;
    }
}



class ActionAdapter extends RecyclerView.Adapter<ActionAdapter.ActionViewHolder>{


    class ActionViewHolder extends RecyclerView.ViewHolder{

        TextView actionName;
        TextView actionDate;
        TextView actiionTime;

        public ActionViewHolder(@NonNull View itemView) {
            super(itemView);
            actionName = itemView.findViewById(R.id.ActionNameText);
            actionDate = itemView.findViewById(R.id.ActionDate);
            actiionTime = itemView.findViewById(R.id.ActionTime);
        }
    }

    ArrayList<Action> actionArrayList = new ArrayList<>();

    public ActionAdapter (ArrayList<Action> actionArrayList){
        this.actionArrayList = actionArrayList;
    }


    @NonNull
    @Override
    public ActionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View ActionLayouView = layoutInflater.inflate(R.layout.new_action_activity_view,null,false);
        ActionViewHolder actionViewHolder = new ActionViewHolder(ActionLayouView);

        return actionViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ActionViewHolder holder, int position) {
        Action action = actionArrayList.get(position);
        holder.actionName.setText(action.getActionName());
        holder.actionDate.setText(action.getActionDate());
        holder.actiionTime.setText(action.getActinTime());
    }

    @Override
    public int getItemCount() {
        return actionArrayList.size();
    }
}