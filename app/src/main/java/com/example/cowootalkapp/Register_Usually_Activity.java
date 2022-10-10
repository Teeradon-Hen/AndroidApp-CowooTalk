package com.example.cowootalkapp;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
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
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import jp.wasabeef.picasso.transformations.CropCircleTransformation;

import java.util.HashSet;
import java.util.regex.*;
public class Register_Usually_Activity extends AppCompatActivity {
    EditText password ;
    EditText confirm_password ;
    EditText displayname ;
    EditText username;
    boolean valid_user;
    boolean valid_display;
    boolean valid_password;
    boolean valid_confirm_password;

    ArrayList<String> user_list ;

    @Override
    protected void onResume() {
        super.onResume();


    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_regiter_usually);

        TextView add_img = findViewById(R.id.reg_desimg);
        add_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                Intent.createChooser(intent,"Select photo from");
                launcher.launch(intent);
            }
        });

        ImageView img = findViewById(R.id.reg_img_an);
        int width_img = 250;
        int height_img = 250;
        Picasso.get()
                .load(R.drawable.user_img)
                .resize(width_img, height_img)
                .transform(new CropCircleTransformation())
                .into(img);

        username = findViewById(R.id.reg_edtusername);
        displayname = findViewById(R.id.reg_edtdisplayname);
        password = findViewById(R.id.reg_edtpassword);
        confirm_password = findViewById(R.id.reg_edtconfirmpassword);
        Button button = findViewById(R.id.reg_create);
        button.setEnabled(false);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();

        username.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }
            @Override
            public void onTextChanged(CharSequence charSequence, int i1, int i2, int i3) {
                String name = username.getText().toString();

                boolean valid_username = true;
                valid_user = false;


                for(int i=0;i<name.length();i++)
                    if(!Character.isLetterOrDigit(name.charAt(i)))
                        valid_username = false;
                    else if(Character.isLetter(name.charAt(i)))
                         if(name.charAt(i) < 'a' || name.charAt(i) > 'z'|| name.charAt(i) < 'A' || name.charAt(i) < 'Z')
                             valid_username = false;

                if( name.length() < 5)
                    valid_username = false;

                if(!valid_username)
                    username.setError("Username required at least 5 characters with English letters or numbers 0-9 without space");
                else{
                    valid_user = true;
                    db.collection("user")
                            .get()
                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if (task.isSuccessful())
                                        for (QueryDocumentSnapshot document : task.getResult())
                                            if (name.equals(document.getId())){
                                                System.out.println(name);
                                                System.out.println(document.getId().equals(name));
                                                username.setError("This username has already been taken");
                                                valid_user = false;
                                            }


                                }
                            });

                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                System.out.println("user  " + valid_user);
                System.out.println("display  " + valid_display);
                System.out.println("pass  " + valid_password);
                System.out.println("conpass  " + valid_confirm_password);
                if(valid_confirm_password && valid_password && valid_display && valid_user) {
                    button.setEnabled(true);
                }
                else{
                    button.setEnabled(false);
                }


            }
        });

        displayname.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                valid_display = false;
                String display = displayname.getText().toString();

                if(display.length()<1 )
                    displayname.setError("Display name required at least 1 character with English letters, numbers 0-9 or space");
                else if (display.length()>=1 && display.charAt(0) == ' ')
                    displayname.setError("Display name required at least 1 character with English letters, numbers 0-9 or space");
                else
                   valid_display = true;
            }

            @Override
            public void afterTextChanged(Editable editable) {
                System.out.println("user  " + valid_user);
                System.out.println("display  " + valid_display);
                System.out.println("pass  " + valid_password);
                System.out.println("conpass  " + valid_confirm_password);
                if(valid_confirm_password && valid_password && valid_display && valid_user) {
                    button.setEnabled(true);
                }
                else{
                    button.setEnabled(false);
                }
            }
        });

        password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }
            @Override
            public void onTextChanged(CharSequence charSequence, int i1, int i2, int i3) {
                String pass = password.getText().toString();
                valid_password = false;
                boolean valid_pass = true;
                for(int i=0;i<pass.length();i++)
                    if(!Character.isLetterOrDigit(pass.charAt(i)))
                        valid_pass = false;

                if(pass.length() > 16 || pass.length() < 8)
                    valid_pass = false;

                if(!valid_pass)
                    password.setError("Password must be 8-15 characters with English letters or number 0-9 without space.");
                else{
                    boolean letter = false;
                    boolean num = false;

                    for(int i=0;i<pass.length();i++)
                        if(Character.isLetter(pass.charAt(i))) {
                            letter = true;
                        }
                        else if(Character.isDigit(pass.charAt(i))) {
                            num = true;
                        }
                    if(num && letter){
                        if(pass.equals(confirm_password.getText().toString())){
                            password.setError(null);
                            confirm_password.setError(null);
                            valid_password = true;
                            valid_confirm_password = true;
                        }
                        else{
                            password.setError("Your password and confirm/new password do not match");
                            confirm_password.setError("Your password and confirm/new password do not match");
                        }


                    }

                    else{
                        valid_password = false;
                        valid_confirm_password = false;
                        password.setError("Password must be 8-15 characters with English letters or number 0-9 without space.");
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                System.out.println("user  " + valid_user);
                System.out.println("display  " + valid_display);
                System.out.println("pass  " + valid_password);
                System.out.println("conpass  " + valid_confirm_password);
                if(valid_confirm_password && valid_password && valid_display && valid_user) {
                    button.setEnabled(true);
                }
                else{
                    button.setEnabled(false);
                }

            }
        });

        confirm_password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String pass = password.getText().toString();
                valid_confirm_password = false;
                valid_password = false;
                String confirm_pass = confirm_password.getText().toString();
                if(!pass.equals(confirm_pass)){
                    password.setError("Your password and confirm/new password do not match");
                    confirm_password.setError("Your password and confirm/new password do not match");
                }

                else{
                    valid_password = true;
                    valid_confirm_password = true;
                    password.setError(null);
                    confirm_password.setError(null);
                }

            }

            @Override
            public void afterTextChanged(Editable editable) {
                System.out.println("user  " + valid_user);
                System.out.println("display  " + valid_display);
                System.out.println("pass  " + valid_password);
                System.out.println("conpass  " + valid_confirm_password);
                if(valid_confirm_password && valid_password && valid_display && valid_user) {
                    button.setEnabled(true);
                }
                else{
                    button.setEnabled(false);
                }
            }

        });



        Button back = findViewById(R.id.reg_back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Register_Usually_Activity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });


        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                FirebaseStorage storage = FirebaseStorage.getInstance();
                StorageReference storageRef = storage.getReference();
                StorageReference riversRef = storageRef.child("image_profile/"+username.getText().toString());
                ImageView img1 = findViewById(R.id.reg_img_an);

                Bitmap bitmaps = ((BitmapDrawable) img1.getDrawable()).getBitmap();
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmaps.compress(Bitmap.CompressFormat.PNG, 100, baos);
                byte[] bytes = baos.toByteArray();
                Blob x = Blob.fromBytes(bytes);
/*
                UploadTask uploadTask = riversRef.putBytes(bytes);
                uploadTask.addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Handle unsuccessful uploads
                    }
                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
                        // ...
                    }
                });
*/
                HashMap<String, Object> user = new HashMap<>();
                user.put("username", username.getText().toString());
                user.put("displayname", displayname.getText().toString());
                user.put("password", password.getText().toString());
                user.put("type", "normal");
                user.put("image", x);
                db.collection("user").document(username.getText().toString())
                        .set(user)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Context context = getApplicationContext();
                                CharSequence text = "Registraion Complete";
                                int duration = Toast.LENGTH_SHORT;

                                Toast toast = Toast.makeText(context, text, duration);
                                toast.show();
                                finish();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                //      Log.w( "Error adding document", e);
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
                                ImageView img = findViewById(R.id.reg_img_an);
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

}
/*


        but.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                db.collection("user").document("test")
                        .set(user)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {                    @Override
                        public void onSuccess(Void aVoid) {
                            System.out.println("sucess");
                            //    Log.d( "DocumentSnapshot added with ID: " + documentReference.getId());
                        }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                //      Log.w( "Error adding document", e);
                            }
                        });*/