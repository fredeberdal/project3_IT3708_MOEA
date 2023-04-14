package Utils;

import GA.Gene;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class Pixel {
    public final RGB color;
    public final int width;
    public final int height;
    private Map<Integer, Pixel> neighbours = new HashMap<>();

    public Pixel (RGB color, int width, int height) {
        this.color = color;
        this.width = width; // width position
        this.height = height; // height position
    }

    public Map<Integer, Pixel> getNeighbours() { return new HashMap<>(neighbours); }
    public void setNeighbours(Map<Integer, Pixel> neighbours) { this.neighbours = neighbours; }

    public Map<Integer, Pixel> directionalNeighbours() {
        return neighbours.entrySet().stream()
                .filter(e -> e.getKey() < 5 && e.getValue() != null)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    // Kan evt lage en metode som tar inn en int direction i stedet.
    public Pixel directionalNeighbour(Gene gene) {
        return switch(gene) {
            case RIGHT -> neighbours.get(1);
            case LEFT -> neighbours.get(2);
            case UP -> neighbours.get(3);
            case DOWN -> neighbours.get(4);
            case NONE -> this;
        };
    }

    public List<Gene> getValidGenes() {
        return directionalNeighbours().entrySet().stream().filter(e -> e.getValue() != null)
                .map(Map.Entry::getKey).map(Gene::fromInt).collect(Collectors.toList());
    }


    // Usikker på om disse overridene er nødvendige. Ble ikke brukt?
    @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof Pixel)) {
            return false;
        }
        Pixel pixel = (Pixel) o;
        return Objects.equals(color, pixel.color) && width == pixel.width && height == pixel.height;
    }

    @Override
    public int hashCode() {
        return Objects.hash(color, width, height);
    }

}
