import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

public class ImageRotator {

    public static BufferedImage rotate(BufferedImage inputImage, double angle) {

        // Calculate size of output image
        double radians = Math.toRadians(angle);
        double sin = Math.abs(Math.sin(radians));
        double cos = Math.abs(Math.cos(radians));
        int newWidth = (int) Math.round(inputImage.getWidth() * cos + inputImage.getHeight() * sin);
        int newHeight = (int) Math.round(inputImage.getHeight() * cos + inputImage.getWidth() * sin);

        // Create intermediate array for super-sampled image
        double[][][] intermediate = new double[newWidth][newHeight][3];

        // Rotate each pixel of input image and place it in intermediate array
        for (int x = 0; x < inputImage.getWidth(); x++) {
            for (int y = 0; y < inputImage.getHeight(); y++) {
                Color color = new Color(inputImage.getRGB(x, y));
                double[] rotatedXY = rotatePoint(x, y, inputImage.getWidth() / 2.0, inputImage.getHeight() / 2.0, radians);
                addColorToIntermediate(intermediate, rotatedXY[0], rotatedXY[1], color);
            }
        }

        // Create output image from intermediate array
        BufferedImage outputImage = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_RGB);
        for (int x = 0; x < newWidth; x++) {
            for (int y = 0; y < newHeight; y++) {
                int r = (int) Math.round(intermediate[x][y][0]);
                int g = (int) Math.round(intermediate[x][y][1]);
                int b = (int) Math.round(intermediate[x][y][2]);
                outputImage.setRGB(x, y, new Color(r, g, b).getRGB());
            }
        }

        return outputImage;
    }

    private static double[] rotatePoint(double x, double y, double cx, double cy, double angle) {
        double cos = Math.cos(angle);
        double sin = Math.sin(angle);
        double nx = cos * (x - cx) + sin * (y - cy) + cx;
        double ny = -sin * (x - cx) + cos * (y - cy) + cy;
        return new double[] {nx, ny};
    }

    private static void addColorToIntermediate(double[][][] intermediate, double x, double y, Color color) {
        // Calculate the four corners of the square that the pixel will cover
        int x1 = (int) Math.floor(x);
        int y1 = (int) Math.floor(y);
        int x2 = (int) Math.ceil(x);
        int y2 = (int) Math.ceil(y);

        // Calculate the area of each of the four "fragments" that the pixel is split into
        double area1 = (x2 - x) * (y2 - y);
        double area2 = (x - x1) * (y2 - y);
        double area3 = (x2 - x) * (y - y1);
        double area4 = (x - x1) * (y - y1);

        // Add the appropriate proportion of color to each of the four "fragments"
        addColorToIntermediatePixel(intermediate, x1, y