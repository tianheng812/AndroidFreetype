package com.xian.freetype.word;

/**
 * Created by xian on 2019/4/28.
 */

public class WordInfo {

    public int pitch;//节距
    public int rows; //行数(高度)
    public int width; //宽度
    public int bitmap_left;//左行距离
    public int bitmap_top;//上行距离
    public byte[] buffer;//数据(默认8位灰度值)

    public int getRows() {
        return rows;
    }

    public void setRows(int rows) {
        this.rows = rows;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public byte[] getBuffer() {
        return buffer;
    }

    public void setBuffer(byte[] buffer) {
        this.buffer = buffer;
    }

    public int getBitmap_left() {
        return bitmap_left;
    }

    public void setBitmap_left(int bitmap_left) {
        this.bitmap_left = bitmap_left;
    }

    public int getBitmap_top() {
        return bitmap_top;
    }

    public void setBitmap_top(int bitmap_top) {
        this.bitmap_top = bitmap_top;
    }

    public int getPitch() {
        return pitch;
    }

    public void setPitch(int pitch) {
        this.pitch = pitch;
    }
}
