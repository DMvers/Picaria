package nl.davidversluis.picaria;

import java.util.Random;


class Board {
    int [][] multi;
    boolean player;
    boolean placingphase; //placing or moving
    private int piecesplaced;
    private final boolean play1AI;
    private final boolean play2AI;
    private final boolean forbidrepeat;
    private int selectedx;
    private int selectedy;
    private int player1lastx = 4;
    private int player1lasty = 4;
    private int player2lastx = 4;
    private int player2lasty = 4;
    //public Set nodes;

    Board(int xsqrs, int ysqrs, boolean play1AI, boolean play2AI, boolean forbidrepeat)
    {
        this.multi = new int[xsqrs][ysqrs];
        this.placingphase = true;
        this.piecesplaced = 0;
        this.play1AI = play1AI;
        this.play2AI = play2AI;
        this.forbidrepeat = forbidrepeat;
        if(play1AI) {
            makeAImove();
            changeplayer();
        }

        /*
        this.nodes = new HashSet();
        for(int x=0;x<3;x++) {
            for(int y=0;y<3;y++){
                nodes.add(new Node(x,y));
            }
        }
        this.xsqrs = ysqrs;
        this.ysqrs = xsqrs;
        this.player = true;
        this.multiplayer = multiplayer;
        */
    }

    int reportsquare(int x, int y)
    {

        return multi[x][y];
    }

    private void marksquare(int x, int y, int mark)
    {
        multi[x][y] = mark;
        //if(this.multiplayer)
    }

    boolean touched(int x, int y) {
        int playermark = 1 + (player ? 1 : 0); //Calculate mark from current player state
        if (placingphase) {
            if (reportsquare(x, y) == 0) {
                marksquare(x, y, playermark);
                piecesplaced += 1;
                if (piecesplaced >= 6) {
                    placingphase = false;
                }
                changeplayer();
            } else
                return false;
        } else {
            int squarecontent = reportsquare(x, y);
            if (squarecontent == playermark) {
                multi = resettemp(multi);
                this.multi = markadjacentempty(x, y, multi, playermark + 2);
                selectedx = x;
                selectedy = y;
                return false;
            }
            if (squarecontent == playermark + 2) {
                marksquare(x, y, playermark);
                marksquare(selectedx, selectedy, 0);
                if(player)
                {
                    player1lastx = selectedx;
                    player1lasty = selectedy;
                }
                else
                {
                    player2lastx = selectedx;
                    player2lasty = selectedy;
                }
                changeplayer();
            } else
                return false;
            multi = resettemp(multi);
        }
        return checkwin(multi) != 'w';
    }

    void determineAI()
    {
        if(play1AI)
        {
            makeAImove();

            changeplayer();
            if(checkwin(multi)=='w')
                return;

        }
        if(play2AI)
        {
            makeAImove();

            changeplayer();
            //if(checkwin(multi)=='w')
                //return;
        }

    }

    private int[][] markadjacentempty(int x, int y, int[][] tempmulti, int mark)
    {
        int forbiddenx;
        int forbiddeny;
        int relevantplayer = tempmulti[x][y];
        if(relevantplayer == 2)
        {
            forbiddenx = player1lastx;
            forbiddeny = player1lasty;
        }
        else
        {
            forbiddenx = player2lastx;
            forbiddeny = player2lasty;
        }
        for(int i=-1;i<2;i++)
        {
            for(int n=-1;n<2;n++)
            {
                if(i==0 && n==0)  
                {
                    continue;
                }
                int newx = x+i;
                int newy = y+n;
                if(newx>=0&&newy>=0&&newx<3&&newy<3) {
                    if (reportsquare(newx, newy) == 0) {
                        if(forbidrepeat) {
                            if ((newx != forbiddenx) || (newy != forbiddeny)) {
                                tempmulti[newx][newy] = mark;
                            }
                        }
                        else
                        {
                            tempmulti[newx][newy] = mark;
                        }
                    }
                }

            }
        }
        return tempmulti;
    }

    private int[][] resettemp(int[][] multi)
    {
        for(int x=0;x<3;x++)
        {
            for(int y=0;y<3;y++)
            {
                int squarecontent = reportsquare(x,y);
                if(squarecontent >2)
                {
                    multi[x][y] =0;
                }
            }
        }
        return multi;
    }



