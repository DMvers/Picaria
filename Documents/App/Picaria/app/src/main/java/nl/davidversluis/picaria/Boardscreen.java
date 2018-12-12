package nl.davidversluis.picaria;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

public class Boardscreen extends AppCompatActivity {

    private DrawView drawView;
    private Board board;
    private boolean player1AI;
    private boolean player2AI;
    private boolean forbidrepeat;

    private void init()
    {
        //Create an instance of the board class, which manages AI and board positions
        this.board = new Board(3,3,player1AI,player2AI,forbidrepeat);
        setContentView(R.layout.activity_boardscreen);
        ViewGroup layout = (ViewGroup) findViewById(R.id.boardscreen);

        //DrawView manages the touch events, sending them to the board, and chooses when to trigger the AI
        drawView = new DrawView(this,board);
        drawView.setBackgroundColor(Color.LTGRAY);
        drawView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        layout.addView(drawView);
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.player1AI = getIntent().getExtras().getBoolean("P1AI");
        this.player2AI = getIntent().getExtras().getBoolean("P2AI");
        this.forbidrepeat = getIntent().getExtras().getBoolean("Forbidrepeat");
        init();
    }
    public void resetboard(View view) {
        init();
        this.drawView.invalidate();
    }
    public void skipturn(View view)
    {
        if(drawView.winstate == 'f') {
            this.board.determineAI();
            this.drawView.winstate = this.board.checkwin(this.board.multi);
            this.drawView.invalidate();
        }
    }
}