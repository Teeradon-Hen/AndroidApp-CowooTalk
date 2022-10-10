package com.example.cowootalkapp;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
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

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.Blob;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;

import jp.wasabeef.picasso.transformations.CropCircleTransformation;

public class FacebookRegActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_facebook_reg);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        boolean FBisLoggedIn = accessToken != null && !accessToken.isExpired();
        if(FBisLoggedIn){

            GraphRequest request = GraphRequest.newMeRequest(AccessToken.getCurrentAccessToken(),
                    new GraphRequest.GraphJSONObjectCallback() {
                        @Override
                        public void onCompleted(JSONObject object, GraphResponse response) {
                            try {
                                String id = object.getString("id");
                                db.collection("user").document(id)
                                        .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                        if (task.isSuccessful()) {
                                            DocumentSnapshot document = task.getResult();
                                            if (document.exists()) {
                                                SharedPreferences login = getSharedPreferences("login",MODE_PRIVATE);
                                                SharedPreferences.Editor editor = login.edit();
                                                editor.putString("username",id);
                                                editor.putString("displayname",document.getString("displayname"));
                                                editor.apply();
                                                System.out.println(789);
                                                Intent intent = new Intent(FacebookRegActivity.this,
                                                        home.class);
                                                startActivity(intent);
                                                finish();
                                            }
                                            else{
                                                String x = null;
                                                setContentView(R.layout.activity_regiter_another);
                                                ImageView img = findViewById(R.id.reg_img_an1);
                                                int width_img = 250;
                                                int height_img = 250;
                                                try {
                                                    x = object.getJSONObject("picture")
                                                            .getJSONObject("data").getString("url");
                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                }


                                                Picasso.get()
                                                        .load(x)
                                                        .resize(width_img, height_img)
                                                        .transform(new CropCircleTransformation())
                                                        .into(img);
                                                EditText edt = findViewById(R.id.reg_edtdisplay);
                                                try {
                                                    edt.setText(object.getString("name"));
                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                }
                                                Button create = findViewById(R.id.reg_create1);
                                                create.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View view) {
                                                        register(id,"Facebook");
                                                        Intent intent = new Intent(FacebookRegActivity.this,
                                                                home.class);
                                                        startActivity(intent);
                                                        finish();
                                                    }
                                                });

                                                Button back = findViewById(R.id.reg_back1);
                                                back.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View view) {
                                                        LoginManager.getInstance().logOut();
                                                        Intent intent = new Intent(FacebookRegActivity.this,
                                                                LoginActivity.class);
                                                        startActivity(intent);
                                                        finish();
                                                    }
                                                });


                                                TextView change = findViewById(R.id.reg_desimg1);
                                                change.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View view) {
                                                        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                                                        intent.setType("image/*");
                                                        Intent.createChooser(intent,"Select photo from");
                                                        launcher.launch(intent);
                                                    }
                                                });

                                                EditText displayname = findViewById(R.id.reg_edtdisplay);
                                                displayname.addTextChangedListener(new TextWatcher() {
                                                    @Override
                                                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }
                                                    @Override
                                                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

                                                    @Override
                                                    public void afterTextChanged(Editable editable) {

                                                        String display = displayname.getText().toString();
                                                        Button create = findViewById(R.id.reg_create1);
                                                        if(display.length()<1 ){
                                                            displayname.setError("Display name required at least 1 character with English letters, numbers 0-9 or space");
                                                            create.setEnabled(false);
                                                        }

                                                        else if (display.length()>=1 && display.charAt(0) == ' '){
                                                            displayname.setError("Display name required at least 1 character with English letters, numbers 0-9 or space");
                                                            create.setEnabled(false);
                                                        }

                                                        else{
                                                            create.setEnabled(true);
                                                        }

                                                    }
                                                });


                                            }

                                        }

                                    }
                                });



                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });
            Bundle parameters = new Bundle();
            parameters.putString("fields", "gender,name,id,first_name,last_name,email,picture");
            request.setParameters(parameters);
            request.executeAsync();


        }
    }
    private void register(String id,String type){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        EditText edt = findViewById(R.id.reg_edtdisplay);
        ImageView img1 = findViewById(R.id.reg_img_an1);

        SharedPreferences login = getSharedPreferences("login",MODE_PRIVATE);
        SharedPreferences.Editor editor = login.edit();
        editor.putString("username",id);
        editor.putString("displayname",edt.getText().toString());
        editor.apply();

        Bitmap bitmaps = ((BitmapDrawable) img1.getDrawable()).getBitmap();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmaps.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] bytes = baos.toByteArray();
        Blob x = Blob.fromBytes(bytes);

        HashMap<String, Object> user = new HashMap<>();
        user.put("username", id);
        user.put("displayname", edt.getText().toString());
        user.put("password", "");
        user.put("type", type);
        user.put("image", x);
        db.collection("user").document(id)
                .set(user)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {


                        //    Log.d( "DocumentSnapshot added with ID: " + documentReference.getId());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //      Log.w( "Error adding document", e);
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
                                ImageView img = findViewById(R.id.reg_img_an1);
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