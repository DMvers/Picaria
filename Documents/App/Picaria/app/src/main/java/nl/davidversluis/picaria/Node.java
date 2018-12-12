package nl.davidversluis.picaria;

//Currently unused, might be used when expanding beyond a 3x3 grid
class Node {
    private static int xloc;
    private static int yloc;
    private int mark;

    public Node(int xloc,int yloc)
    {
        Node.xloc = xloc;
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
