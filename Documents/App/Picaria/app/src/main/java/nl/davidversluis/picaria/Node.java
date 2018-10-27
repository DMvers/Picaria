package nl.davidversluis.picaria;

//Currently unused, might be used when expanding beyond a 3x3 grid
public class Node {
    public static int xloc;
    public static int yloc;
    public int mark;

    public Node(int xloc,int yloc)
    {
        this.xloc = xloc;
        this.yloc = yloc;
    }

    public void marknode(int mark)
    {
        this.mark = mark;
    }

    public int requestmark()
    {
        return mark;
    }
}
