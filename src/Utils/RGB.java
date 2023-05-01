package Utils;

import java.awt.Color;
import java.util.Objects;

public class RGB {

    public final int r;
    public final int g;
    public final int b;

    public RGB (int r, int g, int b) {
        this.r = r;
        this.g = g;
        this.b = b;
    }

    public static RGB black = new RGB (0, 0, 0);
    public static RGB green = new RGB (0, 255, 0);
    public static RGB white = new RGB (255, 255, 255);

    public int findRGBInt () { return new Color(r, g, b).getRGB(); }

    @Override
    public int hashCode() { return Objects.hash(r, g, b); }

    @Override
    public boolean equals(Object object) {
        if (object == this)  {return true;}
        if (!(object instanceof RGB)) {return false;}

        RGB rgb = (RGB) object;

        return r == rgb.r
                && b == rgb.b
                && g == rgb.g;
    }
}
