package nl.davidversluis.picaria;

import java.util.ArrayList;
import java.util.List;


class Board {
    List boardspace; //List of nodes
    boolean player; //Diamonds is true/player2, squares is false/player1. player1 has playermark1, player 2 has 2.
    boolean placingphase; //placing or moving
    private final boolean play1AI;
    private final boolean play2AI;
    private final boolean forbidrepeat;
    private Node selectednode;

    private int player1laststart = -1;
    private int player1lastend = -1;
    private int player2laststart = -1;
    private int player2lastend = -1;

    Board(int boardtype, boolean play1AI, boolean play2AI, boolean forbidrepeat, List boardspace)
    {

        this.boardspace = boardspace;

        this.placingphase = true;
        this.play1AI = play1AI;
        this.play2AI = play2AI;
        this.forbidrepeat = forbidrepeat;
        if(play1AI) {
            makeAImove();
            changeplayer();
        }
    }

    boolean touched(double x, double y) {
        int playermark = 1 + (player ? 1 : 0); //Calculate mark from current player state

        Node closestnode = null;
        double shortdist = 1000;
        for (Object element : this.boardspace) {
            Node nod = (Node) element;
            double nodx = nod.xloc;
            double nody = nod.yloc;
            double distance = Math.hypot(x - nodx, y - nody);
            if (distance < shortdist) {
                shortdist = distance;
                closestnode = nod;
            }
        }

        int nodecontent = closestnode.getmark();


        if (placingphase) {
            if (nodecontent == 0) {
                closestnode.setmark(playermark);
                changeplayer();
            } else
                return false; //No valid square, AI move not necessary
        } else {

            if (nodecontent == playermark) {
                resettemp();
                List adjacentempties = closestnode.getadjacentempty();
                for(Object element : adjacentempties) {
                    Node adj = (Node) element;
                    int adjid = boardspace.indexOf(adj); //Object-independent id of this particular node
                    int closid = boardspace.indexOf(closestnode);
                    if (forbidrepeat) {
                        if(playermark ==1)
                        {
                            if(player1laststart==adjid && player1lastend == closid){ //This move would reverse the last move player 1 did
                                continue;
                            }
                        }
                        else
                        {
                            if(player2laststart == adjid && player2lastend == closid){ //Idem for player 2
                                continue;
                            }
                        }

                    }
                    adj.setmark(playermark + 2);
                }
                selectednode = closestnode;
                return false; //Merely marking squares, no AI move necessary
            }
            if (nodecontent == playermark + 2) {
                closestnode.setmark(playermark);
                selectednode.setmark(0); //Previously selected node should be empty, because the token is removed from it

                int adjid = boardspace.indexOf(selectednode);
                int closid = boardspace.indexOf(closestnode);

                if(player)
                {
                    player2lastend = closid;
                    player2laststart = adjid;
                }
                else
                {
                    player1lastend = closid;
                    player1laststart = adjid;
                }
                changeplayer();
            } else {
                return false; //No valid move, no AI neccesary
            }
            resettemp();
        }
        return !checkwin(boardspace); //If the game has not yet been won, return true
    }

    void determineAI() //Make AI moves as necessary for the current game settings
    {
        if(play1AI)
        {
            makeAImove();
            changeplayer();
            if(checkwin(boardspace))
                return;

        }
        if(play2AI)
        {
            makeAImove();
            changeplayer();
        }

    }

    //Remove temporary marks
    private void resettemp()
    {
        for(Object element : boardspace) {
            Node adj = (Node) element;
            if (adj.getmark() >2) {
                adj.setmark(0);
            }
        }
    }

