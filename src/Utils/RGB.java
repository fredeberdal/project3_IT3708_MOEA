package Utils;

import java.awt.Color;

public class RGB {

    public final int r;
    public final int g;
    public final int b;

    public RGB (int r, int g, int b) {
        this.r = r;
        this.g = g;
        this.b = b;
    }

    // Skal brukes når vi skal lese av bildene
    public static RGB green = new RGB (0, 255, 0);
    public static RGB black = new RGB (0, 0, 0);
    public static RGB white = new RGB (255, 255, 255);

    public int findRGBInt () { return new Color(r, g, b).getRGB(); }

    /*
    Ikke blitt brukt av OWREN

        @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof RGB)) {
            return false;
        }
        RGB rgb = (RGB) o;
        return r == rgb.r && g == rgb.g && b == rgb.b;
    }

    @Override
    public int hashCode() { return Objects.hash(r, g, b); }
     */


}
