package com.mlw.ccera;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    final  List<String> list = new ArrayList<>();

    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final ListView listView = findViewById(R.id.listView);
        final  TextAdapter adapter = new TextAdapter();

        readInfo();

        adapter.setData(list);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick (AdapterView<?> parent, View view, final int position, long id) {
                AlertDialog dialog = new AlertDialog.Builder(MainActivity.this)
                                    .setTitle("Delete")
                                    .setMessage("You won't be able to undo this!")
                                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick (DialogInterface dialog, int which) {
                                            list.remove(position);
                                            adapter.setData(list);
                                            saveInfo();
                                        }
                                    })
                                    .setNegativeButton("No", null)
                                    .create();

                dialog.show();
            }
        });

        final Button newTaskButton = findViewById(R.id.newTaskButton);

        newTaskButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View v) {
                final EditText taskInput = new EditText(MainActivity.this);

                taskInput.setSingleLine();

                AlertDialog dialog = new AlertDialog.Builder(MainActivity.this)
                                    .setTitle("Add a new task")
                                    .setMessage("What is your new task?")
                                    .setView(taskInput)
                                    .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick (DialogInterface dialog, int which) {
                                        list.add(taskInput.getText().toString());
                                        adapter.setData(list);
                                        saveInfo();
                                    }
                })
                .setNegativeButton("Cancel", null)
                .create();

                dialog.show();
            }
        });


        final  Button deleteAllTasksButton = findViewById(R.id.deleteAllTasksButton);

        deleteAllTasksButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View v) {
                AlertDialog dialog = new AlertDialog.Builder(MainActivity.this)
                                    .setTitle("Delete All")
                                    .setMessage("Are you sure you want to delete all tasks?")
                                    .setPositiveButton("Delete All", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick (DialogInterface dialog, int which) {
                                            list.clear();
                                            adapter.setData(list);
                                            saveInfo();
                                        }
                                    })
                                    .setNegativeButton("Cancel", null)
                                    .create();

                dialog.show();
            }
        });
    }

/*    @Override
    protected void onPause(){
        super.onPause();
        saveInfo();
    }*/

    private void saveInfo(){
        try {
            File file = new File(this.getFilesDir(), "saved");

            FileOutputStream fOut = new FileOutputStream(file);
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fOut));

            for (int i = 0; i < list.size(); i++){
                bw.write(list.get(i));
                bw.newLine();
            }

            bw.close();
            fOut.close();

        } catch (Exception e){
            e.printStackTrace();
        }
    }


    private  void readInfo(){
        File file = new File(this.getFilesDir(), "saved");

        if (! file.exists()){
            return;
        }

        try {
            FileInputStream is = new FileInputStream(file);
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            String line = reader.readLine();

            while (line != null){
                list.add(line);
                line = reader.readLine();
            }

        } catch (Exception e){
            e.printStackTrace();
        }
    }

    class TextAdapter extends BaseAdapter {

        List<String> list = new ArrayList<>();


        // update data
        void setData(List<String> mList){
            list.clear();
            list.addAll(mList);

            notifyDataSetChanged();
        }

        @Override
        public int getCount(){
            return  list.size();
        }

        @Override
        public Object getItem(int position){
            return null;
        }

        @Override
        public long getItemId(int position){
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent){

            LayoutInflater inflater;
            inflater = (LayoutInflater)
                    MainActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            assert inflater != null;
            @SuppressLint("ViewHolder") View rowView  = inflater.inflate(R.layout.item, parent, false);

            final TextView textView = rowView.findViewById(R.id.task);

            final String cellContent = textView.getText().toString();

            Log.d("Error", cellContent);

            if (cellContent.startsWith("important")){
                textView.setBackgroundColor(Color.RED);
                textView.setTextColor(Color.BLACK);
            }else if(position % 2 == 0){
                textView.setBackgroundColor(Color.GRAY);
                textView.setTextColor(Color.WHITE);
            }else{
                textView.setBackgroundColor(Color.WHITE);
                textView.setTextColor(Color.BLACK);
            }

            textView.setText(list.get(position));
            return rowView;
        }
    }
}