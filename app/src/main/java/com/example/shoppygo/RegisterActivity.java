package com.example.shoppygo;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity {

    RadioGroup accounttype;
    EditText emailEditText, passwordEditText, confirmPassEditText, companynameregister;
    Button registrationbtn, loginbtn;
    FirebaseAuth firebaseAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);

        accounttype = findViewById(R.id.accounttype);
        emailEditText = findViewById(R.id.emailregister);
        passwordEditText = findViewById(R.id.passwordregister);
        confirmPassEditText = findViewById(R.id.confirmpasswordregister);
        companynameregister = findViewById(R.id.companynameregister);

        registrationbtn = findViewById(R.id.registerbtn);
        loginbtn = findViewById(R.id.loginregbtn);

        firebaseAuth = FirebaseAuth.getInstance();

        accounttype.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.seller) {
                companynameregister.setVisibility(View.VISIBLE);
            } else {
                companynameregister.setVisibility(View.GONE);
            }
        });

        loginbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });

        registrationbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerUser();
            }
        });

    }


    private void registerUser() {
        String email = emailEditText.getText().toString().trim();
        String pass = passwordEditText.getText().toString().trim();
        String confirmPass = confirmPassEditText.getText().toString().trim();
        String companyname = companynameregister.getText().toString().trim();

        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(pass) || TextUtils.isEmpty(confirmPass)) {
            Toast.makeText(this, "Please fill all the fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailEditText.setError("Invalid Email");
            emailEditText.requestFocus();
            return;
        }



        if (pass.length() < 8) {
            passwordEditText.setError("Password should be longer that 8 char");
            passwordEditText.requestFocus();
            return;
        }

        if (!pass.equals(confirmPass)) {
            confirmPassEditText.setError("Password not matching");
            confirmPassEditText.requestFocus();
            return;
        }

        int type = accounttype.getCheckedRadioButtonId();
        if (type == -1) {
            Toast.makeText(RegisterActivity.this, "Please select an account type", Toast.LENGTH_SHORT).show();
            return;
        }

        String account = ((RadioButton)findViewById(type)).getText().toString();
        boolean isSeller = account.equals("Seller");
        if (isSeller) {
            if (companyname.isEmpty()){
                Toast.makeText(RegisterActivity.this, "Please enter the company name", Toast.LENGTH_SHORT).show();
                return;
            }
        }




        firebaseAuth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(RegisterActivity.this, "Register successful", Toast.LENGTH_SHORT).show();
                    DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("Users");
                    String id = task.getResult().getUser().getUid();
                    if (isSeller) {
                        Seller seller = new Seller(id, companyname, email);
                        usersRef.child(id).setValue(seller);
                        startActivity(new Intent(RegisterActivity.this, MainActivity.class));
                    } else {
                        Customer customer = new Customer(id, email, "", "");
                        usersRef.child(id).setValue(customer);
                        startActivity(new Intent(RegisterActivity.this, CustomerActivity.class));
                    }
                    finish();
                } else {
                    showRegisterException(task.getException());
                    Toast.makeText(RegisterActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void showRegisterException(Exception e) {
        if (e instanceof FirebaseAuthInvalidCredentialsException) {
            // email format
            Toast.makeText(this, "Invalid email format", Toast.LENGTH_SHORT).show();
        }
        else if (e instanceof FirebaseAuthUserCollisionException) {
            // email exists
            Toast.makeText(this, "This email is already registered", Toast.LENGTH_SHORT).show();
        }
        else {
            Toast.makeText(RegisterActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
        }
    }
}