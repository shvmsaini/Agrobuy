package com.agrobuy.app;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;

import com.agrobuy.app.databinding.UploadInvoiceBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicBoolean;

public class UploadInvoiceActivity extends Activity {
    UploadInvoiceBinding uploadInvoice;
    FirebaseStorage storage ;
    StorageReference storageRef;
    FirebaseAuth mAuth;
    private FirebaseUser currUser;
    DatabaseReference dbRef;
    private final FirebaseDatabase database = FirebaseDatabase.getInstance();
    Uri uri;
    private final int FILE_PICK_REQUEST=200;
    private final int REQUEST_CAPTURE_IMAGE = 201;
    String imageFilePath;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        uploadInvoice = UploadInvoiceBinding.inflate(getLayoutInflater());
        setContentView(uploadInvoice.getRoot());

        // getting instances
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();
        mAuth = FirebaseAuth.getInstance();
        currUser = mAuth.getCurrentUser();
        dbRef = FirebaseDatabase.getInstance().getReference();

        // choose file
        uploadInvoice.browse.setOnClickListener(v->{
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            String[] supportedMimeTypes = {"application/pdf","application/msword","image/*"};

            intent.setType("*/*");
            intent.putExtra(Intent.EXTRA_MIME_TYPES, supportedMimeTypes);

            try {
                startActivityForResult(
                        Intent.createChooser(intent, "Select a File to Upload"), FILE_PICK_REQUEST);
            } catch (android.content.ActivityNotFoundException ex) {
                // Potentially direct the user to the Market with a Dialog
                Toast.makeText(this, "Please install a File Manager.", Toast.LENGTH_SHORT).show();
            }
        });

        // upload
        uploadInvoice.upload.setOnClickListener(v->{
            String invoiceNumber = uploadInvoice.uploadInvoiceNumber.getText().toString();
            if(invoiceNumber.length()==0){
                Toast.makeText(this, "enter an unique invoice number", Toast.LENGTH_SHORT).show();
                uploadInvoice.uploadInvoiceNumber.requestFocus();
                return;
            }
            if(uploadInvoice.fileSelect.getText().equals(getResources().getString(R.string.no_file))){
                Toast.makeText(this, "Select a file first or Use camera", Toast.LENGTH_SHORT).show();
                return;
            }
            Toast.makeText(this, "Wait...", Toast.LENGTH_LONG).show();

            // Checking if invoice already exist or not
            if(!isInvoiceExists()){
                storageRef = storageRef.child(currUser.getUid() + "/" + invoiceNumber);
                //Uploading file
                storageRef.putFile(uri).addOnCompleteListener(task -> {
                    if(!task.isSuccessful()){
                        Toast.makeText(getApplicationContext(), "Unable to Upload", Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(getApplicationContext(), "Successfully Uploaded", Toast.LENGTH_SHORT).show();
                        // putting link in database
                        dbRef = database.getReference("users").child(currUser.getUid()).child("invoices");
                        storageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                            HashMap<String,Object> item = new HashMap<>();
                            HashMap<String,Object> details = new HashMap<>();
                            details.put("link",uri.toString());
                            item.put(uploadInvoice.uploadInvoiceNumber.getText().toString(),details);
                            dbRef.updateChildren(item, (error, ref) -> {
                                //updating invoice_count
                                database.getReference("users").child(currUser.getUid()).child("invoice_count")
                                        .get().addOnCompleteListener(invoiceTask -> {
                                    if (!invoiceTask.isSuccessful()) {
                                        Log.e("firebase", "Error getting invoice_count", invoiceTask.getException());
                                    }
                                    else {
                                        Log.d("firebase", "invoice_count" + invoiceTask.getResult().getValue());
                                        String invoiceCount = invoiceTask.getResult().getValue().toString();
                                        database.getReference("users").child(currUser.getUid()).child("invoice_count")
                                                .setValue(Integer.parseInt(invoiceCount)+1);
                                    }
                                    Toast.makeText(getApplicationContext(), "invoice created", Toast.LENGTH_SHORT).show();
                                });
                            });
                        });

                    }
                });
            }
            else{
                Toast.makeText(getApplicationContext(), "Invoice number already exists", Toast.LENGTH_SHORT).show();
            }


        });

        uploadInvoice.openCamera.setOnClickListener(v->{
            Intent pictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if(pictureIntent.resolveActivity(getPackageManager()) != null){
                //Create a file to store the image
                File photoFile = null;
                try {
                    photoFile = createImageFile();
                } catch (IOException ex) {
                    // Error occurred while creating the File
                }
                if (photoFile != null) {
                    Uri photoURI = FileProvider.getUriForFile(getApplicationContext(),
                            "com.agrobuy.app.provider", photoFile);
                    pictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                    startActivityForResult(pictureIntent, REQUEST_CAPTURE_IMAGE);
                }
            }
        });

        //back button
        uploadInvoice.backButton.setOnClickListener(view -> onBackPressed());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == FILE_PICK_REQUEST) {
            if (resultCode == RESULT_OK) {
                // Get the Uri of the selected file
                uri = data.getData();
                Log.d("f", "File Uri: " + uri.toString());
                String path;
                String[] projection = {MediaStore.Files.FileColumns.DATA};
                Cursor cursor = getContentResolver().query(uri, projection, null, null, null);
                if (cursor == null) {
                    path = uri.getPath();
                } else {
                    cursor.moveToFirst();
                    int column_index = cursor.getColumnIndexOrThrow(projection[0]);
                    path = cursor.getString(column_index);
                    cursor.close();
                }
                if (path == null || path.isEmpty()) {
                    Log.d("uri.getPath() = ", uri.getPath());
                    uploadInvoice.fileSelect.setText(uri.getPath());
                } else {
                    Log.d("path = ", path);
                    uploadInvoice.fileSelect.setText(path);
                }
            }
        }
        if (requestCode == REQUEST_CAPTURE_IMAGE) {
            Log.d(requestCode + "",resultCode + "");
            if (resultCode == RESULT_OK) {
                Toast.makeText(this, "Captured! Click on Upload to upload the image.",
                        Toast.LENGTH_SHORT).show();
               Log.d("Image Captured, Path: ",imageFilePath);
               uploadInvoice.fileSelect.setText(imageFilePath);
               uri = Uri.fromFile( new File(imageFilePath));

            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private File createImageFile() throws IOException {
        String timeStamp =
                new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "IMG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",   /* suffix */
                storageDir      /* directory */
        );

        imageFilePath = image.getAbsolutePath();
        return image;
    }

    public boolean isInvoiceExists(){
        AtomicBoolean flag = new AtomicBoolean();
        flag.set(false);
        FirebaseDatabase.getInstance().getReference("users" + "/" + currUser.getUid() +  "/" + "invoices" +  "/"
                + uploadInvoice.uploadInvoiceNumber.getText().toString()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    flag.set(true);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
        return flag.get();
    }
}
