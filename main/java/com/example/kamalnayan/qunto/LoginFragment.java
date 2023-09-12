package com.example.kamalnayan.qunto;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCanceledListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class LoginFragment extends Fragment {
   private TextInputEditText email;
   private TextInputEditText password;
   private ImageView slideLeft;
   private TextView fPassword;
   private TextView login;
    private Animation leftToRight;
    private Animation zoomIn;
    private FirebaseAuth mAuth;
    public LoginFragment() {

    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        email=view.findViewById(R.id.email_input_edit);
        password=view.findViewById(R.id.password_input_edit);
        slideLeft=view.findViewById(R.id.swiplefticon);
        fPassword=view.findViewById(R.id.fpassword);
        login=view.findViewById(R.id.caption);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
      leftToRight= AnimationUtils.loadAnimation(getContext(),R.anim.left_to_right);
      zoomIn=AnimationUtils.loadAnimation(getContext(),R.anim.zoom_in);
        mAuth = FirebaseAuth.getInstance();
        fPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                forgotPassword();
            }
        });
    }
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser){
        super.setUserVisibleHint(isVisibleToUser);
        if(isVisibleToUser && getActivity()!=null){
            slideLeft.startAnimation(leftToRight);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        setUserVisibleHint(true);
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login.startAnimation(zoomIn);
                signIn(getActivity());
            }
        });

    }
    void updateUI(FirebaseUser currentUser)
    {
        if(currentUser!=null)
        {
            //write code;
            Intent intent=new Intent(getContext(),Home.class);
            startActivity(intent);
            getActivity().finish();
        }
        else return;
    }
    void signIn(final Activity context)
    {
        String emailid=email.getText().toString().trim();
        String pass=password.getText().toString().trim();
        if(emailid.isEmpty())
        {
            email.setError("Please enter your registered email");
        }
        else if(pass.isEmpty())
        {
            password.setError("please enter password");
        }
        else
        {
            mAuth.signInWithEmailAndPassword(emailid, pass)
                    .addOnCompleteListener(context, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                Log.d("email", "signInWithEmail:success");
                                FirebaseUser user = mAuth.getCurrentUser();
                                updateUI(user);
                            } else {

                                Log.w("failed", "signInWithEmail:failure", task.getException());
                                Toast.makeText(context, "Authentication failed.",Toast.LENGTH_SHORT).show();
                                updateUI(null);
                            }

                        }
                    });
        }
    }
    void forgotPassword()
    {

        final String emailid=email.getText().toString().trim();
        if(emailid.isEmpty())
        {
            email.setError("Please enter your registered email");
        }
        else
        {
            mAuth.sendPasswordResetEmail(emailid)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                               new AlertDialog.Builder(getContext())
                                       .setTitle("Password Reset Successful")
                                       .setMessage("E-mail has been sent to email id "+emailid)
                                       .setCancelable(true)
                                       .setPositiveButton(
                                               "Ok",
                                               new DialogInterface.OnClickListener()   {

                                                   @Override
                                                   public void onClick(DialogInterface dialog, int which) {
                                                       dialog.cancel();
                                                   }
                                               })
                                       .show();
                            }
                        }
                    })
            .addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getContext(),e.toString(),Toast.LENGTH_SHORT).show();
                }

                                   }

            );
        }
    }
}
