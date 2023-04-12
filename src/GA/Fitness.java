package GA;

import Utils.RGB;

public class Fitness {



    public static double distance(RGB i, RGB j) {
        return Math.sqrt(
                Math.pow(Math.abs(j.r-i.r), 2)
                        + Math.pow(Math.abs(j.g-i.g), 2)
                        + Math.pow(Math.abs(j.b-i.b), 2)
        );
    }

}
