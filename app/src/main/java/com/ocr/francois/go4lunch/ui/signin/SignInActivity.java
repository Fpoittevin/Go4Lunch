package com.ocr.francois.go4lunch.ui.signin;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;
import com.ocr.francois.go4lunch.ui.MainActivity;
import com.ocr.francois.go4lunch.R;
import com.ocr.francois.go4lunch.ui.base.BaseActivity;

import butterknife.BindView;

import static com.ocr.francois.go4lunch.R.id.activity_sign_in_progress_bar;

public class SignInActivity extends BaseActivity {

    private static final int RC_GOOGLE_SIGN_IN = 123;
    //@BindView(R.id.sign_in_activity_facebook_sign_in_button)
    //LoginButton facebookSignInButton;
    @BindView(R.id.sign_in_activity_google_sign_in_button)
    Button googleSignInButton;
    private FirebaseAuth auth;
    private GoogleSignInClient googleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        updateUI();
        auth = FirebaseAuth.getInstance();
        configureGoogleSignIn();
        configureFacebookSignIn();
    }

    @Override
    protected int getLayout() {
        return R.layout.activity_sign_in;
    }

    private void configureGoogleSignIn() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        googleSignInClient = GoogleSignIn.getClient(this, gso);
        googleSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent signInIntent = googleSignInClient.getSignInIntent();
                startActivityForResult(signInIntent, RC_GOOGLE_SIGN_IN);
            }
        });
    }

    private void configureFacebookSignIn() {

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_GOOGLE_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);

                authWithFirebase(credential);
            } catch (ApiException e) {
                Snackbar.make(findViewById(getLayout()), R.string.google_sign_in_error, Snackbar.LENGTH_SHORT).show();
            }
        }
    }

    private void authWithFirebase(AuthCredential credential) {
        showProgressBar(activity_sign_in_progress_bar);
        auth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // TODO: add createUserInFirestore
                            updateUI();
                        } else {
                            Snackbar.make(findViewById(getLayout()), R.string.auth_error, Snackbar.LENGTH_SHORT).show();
                        }
                        hideProgressBar(activity_sign_in_progress_bar);
                    }
                });
    }

    private void updateUI() {
        if (isCurrentUserLogged()) {
            Intent mainActivityIntent = new Intent(this, MainActivity.class);
            startActivity(mainActivityIntent);
        }
    }
}