package com.example.cowootalkapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

public class ChangePasswordActivity extends AppCompatActivity {
    boolean valid_password,valid_confirm_password;
    HashMap<String, Object> user = new HashMap<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
        EditText edtcurpwd = findViewById(R.id.prof_edtcurpwd);
        EditText edtnewpwd = findViewById(R.id.prof_edtnewpwd);
        EditText edtconnewpwd = findViewById(R.id.prof_edtconnewpwd);

        Button save_button = findViewById(R.id.prof_save_changepwd);
        Button back = findViewById(R.id.prof_back_pwd);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ChangePasswordActivity.this, ProfileActivity.class);
                startActivity(intent);
                finish();
            }
        });

        edtnewpwd.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }
            @Override
            public void onTextChanged(CharSequence charSequence, int i1, int i2, int i3) {
                String pass = edtnewpwd.getText().toString();
                valid_password = false;
                boolean valid_pass = true;
                for(int i=0;i<pass.length();i++)
                    if(!Character.isLetterOrDigit(pass.charAt(i)))
                        valid_pass = false;

                if(pass.length() > 16 || pass.length() < 8)
                    valid_pass = false;

                if(!valid_pass)
                    edtnewpwd.setError("Password must be 8-15 characters with English letters or number 0-9 without space");
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
                        if(pass.equals(edtconnewpwd.getText().toString())){
                            edtnewpwd.setError(null);
                            edtconnewpwd.setError(null);
                            valid_password = true;
                            valid_confirm_password = true;
                        }
                        else{
                            edtnewpwd.setError("Your password and confirm/new password do not match");
                            edtconnewpwd.setError("Your password and confirm/new password do not match");
                        }


                    }

                    else{
                        valid_password = false;
                        valid_confirm_password = false;
                        edtnewpwd.setError("Password must be 8-15 characters with English letters or number 0-9 without space");
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(valid_confirm_password && valid_password ) {
                    save_button.setEnabled(true);
                }
                else{
                    save_button.setEnabled(false);
                }

            }
        });

        edtconnewpwd.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String pass = edtnewpwd.getText().toString();
                valid_confirm_password = false;
                valid_password = false;
                String confirm_pass = edtconnewpwd.getText().toString();
                if(!pass.equals(confirm_pass)){
                    edtnewpwd.setError("Your password and confirm/new password do not match");
                    edtconnewpwd.setError("Your password and confirm/new password do not match");
                }

                else{
                    valid_password = true;
                    valid_confirm_password = true;
                    edtnewpwd.setError(null);
                    edtconnewpwd.setError(null);
                }

            }

            @Override
            public void afterTextChanged(Editable editable) {

                if(valid_confirm_password && valid_password ) {
                    save_button.setEnabled(true);
                }
                else{
                    save_button.setEnabled(false);
                }
            }

        });

        edtcurpwd.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i1, int i2, int i3) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i1, int i2, int i3) {
                String pass = edtcurpwd.getText().toString();
                boolean valid_pass = true;
                for(int i=0;i<pass.length();i++)
                    if(!Character.isLetterOrDigit(pass.charAt(i)))
                        valid_pass = false;

                if(pass.length() > 16 || pass.length() < 8)
                    valid_pass = false;

                if(!valid_pass)
                    edtcurpwd.setError("Password must be 8-15 characters with English letters or number 0-9 without space");
                else{
                    boolean letter = false;
                    boolean num = false;

                    for(int i=0;i<pass.length();i++){
                        if(Character.isLetter(pass.charAt(i))) {
                            letter = true;
                        }
                        else if(Character.isDigit(pass.charAt(i))) {
                            num = true;
                        }}
                    if(num && letter){
                        System.out.println(57);
                        edtcurpwd.setError(null);
                    }

                    else{
                        System.out.println(588);
                        edtcurpwd.setError("Password must be 8-15 characters with English letters or number 0-9 without space");
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable editable) { }
        });

        save_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                SharedPreferences login = getSharedPreferences("login",MODE_PRIVATE);
                String username = login.getString("username",null);

                db.collection("user").document(username)
                        .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                user.put("username", username);
                                user.put("displayname", document.getString("displayname"));
                                user.put("image", document.getBlob("image"));
                                user.put("type", document.getString("type"));

                                String password = edtcurpwd.getText().toString();
                                String pass = String.valueOf(document.get("password"));
                                if (password.equals(pass)) {
                                    changepwd(edtnewpwd.getText().toString());
                                    Context context = getApplicationContext();
                                    CharSequence text = "Change Password Complete";
                                    int duration = Toast.LENGTH_SHORT;

                                    Toast toast = Toast.makeText(context, text, duration);
                                    toast.show();
                                } else {
                                    showDialog();
                                    edtcurpwd.setText("");
                                }
                            }
                        }
                    }
                });

            }
        });


    }
    private void showDialog(){
        final AlertDialog.Builder viewDialog = new AlertDialog.Builder(this);
        viewDialog.setTitle("ERROR");
        viewDialog.setMessage("Incorrect password" );
        viewDialog.setPositiveButton("OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        viewDialog.setCancelable(false);
        viewDialog.show();
    }

    private void changepwd(String password){
        SharedPreferences login = getSharedPreferences("login",MODE_PRIVATE);
        String username = login.getString("username",null);


        user.put("password", password);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("user").document(username)
                .set(user)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        EditText edtnewpwd = findViewById(R.id.prof_edtnewpwd);
                        EditText edtconnewpwd = findViewById(R.id.prof_edtconnewpwd);
                        edtnewpwd.setText("");
                        edtconnewpwd.setText("");

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //      Log.w( "Error adding document", e);
                    }
                });

    }

}

