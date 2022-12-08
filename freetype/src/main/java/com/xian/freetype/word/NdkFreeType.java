package com.xian.freetype.word;

import android.util.Log;

import java.nio.ByteBuffer;

/**
 * Created by xian on 2019/4/24.
 */

public class NdkFreeType {
    public static final int HORIZONTAL = 0;
    public static final int VERTICAL = 1;

    private static String TAG = "NdkFreeType";

    static {
        System.loadLibrary("freetype");
    }

    /**
     * 初始化freetype
     *
     * @param jFontPath 字体路径
     * @return
     */
    public static native boolean FT_Init_FreeType(String jFontPath);


    /**
     * 获取字的信息
     *
     * @param fontSize 字的大小
     * @param charCode 字符编码
     * @return
     */
    public static native WordInfo FT_GET_Word_Info(int fontSize, long charCode);


    /**
     * 销毁freetype
     */
    public static native void FT_Destroy_FreeType();


    /**
     * 获取字的unicode
     *
     * @param str 字符串
     * @return
     */
    public static int[] FT_GET_Word_Unicode(String str) {
        int[] bm = new int[str.length()];
        for (int i = 0; i < str.length(); i++) {
            bm[i] = str.codePointAt(i);
            Log.i(TAG, "字符集： " + bm[i]);
        }
        return bm;
    }


    /**
     * 获取字的点阵
     *
     * @param fontSize 字的大小
     * @param charCode 字的unicode编码
     * @return
     */
    public static byte[][] FT_GET_Word_Lattice(int fontSize, long charCode) {
        int rows = 0;
        int h = 0;
        if (charCode <= 0x80) {
            h = fontSize / 2;
        } else {
            h = fontSize;
        }
        byte[][] imagePixels = new byte[fontSize][h];
        if (charCode == 32) {//空格
            for (int i = 0; i < imagePixels.length; i++) {
                for (int j = 0; j < imagePixels[0].length; j++) {
                    imagePixels[i][j] = 1;
                }
            }
            return imagePixels;
        }

        WordInfo wordInfo = FT_GET_Word_Info(fontSize, charCode);
        int line = 0;
        StringBuilder builder = new StringBuilder();
        int i, j, k, counter;
        byte temp;


        int size = (int) Math.floor(fontSize * 26 / 32.0f - wordInfo.getBitmap_top());
        Log.i(TAG,"size1: "+(fontSize * 26 / 32.0f));
        Log.i(TAG,"size2: "+(fontSize * 26 / 32.0f- wordInfo.getBitmap_top()));
        Log.i(TAG,"size3: "+size);
        for (j = 0; j < size; j++) {
            for (i = 0; i < h; i++) {
                builder.append("_");
                imagePixels[rows][i] = 1;
            }
            builder.append("\n");
            rows++;
        }

        for (; j < wordInfo.getRows() + size; j++) {
            line = 0;
            for (i = 1; i <= wordInfo.getBitmap_left(); i++) {
                builder.append("_");
                imagePixels[rows][line++] = 1;
            }

            for (k = 0; k < wordInfo.pitch; k++) {
                temp = wordInfo.buffer[wordInfo.pitch * (j + wordInfo.getBitmap_top() - (fontSize * 26) / 32) + k];
                for (counter = 0; counter < 8; counter++) {
                    if ((temp & 0x80) > 0) {
                        builder.append("*");
                        if (line < h) {
                            imagePixels[rows][line++] = 0;
                        }
                    } else {
                        builder.append("_");
                        if (line < h) {
                            imagePixels[rows][line++] = 1;
                        }
                    }
                    temp <<= 1;
                    i++;
                    if (i > h) {
                        break;
                    }
                }
            }

            int s = i;
            for (int o = 0; o < h - s + 1; o++) {
                builder.append("-");
                imagePixels[rows][line++] = 1;
                i++;
                if (i > h) {
                    break;
                }
            }

            rows++;
            builder.append("\n");
        }


        for (; j < fontSize; j++) {
            for (i = 0; i < h; i++) {
                builder.append("-");
                imagePixels[rows][i] = 1;
            }
            rows++;
            builder.append("\n");
        }

        Log.i(TAG, "111\n"+builder.toString());

        return imagePixels;
    }


    public static byte[][] FT_GET_Word_Lattice(String str, int fontSize, int orientation) {
        int line = 0;
        int rows = 0;
        byte[][] arr = null;

        int length = 0;
        if (orientation == HORIZONTAL) {
            for (int i = 0; i < str.length(); i++) {
                int charCode = str.codePointAt(i);
                if (charCode <= 0x80) {
                    length += fontSize / 2;
                } else {
                    length += fontSize;
                }
            }
           // Log.i("1111", "长度：" + length);
            arr = new byte[fontSize][length];
        } else {
            boolean isHas = false;
            for (int i = 0; i < str.length(); i++) {
                int charCode = str.codePointAt(i);
                if (charCode > 0x80) {
                    isHas = true;
                }
            }

            if (isHas) {
                length = fontSize;
            } else {
                length = fontSize / 2;
            }

            arr = new byte[fontSize * str.length()][length];
        }

        for (int s = 0; s < str.length(); s++) {
            byte[][] bytes = FT_GET_Word_Lattice(fontSize, str.codePointAt(s));


            for (int i = 0; i < bytes.length; i++) {
                for (int j = 0; j < bytes[0].length; j++) {
                    arr[rows + i][line + j] = bytes[i][j];
                }
            }

            if (orientation == HORIZONTAL) {
                line += bytes[0].length;
            } else {
                rows += fontSize;
            }
        }

        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < arr.length; i++) {
            for (int j = 0; j < arr[0].length; j++) {
                if (arr[i][j] == 1) {
                    builder.append("1");
                } else {
                    builder.append("0");
                }
            }
            builder.append("\n");
        }

        Log.i(TAG, "\n"+builder.toString());
        return arr;

    }

}
