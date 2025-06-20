package com.balazs.visual_diff.Utilities;

public final class CompareUtilities {
    
    private final static int COLOUR_MAX_VALUE = 255;
    private final static float ALPHA = 0.1f;                    //Used for drawing gray pixels to lighten the intensity of the final value
    private final static byte[] DIFF_COLOUR = {(byte)255, 0, 0};
    private final static int MAX_DELTA = 352;                   //Maximum delta in YIQ colour space where anything less, colours would appear too similar
    private final static int GRAY = 48;                         //Applied to transparent pixels to make it light gray and visible in the diff mask

    private CompareUtilities() { }

    /**
     * Compares all pixels from the incoming baselineRGBA and compareRGBA byte[] arrays pixel by pixel. byte[] arrays in 
     * the format of {r, g, b, a, r, g, b, a}. outputRGBA holds the diff output between the baseline and comparison
     * pixels after comparison. Any delta > MAX_DELTA will be drawn DIFF_COLOUR, otherwise it will be drawn gray.
     *
     * @param byte[] baselineRGBA byte[] array of the baseline image in RGBA format
     * @param byte[] compareRGBA byte[] array of the comparison image in RGBA format
     * @param byte[] outputRGBA byte[] array of the output image in RGBA format
     * @param int width of the baseline and comaprison images
     * @param int height of the baseline and comaprison images
     */
    public static void comparePixels(byte[] baselineRGBA, byte[] compareRGBA, byte[] outputRGBA, int width, int height) {
        int numPixels = width * height;

        //Compare pixels one by one. If delta is greater than the threshold
        //then change it to the diff colour (red), otherwise, change it to be gray
        //index*4 since byte[] is 4 values per pixel, r, g, b, a
        for (int i = 0; i < numPixels; i++) {
            int index = i * 4;
            int delta = calculateDelta(baselineRGBA, compareRGBA, index);

            //Make pixel DIFF_COLOUR (Red) if the delta is greater than the threshold
            if (Math.abs(delta) > MAX_DELTA) {
                drawPixel(outputRGBA, DIFF_COLOUR, index);
            }
            //Make pixel gray if the delta is less than the threshold
            else {
                drawGrayPixel(baselineRGBA, outputRGBA, index);
            }
        }
    }

    /**
     * Fill outputRGBA byte[] array at index with the incoming rgba values from incomingRGBA.
     *
     * @param byte[] outputRGBA byte[] array of the output image in RGBA format
     * @param int[] incomingRGBA int[] array of the incoming pixel. A single {r, g, b, a}
     * @param int index of the pixel to be copied
     */
    public static void drawPixel(byte[] outputRGBA, byte[] incomingRGBA, int index) {
        outputRGBA[index] = incomingRGBA[0];
        outputRGBA[index + 1] = incomingRGBA[1];
        outputRGBA[index + 2] = incomingRGBA[2];
        outputRGBA[index + 3] = (byte)COLOUR_MAX_VALUE;
    }

    /**
     * Calculates RGB values of gray pixel
     *
     * @param byte[] baselineRGBA byte[] array of the baseline image in RGBA format
     * @param byte[] outputRGBA byte[] array of the output image in RGBA format
     * @param int index of the pixel to be copied
     */
    public static void drawGrayPixel(byte[] baselineRGBA, byte[] outputRGBA, int index) {
        final float LUMINANCE_COEFFICIENT_1 = 0.29889531f;
        final float LUMINANCE_COEFFICIENT_2 = 0.58662247f;
        final float LUMINANCE_COEFFICIENT_3 = 0.11448223f;

        float value = 255 + (baselineRGBA[index] * LUMINANCE_COEFFICIENT_1 + baselineRGBA[index + 1] * LUMINANCE_COEFFICIENT_2 + baselineRGBA[index + 2] * LUMINANCE_COEFFICIENT_3 - 255) * ALPHA * baselineRGBA[index + 3] / 255.0f;
        byte gray = (byte)Math.max(0, Math.min(255, value)); //Takes either 255 or the brightness otherwise it may end up black
        drawPixel(outputRGBA, new byte[] {gray, gray, gray}, index);
    }

    /**
     * Calculates delta for RGBA between baseline, and comparison images, then applies gray if transparency is involved, 
     * converts delta to YIQ colour space. YIQ separates luminance and chrominance similar to HSL. Old televisions used 
     * YIQ to separate lumaniance from chrominance for compatibility with black and white. Humans can see difference in 
     * brightness much easier than difference in hue.
     * 
     * @param byte[] baselineRGBA byte[] array of the baseline image in RGBA format
     * @param byte[] compareRGBA byte[] array of the comparison image in RGBA format
     * @param int index of the pixel to be evaluated
     * @return int delta of the pixel
     */
    public static int calculateDelta(byte[] baselineRGBA, byte[] compareRGBA, int index) {
        //Calculate delta from baseline RGBA and compare RGBA values
        float deltaR = calculateColourDelta(baselineRGBA[index], compareRGBA[index]);
        float deltaG = calculateColourDelta(baselineRGBA[index+1], compareRGBA[index+1]);
        float deltaB = calculateColourDelta(baselineRGBA[index+2], compareRGBA[index+2]);
        float deltaA = calculateColourDelta(baselineRGBA[index+3], compareRGBA[index+3]);

        //Used for transparency. If pixel's Alpha in baselineRGBA or compareRGBA aren't COLOUR_MAX_VALUE (255), 
        //Then add gray onto the pixel to make it whiteish for the diff mask
        if (baselineRGBA[index+3] < 255 || compareRGBA[index+3] < 255) {
            deltaR = applyTransparency(baselineRGBA[index], baselineRGBA[index+3], compareRGBA[index], compareRGBA[index+3], deltaA);
            deltaG = applyTransparency(baselineRGBA[index+1], baselineRGBA[index+3], compareRGBA[index+1], compareRGBA[index+3], deltaA);
            deltaB = applyTransparency(baselineRGBA[index+2], baselineRGBA[index+3], compareRGBA[index+2], compareRGBA[index+3], deltaA);
        }

        //Y = Luminance
        //I = Chrominance for Orange/Blue
        //Q = Chrominance for Purple/Green
        float y = calculateY(deltaR, deltaG, deltaB);
        float i = calculateI(deltaR, deltaG, deltaB);
        float q = calculateQ(deltaR, deltaG, deltaB);
        float delta = calculateCombinedDelta(y, i, q);
        return (int)delta;
    }

