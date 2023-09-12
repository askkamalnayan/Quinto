package com.example.kamalnayan.qunto;

import android.app.Activity;
import android.content.Context;
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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class SignupFragment extends Fragment {
    private TextInputEditText email;
    private TextInputEditText password;
    private ImageView slideLeft;
    private TextInputEditText rPassword;
    private TextView signup;
    private Animation zoomIn;
    private Animation RightToLeft;
    private FirebaseAuth mAuth;
    public SignupFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_signup, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        email=view.findViewById(R.id.email_input_edit1);
        password=view.findViewById(R.id.password_input_edit1);
        slideLeft=view.findViewById(R.id.swiperighticon);
        rPassword=view.findViewById(R.id.confirm_password_edit1);
        signup=view.findViewById(R.id.caption1);

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        RightToLeft= AnimationUtils.loadAnimation(getContext(),R.anim.right_to_left );
        mAuth = FirebaseAuth.getInstance();
        zoomIn=AnimationUtils.loadAnimation(getContext(),R.anim.zoom_in);
        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signup.startAnimation(zoomIn);
                createuser(getActivity());
            }
        });
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser){
        super.setUserVisibleHint(isVisibleToUser);
        if(isVisibleToUser && getActivity()!=null){
            slideLeft.startAnimation(RightToLeft);
        }
    }

    @Override
    public void onStart() {
        setUserVisibleHint(true);
        super.onStart();
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
    void createuser(final Activity context)
    {
        String emailid=email.getText().toString().trim();
        String pass=password.getText().toString().trim();
        String fpass=rPassword.getText().toString().trim();
        if(emailid.isEmpty())
        {
            email.setError("enter your email id");
        }
        else if(pass.isEmpty())
        {
            password.setError("enter your password");
        }
        else if(fpass.isEmpty())
        {
            rPassword.setError("confirm your password");
        }
        else if(!pass.equals(fpass))
        {
            rPassword.setError("passwords does not match");
        }
        else {
            mAuth.createUserWithEmailAndPassword(emailid, pass)
                    .addOnCompleteListener(context, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                FirebaseUser user = mAuth.getCurrentUser();
                                updateUI(user);
                            } else {
                                // If sign in fails, display a message to the user.
                                Log.w("email ", "createUserWithEmail:failure", task.getException());
                                Toast.makeText(context, "Authentication failed.", Toast.LENGTH_SHORT).show();
                               updateUI(null);
                            }

                            // ...
                        }
                    });
        }
    }
}
