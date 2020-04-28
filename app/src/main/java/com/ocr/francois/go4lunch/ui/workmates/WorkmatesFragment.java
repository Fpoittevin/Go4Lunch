package com.ocr.francois.go4lunch.ui.workmates;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ocr.francois.go4lunch.R;
import com.ocr.francois.go4lunch.ui.base.BaseFragment;

import java.util.ArrayList;
import java.util.Collections;

import butterknife.BindView;
import butterknife.ButterKnife;


public class WorkmatesFragment extends BaseFragment {

    @BindView(R.id.fragment_workmates_recycler_view)
    RecyclerView recyclerView;
    private WorkmatesAdapter workmatesAdapter;

    public static WorkmatesFragment newInstance() {
        return new WorkmatesFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_workmates, container, false);

        ButterKnife.bind(this, view);
        configureLunchViewModel();
        configureRecyclerView();
        getUsers();

        return view;
    }

    private void configureRecyclerView() {
        workmatesAdapter = new WorkmatesAdapter(new ArrayList<>(), (WorkmatesAdapter.WorkmateItemClickCallback) getActivity());
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setAdapter(workmatesAdapter);
        recyclerView.setLayoutManager(layoutManager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(), layoutManager.getOrientation());
        recyclerView.addItemDecoration(dividerItemDecoration);
    }

    protected void updateUiWhenDataChange() {
        if (!users.isEmpty()) {
            sortWorkmatesList();
            workmatesAdapter.updatesWorkmates(users);
        }
    }

    private void sortWorkmatesList() {

        Collections.sort(users, (user1, user2) -> {
            if (user1.choseARestaurant() == user2.choseARestaurant()) {
                return 0;
            } else if (user1.choseARestaurant()) {
                return -1;
            } else {
                return 1;
            }
        });
    }
}