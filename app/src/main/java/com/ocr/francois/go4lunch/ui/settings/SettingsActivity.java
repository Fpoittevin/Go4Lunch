package com.ocr.francois.go4lunch.ui.settings;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProviders;

import com.firebase.ui.auth.AuthUI;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.ocr.francois.go4lunch.R;
import com.ocr.francois.go4lunch.injection.Injection;
import com.ocr.francois.go4lunch.injection.ViewModelFactory;
import com.ocr.francois.go4lunch.ui.base.BaseActivity;
import com.ocr.francois.go4lunch.ui.viewmodels.UserViewModel;

import java.util.Objects;

import butterknife.BindView;

public class SettingsActivity extends BaseActivity {

    @BindView(R.id.activity_settings_toolbar)
    MaterialToolbar toolbar;
    @BindView(R.id.activity_settings_delete_account_button)
    MaterialButton deleteAccountButton;

    private UserViewModel userViewModel;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.activity_settings_frame_layout, new SettingsFragment())
                .commit();

        configureToolBar();
        configureDeleteAccountButton();
        configureUserViewModel();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_settings;
    }

    private void configureToolBar() {
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    private void configureDeleteAccountButton() {
        deleteAccountButton.setOnClickListener(v -> deleteAccount());
    }

    private void deleteAccount() {
        MaterialAlertDialogBuilder dialogBuilder = new MaterialAlertDialogBuilder(this);
        dialogBuilder.setMessage(getString(R.string.delete_account_dialog_text))
                .setPositiveButton(getString(R.string.yes), (dialog, which) -> {
                    if (getCurrentUser() != null) {
                        userViewModel.deleteUser(getCurrentUser().getUid())
                                .addOnSuccessListener(this, aVoid -> AuthUI.getInstance()
                                        .delete(getApplicationContext())
                                        .addOnSuccessListener(bVoid -> startSignInActivity()));
                    }
                })
                .setNegativeButton(getString(R.string.no), null)
                .create().show();
    }

    private void configureUserViewModel() {
        ViewModelFactory viewModelFactory = Injection.provideViewModelFactory();
        userViewModel = ViewModelProviders.of(this, viewModelFactory).get(UserViewModel.class);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return true;
    }
}