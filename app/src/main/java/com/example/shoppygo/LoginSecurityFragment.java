package com.example.shoppygo;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class LoginSecurityFragment extends Fragment {

    private EditText oldpass, newpass;
    private Button updateAccount, deleteAccount;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser currentUser;


    public LoginSecurityFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_login_security, container, false);

        oldpass = view.findViewById(R.id.oldpass);
        newpass = view.findViewById(R.id.newpass);
        updateAccount = view.findViewById(R.id.updateAccount);
        deleteAccount = view.findViewById(R.id.deleteAccount);

        firebaseAuth = FirebaseAuth.getInstance();
        currentUser = firebaseAuth.getCurrentUser();

        updateAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String oldPassword = oldpass.getText().toString().trim();
                String newPassword = newpass.getText().toString().trim();

                if (oldPassword.isEmpty() || newPassword.isEmpty()) {
                    Toast.makeText(getContext(), "All fields must be filled", Toast.LENGTH_SHORT).show();
                } else {
                    AuthCredential credential = EmailAuthProvider.getCredential(currentUser.getEmail(), oldPassword); //Reauth para confirmar
                    currentUser.reauthenticate(credential).addOnCompleteListener(authTask -> { //que si puede cambiar la cntr
                        if (authTask.isSuccessful()) {
                            currentUser.updatePassword(newPassword).addOnCompleteListener(updateTask -> {
                                if (updateTask.isSuccessful()) {
                                    Toast.makeText(getContext(), "Password updated", Toast.LENGTH_SHORT).show();
                                    oldpass.setText("");
                                    newpass.setText("");
                                } else {
                                    Toast.makeText(getContext(), "Update failed", Toast.LENGTH_SHORT).show();
                                }
                            });
                        } else {
                            Toast.makeText(getContext(), "Incorrect password", Toast.LENGTH_SHORT).show();
                        }
                    });
                }

            }
        });

        deleteAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentUser.delete().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(getContext(), "Account Deleted Successfully", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(getActivity(), RegisterActivity.class);
                        startActivity(intent);
                        requireActivity().finish();
                    } else {
                        Toast.makeText(getContext(), "Something went wrong", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
        return view;
    }
}
