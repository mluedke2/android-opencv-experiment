package org.opencv.samples.cameracalibration;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.ImageView;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

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


  private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
    @Override
    public void onManagerConnected(int status) {
      switch (status) {
        case LoaderCallbackInterface.SUCCESS: {
          Log.e("MYTEST", "OpenCV loaded successfully");
          processAndLoadIfNeeded();
        }
        break;
        default: {
          super.onManagerConnected(status);
        }
        break;
      }
    }
  };

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.activity_some_other);

    ButterKnife.bind(this);
  }

  @Override
  public void onResume() {
    super.onResume();
    if (!OpenCVLoader.initDebug()) {
      Log.e("MYTEST", "Internal OpenCV library not found. Using OpenCV Manager for initialization");
      OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_0_0, this, mLoaderCallback);
    } else {
      Log.e("MYTEST", "OpenCV library found inside package. Using it!");
      mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
    }
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

  @OnClick(R.id.btn_process)
  public void process() {
    processAndLoadIfNeeded();
  }

  private void processAndLoadIfNeeded() {

    BitmapDrawable drawable = (BitmapDrawable) originalImageView.getDrawable();

    if (drawable != null) {
      Bitmap bitmap = drawable.getBitmap();

      if (bitmap != null) {

        Mat mat = new Mat();

        Utils.bitmapToMat(bitmap, mat);

        Mat outmat = new Mat();

        // blur
        //Imgproc.blur(mat, outmat, mat.size());

        // a better blur -- making the last number higher makes blurrier
        Imgproc.GaussianBlur(mat, outmat, new Size(15,15), 10);


        Bitmap newImage = Bitmap.createBitmap(
          (int)mat.size().width,
          (int)mat.size().height, Bitmap.Config.ARGB_8888);

        Utils.matToBitmap(outmat, newImage);

        editedImageView.setImageBitmap(newImage);
      }
    }
  }
}