    //Make an AI move for the currently active player
    void makeAImove()
    {
        resettemp();
        int playermark = 1 + (player ? 1 : 0);
        List tempboardspace = copyboardspace(boardspace);
        int bestscore = -2;
        List bestmove = null; //Will be filled with a new boardspace
        int tempplayer1lastend = -1;
        int tempplayer1laststart = -1;
        int tempplayer2lastend = -1;
        int tempplayer2laststart = -1;

        if(placingphase) {
            for (Object element : tempboardspace) {
                Node adj = (Node) element;
                if (adj.getmark() == 0) {
                    adj.setmark(playermark);
                    if (checkwin(tempboardspace)) {
                        bestmove = tempboardspace;
                        this.boardspace = copyboardspace(bestmove); //A single move will win, no need to run the minimax
                        return;
                    }
                    adj.setmark(0);//Revert change
                }
            }
            for (Object element : tempboardspace) {
                Node adj = (Node) element;
                if(adj.getmark()==0){
                    adj.setmark(playermark);
                        int score = -1*minimaxer(copyboardspace(tempboardspace),0,5,!player);
                        if(score>bestscore){
                            bestscore = score;
                            bestmove = copyboardspace(tempboardspace);
                        }

                    adj.setmark(0);//Revert change
                }
            }
        }
        else {
            for (Object element : tempboardspace) {
                Node nod = (Node) element;
                if (nod.getmark() == playermark) {
                    for (Object secondelement : nod.getadjacentempty()) {
                        Node secondlocation = (Node) secondelement;


                        int secondlocid = tempboardspace.indexOf(secondlocation);
                        int nodid = tempboardspace.indexOf(nod);

                        if (forbidrepeat) {

                            if(playermark == 1)
                            {
                                if(player1laststart==secondlocid && player1lastend == nodid){
                                    continue;
                                }
                            }
                            else
                            {
                                if(player2laststart == secondlocid && player2lastend == nodid){
                                    continue;
                                }
                            }
                        }

                        secondlocation.setmark(playermark);
                        nod.setmark(0);
                        if (checkwin(tempboardspace)) {
                            bestmove = tempboardspace;
                            this.boardspace = copyboardspace(bestmove); //A single move will win, no need to run the minimax
                            if(playermark == 1)
                            {
                                player1lastend = secondlocid;
                                player1laststart = nodid;

                            }
                            else
                            {
                                player2lastend = secondlocid;
                                player2laststart = nodid;
                            }
                            return;
                        }
                        secondlocation.setmark(0);
                        nod.setmark(playermark);
                    }
                }
            }
            for (Object element : tempboardspace) {
                Node nod = (Node) element;
                if (nod.getmark() == playermark) {
                    for (Object secondelement : nod.getadjacentempty()) {
                        Node secondlocation = (Node) secondelement;
                        int secondlocid = tempboardspace.indexOf(secondlocation);
                        int nodid = tempboardspace.indexOf(nod);

                        if (forbidrepeat) {
                            if(playermark == 1)
                            {
                                if(player1laststart==secondlocid && player1lastend == nodid){
                                    continue;
                                }
                            }
                            else
                            {
                                if(player2laststart == secondlocid && player2lastend == nodid){
                                    continue;
                                }
                            }
                        }

                        secondlocation.setmark(playermark);
                        nod.setmark(0);
                        int score = -1 * minimaxer(copyboardspace(tempboardspace), 0, 4, !player);
                        if (score > bestscore) {
                            bestscore = score;
                            bestmove = copyboardspace(tempboardspace);
                            if(playermark == 1)
                            {
                                tempplayer1lastend = secondlocid;
                                tempplayer1laststart = nodid;

                            }
                            else
                            {
                                tempplayer2lastend = secondlocid;
                                tempplayer2laststart = nodid;
                            }
                        }
                        secondlocation.setmark(0);
                        nod.setmark(playermark);
                    }
                }
            }
        }

        if(playermark == 1)
        {
            player1lastend = tempplayer1lastend ;
            player1laststart = tempplayer1laststart;

        }
        else
        {
            player2lastend = tempplayer2lastend;
            player2laststart = tempplayer2laststart;
        }
        this.boardspace = copyboardspace(bestmove);
    }

