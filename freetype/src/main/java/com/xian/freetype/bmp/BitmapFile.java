package com.xian.freetype.bmp;


import android.content.Context;

public class BitmapFile {
    String fileName;
    byte type[];
    byte data[];

    byte pixels[];
    byte outPixels[];

    BitmapHeader dibHeader;
    Context context;

    public BitmapFile() {
        type = new byte[2];
        type[0] = 0x42;
        type[1] = 0x4d;
    }

    public BitmapFile(Context context) {
        this.context = context;
        dibHeader = new BitmapHeader();
    }

    public String getPixelsHex() {
        if (pixels == null) {
            return null;
        }

        return ByteUtils.bytesToHexString(pixels);
    }

    public String getDatasHex() {
        if (data == null) {
            return null;
        }

        return ByteUtils.bytesToHexString(data);
    }

    public byte[] getPixels() {
        return pixels;
    }

    public byte[] getDatas() {
        return data;
    }

    public int setPixels(byte[] pixels) {
        this.pixels = pixels;
        return pixels.length;
    }

    public int setOutPixels(byte[] pixels) {
        this.outPixels = pixels;
        return outPixels.length;
    }


    public boolean setDibHeader(BitmapHeader header) {
        dibHeader = header;
        return true;
    }

    public boolean inversionPixels() {
        outPixels = new byte[pixels.length];
        for (int i = 0; i < pixels.length; i++) {
            outPixels[i] = (byte) (0xff - pixels[i]);
        }

        return true;
    }

    public String getString() {
        StringBuffer stringBuffer = new StringBuffer("Bitmap Info \ntype:");
        stringBuffer.append(ByteUtils.bytesToHexString(type));
        stringBuffer.append("\n");
        stringBuffer.append(dibHeader.getString());
        stringBuffer.append("\npixels:");
        stringBuffer.append(getPixelsHex());

        return stringBuffer.toString();
    }


    public boolean readBitmapById(int id) throws Exception {
        DataReader reader = new DataReader();
        reader.readFromFile(context, id);

        type = reader.readType();

        dibHeader.fileSize = reader.readInt();
        dibHeader.reserved1 = reader.readShort();
        dibHeader.reserved2 = reader.readShort();
        dibHeader.pixelsOffset = reader.readInt();

        dibHeader.dibSize = reader.readInt();
        dibHeader.width = reader.readInt();
        dibHeader.height = reader.readInt();
        dibHeader.planes = reader.readShort();
        dibHeader.bitPerPixel = reader.readShort();
        dibHeader.compressed = reader.readInt();
        dibHeader.pixelSize = reader.readInt();
        dibHeader.xPixelsPerMeter = reader.readInt();
        dibHeader.yPixelsPerMeter = reader.readInt();
        dibHeader.clrUsed = reader.readInt();
        dibHeader.clrImportant = reader.readInt();
        dibHeader.bytePerPixel = reader.readInt();

        data = reader.readBytesFrom(0);

        pixels = reader.readBytesFrom(dibHeader.pixelsOffset);

        return true;
    }


    public boolean readBitmapByPath(String fileName) throws Exception {
        DataReader reader = new DataReader();
        reader.readFromFile(fileName);

        this.fileName = fileName;
        type = reader.readType();

        dibHeader.fileSize = reader.readInt();
        dibHeader.reserved1 = reader.readShort();
        dibHeader.reserved2 = reader.readShort();
        dibHeader.pixelsOffset = reader.readInt();

        dibHeader.dibSize = reader.readInt();
        dibHeader.width = reader.readInt();
        dibHeader.height = reader.readInt();
        dibHeader.planes = reader.readShort();
        dibHeader.bitPerPixel = reader.readShort();
        dibHeader.compressed = reader.readInt();
        dibHeader.pixelSize = reader.readInt();
        dibHeader.xPixelsPerMeter = reader.readInt();
        dibHeader.yPixelsPerMeter = reader.readInt();
        dibHeader.clrUsed = reader.readInt();
        dibHeader.clrImportant = reader.readInt();
        dibHeader.bytePerPixel = reader.readInt();

        data = reader.readBytesFrom(0);

        pixels = reader.readBytesFrom(dibHeader.pixelsOffset);

        return true;
    }


    public byte[] saveData() throws Exception {
        DataWriter writer = new DataWriter(dibHeader.fileSize);

        writer.writeBytes(type, 2);

        writer.writeInt(dibHeader.fileSize);
        writer.writeShort(dibHeader.reserved1);
        writer.writeShort(dibHeader.reserved2);
        writer.writeInt(dibHeader.pixelsOffset);

        writer.writeInt(dibHeader.dibSize);
        writer.writeInt(dibHeader.width);
        writer.writeInt(dibHeader.height);
        writer.writeShort(dibHeader.planes);
        writer.writeShort(dibHeader.bitPerPixel);
        writer.writeInt(dibHeader.compressed);
        writer.writeInt(dibHeader.pixelSize);
        writer.writeInt(dibHeader.xPixelsPerMeter);
        writer.writeInt(dibHeader.yPixelsPerMeter);
        writer.writeInt(dibHeader.clrUsed);
        writer.writeInt(dibHeader.clrImportant);
        writer.writeInt(0);
        byte[] s = {(byte) 0xFF, (byte) 0xFF, (byte) 0xFF, 0};
        writer.writeBytes(s, 4);

        writer.writeBytes(outPixels, outPixels.length);

        return writer.buf;
    }


    public int saveBitmap(String fileName) throws Exception {
        DataWriter writer = new DataWriter(dibHeader.fileSize);

        writer.writeBytes(type, 2);

        writer.writeInt(dibHeader.fileSize);
        writer.writeShort(dibHeader.reserved1);
        writer.writeShort(dibHeader.reserved2);
        writer.writeInt(dibHeader.pixelsOffset);

        writer.writeInt(dibHeader.dibSize);
        writer.writeInt(dibHeader.width);
        writer.writeInt(dibHeader.height);
        writer.writeShort(dibHeader.planes);
        writer.writeShort(dibHeader.bitPerPixel);
        writer.writeInt(dibHeader.compressed);
        writer.writeInt(dibHeader.pixelSize);
        writer.writeInt(dibHeader.xPixelsPerMeter);
        writer.writeInt(dibHeader.yPixelsPerMeter);
        writer.writeInt(dibHeader.clrUsed);
        writer.writeInt(dibHeader.clrImportant);
        writer.writeInt(0);
        byte[] s = {(byte) 0xFF, (byte) 0xFF, (byte) 0xFF, 0};
        writer.writeBytes(s, 4);

        writer.writeBytes(outPixels, outPixels.length);

        writer.writeToFile(fileName);

        return dibHeader.fileSize;
    }


}
