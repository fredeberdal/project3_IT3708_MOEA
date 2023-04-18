package Utils;

import java.util.Random;

public class Utils {
    private static Random random = new Random();
    public static Tuple<Integer, Integer> toPixelCoordinates(int x, int xLength){
        return new Tuple<>(x % xLength, Math.floorDiv(x, xLength));
    }

    public static int toIndexGenotype(int width, int height, int widthLength){
        return height*widthLength + width;
    }

    public static double randomDouble(){
        return random.nextDouble();
    }
    public static int randomInt(int i){
        return random.nextInt(i);
    }


}
