package com.example.ioc.CHATPOST;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.ioc.R;
import com.example.ioc.ui.Suggestionsclass.suggestionsmain;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.List;

public class Createpost extends AppCompatActivity {
    EditText Title_blog, Description_blog;
    Button upload;
    ImageView Image_blog;
    String a, b;
    private final static int CAMERA_IMAGE_CODE = 10;
    private final static int GALLERY_IMAGE_CODE = 12;
    Uri Image_uri = null;
    ProgressDialog pd;
    FirebaseAuth mAuth;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_createpost);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Add Post");
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);
        permission();
        Title_blog = findViewById(R.id.post_Title);
        Description_blog = findViewById(R.id.postDes);
        pd = new ProgressDialog(this);
        Image_blog = findViewById(R.id.post_Image);


        mAuth = FirebaseAuth.getInstance();


        Image_blog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImagePick();
            }
        });
        upload = findViewById(R.id.blog_upload);
        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

               if(Validate()){

                   String title = Title_blog.getTag().toString();
                   String description = Description_blog.getTag().toString();

                  uploadToFirebase(title, description);

               }
            }
        });
    }

    void uploadToFirebase(final String title, final String description) {

        pd.setMessage("Publishing Post");
        pd.show();

        final String timestamp = String.valueOf(System.currentTimeMillis());

        String filePath = "Posts/"+"post_"+timestamp;

        if(Image_blog.getDrawable() != null){
            Bitmap bitmap = ((BitmapDrawable)Image_blog.getDrawable()).getBitmap();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG,100, baos);
            byte[] data = baos.toByteArray();

            StorageReference ref = FirebaseStorage.getInstance().getReference().child(filePath);
            ref.putBytes(data).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                    while (!uriTask.isSuccessful());

                    String downloadUri = uriTask.getResult().toString();

                    if(uriTask.isSuccessful()){

                        FirebaseUser user = mAuth.getCurrentUser();
                        HashMap<String, Object> hashMap = new HashMap<>();

                        hashMap.put("uid", user.getUid());
                        hashMap.put("uEmail", user.getEmail());
                        hashMap.put("pId", timestamp);
                        hashMap.put("pTitle", title);
                        hashMap.put("pImage", downloadUri);
                        hashMap.put("pDescription", description);
                        hashMap.put("pTime", timestamp);

                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Posts");
                        reference.child(timestamp).setValue(hashMap)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        pd.dismiss();
                                        Toast.makeText(Createpost.this,"Posted Successfully",Toast.LENGTH_SHORT).show();
                                        Title_blog.setText("");
                                        Description_blog.setText("");
                                        Image_blog.setImageURI(null);
                                        Image_uri = null;

                                        startActivity(new Intent(Createpost.this, suggestionsmain.class));

                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                pd.dismiss();
                                Toast.makeText(Createpost.this,""+e.getMessage(),Toast.LENGTH_SHORT).show();
                            }
                        });

                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(Createpost.this,""+e.getMessage(),Toast.LENGTH_SHORT).show();
                    pd.dismiss();
                }
            });
        }
    }


    private void ImagePick() {

        String[] option = {"Camera", "Gallery"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Chose Via");

        builder.setItems(option, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(which == 0){
                    Camera();
                } if(which == 1){
                    gallery();
                }
            }
        });
        builder.create().show();
    }

    private void gallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select image"), GALLERY_IMAGE_CODE);
    }

    private void Camera() {
        Intent camera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(camera, CAMERA_IMAGE_CODE);
    }


    public void permission(){
        Dexter.withContext(this)
                .withPermissions(
                        Manifest.permission.CAMERA,
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                ).withListener(new MultiplePermissionsListener() {
            @Override public void onPermissionsChecked(MultiplePermissionsReport report) {}
            @Override public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {}
        }).check();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if(resultCode == RESULT_OK){
            if(requestCode == CAMERA_IMAGE_CODE){
                Bitmap imagebitmap = (Bitmap) data.getExtras().get("data");
                Image_blog.setImageBitmap(imagebitmap);
            }
            if(requestCode == GALLERY_IMAGE_CODE){
                Image_uri = data.getData();
                Image_blog.setImageURI(Image_uri);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);


    }
    Boolean Validate() {
        Boolean result = false;
        a = Title_blog.getText().toString();
        b = Description_blog.getText().toString();

        if(a.isEmpty() || b.isEmpty() ) {
            Toast.makeText(this,"Please enter all the details", Toast.LENGTH_LONG).show();
        }else {
            result = true;
        }
        return result;
    }
}