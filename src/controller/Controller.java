package controller;

import model.Graph;
import model.Vertex;
import view.Board;

import javax.swing.*;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class Controller {
    private ArrayList<Graph> listGraph;
    private Board board;
    private JFrame window;

    private Boolean flag;
    private int prisonPos;
    private ArrayList<Integer> listFire;
    private HashSet<Integer> to_visit;
    private LinkedList<Integer> theoryPath;
    private LinkedList<Integer> currentPath;
    private int step;
    private ArrayList<Boolean> listOutput;

    public Controller() {
        listGraph = Util.convertDataToGraph(Util.readData("info.data"));
        for (Graph graph : listGraph) {
            initGraphWeights(graph);
        }
        theoryPath = new LinkedList<>();
        currentPath = new LinkedList<>();
        to_visit = new HashSet<>();
        listOutput = new ArrayList<>();
    }

    public void runAllWithGraphic() {
        for (Graph graph : listGraph) {
            drawGraph(graph);
            flag = true;
            theoryPath = AStar(graph);
            step = 0;
            do {
                if (!flag) {
                    break;
                }
                update(graph, true);
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    System.out.println("stop");
                }
            } while (prisonPos != graph.getEndV());
            listOutput.add(flag);
            theoryPath.clear();
            currentPath.clear();
            to_visit.clear();
            window.dispose();
        }

        printOutput();
        saveData();
    }

    public void runAllWithOutGraphic() {
        for (Graph graph : listGraph) {
            flag = true;
            theoryPath = AStar(graph);
            step = 0;
            do {
                if (!flag) {
                    break;
                }
                update(graph, false);
            } while (prisonPos != graph.getEndV());
            listOutput.add(flag);
            theoryPath.clear();
            currentPath.clear();
            to_visit.clear();
        }

        printOutput();
        saveData();
    }

    public void update(Graph graph, boolean graphic) {
        if(currentPath.size() == 0){
            prisonerMove(graph, graphic);
        } else {
            prisonerMove(graph, graphic);
            fireMove(graph);
            for (int fire : listFire) {
                if (fire == prisonPos) {
                    flag = false;
                    break;
                }
            }
        }
        if(graphic){
            board.repaint();
        }
    }

    public void fireMove(Graph graph) {
        listFire = new ArrayList<>();
        for (int i = 0; i < graph.getNum_v(); i++) {
            // if the vertex is fire
            if (graph.getVertexlist().get(i).getIndivTime() == 500) {
                listFire.add(i);
            }
        }

        ArrayList<Integer> fireDirections = new ArrayList<>();
        for (Integer fire : listFire) {
            fireDirections.clear();
            int ncols = graph.getNcols();
            int nlines = graph.getNlines();
            int col = fire % ncols;
            int line = fire / ncols;

            // top left
            if (line == 0 && col == 0) {
                fireDirections.add(fire + 1);
                fireDirections.add(fire + graph.getNcols());
            }
            // top middle
            else if (line == 0 && col != 0 && col != ncols - 1) {
                fireDirections.add(fire - 1);
                fireDirections.add(fire + 1);

                fireDirections.add(fire + graph.getNcols());
            }
            // top right
            else if (line == 0 && col == ncols - 1) {
                fireDirections.add(fire - 1);
                fireDirections.add(fire + graph.getNcols());
            }

            // middle left
            else if (line != 0 && line != nlines - 1 && col == 0) {
                fireDirections.add(fire - graph.getNcols());
                fireDirections.add(fire + graph.getNcols());

                fireDirections.add(fire + 1);
            }

            // middle middle
            else if (line != 0 && line != nlines - 1 && col != 0 && col != ncols - 1) {
                fireDirections.add(fire - graph.getNcols());
                fireDirections.add(fire + graph.getNcols());
                fireDirections.add(fire - 1);
                fireDirections.add(fire + 1);
            }

            // middle right
            else if (line != 0 && line != nlines - 1 && col == ncols - 1) {
                fireDirections.add(fire - graph.getNcols());
                fireDirections.add(fire + graph.getNcols());

                fireDirections.add(fire - 1);
            }

            // bottom left
            else if (line == nlines - 1 && col == 0) {
                fireDirections.add(fire - graph.getNcols());
                fireDirections.add(fire + 1);
            }

            // bottom middle
            else if (line == nlines - 1 && col != 0 && col != ncols - 1) {
                fireDirections.add(fire - 1);
                fireDirections.add(fire + 1);

                fireDirections.add(fire - graph.getNcols());
            }

            // bottom right
            else if (line == nlines - 1 && col == ncols - 1) {
                fireDirections.add(fire - 1);
                fireDirections.add(fire - graph.getNcols());
            }


            for (int direction : fireDirections) {
                if (graph.getVertexlist().get(direction).getIndivTime() != 1000)
                    graph.getVertexlist().get(direction).setIndivTime(500);
            }
        }
        for (int direction : fireDirections) {
            if (graph.getVertexlist().get(direction).getIndivTime() != 1000)
                listFire.add(direction);
        }
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

    public void prisonerMove(Graph graph, boolean graphic) {
        int min_v = theoryPath.get(step);
        currentPath.addFirst(min_v);
        if (graphic) {
            board.addPath(graph, currentPath);
        }
        flag = true;
        prisonPos = min_v;
        step++;
    }

    public LinkedList<Integer> AStar(Graph graph) {
        int ncols = graph.getNcols();
        int start = graph.getStartV();
        graph.getVertexlist().get(start).setTimeFromSource(0);
        int end = graph.getEndV();

        //mettre tous les noeuds du graphe dans la liste des noeuds � visiter:
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

        while (to_visit.contains(end)) {
            // trouver le noeud min_v parmis tous les noeuds v ayant la distance temporaire
            //      (graph.vertexlist.get(v).timeFromSource + heuristic) minimale.

            //We choose the wanted node, and delete it in the visit group
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
            for (int i = 0; i < graph.getVertexlist().get(min_v).getAdjacencylist().size(); i++) {
                int to_try = graph.getVertexlist().get(min_v).getAdjacencylist().get(i).getDestination();
                // to_try node timeFromSource += weight
                if (to_visit.contains(to_try)) {
                    double newTimeFromSource = graph.getVertexlist().get(min_v).getTimeFromSource()
                            + graph.getVertexlist().get(min_v).getAdjacencylist().get(i).getWeight();

                    if (newTimeFromSource < graph.getVertexlist().get(to_try).getTimeFromSource()) {
                        graph.getVertexlist().get(to_try).setTimeFromSource(newTimeFromSource);
                        graph.getVertexlist().get(to_try).setPrev(graph.getVertexlist().get(min_v));
                    }
                }
            }
        }
        LinkedList<Integer> path = new LinkedList<>();
        //remplir la liste path avec le chemin
        Vertex node = graph.getVertexlist().get(end);
        while (node.getNum() != start) {
            if (node.getTimeFromSource() > 400) {
                flag = false;
                break;
            }
            path.addFirst(node.getNum());
            node = node.getPrev();
        }
        path.addFirst(start);
        return path;
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

    public void printOutput(){
        for (boolean b : listOutput) {
            char c = (b)?'Y':'N';
            System.out.println(c);
        }
    }

    public void saveData(){
        try {
            File file = new File("src/data/out.txt");
            if (!file.exists()) {
                file.createNewFile();
            }
            FileWriter fw = new FileWriter(file.getAbsoluteFile());
            BufferedWriter bw = new BufferedWriter(fw);

            for (boolean b : listOutput) {
                char c = (b)?'Y':'N';
                bw.write(c);
                bw.write('\n');
            }
            bw.close();
            System.out.println("File out.txt has saved.");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
