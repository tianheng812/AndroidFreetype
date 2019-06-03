package com.xian.freetype.word;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;


import com.xian.freetype.bmp.DataWriter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;

/**
 * Created by xian on 2019/4/15.
 * <p>
 * 字管理类
 */

public class WordManager {
    private String TAG = "WordManager";

    public static final int HORIZONTAL = 0;
    public static final int VERTICAL = 1;

    private Context context;
    private String fontPath;

    private static WordManager instance;

    private WordManager() {
    }

    public static WordManager getInstance() {
        if (instance == null) {
            synchronized (WordManager.class) {
                if (instance == null) {
                    instance = new WordManager();
                }
            }
        }
        return instance;
    }

    /**
     * 初始化
     *
     * @param context
     */
    public synchronized boolean init(Context context) {
        this.context = context;
        this.fontPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/font/simsun.ttc";
        File file = new File(fontPath);
        try {
            if (!file.exists()) {
                File parentFile = file.getParentFile();
                if (!parentFile.exists()) {
                    parentFile.mkdirs();
                }
            }
            OutputStream myOutput = new FileOutputStream(file);
            InputStream myInput = context.getAssets().open("simsun.ttc");
            byte[] buffer = new byte[1024];
            int length = myInput.read(buffer);
            while (length > 0) {
                myOutput.write(buffer, 0, length);
                length = myInput.read(buffer);
            }

            myOutput.flush();
            myInput.close();
            myOutput.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return NdkFreeType.FT_Init_FreeType(fontPath);
    }


    /**
     * 初始化
     *
     * @param context
     * @param fontPath 字体路径
     */
    public synchronized boolean init(Context context, String fontPath) {
        this.context = context;
        this.fontPath = fontPath;

        return NdkFreeType.FT_Init_FreeType(fontPath);
    }


    /**
     * 销毁
     */
    public void destroy() {
        NdkFreeType.FT_Destroy_FreeType();
    }

    /**
     * 字符串转bmp数据
     *
     * @param s 字符串
     * @return
     */
    public byte[] stringToBmpByte(String s) {
        return stringToBmpByte(s, 16, HORIZONTAL);
    }


    /**
     * 字符串转bmp数据
     *
     * @param s           字符串
     * @param fontSize    字体大小
     * @param orientation 排序方式
     * @return
     * @throws Exception
     */
    public byte[] stringToBmpByte(String s, int fontSize, int orientation) {
        byte[][] bytes = stringToLattice(s, fontSize, orientation);
        return latticeToBmpByte(bytes);
    }

    /**
     * 字转bmp图
     *
     * @param s           字符串
     * @param orientation 字的排序， 0 横向 1 纵向
     * @return bmp图
     */
    public Bitmap stringToBmp(String s, int fontSize, int orientation) {
        byte[][] bytes = stringToLattice(s, fontSize, orientation);
        return latticeToBmp(bytes);
    }


    /**
     * 点阵转bmp字节数据
     *
     * @param bytes 点阵数据
     * @return
     */
    public byte[] latticeToBmpByte(byte[][] bytes) {
        if (bytes == null) {
            return null;
        }
        int w = bytes[0].length;
        if (w < 32) {
            w = 32;
        } else {
            int i = w % 32;
            if (i != 0) {
                w = (w / 32 + 1) * 32;
            }
        }
        int length = bytes.length * w / 8;
        Log.i(TAG, "像素点的长度： " + length + "  宽= " + w + " 高=" + bytes.length);
        byte[] datas = new byte[length];
        int count = 0;
        StringBuilder builder = new StringBuilder();
        byte bit[] = {0x01, 0x02, 0x04, 0x08, 0x10, 0x20, 0x40, (byte) 0x80};
        for (int i = bytes.length - 1; i >= 0; i--) {
            byte ret = 0x0;
            for (int j = 1; j <= w; j++) {
                if (j - 1 < bytes[0].length) {
                    if (bytes[i][j - 1] == 1) {
                        ret |= bit[7 - ((j - 1) % 8)];
                    }
                } else {
                    ret |= bit[7 - ((j - 1) % 8)];
                }
                if (j % 8 == 0) {
                    if (count < datas.length) {
                        datas[count++] = ret;
                        builder.append(Integer.toHexString(ret) + " ");
                    }
                    ret = 0;
                }
            }
        }

        Log.i("像素的内容", builder.toString());

        try {
            int fileSize = datas.length + 62;
            Log.i(TAG, "文件长度：" + fileSize + "  " + datas.length);
            DataWriter writer = new DataWriter(fileSize);

            writer.writeBytes(new byte[]{0x42, 0x4d}, 2);

            writer.writeInt(fileSize);
            writer.writeShort((short) 0);
            writer.writeShort((short) 0);
            writer.writeInt(0x3e);

            writer.writeInt(0x28);
            writer.writeInt(bytes[0].length);//宽
            writer.writeInt(bytes.length);//高
            writer.writeShort((short) 0x1);//平面数
            writer.writeShort((short) 0x1);//像素
            writer.writeInt(0);//压缩类型
            writer.writeInt(datas.length);//图像大小
            writer.writeInt(0xdc4);//水平分辨率
            writer.writeInt(0xdc4);//垂直分辨率
            writer.writeInt(0);//使用的颜色数，如果为0，则表示默认值(2^颜色位数)
            writer.writeInt(0);//重要颜色数，如果为0，则表示所有颜色都是重要的
            writer.writeInt(0);
            byte[] s1 = {(byte) 0xFF, (byte) 0xFF, (byte) 0xFF, 0};
            writer.writeBytes(s1, 4);

            writer.writeBytes(datas, datas.length);

           // writer.writeToFile(Environment.getExternalStorageDirectory().getAbsolutePath() + "/3.bmp");
            return writer.buf;
        } catch (Throwable throwable) {
            throwable.printStackTrace();
            return null;
        }
    }


    /**
     * 点阵转bmp图
     *
     * @param bytes
     * @return
     */
    public Bitmap latticeToBmp(byte[][] bytes) {
        try {
            byte[] datas1 = latticeToBmpByte(bytes);
            StringBuilder d = new StringBuilder();
            for (int i = 0; i < datas1.length; i++) {
                d.append(Integer.toHexString(datas1[i]) + " ");
            }
            Log.i(TAG, d.toString());
            Bitmap bitmap = BitmapFactory.decodeByteArray(datas1, 0, datas1.length);
            return bitmap;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }


    /**
     * 字符串转点阵
     *
     * @param str         字符串
     * @param orientation 方向，纵向 1 ,横向 0
     * @return
     */
    public byte[][] stringToLattice(String str, int fontSize, int orientation) {
        return NdkFreeType.FT_GET_Word_Lattice(str, fontSize, orientation);
    }


    /**
     * 提取一个字的信息
     *
     * @param font_size 字的大小
     * @param s         要提取的字
     * @return
     */
    public WordInfo getWordInfo(int font_size, String s) {
        if (TextUtils.isEmpty(s)) return null;
        if (s.length() > 1) {
            throw new IllegalArgumentException("只支持一个字提取");
        }
        return NdkFreeType.FT_GET_Word_Info(font_size, s.codePointAt(0));
    }

}
