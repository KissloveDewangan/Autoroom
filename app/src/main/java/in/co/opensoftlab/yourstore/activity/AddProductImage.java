package in.co.opensoftlab.yourstore.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import in.co.opensoftlab.yourstore.BuildConfig;
import in.co.opensoftlab.yourstore.R;
import in.co.opensoftlab.yourstore.adapter.ListCarImgAdapter;
import in.co.opensoftlab.yourstore.fragment.SelectImagesOption;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

/**
 * Created by dewangankisslove on 26-12-2016.
 */

public class AddProductImage extends BaseActivity implements View.OnClickListener, SelectImagesOption.onSelectImgOption {

    Toolbar toolbar;
    private static final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 100;
    private static final int RESULT_LOAD_IMG = 200;
    public static final int MEDIA_TYPE_IMAGE = 1;
    // directory name to store captured images and videos
    private static final String IMAGE_DIRECTORY_NAME = "Autoroom Images";

    private Uri fileUri; // file url to store image/video

    RelativeLayout rlNext;
    TextView headline;
    ImageView imgPreview;
    private RecyclerView listCarImgs;
    private ListCarImgAdapter listCarImgAdapter;
    LinearLayoutManager linearLayoutManager;
    private ImageView btnCapturePicture;
    private Button next;
    private Bundle bundle;
    private ImageView close;
    CardView userInfo;
    TextView snapPhoto;

    private FirebaseAuth mAuth;

    StorageReference storage;
    List<String> downloadedUrls = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addproduct_image);
        bundle = getIntent().getExtras().getBundle("temp");

        mAuth = FirebaseAuth.getInstance();
        storage = FirebaseStorage.getInstance().getReferenceFromUrl(BuildConfig.STORAGE_URL);

        initUI();
    }

    private void initUI() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        rlNext = (RelativeLayout) findViewById(R.id.rl_next);
        userInfo = (CardView) findViewById(R.id.cv_info);
        headline = (TextView) findViewById(R.id.tv_headline);
        imgPreview = (ImageView) findViewById(R.id.imgPreview);
        listCarImgs = (RecyclerView) findViewById(R.id.list_car_imgs);
        btnCapturePicture = (ImageView) findViewById(R.id.iv_CapturePicture);
        next = (Button) findViewById(R.id.b_next);
        close = (ImageView) findViewById(R.id.iv_back);
        snapPhoto = (TextView) findViewById(R.id.tv_snap);

        rlNext.setVisibility(View.GONE);

        headline.setText(bundle.getString("productType") + " Images");

        listCarImgAdapter = new ListCarImgAdapter(this, downloadedUrls);
        listCarImgs.setHasFixedSize(true);
        linearLayoutManager = new LinearLayoutManager(AddProductImage.this, LinearLayoutManager.VERTICAL, false);
        listCarImgs.setLayoutManager(linearLayoutManager);
        listCarImgs.setAdapter(listCarImgAdapter);

        listCarImgAdapter.setOnItemClickListener(new ListCarImgAdapter.MyClickListener() {
            @Override
            public void onItemClick(int position, View v) {
                downloadedUrls.remove(position);
                storage.child("users/" + getUserUID() + "/productImg/" + bundle.getString("dataKey") + "/" + position).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        if (downloadedUrls.isEmpty())
                            rlNext.setVisibility(View.GONE);
                        else
                            rlNext.setVisibility(View.VISIBLE);
                    }
                });
                listCarImgAdapter.notifyDataSetChanged();
            }
        });

        snapPhoto.setOnClickListener(this);
        btnCapturePicture.setOnClickListener(this);
        next.setOnClickListener(this);
        close.setOnClickListener(this);
    }

    private void captureImage() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        fileUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE);

        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);

        // start the image capture Intent
        startActivityForResult(intent, CAMERA_CAPTURE_IMAGE_REQUEST_CODE);
    }

    private void uploadImage() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
