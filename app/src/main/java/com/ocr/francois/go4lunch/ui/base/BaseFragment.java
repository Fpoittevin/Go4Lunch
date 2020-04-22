package com.ocr.francois.go4lunch.ui.base;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.ocr.francois.go4lunch.injection.Injection;
import com.ocr.francois.go4lunch.injection.ViewModelFactory;
import com.ocr.francois.go4lunch.models.User;
import com.ocr.francois.go4lunch.ui.viewmodels.LunchViewModel;

import java.util.List;

public abstract class BaseFragment extends Fragment {

    protected LunchViewModel lunchViewModel;

    protected void configureLunchViewModel() {

        ViewModelFactory viewModelFactory = Injection.provideViewModelFactory();
        lunchViewModel = ViewModelProviders.of(getActivity(), viewModelFactory).get(LunchViewModel.class);
    }
}
