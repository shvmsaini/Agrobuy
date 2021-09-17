package com.agrobuy.app;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.agrobuy.app.databinding.UploadInvoiceBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;

public class UploadInvoiceActivity extends Activity {
    UploadInvoiceBinding uploadInvoice;
    FirebaseStorage storage ;
    StorageReference storageRef;
    FirebaseAuth mAuth;
    private FirebaseUser currUser;
    DatabaseReference dbRef;
    private final FirebaseDatabase database = FirebaseDatabase.getInstance();
    Uri uri;

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

        // choose file
        uploadInvoice.browse.setOnClickListener(v->{
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            String[] supportedMimeTypes = {"application/pdf","application/msword","image/*"};

            intent.setType("*/*");
            intent.putExtra(Intent.EXTRA_MIME_TYPES, supportedMimeTypes);

            try {
                startActivityForResult(
                        Intent.createChooser(intent, "Select a File to Upload"),
                        200);
            } catch (android.content.ActivityNotFoundException ex) {
                // Potentially direct the user to the Market with a Dialog
                Toast.makeText(this, "Please install a File Manager.", Toast.LENGTH_SHORT).show();
            }
        });
        // upload
        uploadInvoice.upload.setOnClickListener(v->{
            Log.d("tag",uploadInvoice.uploadInvoiceNumber.getText().toString());
            //TODO: invoice doesn't preexist
            if(uploadInvoice.uploadInvoiceNumber.getText().toString().length()==0){
                Toast.makeText(this, "enter an unique invoice number", Toast.LENGTH_SHORT).show();
                uploadInvoice.uploadInvoiceNumber.requestFocus();
                return;
            }
            Toast.makeText(this, "Wait...", Toast.LENGTH_LONG).show();
            storageRef = storageRef.child(currUser.getUid() + "/" + uploadInvoice.uploadInvoiceNumber.getText().toString());
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


            //backbutton
            uploadInvoice.backButton.setOnClickListener(view -> onBackPressed());

        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 200) {
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
        super.onActivityResult(requestCode, resultCode, data);
    }
}
