package model;

import java.util.ArrayList;

public class Graph {
    private ArrayList<Vertex> vertexlist;
    private int num_v = 0;

    private int nlines;
    private int ncols;
    private int startV;
    private int endV;

    public Graph() {
        vertexlist = new ArrayList<Vertex>();
    }

    public void addVertex(double indivTime)
    {
        Vertex v = new Vertex(num_v);
        v.setIndivTime(indivTime);
        vertexlist.add(v);
        num_v = num_v + 1;
    }

    public void addEgde(int source, int destination, double weight) {
        Edge edge = new Edge(source, destination, weight);
        vertexlist.get(source).addNeighbor(edge);
    }

    public ArrayList<Vertex> getVertexlist() {
        return vertexlist;
    }

    public void setVertexlist(ArrayList<Vertex> vertexlist) {
        this.vertexlist = vertexlist;
    }

    public int getNum_v() {
        return num_v;
    }

    public void setNum_v(int num_v) {
        this.num_v = num_v;
    }

    public int getNlines() {
        return nlines;
    }

    public void setNlines(int nlines) {
        this.nlines = nlines;
    }

    public int getNcols() {
        return ncols;
    }

    public void setNcols(int ncols) {
        this.ncols = ncols;
    }

    public int getStartV() {
        return startV;
    }

    public void setStartV(int startV) {
        this.startV = startV;
    }

    public int getEndV() {
        return endV;
    }

    public void setEndV(int endV) {
        this.endV = endV;
    }

    public String toString() {
        String result = "";
        for(Vertex v:vertexlist){
            result += v;
        }
        result += "\n";
        return result;
    }
}