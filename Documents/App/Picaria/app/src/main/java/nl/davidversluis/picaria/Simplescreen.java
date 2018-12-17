package nl.davidversluis.picaria;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class Simplescreen extends AppCompatActivity {
    private boolean player1AI = false;
    private boolean player2AI = false;
    private boolean forbidrepeat = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_simplescreen);
    }
    public void startgame(View view) {
        Intent intent = new Intent(this, Boardscreen.class);
        intent.putExtra("P1AI",player1AI);
        intent.putExtra("P2AI",player2AI);
        intent.putExtra("Forbidrepeat",forbidrepeat);
        intent.putExtra("Boardtype",1);
        startActivity(intent);
    }
    public void checkbox1click(View view)
    {
        player1AI = !player1AI;
    }

    public void checkbox2click(View view)
    {
        player2AI = !player2AI;
    }

    public void checkbox3click(View view)
    {
        forbidrepeat = !forbidrepeat;
    }
}
