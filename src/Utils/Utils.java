package Utils;

public class Utils {
    public static Tuple<Integer, Integer> toPixelCoordinates(int x, int xLength){
        return new Tuple<>(x % xLength, Math.floorDiv(x, xLength));
    }
}
