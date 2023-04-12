package Utils;

import GA.Fitness;

public class Edge implements Comparable<Edge> {
    public Pixel from, to;
    public double distance;

    public Edge (Pixel from, Pixel to) {
        this.from = from;
        this.to = to;
        this.distance = Fitness.distance(from.color, to.color);
    }

    public int compareTo  (Edge edge) {
        if (this.distance > edge.distance) return 1;
        if (this.distance < edge.distance) return -1;
        return 0;
    }

}
