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
        String companyname = companynameregister.getText().toString().trim(); //ESTA. COMO LA GUARDO EN DB

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
        if (account.equals("Seller")) {
            if (companyname.isEmpty()){
                Toast.makeText(RegisterActivity.this, "Please enter the company name", Toast.LENGTH_SHORT).show();
            }
        }
        //if i am here it means
        firebaseAuth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(RegisterActivity.this, "success register", Toast.LENGTH_SHORT).show();
                    if (!companyname.isEmpty()) {
                        startActivity(new Intent(RegisterActivity.this, MainActivity.class));
                        finish();
                    } else {
//                        startActivity(new Intent(RegisterActivity.this, UserDashboard.class));
                        finish();
                    }
                } else {
                    Toast.makeText(RegisterActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}