package nl.davidversluis.picaria;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import java.util.ArrayList;
import java.util.List;

public class Boardscreen extends AppCompatActivity {

    private DrawView drawView;
    private Board board;
    private boolean player1AI;
    private boolean player2AI;
    private boolean forbidrepeat;

    private void init()
    {
        //Create an instance of the board class, which manages AI and board positions
        int boardtype = 1;
        List<Node> boardspace = new ArrayList<>();
        if(boardtype==1){ //Currently the only option
            for(double x=0;x<1.1;x=x+0.5) {
                for(double y=0;y<1.1;y=y+0.5){
                    boardspace.add(new Node(x,y));
                }
            }
            //make all connections between nodes
            for(int n=0;n<9;n=n+3) {
                for(int i=0;i<3;i++) {
                    int index = n+i;
                    boolean left = (n==0);
                    boolean right = (n==6);
                    boolean bottom = (i==2);
                    boolean top = (i==0);
                    if(!top){boardspace.get(index).setadjacent(boardspace.get(index-1));}
                    if(!bottom){boardspace.get(index).setadjacent(boardspace.get(index+1));}
                    if(!right){boardspace.get(index).setadjacent(boardspace.get(index+3));}
                    if(!left){boardspace.get(index).setadjacent(boardspace.get(index-3));}
                    if(!right && !bottom){boardspace.get(index).setadjacent(boardspace.get(index+4));}
                    if(!left && !bottom){boardspace.get(index).setadjacent(boardspace.get(index-2));}
                    if(!right && !top){boardspace.get(index).setadjacent(boardspace.get(index+2));}
                    if(!left && !top){boardspace.get(index).setadjacent(boardspace.get(index-4));}
                }
            }
        }
        boardspace.get(0).setadjacent(boardspace.get(1));
        this.board = new Board(1,player1AI,player2AI,forbidrepeat,boardspace);
        setContentView(R.layout.activity_boardscreen);
        ViewGroup layout = findViewById(R.id.boardscreen);

        //DrawView manages the touch events, sending them to the board, and chooses when to trigger the AI
        drawView = new DrawView(this,board,boardspace);
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
        //TODO different board options on previous screen
        init();
    }

    public void resetboard(View view) {
        init();
        this.drawView.invalidate();
    }

    public void skipturn(View view)
    {
        if(!drawView.winstate) {
            this.board.makeAImove();
            this.board.changeplayer();
            this.drawView.winstate = this.board.checkwin(this.board.boardspace);
            this.drawView.invalidate();
        }
    }
}