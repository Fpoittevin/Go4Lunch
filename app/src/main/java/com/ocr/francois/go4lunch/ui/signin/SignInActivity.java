package com.ocr.francois.go4lunch.ui.signin;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import androidx.lifecycle.ViewModelProviders;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.ocr.francois.go4lunch.R;
import com.ocr.francois.go4lunch.injection.Injection;
import com.ocr.francois.go4lunch.injection.ViewModelFactory;
import com.ocr.francois.go4lunch.ui.MainActivity;
import com.ocr.francois.go4lunch.ui.base.BaseActivity;
import com.ocr.francois.go4lunch.ui.viewmodels.UserViewModel;

import butterknife.BindView;

import static com.ocr.francois.go4lunch.R.id.activity_sign_in_progress_bar;

public class SignInActivity extends BaseActivity {

    private static final int RC_GOOGLE_SIGN_IN = 123;
    @BindView(R.id.sign_in_activity_facebook_sign_in_button)
    LoginButton facebookSignInButton;
    @BindView(R.id.sign_in_activity_fake_facebook_sign_in_button)
    Button fakeFacebookSignInButton;
    @BindView(R.id.sign_in_activity_google_sign_in_button)
    Button googleSignInButton;
    private FirebaseAuth auth;
    private GoogleSignInClient googleSignInClient;
    private CallbackManager callbackManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        updateUI();
        callbackManager = CallbackManager.Factory.create();
        auth = FirebaseAuth.getInstance();
        configureGoogleSignIn();
        configureFacebookSignIn();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_sign_in;
    }

    private void configureGoogleSignIn() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        googleSignInClient = GoogleSignIn.getClient(this, gso);
        googleSignInButton.setOnClickListener(v -> {
            Intent signInIntent = googleSignInClient.getSignInIntent();
            startActivityForResult(signInIntent, RC_GOOGLE_SIGN_IN);
        });
    }

    private void configureFacebookSignIn() {
        fakeFacebookSignInButton.setOnClickListener(v -> facebookSignInButton.performClick());
        callbackManager = CallbackManager.Factory.create();
        facebookSignInButton.setPermissions("email", "public_profile");
        facebookSignInButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d("facebook", "facebook success");
                AuthCredential credential = FacebookAuthProvider.getCredential(loginResult.getAccessToken().getToken());
                authWithFirebase(credential);
            }

            @Override
            public void onCancel() {
                Log.d("Facebook", "facebook cancel");
            }

            @Override
            public void onError(FacebookException error) {
                Log.d("Facebook error", "facebook:onError", error);
                Snackbar.make(findViewById(getLayoutId()), R.string.facebook_sign_in_error, Snackbar.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_GOOGLE_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                if (account != null) {
                    AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
                    authWithFirebase(credential);
                }
            } catch (ApiException e) {
                Snackbar.make(findViewById(getLayoutId()), R.string.google_sign_in_error, Snackbar.LENGTH_SHORT).show();
            }
        } else {
            callbackManager.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void authWithFirebase(AuthCredential credential) {
        showProgressBar(activity_sign_in_progress_bar);
        auth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        updateUI();
                    } else {
                        Toast.makeText(this, R.string.auth_error, Toast.LENGTH_LONG).show();
                    }
                    hideProgressBar(activity_sign_in_progress_bar);
                });
    }

    private void updateUI() {
        if (isCurrentUserLogged()) {
            ViewModelFactory viewModelFactory = Injection.provideViewModelFactory();
            UserViewModel userViewModel = ViewModelProviders.of(this, viewModelFactory).get(UserViewModel.class);

            FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
            if (currentUser != null) {
                String urlPicture = "";
                if (currentUser.getPhotoUrl() != null) {
                    urlPicture = currentUser.getPhotoUrl().toString();
                }
                userViewModel.createUser(currentUser.getUid(), currentUser.getDisplayName(), urlPicture);

                Intent mainActivityIntent = new Intent(this, MainActivity.class);
                startActivity(mainActivityIntent);
            }
        }
    }
}