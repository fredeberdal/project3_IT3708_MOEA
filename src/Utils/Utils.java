package Utils;

import java.util.Random;

public class Utils {
    public static Tuple<Integer, Integer> toPixelCoordinates(int x, int xLength){
        return new Tuple<>(x % xLength, Math.floorDiv(x, xLength));
    }
    public static int toIndexGenotype(int width, int height, int widthLength){
        return height*widthLength + width;
    }

}
