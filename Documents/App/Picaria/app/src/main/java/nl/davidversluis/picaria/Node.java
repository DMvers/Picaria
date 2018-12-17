package nl.davidversluis.picaria;

import java.util.ArrayList;
import java.util.List;


class Node {
    final double xloc; //Between 0 and 1, indicates relative gameboard position, same for y
    final double yloc;
    private int mark;
    private final List<Node> adjacents;

    Node(double xloc, double yloc)
    {
        this.xloc = xloc;
        this.yloc = yloc;
        adjacents = new ArrayList<Node>();
    }

    Node(Node oldnode){
        this.xloc = oldnode.xloc;
        this.yloc = oldnode.yloc;
        adjacents = new ArrayList<Node>();
        this.mark = oldnode.mark;
    }

    public void setmark(int mark)
    {
        this.mark = mark;
    }

    public int getmark()
    {
        return mark;
    }

    public void setadjacent(Node adjnode) {
        adjacents.add(adjnode);
    }

    public List getadjacentempty()
    {
        List newlist = new ArrayList<>();
        for(Node adj : adjacents) {
            //Node adj = (Node) element;
            if (adj.getmark() == 0) {
                newlist.add(adj);
            }
        }

        return newlist;
    }
}
