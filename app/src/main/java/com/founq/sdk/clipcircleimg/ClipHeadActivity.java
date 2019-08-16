package com.founq.sdk.clipcircleimg;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.founq.sdk.clipcircleimg.widget.PhotoView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class ClipHeadActivity extends AppCompatActivity {

    private PhotoView mPreviewImg;

    private String imgUri;
    private String mPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clip_head);
        mPreviewImg = findViewById(R.id.img_preview);
        imgUri = getIntent().getStringExtra("uri");
        mPath = getIntent().getStringExtra("path");
        mPreviewImg.setPath(mPath);
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_sure:
                Bitmap bitmap = mPreviewImg.clipBitmap();
                File outputImgFile = new File(getExternalCacheDir(), "output_image.jpg");
                if (outputImgFile.exists()) {
                    outputImgFile.delete();
                }
                try {
                    FileOutputStream outputStream = new FileOutputStream(outputImgFile);
                    bitmap.compress(Bitmap.CompressFormat.PNG, 90, outputStream);
                    outputStream.flush();
                    outputStream.close();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Intent intent = new Intent();
                intent.putExtra("path", outputImgFile.getAbsolutePath());
                setResult(RESULT_OK, intent);
                finish();
                break;
        }
    }
}
