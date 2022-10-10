package com.example.cowootalkapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

public class FacebookLoginActivity extends AppCompatActivity {

    private CallbackManager mCallbackManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mCallbackManager = CallbackManager.Factory.create();
        LoginManager.getInstance().logInWithReadPermissions(
                this, Arrays.asList("user_photos", "email", "user_birthday", "public_profile")
        );
        LoginManager.getInstance().registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(@NonNull LoginResult loginResult) {
                setResult(RESULT_OK);
                Intent loginIntent = new Intent(FacebookLoginActivity.this, FacebookRegActivity.class);
                startActivity(loginIntent);
                finish();
            }

            @Override
            public void onCancel() {
                System.out.println("Cancel");
                finish();
                setResult(RESULT_CANCELED);
            }

            @Override
            public void onError(@NonNull FacebookException e) {
                // Handle exception
            }
        });

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
    }

}