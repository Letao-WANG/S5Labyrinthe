package controller;

import model.Graph;
import model.Vertex;
import view.Board;

import javax.swing.*;
import java.io.*;
import java.util.*;

public class Controller {
    private ArrayList<Graph> listGraph;

    public Controller() {
        listGraph = Util.convertDataToGraph(Util.readData());

        for (Graph graph : listGraph) {
            initGraphWeights(graph);
        }

//        drawGraph(listGraph.get(0));
//        drawGraph(listGraph.get(1));
        drawGraph(listGraph.get(2));
    }

    public void drawBoard(Board board, int nlines, int ncols, int pixelSize) {
        JFrame window = new JFrame("Plus court chemin");
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setBounds(50, 50, ncols * pixelSize + 0, nlines * pixelSize + 70);
        window.getContentPane().add(board);
        window.setVisible(true);
    }

    public LinkedList<Integer> AStar(Graph graph, int start, int end, int ncols, int numberV, Board board) {
        graph.getVertexlist().get(start).setTimeFromSource(0);
        int number_tries = 0;

        //mettre tous les noeuds du graphe dans la liste des noeuds � visiter:
        HashSet<Integer> to_visit = new HashSet<Integer>();
        for(Vertex v : graph.getVertexlist()){
            to_visit.add(v.getNum());
        }
        // Remplir l'attribut graph.vertexlist.get(v).heuristic pour tous les noeuds v du graphe:
        int lineEnd = end/ncols;
        int colEnd = end%ncols;
        for(Vertex v : graph.getVertexlist()){
            int lineV = v.getNum()/ncols;
            int colV = v.getNum()%ncols;
            v.setHeuristic(Util.getEuclideanDistance(lineEnd, lineV, colEnd, colV)*5);
        }

//        System.out.println("ncols: " + ncols);
//        for(Vertex v: graph.getVertexlist()){
//            System.out.println("Num :" + v.getNum() + " line :" + v.getNum()/ncols
//                    + " col :" + v.getNum()%ncols + " Heuristic :" + v.getHeuristic());
//        }

        while (to_visit.contains(end)) {
            // trouver le noeud min_v parmis tous les noeuds v ayant la distance temporaire
            //      (graph.vertexlist.get(v).timeFromSource + heuristic) minimale.

            //On l'enl�ve des noeuds � visiter
            int min_v = start;
            double fmin = Double.POSITIVE_INFINITY;
            for(Vertex v : graph.getVertexlist()){
                if(v.getTimeFromSource() + v.getHeuristic() < fmin && to_visit.contains(v.getNum())) {
                    fmin = v.getTimeFromSource() + v.getHeuristic();
                    min_v = v.getNum();
                }
            }
            to_visit.remove(min_v);
            number_tries += 1;

            //pour tous ses voisins, on v�rifie si on est plus rapide en passant par ce noeud.
            for (int i = 0; i < graph.getVertexlist().get(min_v).getAdjacencylist().size(); i++) {
                int to_try = graph.getVertexlist().get(min_v).getAdjacencylist().get(i).getDestination();
                // to_try node timeFromSource += weight
                if(to_visit.contains(to_try)){
                    double newTimeFromSource = graph.getVertexlist().get(min_v).getTimeFromSource()
                            + graph.getVertexlist().get(min_v).getAdjacencylist().get(i).getWeight();
                    if(newTimeFromSource > 400){
                        System.out.println("Impossible! ");
                        continue;
                    }
                    if (newTimeFromSource < graph.getVertexlist().get(to_try).getTimeFromSource() ) {
                        graph.getVertexlist().get(to_try).setTimeFromSource(newTimeFromSource);
                        graph.getVertexlist().get(to_try).setPrev(graph.getVertexlist().get(min_v));
                    }
                }
            }


            //On met � jour l'affichage
            try {
                board.update(graph, min_v);
                Thread.sleep(100);
            } catch (InterruptedException e) {
                System.out.println("stop");
            }
        }

        System.out.println("Done! Using A*:");
        System.out.println("	Number of nodes explored: " + number_tries);
        System.out.println("	Total time of the path: " + graph.getVertexlist().get(end).getTimeFromSource());
        LinkedList<Integer> path = new LinkedList<Integer>();
        //remplir la liste path avec le chemin
        Vertex node = graph.getVertexlist().get(end);
        while(node.getNum() != start){
            path.addFirst(node.getNum());
            node = node.getPrev();
        }
        path.addFirst(start);
        board.addPath(graph, path);
        return path;
    }

