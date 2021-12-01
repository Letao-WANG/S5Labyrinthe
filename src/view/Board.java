package view;

import model.Graph;
import model.Vertex;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.util.HashMap;
import java.util.LinkedList;

public class Board extends JComponent
{
    private static final long serialVersionUID = 1L;
    Graph graph;
    int pixelSize;
    int ncols;
    int nlines;
    HashMap<Integer, String> colors;
    int start;
    int end;
    double max_distance;
    int current;
    LinkedList<Integer> path;

    public Board(Graph graph, int pixelSize, int ncols, int nlines, HashMap<Integer, String> colors, int start, int end)
    {
        super();
        this.graph = graph;
        this.pixelSize = pixelSize;
        this.ncols = ncols;
        this.nlines = nlines;
        this.colors = colors;
        this.start = start;
        this.end = end;
        this.max_distance = ncols * nlines;
        this.current = -1;
        this.path = null;
    }

    //Mise � jour de l'affichage
    public void paint(Graphics g)
    {
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        //Ugly clear of the frame
        g2.setColor(Color.cyan);
        g2.fill(new Rectangle2D.Double(0,0,this.ncols*this.pixelSize, this.nlines*this.pixelSize));


        int num_case = 0;
        for (Vertex v : this.graph.getVertexlist())
        {
            double type = v.getIndivTime();
            int i = num_case / this.ncols;
            int j = num_case % this.ncols;

            if (colors.get((int)type).equals("green"))
                g2.setPaint(Color.green);
            if (colors.get((int)type).equals("gray"))
                g2.setPaint(Color.gray);
            if (colors.get((int)type).equals("blue"))
                g2.setPaint(Color.blue);
            if (colors.get((int)type).equals("yellow"))
                g2.setPaint(Color.yellow);

            g2.fill(new Rectangle2D.Double(j*this.pixelSize, i*this.pixelSize, this.pixelSize, this.pixelSize));

            if (num_case == this.current)
            {
                g2.setPaint(Color.red);
                g2.draw(new Ellipse2D.Double(j*this.pixelSize+this.pixelSize/2, i*this.pixelSize+this.pixelSize/2, 6, 6));
            }
            if (num_case == this.start)
            {
                g2.setPaint(Color.white);
                System.out.println("start");
                g2.fill(new Ellipse2D.Double(j*this.pixelSize+this.pixelSize/2, i*this.pixelSize+this.pixelSize/2, 20, 20));

            }
            if (num_case == this.end)
            {
                g2.setPaint(Color.black);
                System.out.println("end");
                g2.fill(new Ellipse2D.Double(j*this.pixelSize+this.pixelSize/2, i*this.pixelSize+this.pixelSize/2, 20, 20));
            }

            num_case += 1;
        }

        num_case = 0;
        for (Vertex v : this.graph.getVertexlist())
        {
            int i = num_case / this.ncols;
            int j = num_case % this.ncols;
            if (v.getTimeFromSource() < Double.POSITIVE_INFINITY)
            {
                float g_value = (float) (1 - v.getTimeFromSource() / this.max_distance);
                if (g_value < 0)
                    g_value = 0;
                g2.setPaint(new Color(g_value, g_value, g_value));
                g2.fill(new Ellipse2D.Double(j*this.pixelSize+this.pixelSize/2, i*this.pixelSize+this.pixelSize/2, 4, 4));
                Vertex previous = v.getPrev();
                if (previous != null)
                {
                    int i2 = previous.getNum() / this.ncols;
                    int j2 = previous.getNum() % this.ncols;
                    g2.setPaint(Color.black);
                    g2.draw(new Line2D.Double(j * this.pixelSize + this.pixelSize/2, i * this.pixelSize + this.pixelSize/2, j2 * this.pixelSize + this.pixelSize/2, i2 * this.pixelSize + this.pixelSize/2));
                }
            }

            num_case += 1;
        }

        int prev = -1;
        if (this.path != null)
        {
            g2.setStroke(new BasicStroke(3.0f));
            for (int cur : this.path)
            {
                if (prev != -1)
                {
                    g2.setPaint(Color.red);
                    int i = prev / this.ncols;
                    int j = prev % this.ncols;
                    int i2 = cur / this.ncols;
                    int j2 = cur % this.ncols;
                    g2.draw(new Line2D.Double(j * this.pixelSize + this.pixelSize/2, i * this.pixelSize + this.pixelSize/2, j2 * this.pixelSize + this.pixelSize/2, i2 * this.pixelSize + this.pixelSize/2));
                }
                prev = cur;
            }
        }
    }

    //Mise � jour du graphe (� appeler avant de mettre � jour l'affichage)
    public void update(Graph graph, int current)
    {
        this.graph = graph;
        this.current = current;
        repaint();
    }

    //Indiquer le chemin (pour affichage)
    public void addPath(Graph graph, LinkedList<Integer> path)
    {
        this.graph = graph;
        this.path = path;
        this.current = -1;
        repaint();
    }
}