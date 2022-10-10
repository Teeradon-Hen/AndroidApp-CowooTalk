package com.example.cowootalkapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class LoginActivity extends AppCompatActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
/*
        LoginManager.getInstance().logOut();

      GoogleSignInClient mGoogleSignInClient;
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        mGoogleSignInClient.signOut()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                    }
                });  SharedPreferences login = getSharedPreferences("login",MODE_PRIVATE);
        SharedPreferences.Editor editor = login.edit();
        editor.clear();
        editor.commit();

*/

        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        SharedPreferences login = getSharedPreferences("login",MODE_PRIVATE);
        String temp = login.getString("username",null);

        GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(this);
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        boolean isLoggedIn = accessToken != null && !accessToken.isExpired();
        if (acct != null) {
            Intent intent = new Intent(LoginActivity.this, home.class);
            startActivity(intent);
            finish();
        }
        else if(isLoggedIn){
            Intent intent = new Intent(LoginActivity.this, home.class);
            startActivity(intent);
            finish();
        }
        else if(temp!=null){
            Intent intent = new Intent(LoginActivity.this, home.class);
            startActivity(intent);
            finish();
        }
        else {
            setContentView(R.layout.activity_login);

            ScrollView f =  findViewById(R.id.layout1);
            if(f!=null)
            f.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                    return true;
                }
            });

        }

    }

    @Override
    protected void onResume() {
        super.onResume();

        GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(this);
        if (acct != null) {
            Intent intent = new Intent(LoginActivity.this, home.class);
            startActivity(intent);
            finish();
        }
        else {
            setContentView(R.layout.activity_login);
            EditText username = findViewById(R.id.username);
            EditText password = findViewById(R.id.password);
            Button login_button = findViewById(R.id.login_button);
         //   login_button.setPressed(true);
            login_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String username_text = username.getText().toString();
                    String password_text = password.getText().toString();

                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                    if(!username_text.isEmpty())
                    db.collection("user").document(username_text)
                            .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                DocumentSnapshot document = task.getResult();
                                if (document.exists()) {
                                    String id = String.valueOf(document.get("username"));
                                    String pass = String.valueOf(document.get("password"));
                                    if (username_text.equals(id) && password_text.equals(pass)) {
                                        SharedPreferences login = getSharedPreferences("login",MODE_PRIVATE);
                                        SharedPreferences.Editor editor = login.edit();
                                        editor.putString("username",username_text);
                                        editor.putString("displayname",document.getString("displayname"));
                                        editor.apply();

                                        Intent intent = new Intent(LoginActivity.this,home.class);
                                        startActivity(intent);
                                        finish();
                                    } else {
                                        //ผิด
                                    }
                                } else {
                                    showDialog();
                                    password.setText("");
                                    System.out.println("ไม่มี");
                                }
                            }
                        }
                    });


                }
            });

            if(username.getText().toString().isEmpty() && password.getText().toString().isEmpty())
                login_button.setEnabled(false);

            ImageView google_button = findViewById(R.id.google_login);
            google_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(LoginActivity.this, GoogleLoginActivity.class);
                    startActivity(intent);
                }
            });

            ImageView facebook_button = findViewById(R.id.facebook_login);
            facebook_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(LoginActivity.this, FacebookLoginActivity.class);
                    startActivity(intent);
                }
            });

            Button reg_but = findViewById(R.id.register_button);
            reg_but.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent r = new Intent(LoginActivity.this, Register_Usually_Activity.class);
                    startActivity(r);
                }
            });

            TextView connect = findViewById(R.id.middle);
            connect.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(LoginActivity.this, ChatActivity.class);
                    startActivity(intent);
                }
            });
            username.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    if(username.getText().toString().isEmpty())
                        login_button.setEnabled(false);
                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

                @Override
                public void afterTextChanged(Editable editable) {
                    if(!username.getText().toString().isEmpty() && !password.getText().toString().isEmpty())
                        login_button.setEnabled(true);
                }
            });

            password.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    if(password.getText().toString().isEmpty())
                        login_button.setEnabled(false);
                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

                @Override
                public void afterTextChanged(Editable editable) {
                    if(!username.getText().toString().isEmpty() && !password.getText().toString().isEmpty())
                        login_button.setEnabled(true);
                }
            });
        }
    }
    void showDialog(){
        final AlertDialog.Builder viewDialog = new AlertDialog.Builder(this);
        viewDialog.setTitle("ERROR");
        viewDialog.setMessage("Invalid username or password,please try again " );
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