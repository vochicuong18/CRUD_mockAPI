package com.example.API_CRUD;

import android.app.AlertDialog;
import android.content.Intent;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;


public class MainActivity extends AppCompatActivity {
    String url = "https://5fcec9123e19cc00167c62a3.mockapi.io/user";
    EditText txtEmail, txtPassword, reg_email, reg_password;
    Button btnLogin, btnSignUp, reg_register;
    TextInputLayout txtInLayoutUsername, txtInLayoutPassword, txtInLayoutRegPassword;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // get firebase auth instance
        auth = FirebaseAuth.getInstance();

        txtEmail = findViewById(R.id.email);
        txtPassword = findViewById(R.id.password);
        btnLogin = findViewById(R.id.login);
        btnSignUp = findViewById(R.id.signUp);
        txtInLayoutUsername = findViewById(R.id.txtInLayoutUsername);
        txtInLayoutPassword = findViewById(R.id.txtInLayoutPassword);

        clickLogin();
        //SignUp's Button for showing registration page
        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickSignUp();
            }
        });

    }

    //This is method for doing operation of check login
    private void clickLogin() {
            btnLogin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    boolean isValid = true;
                    if (txtEmail.getText().toString().trim().isEmpty()) {

                        Snackbar snackbar = Snackbar.make(view, "Please fill out these fields",
                                Snackbar.LENGTH_LONG);
                        View snackbarView = snackbar.getView();
                        snackbarView.setBackgroundColor(getResources().getColor(R.color.red));
                        snackbar.show();
                        txtInLayoutUsername.setError("Username should not be empty");
                        isValid = false;
                    }
                    if (txtPassword.getText().toString().trim().isEmpty()) {
                        Snackbar snackbar = Snackbar.make(view, "Please fill out these fields",
                                Snackbar.LENGTH_LONG);
                        View snackbarView = snackbar.getView();
                        snackbarView.setBackgroundColor(getResources().getColor(R.color.red));
                        snackbar.show();
                        txtInLayoutPassword.setError("Password should not be empty");
                        isValid = false;
                    }
                    if(isValid) {
                        txtInLayoutUsername.setError("");
                        txtInLayoutPassword.setError("");
                        handlerLogin(txtEmail.getText().toString(), txtPassword.getText().toString());
                    }
                }
            });

    }

    //The method for opening the registration page and another processes or checks for registering
    private void clickSignUp() {

        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.register, null);
        dialog.setView(dialogView);

        reg_email = dialogView.findViewById(R.id.reg_username);
        reg_password = dialogView.findViewById(R.id.reg_password);
        reg_register = dialogView.findViewById(R.id.reg_register);
        txtInLayoutRegPassword = dialogView.findViewById(R.id.txtInLayoutRegPassword);

        reg_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean isValid = true;
                if (reg_email.getText().toString().trim().isEmpty()) {

                    reg_email.setError("Please fill out this field");
                    isValid = false;
                }
                if (reg_password.getText().toString().trim().isEmpty()) {
                    txtInLayoutRegPassword.setPasswordVisibilityToggleEnabled(false);
                    reg_password.setError("Please fill out this field");
                    isValid = false;
                }
                if(isValid) {
                    handlerSignUp(reg_email.getText().toString(), reg_password.getText().toString());
                }
            }
        });

        dialog.show();
    }

    public void handlerLogin(String email, final String password) {
        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(MainActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Toast.makeText(MainActivity.this, "Email or password incorrect" + task.isSuccessful(), Toast.LENGTH_LONG).show();
                        } else {
                            Intent intent = new Intent(MainActivity.this, CrudActivity.class);
                            startActivity(intent);
                        }
                    }
                });
    }

    public void handlerSignUp(String username, String password) {
        auth.createUserWithEmailAndPassword(username, password)
                .addOnCompleteListener(MainActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Toast.makeText(MainActivity.this, "createUserWithEmail:onComplete:" + task.isSuccessful(), Toast.LENGTH_SHORT).show();
                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Toast.makeText(MainActivity.this, "Authentication failed." + task.getException(),
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
        clearFormSignUp();
    }

    private void clearFormSignUp() {
        reg_email.setText("");
        reg_password.setText("");
    }
}
