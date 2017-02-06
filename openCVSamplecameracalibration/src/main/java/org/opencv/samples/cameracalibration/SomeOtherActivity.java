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
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.imgproc.Imgproc;

import java.util.Random;

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

  @BindView(R.id.img_edited1)
  ImageView editedImageView1;

  @BindView(R.id.img_edited2)
  ImageView editedImageView2;

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
        editedImageView1.setImageBitmap(convertToGray(bitmap, Imgproc.COLOR_RGB2GRAY));
        editedImageView2.setImageBitmap(convertToGray(bitmap, Imgproc.COLOR_BGR2GRAY));
      }
    }

    // doing this a few times for fun
    findMinEnclosingCircle();
    findMinEnclosingCircle();
    findMinEnclosingCircle();
    findMinEnclosingCircle();
    findMinEnclosingCircle();
  }

  private Bitmap convertToGray(Bitmap original, int conversion) {
    Mat mat = new Mat();
    Utils.bitmapToMat(original, mat);
    Mat outmat = new Mat();
    Imgproc.cvtColor(mat, outmat, conversion);
    Bitmap newImage = Bitmap.createBitmap(
      (int)mat.size().width,
      (int)mat.size().height, Bitmap.Config.ARGB_8888);
    Utils.matToBitmap(outmat, newImage);
    return newImage;
  }

  private void findMinEnclosingCircle() {
    double min = 1;
    double max = 100;

    Point randomPoint1 = randomPointInRange(min, max);
    Point randomPoint2 = randomPointInRange(min, max);
    Point randomPoint3 = randomPointInRange(min, max);
    Point randomPoint4 = randomPointInRange(min, max);
    Point randomPoint5 = randomPointInRange(min, max);

    MatOfPoint2f points = new MatOfPoint2f(randomPoint1, randomPoint2, randomPoint3,
      randomPoint4, randomPoint5);

    Point center = new Point();
    float[] radius = new float[1];
    Imgproc.minEnclosingCircle(points, center, radius);

    Log.d("MYTEST", "\n\npt1: " + randomPoint1 + " pt2: " + randomPoint2 + " pt3: " + randomPoint3
      + " pt4: " + randomPoint4 + " pt5: " + randomPoint5 + " center: " + center + " radius: " +
      radius[0]);
  }

  private Point randomPointInRange(double min, double max) {
    return new Point(randomDoubleInRange(min, max), randomDoubleInRange(min, max));
  }

  private double randomDoubleInRange(double min, double max) {
    Random random = new Random();
    return min + (max - min) * random.nextDouble();
  }
}
