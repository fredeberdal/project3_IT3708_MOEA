package Utils;

import java.util.Random;

public class Utils {
    private static Random random = new Random();
    public static Pair<Integer, Integer> toPixelCoordinates(int x, int xLength){
        return new Pair<>(x % xLength, Math.floorDiv(x, xLength));
    }

    public static <T> T selectRandom(T i, T j) {
        return random.nextDouble() < 0.5 ? i : j;
    }

    public static int toIndexGenotype(int width, int height, int widthLength){
        return (height*widthLength) + width;
    }

}
