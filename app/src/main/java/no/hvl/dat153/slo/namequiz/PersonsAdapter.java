package no.hvl.dat153.slo.namequiz;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.URLUtil;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

/**
 * A specialized Adapter class for connecting a RecyclerView with a collection (dataset) containing
 * Person objects.
 */
public class PersonsAdapter extends RecyclerView.Adapter<PersonsAdapter.NamesViewHolder> {
    private int scaleFactor;
    private ViewGroup viewGroup;
    private PersonsCollection personsCollection;
    private LocalStorageHelper localStorageHelper;

    public PersonsAdapter(PersonsCollection personsCollection, File file) {
        this.personsCollection = personsCollection;
        localStorageHelper = new LocalStorageHelper(file);
    }

    @Override
    public PersonsAdapter.NamesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        this.viewGroup = parent;
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.names_row_layout, parent, false);
        scaleFactor = Integer.parseInt(parent.getContext().getString(R.string.scale_factor));

        final NamesViewHolder nwh = new NamesViewHolder(view);
        nwh.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onDeleteButtonClick(nwh.getAdapterPosition());
            }
        });

        nwh.button.setId(R.string.delete_button_id);

        return nwh;
    }

    @Override
    public void onBindViewHolder(PersonsAdapter.NamesViewHolder viewHolder, int position) {
        viewHolder.name.setText(personsCollection.getName(position));
        String picturePath = personsCollection.getPicturePath(position);


        if(picturePath.equals(viewGroup.getContext().getString(R.string.no_image_path_name)))
            viewHolder.image.setImageResource(R.drawable.cat1);
        else if(URLUtil.isContentUrl(picturePath))
            setPictureFromGallery(viewHolder.image, personsCollection.getPicturePath(position));
        else
            setPicture(viewHolder.image, personsCollection.getPicturePath(position));

        viewHolder.image.setScaleType(ImageView.ScaleType.FIT_CENTER);
    }

    @Override
    public int getItemCount() {
        return personsCollection.getSize();
    }

    /**
     * onClick-handler for the "Delete" button.
     *
     * @param position Position of the item being clicked, as seen by the adapter
     */
    public void onDeleteButtonClick(int position) {
        removeItem(position);
    }

    /**
     * Remove an item from the dataset.
     *
     * @param position The index of the item in the dataset
     */
    private void removeItem(int position) {
        String picturePath = personsCollection.getPicturePath(position);

        // Remove from data structure
        personsCollection.remove(position);
        localStorageHelper.savePersonsCollection(personsCollection);

        // Delete image file from system
        if (!URLUtil.isContentUrl(picturePath) || picturePath != null) {
            new File(picturePath).delete();
        }

        // Notify the adapter of updates so it can update the view accordingly
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, personsCollection.getSize());
    }

    /**
     * TODO: Duplicate method.
     */
    private void setPicture(ImageView imageView, String picturePath) {
        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;

        Bitmap bitmap = BitmapFactory.decodeFile(picturePath, bmOptions);
        imageView.setImageBitmap(bitmap);
    }

    private void setPictureFromGallery(ImageView imageView, String stringUri) {
        Uri uri = Uri.parse(stringUri);
        try (InputStream inputStreamImage = viewGroup.getContext().getContentResolver().openInputStream(uri)) {

            BitmapFactory.Options bmOptions = new BitmapFactory.Options();

            // Decode the image file into a Bitmap sized to fill the View
            bmOptions.inJustDecodeBounds = false;
            bmOptions.inSampleSize = scaleFactor;

            Bitmap bitmap = BitmapFactory.decodeStream(inputStreamImage, null, bmOptions);
            imageView.setImageBitmap(bitmap);
        } catch (FileNotFoundException e) {
            Log.e("setPictureFromGallery", "Could not get file from uri.");
        } catch (IOException e) {
            Log.e("setPictureFromGallery", "IOException.");
        }
    }

    /**
     * A specialized ViewHolder class for hosting NameViews.
     */
    public static class NamesViewHolder extends RecyclerView.ViewHolder {
        private TextView name;
        private ImageView image;
        private Button button;

        public NamesViewHolder(View view) {
            super(view);

            name = view.findViewById(R.id.row_name);
            image = view.findViewById(R.id.row_image);
            button = view.findViewById(R.id.row_button);
        }
    }
}
