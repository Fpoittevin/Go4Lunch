package com.ocr.francois.go4lunch.ui.workmates;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ocr.francois.go4lunch.R;
import com.ocr.francois.go4lunch.models.User;
import com.ocr.francois.go4lunch.ui.base.BaseFragment;

import java.util.List;

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
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_workmates, container, false);
        ButterKnife.bind(this, view);

        configureLunchViewModel();
        configureRecyclerView();
        getUsers();

        return view;
    }

    private void configureRecyclerView() {
        workmatesAdapter = new WorkmatesAdapter(getContext(), users);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setAdapter(workmatesAdapter);
        recyclerView.setLayoutManager(layoutManager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(), layoutManager.getOrientation());
        recyclerView.addItemDecoration(dividerItemDecoration);
    }

    private void getUsers() {
        lunchViewModel.getUsers().observe(this, new Observer<List<User>>() {
            @Override
            public void onChanged(List<User> usersnew) {
                if (!usersnew.isEmpty()) {
                    Log.d("CHANGE !!!", "CHANGE !!!!!!!!!!!!!");
                    setUsers(usersnew);
                    Log.d("ON CHANGE !!!!!!!", String.valueOf(users.size()));
                    workmatesAdapter.notifyDataSetChanged();
                }
            }
        });
    }
}
