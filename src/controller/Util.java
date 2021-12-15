package controller;

import model.Graph;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

public class Util {
    public static ArrayList<Graph> convertDataToGraph(ArrayList<String> data) {
        ArrayList<Graph> listGraph = new ArrayList<>();
        int nbInstance = Integer.parseInt(data.get(0));
        int index = 1;
        for (int i = 0; i < nbInstance; i++) {
            // a new graph
            Graph graph = new Graph();
            int lineInstance = Util.charToInt(data.get(index).charAt(0));
            int colInstance = Util.charToInt(data.get(index).charAt(2));
            index++;
            for (int j = 0; j < lineInstance; j++) {
                // a new line
                String line = data.get(index);
                // a new node
                for(int k= 0; k<colInstance; k++){
                    char symbol = line.charAt(k);
                    if(symbol == 'S'){
                        graph.setEndV(j*colInstance+k);
                    } else if(symbol == 'D'){
                        graph.setStartV(j*colInstance+k);
                    }
                    graph.addVertex(Util.convertSymbolToNum(symbol));
                }
                index++;
            }
            graph.setNlines(lineInstance);
            graph.setNcols(colInstance);
            listGraph.add(graph);
        }
        return listGraph;
    }

    public static int convertSymbolToNum(char symbol) {
        switch (symbol) {
            // libre
            case '.': {
                return 1;
            }
            // fire
            case 'F': {
                return 500;
            }
            // mur
            case '#': {
                return 1000;
            }
            // sortie
            case 'S':{
                return 1;
            }
            // debut
            case 'D':{
                return 1;
            }
            default:
                return 1000;
        }
    }

    public static ArrayList<String> readData() {
        File file = new File("src/data/info.data");
        // read file and transform to Class Java
        ArrayList<String> data = new ArrayList<>();
        try {
            Scanner sc = new Scanner(file);
            while (sc.hasNextLine()) {
                String line = sc.nextLine();
                data.add(line);
            }
            sc.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return data;
    }

    public static int charToInt(char n) {
        return n - '0';
    }

    public static ArrayList<Integer> getNeighborDest(int line, int col, int nlines, int ncols) {
        ArrayList<Integer> listNeighbor = new ArrayList<>();

        // line col
        // top left
        if(line == 0 && col == 0 ){
            listNeighbor.add((line) * ncols + col+1);
            listNeighbor.add((line+1) * ncols + col);
        }
        // top middle
        else if(line == 0 && col != 0 && col != ncols-1){
            listNeighbor.add((line) * ncols + col-1);
            listNeighbor.add((line) * ncols + col+1);

            listNeighbor.add((line+1) * ncols + col);
        }
        // top right
        else if(line == 0 && col == ncols-1 ){
            listNeighbor.add((line) * ncols + col-1);
            listNeighbor.add((line+1) * ncols + col);
        }

        // middle left
        else if(line != 0 && line != nlines-1 && col == 0 ){
            listNeighbor.add((line-1) * ncols + col);
            listNeighbor.add((line+1) * ncols + col);

            listNeighbor.add((line) * ncols + col+1);
        }

        // middle middle
        else if(line != 0 && line != nlines-1 && col != 0 && col != ncols-1){
            listNeighbor.add((line-1) * ncols + col);
            listNeighbor.add((line+1) * ncols + col);
            listNeighbor.add((line) * ncols + col+1);
            listNeighbor.add((line) * ncols + col-1);
        }

        // middle right
        else if(line != 0 && line != nlines-1 && col == ncols-1 ){
            listNeighbor.add((line-1) * ncols + col);
            listNeighbor.add((line+1) * ncols + col);

            listNeighbor.add((line) * ncols + col-1);
        }

        // bottom left
        else if(line == nlines-1 && col == 0 ){
            listNeighbor.add((line-1) * ncols + col);
            listNeighbor.add((line) * ncols + col+1);
        }

        // bottom middle
        else if(line == nlines-1 && col != 0 && col != ncols-1){
            listNeighbor.add((line) * ncols + col-1);
            listNeighbor.add((line) * ncols + col+1);

            listNeighbor.add((line-1) * ncols + col);
        }

        // bottom right
        else if(line == nlines-1 && col == ncols-1 ){
            listNeighbor.add((line-1) * ncols + col);
            listNeighbor.add((line) * ncols + col-1);
        }

        return listNeighbor;
    }

    public static double getEuclideanDistance(int line1, int line2, int col1, int col2){
        return Math.sqrt(Math.pow(line1-line2, 2)+ Math.pow(col1-col2,2));
    }

}