    public void drawGraph(Graph graph) {
        HashMap<Integer, String> groundColor = new HashMap<Integer, String>();
        groundColor.put(1, "green");
        groundColor.put(1000, "gray");
        groundColor.put(500, "yellow");

        int ncols = graph.getNcols();
        int nlines = graph.getNlines();
        int startV = graph.getStartV();
        int endV = graph.getEndV();

        int pixelSize = 50;
        Board board = new Board(graph, pixelSize, ncols, nlines, groundColor, startV, endV);
        drawBoard(board, nlines, ncols, pixelSize);
        board.repaint();

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            System.out.println("stop");
        }

        //On appelle Dijkstra
        LinkedList<Integer> path = AStar(graph, startV, endV, ncols, nlines * ncols, board);
        try {
            File file = new File("src/data/out.txt");
            if (!file.exists()) {
                file.createNewFile();
            }
            FileWriter fw = new FileWriter(file.getAbsoluteFile());
            BufferedWriter bw = new BufferedWriter(fw);

            for (int i : path) {
                bw.write(String.valueOf(i));
                bw.write('\n');
            }
            bw.close();

        } catch (IOException e) {
            e.printStackTrace();
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
                    // direction horizontally or vertically
                    if (Math.abs(source - dest) == 1 || Math.abs(source - dest) == ncols) {
                        weight = (timeDest + timeSrc) / 2;
                    }
                    // direction  diagonal

                    // direction right top
                    else if (dest - source == -ncols + 1) {
                        // up and right
                        double timeTopMid = graph.getVertexlist().get(source - ncols).getIndivTime();
                        double weight1 = (timeSrc + timeTopMid) / 2;
                        double weight2 = (timeTopMid + timeDest) / 2;
                        double weight12 = Math.sqrt(Math.pow(weight1, 2) + Math.pow(weight2, 2));

                        // bottom and right
                        double timeBottomMid = graph.getVertexlist().get(source + 1).getIndivTime();
                        double weight3 = (timeSrc + timeBottomMid) / 2;
                        double weight4 = (timeBottomMid + timeDest) / 2;
                        double weight34 = Math.sqrt(Math.pow(weight3, 2) + Math.pow(weight4, 2));

                        weight = Math.min(weight12, weight34);
                    }

                    // direction right bottom
                    else if (dest - source == ncols + 1) {
                        // down and right
                        double timeDownMid = graph.getVertexlist().get(source + ncols).getIndivTime();
                        double weight3 = (timeSrc + timeDownMid) / 2;
                        double weight4 = (timeDownMid + timeDest) / 2;
                        double weight34 = Math.sqrt(Math.pow(weight3, 2) + Math.pow(weight4, 2));

                        // right and down
                        double timeRightMid = graph.getVertexlist().get(source + 1).getIndivTime();
                        double weight1 = (timeSrc + timeRightMid) / 2;
                        double weight2 = (timeRightMid + timeDest) / 2;
                        double weight12 = Math.sqrt(Math.pow(weight1, 2) + Math.pow(weight2, 2));

                        weight = Math.min(weight12, weight34);
                    }

                    // direction left top
                    else if (dest - source == -ncols - 1) {
                        // up and right
                        double timeTopMid = graph.getVertexlist().get(source - ncols).getIndivTime();
                        double weight1 = (timeSrc + timeTopMid) / 2;
                        double weight2 = (timeTopMid + timeDest) / 2;
                        double weight12 = Math.sqrt(Math.pow(weight1, 2) + Math.pow(weight2, 2));

                        // bottom and right
                        double timeLeftMid = graph.getVertexlist().get(source - 1).getIndivTime();
                        double weight3 = (timeSrc + timeLeftMid) / 2;
                        double weight4 = (timeLeftMid + timeDest) / 2;
                        double weight34 = Math.sqrt(Math.pow(weight3, 2) + Math.pow(weight4, 2));

                        weight = Math.min(weight12, weight34);
                    }

                    // direction left down
                    else if (dest - source == ncols - 1) {
                        // down and left
                        double timeDownMid = graph.getVertexlist().get(source + ncols).getIndivTime();
                        double weight1 = (timeSrc + timeDownMid) / 2;
                        double weight2 = (timeDownMid + timeDest) / 2;
                        double weight12 = Math.sqrt(Math.pow(weight1, 2) + Math.pow(weight2, 2));

                        // bottom and right
                        double timeLeftMid = graph.getVertexlist().get(source - 1).getIndivTime();
                        double weight3 = (timeSrc + timeLeftMid) / 2;
                        double weight4 = (timeLeftMid + timeDest) / 2;
                        double weight34 = Math.sqrt(Math.pow(weight3, 2) + Math.pow(weight4, 2));

                        weight = Math.min(weight12, weight34);
                    }

                    if (weight != -1) {
                        graph.addEgde(source, dest, weight);
                    }
                }
            }
        }
    }

    public void printGraph() {
        System.out.println(listGraph);
    }

}
