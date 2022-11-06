package com.xian.freetypeDemo;

import android.Manifest;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.xian.freetype.word.WordInfo;
import com.xian.freetype.word.WordManager;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;


public class MainActivity extends AppCompatActivity {

    TextView textView;
    ImageView iv;
    EditText editText;
    EditText et_font_size;
    boolean isRequestPermissionOk = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        editText = findViewById(R.id.et);
        et_font_size = findViewById(R.id.et_font_size);
        textView = findViewById(R.id.tv);
        iv = findViewById(R.id.iv);
        requestPermission();

        //simsun.ttc   默认字体  如果想换字体，请用 WordManager.getInstance().init(this,字体路径);
        // WordManager.getInstance().init(this);  //获取读写权限才能调这句话
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        WordManager.getInstance().destroy();
    }

    /**
     * 获取字的信息
     *
     */
    public void getWordInfo(View view) {
        if (!isRequestPermissionOk) {
            Toast.makeText(this, "请打开读写权限", Toast.LENGTH_SHORT).show();
            return;
        }

        String content = editText.getText().toString();

        if (TextUtils.isEmpty(content)) {
            Toast.makeText(this, "提取的字不能为空", Toast.LENGTH_SHORT).show();
            return;
        }

        String font_size = et_font_size.getText().toString();
        if (TextUtils.isEmpty(content)) {
            Toast.makeText(this, "字的大小不能为空", Toast.LENGTH_SHORT).show();
            return;
        }

        if (content.length() > 1) {
            Toast.makeText(this, "只支持一个字的提取", Toast.LENGTH_SHORT).show();
            return;
        }

        int fontSize = Integer.parseInt(font_size);
        WordInfo wordInfo = WordManager.getInstance().getWordInfo(fontSize, content);
        StringBuilder builder = new StringBuilder();
        textView.setText("字的宽度：" + wordInfo.getWidth() + "高度：" + wordInfo.getRows() + "\n");

        byte[][] arr = WordManager.getInstance().stringToLattice(content, fontSize, 0);
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

        Log.i("点阵", builder.toString());

    }


    /**
     * 获取字转bmp图片
     *
     * @param view
     */
    public void stringToBmp1(View view) {
        if (!isRequestPermissionOk) {
            Toast.makeText(this, "请打开读写权限", Toast.LENGTH_SHORT).show();
            return;
        }


        String content = editText.getText().toString();

        if (TextUtils.isEmpty(content)) {
            Toast.makeText(this, "提取的字不能为空", Toast.LENGTH_SHORT).show();
            return;
        }

        String s = et_font_size.getText().toString();
        if (TextUtils.isEmpty(s)) {
            Toast.makeText(this, "字的大小不能为空", Toast.LENGTH_SHORT).show();
            return;
        }
        int font_size = Integer.parseInt(s);
        byte[] bytes = WordManager.getInstance().stringToBmpByte(content, font_size, 0);
        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        iv.setImageBitmap(bitmap);
    }


    /**
     * 获取字转bmp图片
     *
     */
    public void stringToBmp2(View view) {
        if (!isRequestPermissionOk) {
            Toast.makeText(this, "请打开读写权限", Toast.LENGTH_SHORT).show();
            return;
        }

        String content = editText.getText().toString();

        if (TextUtils.isEmpty(content)) {
            Toast.makeText(this, "提取的字不能为空", Toast.LENGTH_SHORT).show();
            return;
        }

        String s = et_font_size.getText().toString();
        if (TextUtils.isEmpty(s)) {
            Toast.makeText(this, "字的大小不能为空", Toast.LENGTH_SHORT).show();
            return;
        }
        int font_size = Integer.parseInt(s);
        byte[] bytes = WordManager.getInstance().stringToBmpByte(content, font_size, 1);
        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        iv.setImageBitmap(bitmap);
    }


    /**
     * 动态权限申请
     */
    public void requestPermission() {
        if (ContextCompat.checkSelfPermission(
                this, Manifest.permission.READ_EXTERNAL_STORAGE) != PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this,
                    Manifest.permission.READ_EXTERNAL_STORAGE)) {
                Toast.makeText(this, "需要读写权限，请打开设置开启对应的权限", Toast.LENGTH_LONG).show();
            } else {
                ActivityCompat.requestPermissions(
                        this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                Manifest.permission.READ_EXTERNAL_STORAGE},
                        1);
            }
        } else {
            isRequestPermissionOk = true;
            //simsun.ttc   默认字体  如果想换字体，请用 WordManager.getInstance().init(this,字体路径);
            WordManager.getInstance().init(this);
        }
    }


    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        String requestPermissionsResult = "";
        if (requestCode == 1) {
            for (int i = 0; i < permissions.length; i++) {
                if (grantResults[i] == PERMISSION_GRANTED) {
                    requestPermissionsResult = "读写权限申请成功\n";
                    //simsun.ttc   默认字体  如果想换字体，请用 WordManager.getInstance().init(this,字体路径);
                    WordManager.getInstance().init(this);
                    isRequestPermissionOk = true;
                } else {
                    requestPermissionsResult = "读写权限申请失败\n";
                }
            }
        }
        Toast.makeText(this, requestPermissionsResult, Toast.LENGTH_SHORT).show();
    }

}

