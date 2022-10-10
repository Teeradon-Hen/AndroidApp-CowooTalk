package com.example.cowootalkapp;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.Blob;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;

import jp.wasabeef.picasso.transformations.CropCircleTransformation;

public class ProfileActivity extends AppCompatActivity {
    HashMap<String, Object> user = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        SharedPreferences login = getSharedPreferences("login",MODE_PRIVATE);
        String username = login.getString("username",null);

        TextView add_img = findViewById(R.id.prof_desimg);
        add_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                Intent.createChooser(intent,"Select photo from");
                launcher.launch(intent);
            }
        });



        FirebaseFirestore db = FirebaseFirestore.getInstance();
        EditText edt_username = findViewById(R.id.prof_edtusername);
        EditText edt_displayname = findViewById(R.id.prof_edtdisplay);
        Button save_button = findViewById(R.id.prof_save);

        db.collection("user").document(username)
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        if (document.exists()) {
                            user.put("username", username);
                            user.put("type", document.getString("type"));
                            user.put("password", document.getString("password"));
                            Blob x = document.getBlob("image");
                            byte[] bytes = x.toBytes();
                            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                            ImageView img = findViewById(R.id.img_pro);
                            img.setImageBitmap(bitmap);

                            edt_username.setText(document.getId());
                            edt_displayname.setText(document.getString("displayname"));
                            edt_username.setEnabled(false);

                        }
                    }
                }
            }

        });
        Button back = findViewById(R.id.prof_back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ProfileActivity.this,home.class);
                startActivity(intent);
                finish();
            }
        });
        save_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ImageView img1 = findViewById(R.id.img_pro);
                Bitmap bitmaps = ((BitmapDrawable) img1.getDrawable()).getBitmap();
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmaps.compress(Bitmap.CompressFormat.PNG, 100, baos);
                byte[] bytes = baos.toByteArray();
                Blob x = Blob.fromBytes(bytes);

                user.put("displayname", edt_displayname.getText().toString());
                user.put("image", x);
                db.collection("user").document(username)
                        .set(user)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Context context = getApplicationContext();
                                CharSequence text = "Edit Profile Complete";
                                int duration = Toast.LENGTH_SHORT;

                                Toast toast = Toast.makeText(context, text, duration);
                                toast.show();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                      Log.w( "Error adding document", e);
                            }
                        });

            }

        });

        EditText displayname = findViewById(R.id.prof_edtdisplay);
        displayname.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

            @Override
            public void afterTextChanged(Editable editable) {

                String display = displayname.getText().toString();

                if(display.length()<1 ){
                    displayname.setError("Display name required at least 1 character with English letters, numbers 0-9 or space");
                    save_button.setEnabled(false);
                }

                else if (display.length()>=1 && display.charAt(0) == ' '){
                    displayname.setError("Display name required at least 1 character with English letters, numbers 0-9 or space");
                    save_button.setEnabled(false);
                }

                else{
                    save_button.setEnabled(true);
                }

            }
        });

        Button change =findViewById(R.id.prof_changepwd);
        change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                db.collection("user").document(username)
                        .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                if(document.getString("type").equals("Facebook") || (document.getString("type").equals("Google")))
                                    showDialog();
                                else{
                                    Intent intent = new Intent(ProfileActivity.this,ChangePasswordActivity.class);
                                    startActivity(intent);
                                }

                            }
                        }
                    }

                });

            }
        });



    }
    ActivityResultLauncher<Intent> launcher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        try{
                            Uri uri = data.getData();

                            try{
                                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),uri);
                                ImageView img = findViewById(R.id.img_pro);
                                int width_img = 250;
                                int height_img = 250;
                                Picasso.get().load(uri).resize(width_img, height_img)
                                        .transform(new CropCircleTransformation())
                                        .into(img);
                            } catch (FileNotFoundException e) {
                                e.printStackTrace();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }


            });

    private void showDialog(){
        final AlertDialog.Builder viewDialog = new AlertDialog.Builder(this);
        viewDialog.setTitle("ERROR");
        viewDialog.setMessage("Your account does not have access to change password" );
        viewDialog.setPositiveButton("OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        viewDialog.setCancelable(false);
        viewDialog.show();
    }
}