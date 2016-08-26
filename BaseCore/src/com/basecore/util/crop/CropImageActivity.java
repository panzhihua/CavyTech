
package com.basecore.util.crop;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.basecore.R;
import com.edmodo.cropper.CropImageView;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class CropImageActivity extends Activity {
    private static final String  TAG="CropImageActivity";

    // Static final constants
    private static final int DEFAULT_ASPECT_RATIO_VALUES = 10;
    private static final int ROTATE_NINETY_DEGREES = 90;
    private static final String ASPECT_RATIO_X = "ASPECT_RATIO_X";
    private static final String ASPECT_RATIO_Y = "ASPECT_RATIO_Y";
    private static final int ON_TOUCH = 1;

    // Instance variables
    private int mAspectRatioX = DEFAULT_ASPECT_RATIO_VALUES;
    private int mAspectRatioY = DEFAULT_ASPECT_RATIO_VALUES;
    private int IMAGE_MAX_SIZE = 1024;

    private Bitmap croppedImage;
    private CropImageView cropImageView;
    private Button discard;
    private Button rotate;
    private Button save;
    private ContentResolver mContentResolver;
    private Bitmap.CompressFormat mOutputFormat    = Bitmap.CompressFormat.JPEG;
    private String  mSaveUri;


    // Saves the state upon rotating the screen/restarting the activity
    @Override
    protected void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
        bundle.putInt(ASPECT_RATIO_X, mAspectRatioX);
        bundle.putInt(ASPECT_RATIO_Y, mAspectRatioY);
    }

    // Restores the state upon rotating the screen/restarting the activity
    @Override
    protected void onRestoreInstanceState(Bundle bundle) {
        super.onRestoreInstanceState(bundle);
        mAspectRatioX = bundle.getInt(ASPECT_RATIO_X);
        mAspectRatioY = bundle.getInt(ASPECT_RATIO_Y);
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContentResolver = getContentResolver();
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.crop_image);
        assignViews();
        initCropImageView();
        addListener();
    }

    private void assignViews() {
        cropImageView = (CropImageView) findViewById(R.id.crop_image_view);
        discard = (Button) findViewById(R.id.discard);
        rotate = (Button) findViewById(R.id.rotate);
        save = (Button) findViewById(R.id.save);
    }

    private void initCropImageView() {
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        mSaveUri = extras.getString("output");
        Bitmap mBitmap = extras.getParcelable("data");

        final String imageUri = extras.getString("image-path");

        if (mBitmap == null) {
            mBitmap = getBitmap(imageUri);
        }
        if(mBitmap!=null){
            cropImageView.setImageBitmap(mBitmap);
        }
        if (mSaveUri != null) {
            String outputFormatString = extras.getString("outputFormat");
            if (outputFormatString != null) {
                mOutputFormat = Bitmap.CompressFormat.valueOf(
                        outputFormatString);
            }
        }
        cropImageView.setAspectRatio(DEFAULT_ASPECT_RATIO_VALUES, DEFAULT_ASPECT_RATIO_VALUES);
        boolean isSquare=extras.getBoolean("isSquare",false);
        cropImageView.setFixedAspectRatio(isSquare);
        cropImageView.setGuidelines(0);
    }

    private void addListener() {
        discard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        rotate.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                cropImageView.rotateImage(ROTATE_NINETY_DEGREES);
            }
        });
        save.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                croppedImage = cropImageView.getCroppedImage();
                saveOutput(croppedImage);
            }
        });
    }
    private void saveOutput(Bitmap croppedImage) {

        if (mSaveUri != null) {
            final Uri uri = Uri.parse(mSaveUri);
            OutputStream outputStream = null;
            try {
                outputStream = mContentResolver.openOutputStream(Uri.parse(mSaveUri));
                if (outputStream != null) {
                    croppedImage.compress(mOutputFormat, 90, outputStream);
                }
            } catch (IOException ex) {

                Log.e(TAG, "Cannot open file: " + mSaveUri, ex);
                setResult(RESULT_CANCELED);
                finish();
                return;
            } finally {

                Util.closeSilently(outputStream);
            }

            Bundle extras = new Bundle();
            Intent intent = new Intent();
            intent.putExtras(extras);
            intent.putExtra("image-path", uri.getPath());
            intent.putExtra("orientation_in_degrees", Util.getOrientationInDegree(this));
            setResult(RESULT_OK, intent);
        } else {
            Log.e(TAG, "not defined image url");
        }
        croppedImage.recycle();
        finish();
    }
    private Bitmap getBitmap(String pathUri) {

        Uri uri = Uri.parse(pathUri);
        InputStream in = null;
        try {
            in = mContentResolver.openInputStream(uri);

            //Decode image size
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;

            BitmapFactory.decodeStream(in, null, o);
            in.close();

            int scale = 1;
            if (o.outHeight > IMAGE_MAX_SIZE || o.outWidth > IMAGE_MAX_SIZE) {
                scale = (int) Math.pow(2, (int) Math.round(Math.log(IMAGE_MAX_SIZE / (double) Math.max(o.outHeight, o.outWidth)) / Math.log(0.5)));
            }

            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize = scale;
            in = mContentResolver.openInputStream(uri);
            Bitmap b = BitmapFactory.decodeStream(in, null, o2);
            in.close();

            return b;
        } catch (FileNotFoundException e) {
            Log.e(TAG, "file " + pathUri + " not found");
        } catch (IOException e) {
            Log.e(TAG, "file " + pathUri + " not found");
        }
        return null;
    }


}
