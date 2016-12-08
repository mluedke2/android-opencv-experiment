package org.opencv.samples.cameracalibration;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.ImageView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by mattluedke on 12/8/16.
 */

public class SomeOtherActivity extends Activity {

  private static final int CAMERA_PIC_REQUEST = 1987;
  private static final int PICK_IMAGE = 1776;

  @BindView(R.id.img_original)
  ImageView originalImageView;

  @BindView(R.id.img_edited)
  ImageView editedImageView;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.activity_some_other);

    ButterKnife.bind(this);
  }

  @OnClick(R.id.btn_camera)
  public void camera() {
    Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
    startActivityForResult(cameraIntent, CAMERA_PIC_REQUEST);
  }

  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    if (requestCode == CAMERA_PIC_REQUEST) {
      Bitmap image = (Bitmap) data.getExtras().get("data");
      originalImageView.setImageBitmap(image);
    } else if (requestCode == PICK_IMAGE) {
      Uri selectedImage = data.getData();
      String[] filePathColumn = { MediaStore.Images.Media.DATA };
      Cursor cursor = getContentResolver().query(selectedImage,filePathColumn, null, null, null);
      if (cursor != null) {
        cursor.moveToFirst();
        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
        String picturePath = cursor.getString(columnIndex);
        cursor.close();

        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 8;
        Bitmap bm = BitmapFactory.decodeFile(picturePath, options);
        originalImageView.setImageBitmap(bm);
      }
    }
  }

  @OnClick(R.id.btn_gallery)
  public void gallery() {
    Intent intent = new Intent();
    intent.setType("image/*");
    intent.setAction(Intent.ACTION_GET_CONTENT);
    startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);
  }
}
