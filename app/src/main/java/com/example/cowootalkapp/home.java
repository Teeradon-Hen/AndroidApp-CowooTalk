package com.example.cowootalkapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.Blob;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.util.HashMap;
import java.util.Random;

import jp.wasabeef.picasso.transformations.CropCircleTransformation;

public class home extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SharedPreferences login = getSharedPreferences("login",MODE_PRIVATE);
        String username  = login.getString("username",null);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        ImageView img = findViewById(R.id.img_profile_home);
        int width_img = 30;
        int height_img = 30;



        db.collection("user").document(username)
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Blob x = document.getBlob("image");
                        byte[] bytes = x.toBytes();
                        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);

                        img.setImageBitmap(bitmap);
                        img.setMaxHeight(height_img);
                        img.setMaxWidth(width_img);
                    }
                }
            }
        });
        TextView text = findViewById(R.id.test);

        db.collection("user").document(username).collection("room")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        text.setText("");
                        LinearLayout linearLayout = findViewById(R.id.linear);
                        linearLayout.removeAllViews();
                        for (QueryDocumentSnapshot doc : value) {
                            create_view(doc.getString("name"),doc.getId());
                        }
                    }
                });

/*
        db.collection("room").document(doc.getId())
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                DocumentSnapshot doc1 = task.getResult();
                text.setText(text.getText().toString() + " " + doc1.getString("name"));

            }


        });
*/







        LinearLayout join_layout = findViewById(R.id.join_room_click);
        join_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog_Join();
            }
        });

        LinearLayout create_layout = findViewById(R.id.create_room_click);
        create_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog_Create();
            }
        });

        LinearLayout logout_layout = findViewById(R.id.logout_click);
        logout_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog_Logout();
            }
        });

        LinearLayout profile_layout = findViewById(R.id.profile_click);
        profile_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               Intent intent = new Intent(home.this, ProfileActivity.class);
               startActivity(intent);
            }
        });

    }/*
    @Override
    public void onClick(View view)
    {
        switch (view.getId()) {
            case R.id.button_send:
                // Do something
        }
    }*/
    void create_view(String name,String no){
        LinearLayout linearLayout = findViewById(R.id.linear);
        LinearLayout newlayout = new LinearLayout(this);
        newlayout.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        layoutParams.setMargins(30, 30, 100, 0);
        newlayout.setBackground(getResources().getDrawable(R.drawable.customborder));
        TextView textView1 = new TextView(this);
        LinearLayout.LayoutParams layouttext1 = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        layouttext1.setMargins(5, 5, 5, 0);
        textView1.setTextSize(18);
        textView1.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
        textView1.setText("#"+no);

        TextView textView2 = new TextView(this);
        LinearLayout.LayoutParams layouttext2 = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        layouttext2.setMargins(5, 5, 5, 0);
        textView2.setTextSize(18);
        textView2.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
        textView2.setText("Room : " + name );


        LinearLayout newlayout1 = new LinearLayout(this);
        newlayout1.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams layoutParams1 = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        layoutParams1.setMargins(5, 10, 5, 5);
        newlayout1.setGravity(Gravity.CENTER);
        newlayout1.setOrientation(LinearLayout.HORIZONTAL);
/*
        Button button = new Button(this);
        LinearLayout.LayoutParams layoutParams2 = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParams2.setMargins(0, 0, 70, 0);
        button.setText("Join");
        button.setId(Integer.parseInt(no));
        button.setBackgroundColor(getResources().getColor(R.color.joinbut));


        Button button1 = new Button(this);
        button1.setText("Join");
        button1.setBackgroundColor(getResources().getColor(R.color.delbut));


        newlayout1.addView(button,layoutParams2);
        newlayout1.addView(button1);
*/
        newlayout.addView(textView1,layouttext1);
        newlayout.addView(textView2,layouttext2);
        newlayout.addView(newlayout1,layoutParams1);
        linearLayout.addView(newlayout,layoutParams);

    }

    void join_room(String room_name,String room_no){
        HashMap<String, Object> user_room = new HashMap<>();
        user_room.put("name", room_name);
        SharedPreferences login = getSharedPreferences("login",MODE_PRIVATE);
        String username  = login.getString("username",null);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("user").document(username).collection("room").document(room_no).set(user_room);
        Intent intent = new Intent(home.this,ChatActivity.class);
        intent.putExtra("room_no",room_no);
        intent.putExtra("room_name",room_name);
        startActivity(intent);
    }
    void showDialog_error(){
        final AlertDialog.Builder viewDialog = new AlertDialog.Builder(this);
        viewDialog.setTitle("Your Room NO is invalid");
        viewDialog.setMessage("Please try again");
        viewDialog.setPositiveButton("OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        viewDialog.setCancelable(false);
        viewDialog.show();
    }
    void showDialog_Join(){
        //final AlertDialog.Builder viewDialog = new AlertDialog.Builder(this);
        AlertDialog.Builder viewDialog = new AlertDialog.Builder(this);
        final EditText edittext = new EditText(home.this);
        edittext.setHint("#1234");
        edittext.setPadding(40,35,35,40);
        viewDialog.setTitle("Join room");
        viewDialog.setMessage("Room NO");

        viewDialog.setView(edittext);

        viewDialog.setPositiveButton("Join", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        //What ever you want to do with the value
                        Editable YouEditTextValue = edittext.getText();
                        //OR
                        String room_no = edittext.getText().toString();
                        room_no = room_no.replace("#","");
                        FirebaseFirestore db = FirebaseFirestore.getInstance();

                        db.collection("room").document(room_no)
                                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                DocumentSnapshot doc = task.getResult();
                                if (doc.exists()) {
                                    join_room(doc.getString("name"), doc.getId() );
                                    Context context = getApplicationContext();
                                    CharSequence text = "Join Room Complete";
                                    int duration = Toast.LENGTH_SHORT;

                                    Toast toast = Toast.makeText(context, text, duration);
                                    toast.show();
                                } else {
                                    showDialog_error();
                                }


                            }

                        });
                    }
                });
        viewDialog.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        viewDialog.setCancelable(false);
        viewDialog.show();
    }

    void showDialog_Create(){
        AlertDialog.Builder viewDialog = new AlertDialog.Builder(this);
        final EditText edittext = new EditText(home.this);

        edittext.setPadding(40,35,35,40);
        viewDialog.setTitle("Create Room");
        viewDialog.setMessage("Room Name");
        viewDialog.setView(edittext);

        viewDialog.setPositiveButton("Create", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                //What ever you want to do with the value
                Editable YouEditTextValue = edittext.getText();
                String room_name = edittext.getText().toString();
                Random rd = new Random();
                String num="";
                num += String.format("%04d",rd.nextInt(10000));
                HashMap<String, Object> user_room = new HashMap<>();
                user_room.put("name", room_name);
                SharedPreferences login = getSharedPreferences("login",MODE_PRIVATE);
                String username  = login.getString("username",null);
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                db.collection("user").document(username).collection("room").document(num).set(user_room);

                HashMap<String, Object> room = new HashMap<>();
                room.put("name", room_name);
                db.collection("room").document(num).set(room);
                db.collection("room").document(num).collection("chat");
                showDialog_Reuslt(num);
            }
        });

        viewDialog.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        viewDialog.setCancelable(false);
        viewDialog.show();
    }

    void showDialog_Logout(){
        final AlertDialog.Builder viewDialog = new AlertDialog.Builder(this);
        viewDialog.setTitle("Logout");
        viewDialog.setMessage("Do you want to logout?" );
        viewDialog.setPositiveButton("Sure",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        GG_signOut();
                        FB_signOut();
                        SharedPreferences login = getSharedPreferences("login",MODE_PRIVATE);
                        SharedPreferences.Editor editor = login.edit();
                        editor.clear();
                        editor.commit();
                    }
                });
        viewDialog.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        viewDialog.setCancelable(false);
        viewDialog.show();
    }

    void showDialog_Reuslt(String num){
        final AlertDialog.Builder viewDialog = new AlertDialog.Builder(this);
        viewDialog.setTitle("Your Room NO is");
        viewDialog.setMessage("#"+num);
        viewDialog.setPositiveButton("OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        viewDialog.setCancelable(false);
        viewDialog.show();
    }

    private void GG_signOut() {
        GoogleSignInClient mGoogleSignInClient;
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        mGoogleSignInClient.signOut()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Intent home = new Intent(home.this, LoginActivity.class);
                        startActivity(home);
                        finish();
                    }
                });
    }

    private void FB_signOut() {
        LoginManager.getInstance().logOut();
        Intent home = new Intent(home.this, LoginActivity.class);
        startActivity(home);
        finish();
    }

}