// Start the Intent
        startActivityForResult(galleryIntent, RESULT_LOAD_IMG);
    }

    private void showSelectItemDialog() {
        // Create an instance of the dialog fragment and show it
        DialogFragment dialog = new SelectImagesOption();
        dialog.show(getSupportFragmentManager(), "SelectOptionFragment");
    }

    /**
     * Here we store the file url as it will be null after returning from camera
     * app
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        // save file url in bundle as it will be null on scren orientation
        // changes
        outState.putParcelable("file_uri", fileUri);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        // get the file url
        fileUri = savedInstanceState.getParcelable("file_uri");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // if the result is capturing Image
        if (requestCode == CAMERA_CAPTURE_IMAGE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                // successfully captured the image
                // display it in image view
                previewCapturedImage();
            } else if (resultCode == RESULT_CANCELED) {
                // user cancelled Image capture
                Toast.makeText(getApplicationContext(),
                        "Cancelled image capturing task.", Toast.LENGTH_SHORT)
                        .show();
            } else {
                // failed to capture image
                Toast.makeText(getApplicationContext(),
                        "Sorry! Failed to capture image", Toast.LENGTH_SHORT)
                        .show();
            }
        } else if (requestCode == RESULT_LOAD_IMG && resultCode == RESULT_OK
                && data != null) {
            // Get the Image from data

            Uri selectedImage = data.getData();
            String[] filePathColumn = {MediaStore.Images.Media.DATA};

            // Get the cursor
            Cursor cursor = getContentResolver().query(selectedImage,
                    filePathColumn, null, null, null);
            // Move to first row
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            // Set the Image in ImageView after decoding the String
            imgPreview.setVisibility(View.VISIBLE);
            imgPreview.setImageBitmap(BitmapFactory
                    .decodeFile(cursor.getString(columnIndex)));
            cursor.close();
            uploadProductImg();

        }
    }


    private void previewCapturedImage() {
        try {
            imgPreview.setVisibility(View.VISIBLE);

            // Get the dimensions of the View
            int targetW = imgPreview.getWidth();
            int targetH = imgPreview.getHeight();

            BitmapFactory.Options bounds = new BitmapFactory.Options();
            bounds.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(fileUri.getPath(), bounds);

            int photoW = bounds.outWidth;
            int photoH = bounds.outHeight;

            // Determine how much to scale down the image
            int scaleFactor = Math.min(photoW / targetW, photoH / targetH);

            Log.d("outWidth", String.valueOf(bounds.outWidth));
            // Decode the image file into a Bitmap sized to fill the View
            bounds.inJustDecodeBounds = false;
            bounds.inSampleSize = scaleFactor;
            bounds.inPurgeable = true;

            BitmapFactory.Options opts = new BitmapFactory.Options();
            Bitmap bm = BitmapFactory.decodeFile(fileUri.getPath(), opts);
            ExifInterface exif = null;
            try {
                exif = new ExifInterface(fileUri.getPath());
            } catch (IOException e) {
                e.printStackTrace();
            }
            String orientString = exif.getAttribute(ExifInterface.TAG_ORIENTATION);
            int orientation = orientString != null ? Integer.parseInt(orientString) : ExifInterface.ORIENTATION_NORMAL;

            int rotationAngle = 0;
            if (orientation == ExifInterface.ORIENTATION_ROTATE_90) rotationAngle = 90;
            if (orientation == ExifInterface.ORIENTATION_ROTATE_180) rotationAngle = 180;
            if (orientation == ExifInterface.ORIENTATION_ROTATE_270) rotationAngle = 270;

            Matrix matrix = new Matrix();
            matrix.setRotate(rotationAngle, (float) targetW, (float) targetH);
            Bitmap rotatedBitmap = Bitmap.createBitmap(bm, 0, 0, bounds.outWidth, bounds.outHeight, matrix, true);

            imgPreview.setImageBitmap(rotatedBitmap);
            uploadProductImg();
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }


    /**
     * ------------ Helper Methods ----------------------
     * */

    /**
     * Creating file uri to store image/video
     */

    public Uri getOutputMediaFileUri(int type) {
        return Uri.fromFile(getOutputMediaFile(type));
    }

    /*
 * returning image / video
 */
    private static File getOutputMediaFile(int type) {

        // External sdcard location
        File mediaStorageDir = new File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                IMAGE_DIRECTORY_NAME);

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d(IMAGE_DIRECTORY_NAME, "Oops! Failed create "
                        + IMAGE_DIRECTORY_NAME + " directory");
                return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss",
                Locale.getDefault()).format(new Date());
        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator
                    + "IMG_" + timeStamp + ".jpg");
        } else {
            return null;
        }

        return mediaFile;
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(base));
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                onBackPressed();
                break;
            case R.id.tv_snap:
                showSelectItemDialog();
                break;
            case R.id.iv_CapturePicture:
                showSelectItemDialog();
                break;
            case R.id.b_next:
                if (downloadedUrls.isEmpty()) {
                    return;
                } else {
                    String urls = "";
                    for (int i = 0; i < downloadedUrls.size(); i++) {
                        if (i == 0)
                            urls = downloadedUrls.get(i);
                        else
                            urls = urls + "::" + downloadedUrls.get(i);
                    }
                    bundle.putString("urls", urls);
                    Intent intent = new Intent(AddProductImage.this, AddSellerLocation.class);
                    intent.putExtra("temp", bundle);
                    startActivity(intent);
                    finish();
                }
                break;
            default:
                return;
        }
    }

    private void uploadProductImg() {
        // Get the data from an ImageView as bytes
        showProgressDialog();
        imgPreview.setDrawingCacheEnabled(true);
        imgPreview.buildDrawingCache();
        Bitmap bitmap = imgPreview.getDrawingCache();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        UploadTask uploadTask = storage.child("users/" + getUserUID() + "/productImg/" + bundle.getString("dataKey") + "/" + downloadedUrls.size()).putBytes(data);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                imgPreview.setVisibility(View.GONE);
                String downloadUrl = taskSnapshot.getDownloadUrl().toString();
                downloadedUrls.add(downloadUrl);
                userInfo.setVisibility(View.GONE);
                if (downloadedUrls.isEmpty())
                    rlNext.setVisibility(View.GONE);
                else
                    rlNext.setVisibility(View.VISIBLE);
                listCarImgAdapter.notifyDataSetChanged();
                hideProgressDialog();
            }
        });
    }


    public String getUserUID() {
        return mAuth.getCurrentUser().getUid();
    }

    private void warnUser() {
        final AlertDialog.Builder alertDiallogBuilder = new AlertDialog.Builder(AddProductImage.this);
        alertDiallogBuilder.setTitle(Html.fromHtml("<font color='#212121'><b>Alert"));
        alertDiallogBuilder.setMessage(Html.fromHtml("<font color=\"#424242\">Are you sure cancel the listing process. All the filled data will be lost?</font>"));
        alertDiallogBuilder.setPositiveButton(Html.fromHtml("<font color=\"#212121\"><b>Yes</b></font>"), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
                dialog.dismiss();
            }
        });
        alertDiallogBuilder.setNegativeButton(Html.fromHtml("<font color=\"#212121\"><b>No</b></font>"), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        final AlertDialog alertDialog = alertDiallogBuilder.create();
        alertDialog.show();
    }

    @Override
    public void onBackPressed() {
        warnUser();
    }

    @Override
    public void selectImgOption(String option) {
//        if (option.contentEquals("snap")) {
//            captureImage();
//        } else if (option.contentEquals("upload")) {
//            uploadImage();
//        }

        if (option.contentEquals("Take a Snapshot")) {
            captureImage();

        } else if (option.contentEquals("Upload from Gallery")) {
            uploadImage();

        }
    }
}
