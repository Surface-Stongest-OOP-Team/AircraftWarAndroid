package edu.hitsz.application;

import static edu.hitsz.application.Settings.Difficulty.Casual;
import static edu.hitsz.application.Settings.Difficulty.Hard;
import static edu.hitsz.application.Settings.Difficulty.Medium;
import static edu.hitsz.application.Settings.difficulty;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import edu.hitsz.R;
import edu.hitsz.application.game.CasualMode;
import edu.hitsz.application.game.HardMode;
import edu.hitsz.application.game.MediumMode;

public class RankListActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gradelist);
        Button button2=findViewById(R.id.button2);
        Button button3=findViewById(R.id.button3);
        button2.setOnClickListener(this);
        button3.setOnClickListener(this);
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
}