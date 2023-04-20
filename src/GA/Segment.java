package GA;

import Utils.Pixel;
import Utils.RGB;

import java.util.Set;

public class Segment {

    public RGB centroid;
    public double edgeValue, dev, connectivity;
    public Set<Pixel> pixels;

    public Segment(Set <Pixel> pixels){
        this.pixels = pixels;
        setCentroid(locateCentroid());
        this.dev = Fitness.deviation(this);
        this.connectivity = Fitness.segConnectivity(this);
        this.edgeValue = Fitness.calculateEdgeValue(this);
    }

    public boolean hasPixel(Pixel p){
        if(p != null){
            if (pixels.contains(pixels)) {
                return true;
            }
        }
        return false;
    }

    public RGB getCentroid() {
        return this.centroid;
    }
    public RGB locateCentroid(){
        int size = this.pixels.size();
        int r = 0, g = 0, b = 0;
        for(Pixel p: this.pixels){
            r += p.color.r; g += p.color.g; b+= p.color.b;
        }
        int size_r = r/size;
        int size_g = g/size;
        int size_b = b/size;
        RGB locatedCentroid = new RGB(size_r, size_g, size_b);
        return locatedCentroid;
    }

    public void setCentroid(RGB centroid) {
        this.centroid = centroid;
    }

    public double getEdgeValue() {
        return edgeValue;
    }

    public void setEdgeValue(double edgeValue) {
        this.edgeValue = edgeValue;
    }

    public double getDev() {
        return dev;
    }

    public void setDev(double dev) {
        this.dev = dev;
    }

    public double getConnectivity() {
        return connectivity;
    }
    public void addAllPixels(Set<Pixel>pixels){
        this.pixels.addAll(pixels);
    }

    public void setConnectivity(double connectivity) {
        this.connectivity = connectivity;
    }

    public Set<Pixel> getPixels() {
        return pixels;
    }

    public void setPixels(Set<Pixel> pixels) {
        this.pixels = pixels;
    }
}
