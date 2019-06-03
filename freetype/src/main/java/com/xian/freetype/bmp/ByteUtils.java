package com.xian.freetype.bmp;

import android.util.Log;

/**
 * Created by xian on 2019/4/15.
 */

public class ByteUtils {
    private static String TAG = "ByteUtils";

    public static byte[] short2Byte(short number) {
        int temp = number;
        byte[] b = new byte[2];
        for (int i = 0; i < b.length; i++) {
            b[i] = new Integer(temp & 0xff).byteValue();// 将最低位保存在高位
            temp = temp >> 8; // 向右移8位
        }
        return b;
    }

    public static byte[] int2Byte(final int integer) {
        int byteNum = (40 - Integer.numberOfLeadingZeros(integer < 0 ? ~integer : integer)) / 8;
        byte[] byteArray = new byte[4];

        for (int n = 0; n < byteNum; n++) {
            byteArray[n] = (byte) (integer >>> (n * 8));
        }

        Log.d(TAG, "int2Byte integer " + integer + " byteNum " + byteNum + " [" + byteArray[0] + " " + byteArray[1] + " " + byteArray[2] + " " + byteArray[3] + "]");

        return (byteArray);
    }

    public static short char2short(char[] chars) {
        if (chars.length > 2) {
            Log.e(TAG, "char2short fail too many elment [" + chars[0] + " " + chars[1] + " " + chars[3] + "]");
        }

        short ret = 0;
        for (int i = 0; i < chars.length; i++) {
            ret += (short) (((short) chars[i]) << (8 * i));
        }

        return ret;
    }

    public static String bytesToHexString(byte[] src) {
        StringBuilder stringBuilder = new StringBuilder("");
        if (src == null || src.length <= 0) {
            return null;
        }
        for (int i = 0; i < src.length; i++) {
            int v = src[i] & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv);
        }
        return stringBuilder.toString();
    }


    public static byte[] bytesToByte(byte[][] datas) {
        byte[] result = new byte[32];
        byte bit[] = {0x01, 0x02, 0x04, 0x08, 0x10, 0x20, 0x40, (byte) 0x80};
        int k = 0;
        for (int i = 0; i < 16; i++) {
            k = 0;
            byte ret = 0;
            for (int j = 0; j < 16; j++) {
                k++;
                if (datas[j][i] == 1) {
                    ret |= bit[j % 8];
                }
                if (k % 8 == 0) {
                    if (k > 8) {
                        result[i + 16] = ret;
                    } else {
                        result[i] = ret;
                    }
                    ret = 0;
                }
            }

        }

        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < result.length; i++) {
            stringBuilder.append(Integer.toHexString(result[i]) + " ,");
            if (i == 15) {
                stringBuilder.append("\n");
            }
        }
        Log.i(TAG, "结果：" + stringBuilder.toString());
        return result;
    }

}