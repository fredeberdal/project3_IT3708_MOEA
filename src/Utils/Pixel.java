package Utils;

import java.util.HashMap;
import java.util.Map;

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


    /*
    Må nok implementeres når vi vet hva de skal brukes til

    public List<Gene> getValidGenes(){
        return getCardinalNeighbours().entrySet()
                                      .stream()
                                      .filter(entry -> entry.getValue() != null)
                                      .map(Entry::getKey)
                                      .map(Gene::fromNumber)
                                      .collect(Collectors.toList());
    }

        public Map<Integer, Pixel> getCardinalNeighbours() {
        return neighbours.entrySet()
                         .stream()
                         .filter(entry -> entry.getKey() < 5 && entry.getValue() != null)
                         .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    public Pixel getCardinalNeighbour(Gene gene) {
        return switch(gene) {
            case RIGHT -> neighbours.get(1);
            case LEFT  -> neighbours.get(2);
            case UP    -> neighbours.get(3);
            case DOWN  -> neighbours.get(4);
            case NONE  -> this;
        };
    }

    DE UNDER BLE IKKE BRUKT AV OWREN

    public Pixel getCardinalNeighbour(int direction){
        return direction == 0 ? this : neighbours.get(direction);
    }

    @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof Pixel)) {
            return false;
        }
        Pixel pixel = (Pixel) o;
        return Objects.equals(color, pixel.color) && x == pixel.x && y == pixel.y;
    }

    @Override
    public int hashCode() {
        return Objects.hash(color, x, y);
    }
     */

}