    //TODO easy and hard AI (change depth)
    private int minimaxer(List boardspace, int depth, int maxdepth, boolean player)
    {
        //determine if we're in placingphase or movingphase
        if(depth == maxdepth)
        {
            return 0;
        }
        depth +=1;

        int pieces = 0;

        for(Object element: boardspace){
            Node nod = (Node) element;
            if(nod.getmark()>0)
            {pieces++;
            }
        }

        int playermark = 1 + (player ? 1 : 0);
        int bestscore = -2; //Even a losing move (score -1) should be better than no move at all
        List tempboardspace = copyboardspace(boardspace);
        if(pieces<6)//Should place a piece
        {
            for (Object element : tempboardspace) {
                Node adj = (Node) element;
                if (adj.getmark() == 0) {
                    adj.setmark(playermark);
                    if (checkwin(tempboardspace)) {
                        return 1;
                    }
                    adj.setmark(0);//Revert change
                }
            }
            for (Object element : tempboardspace) {
                Node adj = (Node) element;
                if(adj.getmark()==0){
                    adj.setmark(playermark);
                    int score = -1*minimaxer(copyboardspace(tempboardspace),depth,maxdepth,!player);
                    if(score>bestscore){
                        bestscore = score;
                    }

                    adj.setmark(0);//Revert change
                }

            }
        }
        else //Movingphase
            {
            for (Object element : tempboardspace) {
                Node nod = (Node) element;
                if (nod.getmark() == playermark) {
                    for (Object secondelement : nod.getadjacentempty()) {
                        Node adj = (Node) secondelement;
                        adj.setmark(playermark);
                        nod.setmark(0);
                        if (checkwin(tempboardspace)) {
                            return 1; //Win for the opponent if this is minimax depth 0
                        }
                        adj.setmark(0);
                        nod.setmark(playermark);

                    }
                }
            }
            for (Object element : tempboardspace) {
                Node nod = (Node) element;
                if(nod.getmark()==playermark){
                    for(Object secondelement: nod.getadjacentempty()) {
                        Node adj = (Node) secondelement;
                        adj.setmark(playermark);
                        nod.setmark(0);
                        int score = -1 * minimaxer(copyboardspace(tempboardspace), depth, maxdepth, !player);
                        if (score > bestscore) {
                            bestscore = score;
                        }

                        adj.setmark(0);
                        nod.setmark(playermark);
                        }
                    }
                }
            }

        return bestscore;
    }

    void changeplayer()
    {
        int piecesplaced = 0;
        for(Object element: boardspace){
            Node nod = (Node) element;
            if(nod.getmark()>0)
            {piecesplaced++;
            }
        }
        if(piecesplaced >5){placingphase=false;}
        this.player = !this.player;
    }

    private List copyboardspace(List anyboardspace)
    {
        List<Node> newlist = new ArrayList<>();
        for(Object element:anyboardspace){
            newlist.add(new Node((Node)element));
        }
        for(int n=0;n<9;n=n+3) {
            for(int i=0;i<3;i++) {
                int index = n+i;
                boolean left = (n==0);
                boolean right = (n==6);
                boolean bottom = (i==2);
                boolean top = (i==0);
                if(!top){newlist.get(index).setadjacent(newlist.get(index-1));}
                if(!bottom){newlist.get(index).setadjacent(newlist.get(index+1));}
                if(!right){newlist.get(index).setadjacent(newlist.get(index+3));}
                if(!left){newlist.get(index).setadjacent(newlist.get(index-3));}
                if(!right && !bottom){newlist.get(index).setadjacent(newlist.get(index+4));}
                if(!left && !bottom){newlist.get(index).setadjacent(newlist.get(index-2));}
                if(!right && !top){newlist.get(index).setadjacent(newlist.get(index+2));}
                if(!left && !top){newlist.get(index).setadjacent(newlist.get(index-4));}
            }
        }
        return newlist;
    }

    //Check if a certain gamestate is won
    boolean checkwin(List boardspace)
    {
        List<Node> play1list = new ArrayList<>();
        List<Node> play2list = new ArrayList<>();
        for(Object element : boardspace) {
            Node adj = (Node) element;
            int mark = adj.getmark();
            if (mark ==1) {
                play1list.add(adj);
            }
            if(mark==2){
                play2list.add(adj);
            }
        }
        return(checkforstraightline(play1list) || checkforstraightline(play2list));
    }

    private boolean checkforstraightline(List nodelist)
    {
        if (nodelist.size()<3)
        {
            return false; //Can't win with less than three tokens
        }
        //If the tokens are in a straight line, and only then, one of the nodes will be in the average location of all three.
        double totx = 0;
        double toty = 0;

        for(Object element : nodelist) {
            Node nod = (Node) element;
            totx += nod.xloc;
            toty += nod.yloc;
        }

        double avgx = totx/3;
        double avgy = toty/3;

        for(Object element : nodelist) {
            Node nod = (Node) element;
            if(nod.xloc == avgx && nod.yloc == avgy) //Rounding errors may occur, but only when the tokens aren't in a straight line anyway
            {
                return true;
            }
        }
        return false;
    }
}