    private void makeAImove()
    {
        int playermark = 1 + (player ? 1 : 0); //Calculate mark from current player state
        int [][] tempboard = new int [5][5];
        for(int x=0;x<3;x++)
        {
            System.arraycopy(this.multi[x], 0, tempboard[x], 0, 3);
        }

        if(placingphase) {
            piecesplaced+=1;
            if (piecesplaced >= 6) {
                placingphase = false;
            }
            //check if either you or the opponent can win with a single move
            for (int x = 0; x < 3; x++) {
                for (int y = 0; y < 3; y++) {
                    if (tempboard[x][y] == 0) {
                        for (int i = 1; i < 3; i++) {
                            tempboard[x][y] = i;
                            if (checkwin(tempboard) == 'w') {
                                multi[x][y] = playermark;
                                //changeplayer();
                                return;
                            }
                            tempboard[x][y] = 0;

                        }
                    }
                }
            }
            //If neither can win in a single move, place randomly
            boolean unplaced = true;
            while (unplaced) {
                Random rand = new Random();
                int randomx = rand.nextInt(3);
                int randomy = rand.nextInt(3);
                if (multi[randomx][randomy] == 0) {
                    int mark;
                    if (!player) {
                        mark = 1;
                    } else {
                        mark = 2;
                    }
                    multi[randomx][randomy] = mark;
                    unplaced = false;
                }
            }
        }

        else //Moving phase, check if either you or the opponent can win with a single move
        {
            for (int x = 0; x < 3; x++) {
                for (int y = 0; y < 3; y++) {
                    if(multi[x][y]==playermark)
                    {
                        tempboard = markadjacentempty(x,y,tempboard,playermark+2);
                        for (int n = 0; n < 3; n++) {
                            for (int i = 0; i < 3; i++) {
                                if(tempboard[i][n]==playermark+2)
                                {
                                    tempboard[x][y] = 0;
                                    for (int p = 1; p < 3; p++) {
                                        tempboard[i][n]=p;
                                        if (checkwin(tempboard) == 'w')
                                        {
                                            marksquare(i,n,playermark);
                                            marksquare(x,y,0);
                                            multi=resettemp(multi);
                                            if(player)
                                            {
                                                player1lastx = x;
                                                player1lasty = y;
                                            }
                                            else
                                            {
                                                player2lastx = x;
                                                player2lasty = y;
                                            }
                                            return;

                                        }
                                    }
                                    tempboard[i][n]=0;
                                    tempboard[x][y]=playermark;
                                }
                            }
                        }
                    }
                }
            }
            //If neither can win in a single move, play randomly
            while (true) {
                Random rand = new Random();
                int randomx = rand.nextInt(3);
                int randomy = rand.nextInt(3);
                if(multi[randomx][randomy]==playermark) { //Select a random piece owned by the current player
                    tempboard = markadjacentempty(randomx, randomy, tempboard, playermark + 2);
                    for (int x = 0; x < 3; x++) {
                        for (int y = 0; y < 3; y++) {
                            if (tempboard[x][y] == playermark + 2) {
                                marksquare(x, y, playermark);
                                marksquare(randomx, randomy, 0);
                                if (player) {
                                    player1lastx = x;
                                    player1lasty = y;
                                } else {
                                    player2lastx = x;
                                    player2lasty = y;
                                }
                                return;
                            }
                        }
                    }
                }
            }
        }
    }


    private void changeplayer()
    {

        this.player = !this.player;
    }

    char checkwin(int[][] multi)
    {
        //Check for a full row
        for(int x=0;x<3;x++)
        {
            if((multi[x][1]==multi[x][2]) && (multi[x][2]==multi[x][0])) {
                if(multi[x][1]==1||multi[x][1]==2) {
                    return 'w';
                }
            }
        }
        //Check for a full column
        for(int y=0;y<3;y++)
        {
            if((multi[1][y]==multi[2][y]) && (multi[2][y]==multi[0][y])) {
                if(multi[1][y]==1||multi[1][y]==2) {
                    return 'w';
                }
            }
        }
        //First diagonal
        if((multi[2][2]==multi[1][1]) && (multi[1][1]==multi[0][0])) {
            if(multi[1][1]==1||multi[1][1]==2) {
                return 'w';
            }
        }
        //Second diagonal
        if((multi[2][0]==multi[1][1]) && (multi[1][1]==multi[0][2])) {
            if(multi[1][1]==1||multi[1][1]==2) {
                return 'w';
            }
        }
        for(int x=0;x<3;x++)
        {
            for(int y=0;y<3;y++)
            {
                if(multi[x][y] == 0||multi[x][y]>2)
                {
                    return 'f';
                }
            }
        }
        return 't';
    }
}
