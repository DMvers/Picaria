package nl.davidversluis.picaria;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.v4.content.ContextCompat;
import android.view.MotionEvent;
import android.view.View;

import java.util.List;

import nl.davidversluis.picaria.Board;

@SuppressLint("ViewConstructor")
public class DrawView extends View {
    private final Paint boardpaint = new Paint();
    private final Paint squarepaint = new Paint();
    private final Paint transpaint = new Paint();
    private final Paint nodepaint = new Paint();
    private Bitmap diamond;
    private Bitmap transdiamond;
    private int width;
    private int firstline;
    private int height;
    private int gapsize;
    private int padding;
    private int smallpadding;
    boolean winstate = false;

    private final Board board;
    private List boardspace;


    private void init() {
        boardpaint.setColor(Color.BLACK);
        boardpaint.setStyle(Paint.Style.STROKE);
        boardpaint.setStrokeWidth(smallpadding/3);
        boardpaint.setTextSize(padding + 2*smallpadding);
        boardpaint.setAntiAlias(true);

        squarepaint.setColor(ContextCompat.getColor(getContext(),R.color.customblue));
        squarepaint.setStyle(Paint.Style.FILL);
        int linewidth = 15;
        squarepaint.setStrokeWidth(linewidth);

        transpaint.setColor(ContextCompat.getColor(getContext(),R.color.translucentblue));
        transpaint.setStyle(Paint.Style.FILL);
        transpaint.setStrokeWidth(linewidth);

        nodepaint.setColor(Color.BLACK);
        nodepaint.setStyle(Paint.Style.FILL);
        nodepaint.setStrokeWidth(linewidth);
    }

    public DrawView(Context context,Board newboard,List boardspace) {
        super(context);
        board = newboard;
        this.boardspace = boardspace;
        width = Resources.getSystem().getDisplayMetrics().widthPixels;
        height = Resources.getSystem().getDisplayMetrics().heightPixels;
        firstline = height / 6;
        padding = width/15;
        smallpadding = width/50;
        gapsize = (width / 2)-padding;
        init();

    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {

        width = Resources.getSystem().getDisplayMetrics().widthPixels;
        height = Resources.getSystem().getDisplayMetrics().heightPixels;
        firstline = height / 6;
        padding = width/15;
        smallpadding = width/50;
        gapsize = (width / 2)-padding;

        diamond = BitmapFactory.decodeResource(getResources(),R.drawable.yellowdiamond);
        diamond = Bitmap.createScaledBitmap(diamond,padding+ 4*smallpadding,padding+4*smallpadding,false);

        //Load translucent diamond
        transdiamond =BitmapFactory.decodeResource(getResources(),R.drawable.translucentdiamond);
        transdiamond = Bitmap.createScaledBitmap(diamond,padding+ 4*smallpadding,padding+4*smallpadding,false);
        transdiamond.setHasAlpha(true);


    }


    @Override
    public boolean onTouchEvent(MotionEvent event){
        if(winstate) //game ended
            return super.onTouchEvent(event);
        double locx = event.getX();
        double locy = event.getY();
        if (locy > (firstline + 2 * gapsize + padding)|| locy < (firstline-padding)) //Below the bottom of the board or above it, i.e. out of bounds
        {
            return super.onTouchEvent(event);
        }
        double xpart = locx /(double)width;
        double ypart = (locy - (double)firstline) / (double)(gapsize*2 );
        boolean needAI;
        needAI = board.touched(xpart,ypart); //This also changes the board itself, if the move is valid
        winstate = board.checkwin(boardspace);
        invalidate();
        if(needAI) {
            board.determineAI();
        }
        winstate = board.checkwin(boardspace);
        invalidate();
        return super.onTouchEvent(event);
    }


    @Override
    public void onDraw(Canvas canvas) {
        this.boardspace = board.boardspace;
        //Draw grid
        for (int i = 0; i < 3; i++) {
            canvas.drawLine(padding + gapsize * i, firstline, padding + gapsize * i, firstline + 2 * gapsize, boardpaint);
            canvas.drawLine(padding, firstline + gapsize * i, padding + 2 * gapsize, firstline + gapsize * i, boardpaint);
        }
        //Draw major diagonals
        canvas.drawLine(padding, firstline, padding + gapsize * 2, firstline + 2 * gapsize, boardpaint);
        canvas.drawLine(padding + gapsize * 2, firstline, padding, firstline + 2 * gapsize, boardpaint);

        //Draw smaller diagononals
        canvas.drawLine(padding + gapsize, firstline, padding, firstline + gapsize, boardpaint);
        canvas.drawLine(padding + gapsize, firstline, padding + 2 * gapsize, firstline + gapsize, boardpaint);
        canvas.drawLine(padding + 2 * gapsize, firstline + gapsize, padding + gapsize, firstline + 2 * gapsize, boardpaint);
        canvas.drawLine(padding, firstline + gapsize, padding + gapsize, firstline + 2 * gapsize, boardpaint);

        for (int x = 0; x < 3; x++) {
            for (int y = 0; y < 3; y++) {
                //always draw circles on intersections
                canvas.drawOval(padding / 2 + gapsize * x, firstline - padding / 2 + gapsize * y, padding + padding / 2 + gapsize * x, firstline + padding / 2 + gapsize * y, nodepaint);
            }
        }

        //Draw the right token on each node
        for (Object element : boardspace) {
            Node nod = (Node) element;
            int token = nod.getmark();
            double x = nod.xloc*2;
            double y = nod.yloc*2;
            switch (token) {
                //Filled diamond
                case 1:
                    canvas.drawBitmap(diamond,(int)(padding / 2 + gapsize * x - 2 * smallpadding),(int)(firstline - padding / 2 + gapsize * y - 2 * smallpadding), nodepaint);
                    break;
                //Filled square
                case 2:
                    canvas.drawRect((int)(padding / 2 + gapsize * x - smallpadding), (int)(firstline - padding / 2 + gapsize * y - smallpadding), (int)(padding + padding / 2 + gapsize * x + smallpadding),(int)( firstline + padding / 2 + gapsize * y + smallpadding), squarepaint);
                    break;
                //Translucent diamond (potential move)
                case 3:
                    canvas.drawBitmap(transdiamond, (int)(padding / 2 + gapsize * x - 2 * smallpadding),(int)( firstline - padding / 2 + gapsize * y - 2 * smallpadding), transpaint);
                    break;
                //Translucent square (potential move)
                case 4:
                    canvas.drawRect((int)(padding / 2 + gapsize * x - smallpadding),(int)( firstline - padding / 2 + gapsize * y - smallpadding),(int)( padding + padding / 2 + gapsize * x + smallpadding),(int)( firstline + padding / 2 + gapsize * y + smallpadding), transpaint);
            }

        }

        boolean playersturn = board.player;
        boolean phase = board.placingphase;
        if(winstate)
        {
            if(playersturn)
                canvas.drawText("Diamonds have won",padding/2,padding*2,boardpaint);
            else
                canvas.drawText("Squares have won",padding/2,padding*2,boardpaint);
        }
        else {
            if (phase) {
                if (playersturn)
                    canvas.drawText("Squares to place", padding/2, padding * 2, boardpaint);
                else
                    canvas.drawText("Diamonds to place", padding/2, padding * 2, boardpaint);

            } else {
                if (playersturn)
                    canvas.drawText("Squares to move", padding/2, padding * 2, boardpaint);
                else
                    canvas.drawText("Diamonds to move", padding/2, padding * 2, boardpaint);
            }
        }

    }

}