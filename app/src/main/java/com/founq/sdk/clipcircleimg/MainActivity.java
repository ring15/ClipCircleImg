package com.founq.sdk.clipcircleimg;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import java.io.File;
import java.io.IOException;

import static com.founq.sdk.clipcircleimg.utils.getPhotoFromPhotoAlbum.getRealPathFromUri;

public class MainActivity extends AppCompatActivity {

    public static final int TAKE_PHOTO = 1;
    public static final int ALBUM = 2;
    public static final int CLIP_VIEW = 3;

    private ImageView mHeadImg;
    private Uri imageUri;
    private String mPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mHeadImg = findViewById(R.id.iv_head);

        mHeadImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(MainActivity.this)
                        .setCancelable(true)
                        .setItems(new String[]{"相机", "相册"}, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                if (i == 0) {
                                    if (checkPermission(Manifest.permission.CAMERA, 0x01)) {
                                        gotoCamera();
                                    }
                                } else {
                                    if (checkPermission(Manifest.permission.READ_EXTERNAL_STORAGE, 0x02)) {
                                        gotoPhoto();
                                    }
                                }
                            }
                        }).show();
            }
        });
    }

    private void gotoPhoto() {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, ALBUM);
    }

    private void gotoCamera() {
        String name = System.currentTimeMillis() + "";
        File outputImgFile = new File(getExternalCacheDir(), "output_image.jpg");
        mPath = outputImgFile.getAbsolutePath();
        if (outputImgFile.exists()) {
            outputImgFile.delete();
        }
        try {
            outputImgFile.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            imageUri = FileProvider.getUriForFile(MainActivity.this,
                    "com.founq.sdk.clipcircleimg.fileprovider", outputImgFile);
        } else {
            imageUri = Uri.fromFile(outputImgFile);
        }
        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        startActivityForResult(intent, TAKE_PHOTO);
    }

    private boolean checkPermission(String permission, int requestCode) {
        if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{permission}, requestCode);
            return false;
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            if (requestCode == 0x01) {
                gotoCamera();
            } else {
                gotoPhoto();
            }
        }else {
            Toast.makeText(MainActivity.this, "权限未开启", Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case TAKE_PHOTO:
                if (resultCode == RESULT_OK) {
                    Intent intent = new Intent(MainActivity.this, ClipHeadActivity.class);
                    intent.putExtra("uri", imageUri.toString());
                    intent.putExtra("path", mPath);
                    startActivityForResult(intent, CLIP_VIEW);
                }
                break;
            case ALBUM:
                if (resultCode == RESULT_OK) {
                    Uri uri = data != null ? data.getData() : null;
                    Intent intent = new Intent(MainActivity.this, ClipHeadActivity.class);
                    intent.putExtra("uri", uri != null ? uri.toString() : null);
                    intent.putExtra("path",getRealPathFromUri(MainActivity.this, uri));
                    startActivityForResult(intent, CLIP_VIEW);
                }
                break;
            case CLIP_VIEW:
                if (resultCode == RESULT_OK) {
                    String path = data != null ? data.getStringExtra("path") : null;
                    if (!TextUtils.isEmpty(path)) {
                        Bitmap bitmap = BitmapFactory.decodeFile(path);
                        mHeadImg.setImageBitmap(bitmap);
                    }
                }
                break;
            default:
                break;
        }
    }

}
