package com.example.cowootalkapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import com.google.firebase.firestore.Blob;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.util.Date;
import java.util.HashMap;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class ChatActivity extends AppCompatActivity {
    Blob x ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        SharedPreferences login = getSharedPreferences("login",MODE_PRIVATE);
        String username  = login.getString("username",null);
        String displayname  = login.getString("displayname",null);

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("user").document(username)
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                x = document.getBlob("image");

                            }
                        }
                    }
                });

        TextView textView = findViewById(R.id.room_name);
        textView.setText(getIntent().getStringExtra("room_name"));
        TextView textView1 = findViewById(R.id.room_no);
        textView1.setText("#"+getIntent().getStringExtra("room_no"));

        Button but = findViewById(R.id.chat_back);
        but.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ChatActivity.this,home.class);
                startActivity(intent);
                finish();
            }
        });
        ImageView sendbutton = findViewById(R.id.sendButton);
        sendbutton.setPressed(true);
        EditText text = findViewById(R.id.messageEditText);
        text.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String x = text.getText().toString();
                if(!x.isEmpty()){
                    ImageView img1 = findViewById(R.id.sendButton);
                    img1.setImageResource(R.drawable.outline_send_24);
                }
                else{
                    ImageView img1 = findViewById(R.id.sendButton);
                    img1.setImageResource(R.drawable.outline_send_gray_24);

                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        // Create a new user with a first and last name
        HashMap<String, Object> user = new HashMap<>();

        sendbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!text.getText().toString().isEmpty()){
                    LocalDateTime myDateObj = LocalDateTime.now();
                    //DateTimeFormatter myFormatObj = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
                    DateTimeFormatter myFormatObj = DateTimeFormatter.ofPattern("dd.MM.yyyy 'at' HH:mm:ss");

                    //String myFormatObj = firebase.firestore.FieldValue.serverTimestamp;
                    //user.put("timestamp" , myDateObj.format(myFormatObj) );
                    user.put("displayname",displayname);
                    user.put("username",username);
                    user.put("message" , text.getText().toString() );
                    user.put("image_prof" , x );
                    text.setText("");
                    InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);

                    ////insert===============================
                    db.collection("room").document(getIntent().getStringExtra("room_no")).collection("chat").document(myDateObj.format(myFormatObj))
                            .set(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                        }
                    });
                }
            }
        });
        TextView content = findViewById(R.id.content);


        db.collection("room").document(getIntent().getStringExtra("room_no")).collection("chat")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (error != null) {
                            // Log.w(TAG, "Listen failed.", e);
                            return;
                        }
                        content.setText("");
                        LinearLayout linearLayout = findViewById(R.id.layout_chat);
                        linearLayout.removeAllViews();
                        for (QueryDocumentSnapshot doc : value) {
                            String id = String.valueOf(doc.get("username"));
                            String message = String.valueOf(doc.get("message"));
                            String display = String.valueOf(doc.get("displayname"));
                            Blob image_prof = doc.getBlob("image_prof");
                            create_chat(id,String.format("%1s\n%2s\n%3s\n\n",display, doc.getId(),message),image_prof);
                        //    content.setText(content.getText().toString() + String.format("%1s\n%2s\n%3s\n\n",display,timestamp,message));


                        }

                        //  Log.d(TAG, "Current cites in CA: " + cities);


                    }

                });

        SearchView searchtext = findViewById(R.id.searchText);
        searchtext.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                db.collection("room").document(getIntent().getStringExtra("room_no")).collection("chat")
                        .addSnapshotListener(new EventListener<QuerySnapshot>() {
                            @Override
                            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                                if (error != null) {
                                    // Log.w(TAG, "Listen failed.", e);
                                    return;
                                }
                                content.setText("");
                                LinearLayout linearLayout = findViewById(R.id.layout_chat);
                                linearLayout.removeAllViews();
                                for (QueryDocumentSnapshot doc : value) {
                                    String id = String.valueOf(doc.get("username"));
                                    String message = String.valueOf(doc.get("message"));
                                    String display = String.valueOf(doc.get("displayname"));
                                    Blob image_prof = doc.getBlob("image_prof");
                                    if(message.contains(s.toUpperCase()) || message.contains(s.toLowerCase()))


                                        create_chat(id,String.format("%1s\n%2s\n%3s\n\n",display, doc.getId(),message),image_prof);
                                    //    content.setText(content.getText().toString() + String.format("%1s\n%2s\n%3s\n\n",display,timestamp,message));


                                }

                                //  Log.d(TAG, "Current cites in CA: " + cities);


                            }

                        });

                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                db.collection("room").document(getIntent().getStringExtra("room_no")).collection("chat")
                        .addSnapshotListener(new EventListener<QuerySnapshot>() {
                            @Override
                            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                                if (error != null) {
                                    // Log.w(TAG, "Listen failed.", e);
                                    return;
                                }
                                content.setText("");
                                LinearLayout linearLayout = findViewById(R.id.layout_chat);
                                linearLayout.removeAllViews();
                                for (QueryDocumentSnapshot doc : value) {
                                    String id = String.valueOf(doc.get("username"));
                                    String message = String.valueOf(doc.get("message"));
                                    String display = String.valueOf(doc.get("displayname"));
                                    Blob image_prof = doc.getBlob("image_prof");
                                    if(message.contains(s.toUpperCase()) || message.contains(s.toLowerCase()))
                                        create_chat(id,String.format("%1s\n%2s\n%3s\n\n",display, doc.getId(),message),image_prof);
                                    //    content.setText(content.getText().toString() + String.format("%1s\n%2s\n%3s\n\n",display,timestamp,message));


                                }

                                //  Log.d(TAG, "Current cites in CA: " + cities);


                            }

                        });

                return false;
            }
        });



    }
    void create_chat(String id , String content, Blob image){
        LinearLayout linearLayout = findViewById(R.id.layout_chat);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        LinearLayout newlayout = new LinearLayout(this);
        newlayout.setOrientation(LinearLayout.HORIZONTAL);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        layoutParams.setMargins(30, 30, 30, 0);

        TextView textView1 = new TextView(this);
        textView1.setText(content);
        ImageView img = new ImageView(this);
        LinearLayout.LayoutParams layoutParams1 = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        layoutParams1.setMargins(30, 30, 0, 0);

        byte[] bytes = image.toBytes();
        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        img.setMaxHeight(1);
        img.setMaxWidth(1);
        img.setImageBitmap(bitmap);


        newlayout.addView(img);
        newlayout.addView(textView1,layoutParams1);
        linearLayout.addView(newlayout,layoutParams);


    }
}