package com.xian.freetype.bmp;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.util.Log;


/**
 * Created by xian on 2019/4/16.
 * <p>
 * bmp图片管理类
 */

public class BmpManager {
    private String TAG = "BmpManager";
    public static final int HORIZONTAL = 0;
    public static final int VERTICAL = 1;
    private Context context;
    private BitmapFile bitmapFile;

    public BmpManager(Context context) {
        this.context = context;
        this.bitmapFile = new BitmapFile(context);
    }


    /**
     * bmp 合并
     * @param bmp1 要合并的图片
     * @param bmp2 要合并的图片
     * @param o 要合并的方向  0 横向  1 纵向
     * @return 合并后的图片
     */
    public Bitmap bmpMerge(Bitmap bmp1, Bitmap bmp2, int o) {
        Bitmap retBmp;
        int width = bmp1.getWidth();
        if (bmp2.getWidth() != width) {
            if (HORIZONTAL == o) {
                int h2 = Math.max(bmp2.getHeight(), bmp1.getHeight());
                retBmp = Bitmap.createBitmap(width + bmp2.getWidth(), h2, Bitmap.Config.RGB_565);
                Canvas canvas = new Canvas(retBmp);
                canvas.drawColor(Color.WHITE);
                canvas.drawBitmap(bmp1, 0, 0, null);
                canvas.drawBitmap(bmp2, 0, bmp1.getHeight(), null);
            } else {
                int w = Math.max(bmp2.getWidth(), bmp1.getWidth());
                retBmp = Bitmap.createBitmap(w, bmp2.getHeight() + bmp1.getHeight(), Bitmap.Config.RGB_565);
                Canvas canvas = new Canvas(retBmp);
                canvas.drawColor(Color.WHITE);
                canvas.drawBitmap(bmp1, 0, 0, null);
                canvas.drawBitmap(bmp2, 0, bmp1.getHeight(), null);
            }
        } else {
            //两张图片宽度相等，则直接拼接。
            if (HORIZONTAL == o) {
                retBmp = Bitmap.createBitmap(width + bmp2.getWidth(), bmp1.getHeight(), Bitmap.Config.RGB_565);
                Canvas canvas = new Canvas(retBmp);
                canvas.drawBitmap(bmp1, 0, 0, null);
                canvas.drawBitmap(bmp2, width, 0, null);
            } else {
                retBmp = Bitmap.createBitmap(width, bmp1.getHeight() + bmp2.getHeight(), Bitmap.Config.RGB_565);
                Canvas canvas = new Canvas(retBmp);
                canvas.drawBitmap(bmp1, 0, 0, null);
                canvas.drawBitmap(bmp2, 0, bmp1.getHeight(), null);
            }
        }
        return retBmp;
    }

    public Bitmap resizeBitmap(Bitmap bitmap, int newWidth, int newHeight) {
        float scaleWidth = ((float) newWidth) / bitmap.getWidth();
        float scaleHeight = ((float) newHeight) / bitmap.getHeight();
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        Bitmap bmpScale = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        return bmpScale;
    }


