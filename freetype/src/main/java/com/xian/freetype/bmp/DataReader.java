package com.xian.freetype.bmp;

import android.content.Context;
import android.content.res.Resources;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

/**
 * Created by xian on 2019/4/15.
 */

public class DataReader {
    private static String TAG = "BitmapFile";
    protected byte buf[];

    protected int pos;

    protected int mark = 0;

    protected int count;

    public DataReader() {
        this.buf = null;
        this.pos = 0;
        this.count = 0;
    }


    public int readFromFile(Context context, int rid) {
        try {
            Resources r = context.getResources();
            InputStream is = r.openRawResource(rid);
            buf = new byte[is.available()];
            is.read(buf);
            is.close();
            count = buf.length;
            String result = ByteUtils.bytesToHexString(buf);
            Log.d(TAG, "readSaveFile HEX:" + result);
        } catch (Exception e) {
            Log.e(TAG, "readFromFile fail " + e.toString());
        } finally {
            return count;
        }
    }


    public int readFromFile(String fileName) {
        try {
            File file = new File(fileName);
            FileInputStream is = new FileInputStream(file);
            buf = new byte[is.available()];
            is.read(buf);
            is.close();
            count = buf.length;
            String result = ByteUtils.bytesToHexString(buf);
            Log.d(TAG, "readSaveFile HEX:" + result);
        } catch (Exception e) {
            Log.d(TAG, "readFromFile fail " + e.toString());
        } finally {
            return count;
        }
    }

    public int readInt() throws Exception {
        int ret = 0;
        if (count < pos + 4) {
            throw new Exception("can not read int value at " + pos);
        }

        for (int i = 0; i < 4; i++) {
            ret += buf[pos + i] << i * 8;
        }

        pos += 4;

        return ret;
    }

    public short readShort() throws Exception {
        short ret = 0;
        if (count < pos + 2) {
            throw new Exception("can not read short value at " + pos);
        }

        for (int i = 0; i < 2; i++) {
            ret += buf[pos + i] << i * 8;
        }

        pos += 2;

        return ret;
    }

    public byte[] readType() throws Exception {
        byte ret[] = new byte[2];
        if (count < pos + 2) {
            throw new Exception("can not read char value at " + pos);
        }

        for (int i = 0; i < 2; i++) {
            ret[i] = buf[pos++];
        }

        return ret;
    }

    public byte[] readBytesFrom(int index) {
        if (index >= count) {
            return null;
        }

        int size = count - index;

        byte[] bytes = new byte[size];
        int i = 0;
        while (i < size) {
            bytes[i] = buf[index + i];
            i++;
        }
        return bytes;
    }
}