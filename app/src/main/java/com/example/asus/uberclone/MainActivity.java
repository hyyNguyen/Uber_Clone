package com.example.asus.uberclone;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.constraint.solver.widgets.ConstraintWidget;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telecom.Call;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.asus.uberclone.Model.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.rengwuxian.materialedittext.MaterialEditText;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class MainActivity extends AppCompatActivity {

    private Button btnSignin, btnRegister;
    RelativeLayout rootLayout;

    FirebaseAuth auth;
    FirebaseDatabase db;
    DatabaseReference users;



    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //font
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                                            .setDefaultFontPath("fonts/Parisian.ttf")
                                            .setFontAttrId(R.attr.fontPath)
                                            .build());
        setContentView(R.layout.activity_main);

        initView();
        initFirebase();
    }

    private void initFirebase() {
        auth = FirebaseAuth.getInstance();
        db = FirebaseDatabase.getInstance();
        users = db.getReference("Users");
    }

    private void initView() {
        rootLayout = findViewById(R.id.rootlayout);
        btnSignin = findViewById(R.id.btn_singin);
        btnSignin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showlogindialog();
            }
        });
        btnRegister= findViewById(R.id.btn_register);
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialogRegister();
            }
        });
    }

    private void showlogindialog() {

        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("SIGN IN");
        dialog.setMessage("please use email to sign in");

        LayoutInflater inflater = getLayoutInflater().from(this);
        View sign_layout = inflater.inflate(R.layout.layout_login,null);

        final MaterialEditText edtEmail = sign_layout.findViewById(R.id.edt_email);
        final MaterialEditText edtpassword = sign_layout.findViewById(R.id.edt_password);



        dialog.setView(sign_layout);
        dialog.setPositiveButton("REGISTER", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();

                //check vallitation
                if(TextUtils.isEmpty(edtEmail.getText().toString())){
                    Snackbar.make(rootLayout,"please enter email address",Snackbar.LENGTH_SHORT)
                            .show();
                    return;
                }

                if(TextUtils.isEmpty(edtpassword.getText().toString())){
                    Snackbar.make(rootLayout,"please enter password",Snackbar.LENGTH_SHORT)
                            .show();
                    return;
                }

                if(edtpassword.getText().toString().length() < 6){
                    Snackbar.make(rootLayout,"password to short !!!",Snackbar.LENGTH_SHORT)
                            .show();
                    return;
                }
            }
        });
        //login

        auth.signInWithEmailAndPassword(edtEmail.getText().toString(),edtpassword.getText().toString())
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        startActivity(new Intent(MainActivity.this, welcome.class));
                        finish();
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Snackbar.make(rootLayout,"Failed"+e.getMessage(),Snackbar.LENGTH_SHORT)
                        .show();
            }
        });

        dialog.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }


    private void showDialogRegister() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("Register");
        dialog.setMessage("please use email to register");

        LayoutInflater inflater = getLayoutInflater().from(this);
        View register_layout = inflater.inflate(R.layout.layout_register,null);

        final MaterialEditText edtEmail = register_layout.findViewById(R.id.edt_email);
        final MaterialEditText edtpassword = register_layout.findViewById(R.id.edt_password);
        final MaterialEditText edtname = register_layout.findViewById(R.id.edt_name);
        final MaterialEditText edtphone = register_layout.findViewById(R.id.edt_phone);


        dialog.setView(register_layout);
        dialog.setPositiveButton("REGISTER", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();

                //check vallitation
                if(TextUtils.isEmpty(edtEmail.getText().toString())){
                    Snackbar.make(rootLayout,"please enter email address",Snackbar.LENGTH_SHORT)
                            .show();
                    return;
                }

                if(TextUtils.isEmpty(edtpassword.getText().toString())){
                    Snackbar.make(rootLayout,"please enter password",Snackbar.LENGTH_SHORT)
                            .show();
                    return;
                }

                if(edtpassword.getText().toString().length() < 6){
                    Snackbar.make(rootLayout,"password to short !!!",Snackbar.LENGTH_SHORT)
                            .show();
                    return;
                }

                if(TextUtils.isEmpty(edtname.getText().toString())){
                    Snackbar.make(rootLayout,"please enter name",Snackbar.LENGTH_SHORT)
                            .show();
                    return;
                }

                if(TextUtils.isEmpty(edtphone.getText().toString())){
                    Snackbar.make(rootLayout,"please enter phone number",Snackbar.LENGTH_SHORT)
                            .show();
                    return;
                }

                //register new user

                auth.createUserWithEmailAndPassword(edtEmail.getText().toString(),edtpassword.getText().toString())
                    .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                        @Override
                        public void onSuccess(AuthResult authResult) {
                            User user = new User();
                            user.setEmail(edtEmail.getText().toString());
                            user.setPassword(edtpassword.getText().toString());
                            user.setName(edtname.getText().toString());
                            user.setPhone(edtphone.getText().toString());

                            //key
                            users.child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                    .setValue(user)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Snackbar.make(rootLayout, "Register sussecss fully !!!", Snackbar.LENGTH_SHORT)
                                                    .show();
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Snackbar.make(rootLayout, "Failed " + e.getMessage(), Snackbar.LENGTH_SHORT)
                                            .show();
                                }
                            });

                        }
                }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Snackbar.make(rootLayout,"Register sussecss fully !!!",Snackbar.LENGTH_SHORT)
                                            .show();
                                }
                            });
            }
        });

        dialog.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }
}

