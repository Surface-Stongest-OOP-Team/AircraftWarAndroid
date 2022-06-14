package edu.hitsz.application;

import static edu.hitsz.application.MainActivity.userDao;
import static edu.hitsz.application.Settings.Difficulty.Casual;
import static edu.hitsz.application.Settings.Difficulty.Hard;
import static edu.hitsz.application.Settings.Difficulty.Medium;
import static edu.hitsz.application.Settings.difficulty;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;

import edu.hitsz.R;
import edu.hitsz.application.game.CasualMode;
import edu.hitsz.application.game.HardMode;
import edu.hitsz.application.game.MediumMode;
import edu.hitsz.user.User;

public class RankListActivity extends AppCompatActivity implements View.OnClickListener {
    private final int WC = ViewGroup.LayoutParams.WRAP_CONTENT;
    private final int FP = ViewGroup.LayoutParams.WRAP_CONTENT;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gradelist);
        Button button2=findViewById(R.id.button2);
        Button button3=findViewById(R.id.button3);
        button2.setOnClickListener(this);
        button3.setOnClickListener(this);
        createTable();
    }
    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.button2:
                System.out.println("A");
                break;
            case R.id.button3:
                System.out.println("B");
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
        System.out.println(userDao.getAllUsers().size());
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