package com.xian.freetype.bmp;

/**
 * Created by xian on 2019/4/15.
 */
public class BitmapHeader {
    int     fileSize;
    short   reserved1;
    short   reserved2;
    int     pixelsOffset;
    int     dibSize;
    int     width;
    int     height;
    short   planes;
    short   bitPerPixel;
    int     compressed;
    int     pixelSize;
    int     xPixelsPerMeter;
    int     yPixelsPerMeter;
    int     clrUsed;
    int     clrImportant;
    int     bytePerPixel;

    public String getString() {
        StringBuffer stringBuffer = new StringBuffer("fileSize:0x");
        stringBuffer.append(Integer.toHexString(fileSize));
        stringBuffer.append("\nreserved1:0x");
        stringBuffer.append(Integer.toHexString(reserved1));
        stringBuffer.append("\nreserved2:0x");
        stringBuffer.append(Integer.toHexString(reserved2));
        stringBuffer.append("\npixelsOffset:0x");
        stringBuffer.append(Integer.toHexString(pixelsOffset));
        stringBuffer.append("\ndibSize:0x");
        stringBuffer.append(Integer.toHexString(dibSize));
        stringBuffer.append("\nwidth:0x");
        stringBuffer.append(Integer.toHexString(width));
        stringBuffer.append("\nheight:0x");
        stringBuffer.append(Integer.toHexString(height));
        stringBuffer.append("\nplanes:0x");
        stringBuffer.append(Integer.toHexString(planes));
        stringBuffer.append("\nbitPerPixel:0x");
        stringBuffer.append(Integer.toHexString(bitPerPixel));
        stringBuffer.append("\ncompressed:0x");
        stringBuffer.append(Integer.toHexString(compressed));
        stringBuffer.append("\npixelSize:0x");
        stringBuffer.append(Integer.toHexString(pixelSize));
        stringBuffer.append("\nxPixelsPerMeter:0x");
        stringBuffer.append(Integer.toHexString(xPixelsPerMeter));
        stringBuffer.append("\nyPixelsPerMeter:0x");
        stringBuffer.append(Integer.toHexString(yPixelsPerMeter));
        stringBuffer.append("\nclrUsed:0x");
        stringBuffer.append(Integer.toHexString(clrUsed));
        stringBuffer.append("\nclrImportant:0x");
        stringBuffer.append(Integer.toHexString(clrImportant));

        return stringBuffer.toString();
    }
}