    /**
     * bitmap 转 bmp数据
     *
     * @param bitmap
     * @return
     */
    public byte[] bitmapToBmpData(Bitmap bitmap) {
        int w = bitmap.getWidth();
        int h = bitmap.getHeight();
        int[] bitmapPixels = new int[w * h];
        Log.i(TAG, "宽度：" + w + " -高度-> " + h);
        bitmap.getPixels(bitmapPixels, 0, w, 0, 0, w, h);

        byte[][] pixels = new byte[h][w];
        StringBuilder d = new StringBuilder();
        int line = 0;
        int c = 0;
        for (int i = 1; i <= bitmapPixels.length; i++) {
            if (bitmapPixels[i - 1] == -1) {
                d.append("1");
                pixels[line][c++] = 1;
            } else {
                d.append("0");
                pixels[line][c++] = 0;
            }
            if (i % w == 0) {
                d.append("\n");
                line++;
                c = 0;
            }
        }
        Log.i(TAG, d.toString());

        d.delete(0, d.length());
        byte[] datas = new byte[bitmap.getWidth() * bitmap.getHeight() / 4];
        int count = 0;
        StringBuilder builder = new StringBuilder();
        byte bit[] = {0x01, 0x02, 0x04, 0x08, 0x10, 0x20, 0x40, (byte) 0x80};

        for (int i = pixels.length - 1; i >= 0; i--) {
            byte ret = 0x0;
            for (int j = 1; j <= pixels[0].length; j++) {
                if (pixels[i][j - 1] == 1) {
                    ret |= bit[7 - ((j - 1) % 8)];
                }
                if (j % 8 == 0) {
                    datas[count++] = ret;
                    builder.append(Integer.toHexString(ret) + " ");
                    ret = 0;
                }
            }

            if ((bitmap.getWidth() / 16) % 2 != 0) {
                if (count < datas.length)
                    datas[count++] = 0;
                if (count < datas.length)
                    datas[count++] = 0;

                builder.append(0 + " ");
                builder.append(0 + " ");
            }
        }


        try {
            int pixSize = datas.length;
            int fileSize = pixSize + 62;
            Log.i(TAG, "文件长度：" + fileSize + "  " + pixSize + " " + builder.toString());
            DataWriter writer = new DataWriter(fileSize);

            writer.writeBytes(new byte[]{0x42, 0x4d}, 2);

            writer.writeInt(fileSize);
            writer.writeShort((short) 0);
            writer.writeShort((short) 0);
            writer.writeInt(0x3e);

            writer.writeInt(0x28);
            writer.writeInt(w);
            writer.writeInt(h);
            writer.writeShort((short) 0x1);
            writer.writeShort((short) 0x1);
            writer.writeInt(0);
            writer.writeInt(pixSize);
            writer.writeInt(0xdc4);
            writer.writeInt(0xdc4);
            writer.writeInt(0);
            writer.writeInt(0);
            writer.writeInt(0);
            byte[] s1 = {(byte) 0xFF, (byte) 0xFF, (byte) 0xFF, 0};
            writer.writeBytes(s1, 4);

            writer.writeBytes(datas, pixSize);

            //writer.writeToFile(Environment.getExternalStorageDirectory().getAbsolutePath() + "/6.bmp");
            return writer.buf;
        } catch (Throwable throwable) {
            throwable.printStackTrace();
            return null;
        }
    }


    /**
     * 图片转点阵
     *
     * @param id 资源id
     * @return
     */
   /* public byte[] imgToLatticeById(int id) {
        try {
            bitmapFile.readBitmapById(id);
            byte[] pixels = bitmapFile.getPixels();
            byte[][] bytes = pixel2Bytes(pixels);
            Log.i("222222222f222", bitmapFile.getString());
            return ByteUtils.bytesToByte(bytes);
        } catch (Throwable e) {
            e.printStackTrace();
            return null;
        }
    }*/

    /**
     * 图片转点阵
     *
     * @param path 图片路径
     * @return
     */
   /* public byte[] imgToLatticeByPath(String path) {
        try {
            bitmapFile.readBitmapByPath(path);
            byte[] pixels = bitmapFile.getPixels();
            byte[][] bytes = pixel2Bytes(pixels);
            return ByteUtils.bytesToByte(bytes);
        } catch (Throwable e) {
            e.printStackTrace();
            return null;
        }
    }


    private byte[][] pixel2Bytes(byte[] pixels) {
        byte[][] arr = new byte[16][16];
        byte bit[] = {0x01, 0x02, 0x04, 0x08, 0x10, 0x20, 0x40, (byte) 0x80};
        for (int i = 0; i < 64; i += 4) {
            byte a = pixels[i];
            byte b = pixels[i + 1];

            for (int j = 0; j < 8; j++) {
                arr[15 - i / 4][7 - j] = 1;
                if ((a & bit[j]) == bit[j]) {
                    arr[15 - i / 4][7 - j] = 0;
                }
            }

            for (int j = 0; j < 8; j++) {
                arr[15 - i / 4][7 - j + 8] = 1;
                if ((b & bit[j]) == bit[j]) {
                    arr[15 - i / 4][7 - j + 8] = 0;
                }
            }
        }


        for (int i = 0; i < 16; i++) {
            StringBuilder builder = new StringBuilder();
            for (int j = 0; j < 16; j++) {
                if (arr[i][j] == 1) {
                    builder.append("1");
                } else {
                    builder.append("0");
                }
            }
            System.out.println(builder.toString());
        }
        return arr;
    }*/
}
