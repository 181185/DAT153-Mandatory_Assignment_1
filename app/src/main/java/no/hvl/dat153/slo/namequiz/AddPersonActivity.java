package no.hvl.dat153.slo.namequiz;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.icu.text.SimpleDateFormat;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import androidx.core.app.NavUtils;
import androidx.core.content.FileProvider;
import androidx.appcompat.app.AlertDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.webkit.URLUtil;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;

public class AddPersonActivity extends BaseActivity {
    static final int REQUEST_IMAGE_GET = 1;
    static final int REQUEST_TAKE_PHOTO = 2;

    private String currentPhotoPath;
    private String currentName;
    private ImageView imageView;
    private EditText editText;
    private File file;
    private PersonsCollection personsCollection;
    private LocalStorageHelper localStorageHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_person);
        imageView = findViewById(R.id.add_person_image);
        editText = findViewById(R.id.add_person_name);

        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                currentName = s.toString();
            }
        });
    }

    // Make the physical "BACK" button on the device behave the same way as the "UP" button
    // supplied by the Android appbar.
    @Override
    public void onBackPressed() {
        NavUtils.navigateUpFromSameTask(this);
        super.onBackPressed();
    }

    /**
     * onClick-handler for the "Add person"-button.
     *
     * @param view The view that fired the onClick-event
     */
    public void onAddPersonButtonClick(View view) {
        boolean textNotEmpty = (currentName != null && currentName != "");
        boolean photoPathNotEmpty = currentPhotoPath != null;
        if (textNotEmpty && photoPathNotEmpty) {
            Toast.makeText(this, getString(R.string.person_added_toast_message), Toast.LENGTH_LONG).show();

            // Initialize File object for working on the persons collection
            file = new File(getFilesDir(), getString(R.string.persons_collection));
            localStorageHelper = new LocalStorageHelper(file);

            // Load the persons collection from file into memory
            personsCollection = localStorageHelper.loadPersonsCollection();

            // Add new name/photoPath-tuple to persons collection
            personsCollection.add(currentPhotoPath, currentName);

            // Save (serialize and overwrite) persons collection on disk
            localStorageHelper.savePersonsCollection(personsCollection);

            // Clear view after saving
            imageView.setImageResource(R.drawable.ic_add_photo);
            editText.setText("");
            currentPhotoPath = "";
            currentName = "";
        }
    }

    /**
     * onClick-handler for the image-placeholder.
     *
     * @param view The view that fired the onClick-event
     */
    public void onImageClick(View view) {
        boolean isNotNullUrl = (currentPhotoPath != null);
        boolean isNotContentUrl = !URLUtil.isContentUrl(currentPhotoPath);
        if (isNotNullUrl && isNotContentUrl) {
            // The user requests to take a new photo when there is one stored for this session already.
            // Delete the old one.
            File imageFile = new File(currentPhotoPath);
            imageFile.delete();
        }

        createOptionsDialog();
    }

    /**
     * Create a dialog showing the alternatives to add a photo for a new person.
     */
    private void createOptionsDialog() {
        CharSequence[] items = {
                getString(R.string.add_person_dialog_first_option),
                getString(R.string.add_person_dialog_second_option)
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle(getString(R.string.add_person_dialog_title))
                .setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                dispatchTakePictureIntent();
                                break;

                            case 1:
                                dispatchChoosePictureIntent();
                                break;
                        }
                    }
                });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    /**
     * Dispatch an intent for picking an image.
     */
    private void dispatchChoosePictureIntent() {
        Intent chooseGalleryPicture = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        chooseGalleryPicture.setType("image/*");
        if (chooseGalleryPicture.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(chooseGalleryPicture, REQUEST_IMAGE_GET);
        }
    }

    /**
     * Dispatch an intent for capturing a photo.
     */
    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {

            // Create the File where the photo should go
            File photoFile = null;
            photoFile = createImageFile();

            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "no.hvl.dat153.slo.namequiz.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }

    /**
     * Creates a handle to a file location where an image/photo can be saved.
     * The location is local, i.e. private for the specific app.
     *
     * @return A File object representing the file location
     * @throws IOException
     */
    private File createImageFile() {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "NameQuiz_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = null;
        try {
            image = File.createTempFile(imageFileName, ".jpg", storageDir);
            currentPhotoPath = image.getAbsolutePath();
        } catch (IOException e) {
            Log.e("createImageFile", "File object could not be created.");
        }

        return image;
    }

    /**
     * TODO: Duplicate method.
     */
    private void setPictureFromCamera() {
        // Get the dimensions of the View
        int targetW = imageView.getWidth();
        int targetH = imageView.getHeight();

        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(currentPhotoPath, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        // Determine how much to scale down the image
        int scaleFactor = Math.min(photoW / targetW, photoH / targetH);

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;

        Bitmap bitmap = BitmapFactory.decodeFile(currentPhotoPath, bmOptions);
        imageView.setImageBitmap(bitmap);
    }

    private void setPictureFromGallery(Uri uri) {
        try (InputStream inputStreamDimensions = getContentResolver().openInputStream(uri);
             InputStream inputStreamImage = getContentResolver().openInputStream(uri)) {

            // Get the dimensions of the View
            int targetW = imageView.getWidth();
            int targetH = imageView.getHeight();

            // Get the dimensions of the bitmap
            BitmapFactory.Options bmOptions = new BitmapFactory.Options();
            bmOptions.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(inputStreamDimensions, null, bmOptions);
            int photoW = bmOptions.outWidth;
            int photoH = bmOptions.outHeight;

            // Determine how much to scale down the image
            int scaleFactor = Math.min(photoW / targetW, photoH / targetH);

            // Decode the image file into a Bitmap sized to fill the View
            bmOptions.inJustDecodeBounds = false;
            bmOptions.inSampleSize = scaleFactor;

            Log.i("setPictureFromGallery", Boolean.toString(URLUtil.isContentUrl(uri.toString())));

            Bitmap bitmap = BitmapFactory.decodeStream(inputStreamImage, null, bmOptions);
            imageView.setImageBitmap(bitmap);
        } catch (FileNotFoundException e) {
            Log.e("setPictureFromGallery", "Could not get file from uri.");
        } catch (IOException e) {
            Log.e("setPictureFromGallery", "IOException.");
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case REQUEST_TAKE_PHOTO: {
                    if (currentPhotoPath != null && imageView != null) {
                        setPictureFromCamera();
                        Toast.makeText(this, getString(R.string.picture_taken_toast_message), Toast.LENGTH_LONG).show();
                    }
                }
                break;

                case REQUEST_IMAGE_GET: {
                    Uri fullPhotoUri = data.getData();
                    if (URLUtil.isContentUrl(fullPhotoUri.toString()))
                        currentPhotoPath = fullPhotoUri.toString();
                    else
                        break;
                    setPictureFromGallery(fullPhotoUri);
                }
                break;
            }
        }
    }
}
