package com.balazs.visual_diff.Compare;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.ByteArrayInputStream;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.springframework.stereotype.Service;

import com.balazs.visual_diff.Utilities.CompareUtilities;
import com.balazs.visual_diff.Utilities.FileUtilities;

@Service
public class CompareService {

    /**
     * Starts the compare process between the baseline image and the comparison image. 
     * Converts incoming byte[] arrays to BufferedImages.
     *
     * @param byte[] baselineData
     * @param byte[] comparisonData
     * @return byte[] diffData of the diff image
     * @throws IOException
     */
    public byte[] compare(byte[] baselineData, byte[] comparisonData) throws IOException {
        //Convert baseline file to BufferedImage
        ByteArrayInputStream baselineByteArrayInputStream = new ByteArrayInputStream(baselineData);
        BufferedImage baselineImg = ImageIO.read(baselineByteArrayInputStream);
        baselineByteArrayInputStream.close();

        //Convert comparison file to BufferedImage
        ByteArrayInputStream comparisonByteArrayInputStream = new ByteArrayInputStream(comparisonData);
        BufferedImage comparisonImg = ImageIO.read(comparisonByteArrayInputStream);
        comparisonByteArrayInputStream.close();

        //Get diff image, and return diff as byte array
        BufferedImage diffImage = createDiffImage(baselineImg, comparisonImg);
        return FileUtilities.convertBufferedImageToByteArray(diffImage, FileUtilities.FORMAT_PNG);
    }

    /**
     * Creates a diffImage from the baseline and comparison images.
     *
     * @param BufferedImage baselineImg
     * @param BufferedImage compareImg
     * @return BufferedImage diffImage of the baseline and comparison images
     */
    public BufferedImage createDiffImage(BufferedImage baselineImg, BufferedImage compareImg) {
        //Convert images to RGBA byte[]. Output will be W * H * 4 since each pixel will be 4 indices long 
        //[r, g, b, a, r, g, b, a], etc. getRater() holds the pixel data, getDataBuffer() return generic buffer, which 
        //is casted to DataBufferByte which stores values as byte[], getData() is the actual byte[]
        int width = baselineImg.getWidth();
        int height = baselineImg.getHeight();
        byte[] baselineRGBA = ((DataBufferByte) baselineImg.getRaster().getDataBuffer()).getData();
        byte[] compareRGBA = ((DataBufferByte) compareImg.getRaster().getDataBuffer()).getData();
        byte[] outputRGBA = new byte[baselineRGBA.length];

        //Compare Images. Check byte[] lengths, check matching widths/heights
        CompareUtilities.comparePixels(baselineRGBA, compareRGBA, outputRGBA, width, height);

        //Create diff image. Create a new BufferedImage witht eh same dimensiosn as original, create a byte array from 
        //the exmpty image, then copy the output data from the PixelMatch process into the new empty array and create a
        //new iamge file "diff.png"
        BufferedImage diffImage = new BufferedImage(width, height, BufferedImage.TYPE_4BYTE_ABGR);
        byte[] diffData = ((DataBufferByte) diffImage.getRaster().getDataBuffer()).getData();
        System.arraycopy(outputRGBA, 0, diffData, 0, outputRGBA.length);

        return diffImage;
    }
}
