package controller;

import model.Graph;
import model.Vertex;
import view.Board;

import javax.swing.*;
import java.util.*;

public class Controller {
    private ArrayList<Graph> listGraph;
    private Board board;
    private JFrame window;

    private Boolean flag;
    private LinkedList<Integer> path;
    private ArrayList<Boolean> listOutput;

    public Controller() {
        listGraph = Util.convertDataToGraph(Util.readData());
        for (Graph graph : listGraph) {
            initGraphWeights(graph);
        }
        path = new LinkedList<>();
        listOutput = new ArrayList<>();
    }

    public void runAllWithGraphic(){
        for(Graph graph : listGraph){
            drawGraph(graph);
            do{
                update(graph, true);
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    System.out.println("stop");
                }
                if(!flag) break;
            }while(path.get(0) != graph.getEndV());
            listOutput.add(flag);
            path.clear();
            window.dispose();
        }
        System.out.println(listOutput);
    }

    public void runAllWithOutGraphic(){
        for(Graph graph : listGraph){
            do{
                update(graph, false);
                if(!flag) break;
            }while(path.get(0) != graph.getEndV());
            listOutput.add(flag);
            path.clear();
        }
        System.out.println(listOutput);
    }

    public void update(Graph graph, Boolean graphic){
        int ncols = graph.getNcols();
        int nlines = graph.getNlines();
        int startV = graph.getStartV();
        int endV = graph.getEndV();

        prisonerMove(graph, startV, endV, ncols, board, graphic);

    }

    public void drawBoard(Board board, int nlines, int ncols, int pixelSize) {
        window = new JFrame("Labyrinthe");
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        // Because the Java UI display is often biased, bias variable is used to adjust
        int bias = 70;
        window.setBounds(50, 50, ncols * pixelSize + bias, nlines * pixelSize + bias);
        window.getContentPane().add(board);
        window.setVisible(true);
    }

    public void prisonerMove(Graph graph, int start, int end, int ncols, Board board, Boolean graphic) {
        graph.getVertexlist().get(start).setTimeFromSource(0);
        //mettre tous les noeuds du graphe dans la liste des noeuds a visiter:
        HashSet<Integer> to_visit = new HashSet<Integer>();
        for (Vertex v : graph.getVertexlist()) {
            to_visit.add(v.getNum());
        }
        // Remplir l'attribut graph.vertexlist.get(v).heuristic pour tous les noeuds v du graphe:
        int lineEnd = end / ncols;
        int colEnd = end % ncols;
        for (Vertex v : graph.getVertexlist()) {
            int lineV = v.getNum() / ncols;
            int colV = v.getNum() % ncols;
            v.setHeuristic(Util.getEuclideanDistance(lineEnd, lineV, colEnd, colV) * 5);
        }

        int min_v = start;
        double fmin = Double.POSITIVE_INFINITY;
        for (Vertex v : graph.getVertexlist()) {
            if (v.getTimeFromSource() + v.getHeuristic() < fmin && to_visit.contains(v.getNum())) {
                fmin = v.getTimeFromSource() + v.getHeuristic();
                min_v = v.getNum();
            }
        }
        to_visit.remove(min_v);

        //pour tous ses voisins, on verifie si on est plus rapide en passant par ce noeud.
        double newDistance = 0;
        for (int i = 0; i < graph.getVertexlist().get(min_v).getAdjacencylist().size(); i++) {
            int to_try = graph.getVertexlist().get(min_v).getAdjacencylist().get(i).getDestination();
            // to_try node timeFromSource += weight
            if (to_visit.contains(to_try)) {
                double newTimeFromSource = graph.getVertexlist().get(min_v).getTimeFromSource()
                        + graph.getVertexlist().get(min_v).getAdjacencylist().get(i).getWeight();
                if (newTimeFromSource < graph.getVertexlist().get(to_try).getTimeFromSource()) {
                    graph.getVertexlist().get(to_try).setTimeFromSource(newTimeFromSource);
                    newDistance = newTimeFromSource;
                    graph.getVertexlist().get(to_try).setPrev(graph.getVertexlist().get(min_v));
                }
            }
        }
        if(newDistance > 400){
            flag = false;
        } else {
            path.addFirst(min_v);
            if(graphic){
                board.addPath(graph, path);
            }
            flag = true;
        }
    }

    public void drawGraph(Graph graph) {
        HashMap<Integer, String> groundColor = new HashMap<>();
        groundColor.put(1, "green");
        groundColor.put(1000, "gray");
        groundColor.put(500, "yellow");

        int ncols = graph.getNcols();
        int nlines = graph.getNlines();
        int startV = graph.getStartV();
        int endV = graph.getEndV();

        int pixelSize = 50;
        board = new Board(graph, pixelSize, ncols, nlines, groundColor, startV, endV);
        drawBoard(board, nlines, ncols, pixelSize);
        board.repaint();

        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            System.out.println("stop");
        }
    }

    public void initGraphWeights(Graph graph) {
        int nlines = graph.getNlines();
        int ncols = graph.getNcols();
        for (int line = 0; line < nlines; line++) {
            for (int col = 0; col < ncols; col++) {
                int source = line * ncols + col;
                double weight = -1;
                ArrayList<Integer> listNeighborDest = Util.getNeighborDest(line, col, nlines, ncols);
                for (int dest : listNeighborDest) {
                    double timeDest = graph.getVertexlist().get(dest).getIndivTime();
                    double timeSrc = graph.getVertexlist().get(source).getIndivTime();

                    if (Math.abs(source - dest) == 1 || Math.abs(source - dest) == ncols) {
                        weight = (timeDest + timeSrc) / 2;
                    }
                    if (weight != -1) {
                        graph.addEgde(source, dest, weight);
                    }
                }
            }
        }
    }
}
