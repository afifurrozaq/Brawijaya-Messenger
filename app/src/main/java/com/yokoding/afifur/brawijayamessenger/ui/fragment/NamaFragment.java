package com.yokoding.afifur.brawijayamessenger.ui.fragment;

import android.app.AlarmManager;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.yokoding.afifur.brawijayamessenger.R;
import com.yokoding.afifur.brawijayamessenger.adapter.ToDoListAdapter;
import com.yokoding.afifur.brawijayamessenger.model.ToDoData;
import com.yokoding.afifur.brawijayamessenger.sqlite.SqliteHelper;
import com.yokoding.afifur.brawijayamessenger.ui.Activity.AlarmRecever;
import com.yokoding.afifur.brawijayamessenger.ui.Activity.MainActivity;

import java.util.ArrayList;
import java.util.Calendar;

import static android.content.Context.ALARM_SERVICE;
import static android.support.design.R.attr.layoutManager;

public class NamaFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    FloatingActionButton addTask;
    RecyclerView recyclerView;
    RecyclerView.Adapter adapter;
    RecyclerView.LayoutManager layoutManager;
    ArrayList<ToDoData> tdd = new ArrayList<>();
    SqliteHelper mysqlite;
    SwipeRefreshLayout swipeRefreshLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View a = inflater.inflate(R.layout.fragment_nama, container, false);
        recyclerView = (RecyclerView) a.findViewById(R.id.recycler_view_s);
        layoutManager = new LinearLayoutManager(getContext());
        addTask = (FloatingActionButton) a.findViewById(R.id.imageButton);
        swipeRefreshLayout = (SwipeRefreshLayout) a.findViewById(R.id.swiperefresh);
        adapter = new ToDoListAdapter(tdd, getContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.text_color_icon), getResources().getColor(R.color.divider));
        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                swipeRefreshLayout.setRefreshing(true);
                updateCardView();
            }
        });
        addTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addTodo();
            }
        });
        return a;
    }

    public void addTodo(){
        final Dialog dialog = new Dialog(getContext());
        dialog.setContentView(R.layout.custom_dailog);
        dialog.show();
        Button save = (Button) dialog.findViewById(R.id.btn_save);
        Button cancel = (Button) dialog.findViewById(R.id.btn_cancel);
        CheckBox cb = (CheckBox) dialog.findViewById(R.id.checkbox);
        TextView tvstatus = (TextView) dialog.findViewById(R.id.status);
        cb.setVisibility(View.GONE);
        tvstatus.setVisibility(View.GONE);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText todoText = (EditText) dialog.findViewById(R.id.input_task_desc);
                EditText todoNotes = (EditText) dialog.findViewById(R.id.input_task_notes);
                if (todoText.getText().length() >= 2) {
                    RadioGroup proritySelection = (RadioGroup) dialog.findViewById(R.id.toDoRG);
                    String RadioSelection = new String();
                    if (proritySelection.getCheckedRadioButtonId() != -1) {
                        int id = proritySelection.getCheckedRadioButtonId();
                        View radiobutton = proritySelection.findViewById(id);
                        int radioId = proritySelection.indexOfChild(radiobutton);
                        RadioButton btn = (RadioButton) proritySelection.getChildAt(radioId);
                        RadioSelection = (String) btn.getText();
                    }
                    Spinner getTime = (Spinner) dialog.findViewById(R.id.spinner);
                    EditText timeInNumb = (EditText) dialog.findViewById(R.id.input_task_time);
                    if(getTime.getSelectedItem().toString().matches("Days")) {
                        // Convert timeInNumb to Days in Miliseconds
                        int longtime = Integer.parseInt(timeInNumb.getText().toString());
                        long miliTime = longtime * 24 * 60 * 60 * 1000 ;
                        scheduleNotification(miliTime,todoText.getText().toString(),RadioSelection);
                    } else if (getTime.getSelectedItem().toString().matches("Minutes")) {
                        // Convert timeInNumb to Minutes in Miliseconds
                        int longtime = Integer.parseInt(timeInNumb.getText().toString());
                        long miliTime = longtime * 60 * 1000 ;
                        scheduleNotification(miliTime,todoText.getText().toString(),RadioSelection);
                    } else if (getTime.getSelectedItem().toString().matches("Hours")) {
                        // Convert timeInNumb to Hours in Miliseconds
                        int longtime = Integer.parseInt(timeInNumb.getText().toString());
                        long miliTime = longtime * 60 * 60 * 1000 ;
                        scheduleNotification(miliTime,todoText.getText().toString(),RadioSelection);
                    }
                    ContentValues contentValues = new ContentValues();
                    contentValues.put("ToDoTaskDetails", todoText.getText().toString());
                    contentValues.put("ToDoTaskPrority", RadioSelection);
                    contentValues.put("ToDoTaskStatus", "Incomplete");
                    contentValues.put("ToDoNotes", todoNotes.getText().toString());
                    mysqlite = new SqliteHelper(getContext());
                    Boolean b = mysqlite.insertInto(contentValues);
                    if (b) {
                        dialog.hide();
                        updateCardView();
                    } else {
                        Toast.makeText(getContext(), "Some thing went wrong", Toast.LENGTH_SHORT).show();
                    }

                } else {
                    Toast.makeText(getContext(), "Please enter To Do Task", Toast.LENGTH_SHORT).show();
                }


            }
        });

    }


    public void scheduleNotification(long time, String TaskTitle, String TaskPrority) {
        Calendar Calendar_Object = Calendar.getInstance();
        AlarmManager alarmManager = (AlarmManager) getContext().getSystemService(ALARM_SERVICE);
        final int _id = (int) System.currentTimeMillis();
        Intent myIntent = new Intent(getContext(), AlarmRecever.class);
        myIntent.putExtra("TaskTitle", TaskTitle);
        myIntent.putExtra("TaskPrority",TaskPrority);
        myIntent.putExtra("id",_id);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getContext(),
                _id, myIntent, PendingIntent.FLAG_ONE_SHOT);
        alarmManager.set(AlarmManager.RTC, Calendar_Object.getTimeInMillis() + time,
                pendingIntent);

    }



    public void updateCardView() {
        swipeRefreshLayout.setRefreshing(true);
        mysqlite = new SqliteHelper(getContext());
        Cursor result = mysqlite.selectAllData();
        if (result.getCount() == 0) {
            tdd.clear();
            adapter.notifyDataSetChanged();
            Toast.makeText(getContext(), "No Tasks", Toast.LENGTH_SHORT).show();
        } else {
            tdd.clear();
            adapter.notifyDataSetChanged();
            while (result.moveToNext()) {
                ToDoData tddObj = new ToDoData();
                tddObj.setToDoID(result.getInt(0));
                tddObj.setToDoTaskDetails(result.getString(1));
                tddObj.setToDoTaskPrority(result.getString(2));
                tddObj.setToDoTaskStatus(result.getString(3));
                tddObj.setToDoNotes(result.getString(4));
                tdd.add(tddObj);
            }
            adapter.notifyDataSetChanged();
        }
        swipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void onRefresh() {
        updateCardView();
    }

}
