package GA;

import java.util.Arrays;
import java.util.List;

public enum Gene {
    RIGHT, LEFT, UP, DOWN, NONE;

    public static Gene fromInt(int num) {
        return switch (num) { //endres kanskje
            case 1 -> RIGHT;
            case 2 -> LEFT;
            case 3 -> UP;
            case 4 -> DOWN;
            case 0 -> NONE;
            default -> throw new IllegalArgumentException("Not a valid value : " + num);
        };
    }

    public static Gene fromVector (int x, int y) {
        return switch(x) {
            case -1 -> LEFT;
            case  1 -> RIGHT;
            case  0 -> switch(y) {
                case -1 -> UP;
                case  1 -> DOWN;
                case  0 -> NONE;
                default -> throw new IllegalArgumentException("This is not a unit vector : " + x + " " + y);
            };
            default -> throw new IllegalArgumentException("This is not a unit vector: " + x + " " + y);
        };
    }

    public static List<Gene> geneDirections() {
        return Arrays.asList(RIGHT, LEFT, UP, DOWN); // Må kanskje endres for å lage en: new Gene[]
    }


}