    /**
     * Calculates the delta between the value of the baseline and comparison channel. This method 
     * only comapres a single value of a single pixel (R, G, B, or A)
     *
     * @param byte baselineChannel - Incoming baseline R, G, B, or A value
     * @param byte compareChannel - Incoming compare R, G, B, or A value
     * @return delta of baseline - compare value
     */
    public static float calculateColourDelta(byte baselineChannel, byte compareChannel) {
        int baseline = Byte.toUnsignedInt(baselineChannel);
        int compare = Byte.toUnsignedInt(compareChannel);
        return baseline - compare;
    }

    /**
     * Used for transparency. If pixel's Alpha in baselineRGBA or compareRGBA aren't COLOUR_MAX_VALUE, 
     * blends pixel into the background. This is done by applying gray onto it
     *
     * @param int baselineChannel - Incoming baseline R, G, or B value
     * @param int baselineA - Alpha value of baselineRGBA array
     * @param int compareChannel - Incoming comparison R, G, or B value
     * @param int compareA - Alpha value of compareRGBA array
     * @param float deltaA - Change in alpha from baselineRGBA to comapreRGBA
     * @return newDelta
     */
    public static float applyTransparency(int baselineChannel, int baselineA, int compareChannel, int compareA, float deltaA) {
        float newDelta = (baselineChannel * baselineA - compareChannel * compareA - GRAY * deltaA) / 255f;
        return newDelta;
    }

    /**
     * Calculates Y (luminance) in YIQ given the incoming RGB values. Uses matrix multiplication 3x3 
     * matrix with 3x1 RGB values.
     *
     * @param float deltaR R value from baseline - comparison
     * @param float deltaG G value from baseline - comparison
     * @param float deltaB B value from baseline - comparison
     * @return float Y (Luminance)
     */
    public static float calculateY(float deltaR, float deltaG, float deltaB) {
        final float LUMINANCE_COEFFICIENT_1 = 0.29889531f;
        final float LUMINANCE_COEFFICIENT_2 = 0.58662247f;
        final float LUMINANCE_COEFFICIENT_3 = 0.11448223f;

        float y = (deltaR * LUMINANCE_COEFFICIENT_1) + (deltaG * LUMINANCE_COEFFICIENT_2) + (deltaB * LUMINANCE_COEFFICIENT_3);
        return y;
    }

    /**
     * Calculates I (chromanince Orange/Blue) in YIQ given the incoming RGB values. Uses matrix multiplication 3x3 
     * matrix with 3x1 RGB values.
     *
     * @param float deltaR R value from baseline - comparison
     * @param float deltaG G value from baseline - comparison
     * @param float deltaB B value from baseline - comparison
     * @return float I (Orange/Blue Chrominance)
     */
    public static float calculateI(float deltaR, float deltaG, float deltaB) {
        final float CHROMINANCE_COEFFICIENT_1 = 0.59597799f;
        final float CHROMINANCE_COEFFICIENT_2 = 0.27417610f;
        final float CHROMINANCE_COEFFICIENT_3 = 0.32180189f;

        float i = (deltaR * CHROMINANCE_COEFFICIENT_1) - (deltaG * CHROMINANCE_COEFFICIENT_2) - (deltaB * CHROMINANCE_COEFFICIENT_3);
        return i;
    }

    /**
     * Calculates Q (chromanince Purple/Green) in YIQ given the incoming RGB values. Uses matrix multiplication 3x3 
     * matrix with 3x1 RGB values.
     *
     * @param float deltaR R value from baseline - comparison
     * @param float deltaG G value from baseline - comparison
     * @param float deltaB B value from baseline - comparison
     * @return float Q (Purple/Green Chrominance)
     */
    public static float calculateQ(float deltaR, float deltaG, float deltaB) {
        final float CHROMINANCE_COEFFICIENT_1 = 0.21147017f;
        final float CHROMINANCE_COEFFICIENT_2 = 0.52261711f;
        final float CHROMINANCE_COEFFICIENT_3 = 0.31114694f;

        float q = (deltaR * CHROMINANCE_COEFFICIENT_1) - (deltaG * CHROMINANCE_COEFFICIENT_2) + (deltaB * CHROMINANCE_COEFFICIENT_3);
        return q;
    }

    /**
     * Calculates the combined delta which is the perceptual delta after RGB has been 
     * converted to YIQ colour space. Used to gauge how different 2 pixels are in YIQ 
     * colour space.
     *
     * @param float y (luminance brightness)
     * @param float i (Chrominance Orange/Blue)
     * @param float q (Chrominance Purple/Green)
     */
    public static float calculateCombinedDelta(float y, float i, float q) {
        final float DELTA_WEIGHT_Y = 0.5053f;
        final float DELTA_WEIGHT_I = 0.299f;
        final float DELTA_WEIGHT_Q = 0.1957f;

        float delta = DELTA_WEIGHT_Y * y * y + DELTA_WEIGHT_I * i * i + DELTA_WEIGHT_Q * q * q;
        return delta;
    }
}
