package edu.hitsz.application;


import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.text.SimpleDateFormat;

import edu.hitsz.R;
import edu.hitsz.user.User;
import edu.hitsz.user.UserDao;
import edu.hitsz.user.UserDaoImpl;

public class RankListActivity extends AppCompatActivity implements View.OnClickListener {
    public static int deleteRow;
    private final int WC = ViewGroup.LayoutParams.WRAP_CONTENT;
    private final int FP = ViewGroup.LayoutParams.WRAP_CONTENT;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gradelist);
        Button deleteButton=findViewById(R.id.deleteButton);
        Button exitButton=findViewById(R.id.reButton);
        deleteButton.setOnClickListener(this);
        exitButton.setOnClickListener(this);
        EditText editText = findViewById(R.id.editTextNumber);
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }
            @Override
            public void afterTextChanged(Editable s) {
                deleteRow= Integer.parseInt(editText.getText().toString());
            }
        });
        createTable();
    }
    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.deleteButton:
                UserDao userDao=new UserDaoImpl(RankListActivity.this);
                if(deleteRow<=userDao.getAllUsers().size()&&deleteRow>=0){
                try {
                    userDao.deleteUser(deleteRow);
                } catch (IOException e) {
                    e.printStackTrace();
                }}
                Intent intent =new Intent(this,RankListActivity.class);
                startActivity(intent);
                break;
            case R.id.reButton:
                intent =new Intent(this,MainActivity.class);
                startActivity(intent);
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + view.getId());
        }
    }

    private void createTable(){
        TableLayout tableLayout = (TableLayout) findViewById(R.id.id_tableLayout);
        tableLayout.setStretchAllColumns(true);
        int i=0;
        SimpleDateFormat formatter= new SimpleDateFormat("yyyy-MM-dd 'at' HH:mm:ss z");
        UserDao userDao=new UserDaoImpl(RankListActivity.this);
        for(User user:userDao.getAllUsers()){
            TableRow tableRow = new TableRow(RankListActivity.this);
            tableRow.setBackgroundColor(Color.rgb(222,220,210));

            TextView textView = new TextView(RankListActivity.this);

            textView.setText(Integer.valueOf(i+1).toString());
            tableRow.addView(textView);

            textView = new TextView(RankListActivity.this);
            textView.setText(user.Name);
            System.out.println(user.Name);
            tableRow.addView(textView);

            textView = new TextView(RankListActivity.this);
            textView.setText(Integer.valueOf(user.score).toString());
            tableRow.addView(textView);

            textView = new TextView(RankListActivity.this);
            textView.setText(formatter.format(user.date));
            tableRow.addView(textView);

            tableLayout.addView(tableRow,new ViewGroup.LayoutParams(FP,WC));
            i++;
        }
    }
}