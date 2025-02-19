package com.inzynierka;

import ij.IJ;
import ij.ImagePlus;
import ij.ImageStack;
import ij.process.ImageProcessor;

public class SNR {

    public static final double snrCounter(
            ImageStack refImage,
            ImageStack testImage) {

        double r = 0.0, t = 0.0, ROI = 0.0;
        double snr = 0.0, mean_noise =0.0, sd_noise = 0.0;
        int N = refImage.getWidth() * refImage.getHeight();

        int bits_per_pixel_ref = refImage.getBitDepth();
        int bits_per_pixel_test = testImage.getBitDepth();

        if (bits_per_pixel_ref != bits_per_pixel_test) {
            IJ.error("ERROR: Images must have the same number of bits per pixel");
        }

        ImageProcessor lRef = refImage.getProcessor(1);
        ImageProcessor lTest = testImage.getProcessor(1);

        int width = refImage.getWidth();
        int height = refImage.getHeight();
        double[][]  noise = new double[width][height];


        //ROI = reference image
        for (int y = 0; y < refImage.getWidth(); y++) {
            for (int x = 0; x < refImage.getHeight(); x++) {
                r = lRef.getPixel(y, x);
                t = lTest.getPixel(y, x);
                ROI += r;
                mean_noise += Math.abs(t - r);
                noise[y][x] = Math.abs(t - r);
            }
        }

        ROI /= N;
        mean_noise /= N;

        for (int y = 0; y < refImage.getWidth(); y++) {
            for (int x = 0; x < refImage.getHeight(); x++) {
                sd_noise += Math.pow((noise[y][x] - mean_noise), 2);
            }
        }
        snr = ROI / Math.sqrt(sd_noise / (N - 1)) ;

        return 10.0 * Math.log10(snr);
    }
    public static final double getSNR(ImagePlus refImage, ImagePlus testImage) {
        return snrCounter(refImage.getStack(), testImage.getStack());
    }
}
