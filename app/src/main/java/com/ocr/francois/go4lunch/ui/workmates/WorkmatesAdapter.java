package com.ocr.francois.go4lunch.ui.workmates;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ocr.francois.go4lunch.R;
import com.ocr.francois.go4lunch.models.User;
import com.ocr.francois.go4lunch.ui.MainActivity;

import java.util.List;

public class WorkmatesAdapter extends RecyclerView.Adapter<WorkmatesViewHolder> {

    private final List<User> workmates;
    private WorkmateItemClickCallback workmateItemClickCallback;

    public WorkmatesAdapter(List<User> users) {
        this.workmates = users;
    }

    void setWorkmateItemClickCallback(WorkmateItemClickCallback workmateItemClickCallback) {
        this.workmateItemClickCallback = workmateItemClickCallback;
    }

    public void updatesWorkmates(List<User> workmatesList, String currentUserId) {
        this.workmates.clear();

        for (User workmate : workmatesList) {
            if (!workmate.getId().equals(currentUserId)) {
                this.workmates.add(workmate);
            }
        }

        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public WorkmatesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.recycler_view_workmates_item, parent, false);

        return new WorkmatesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WorkmatesViewHolder holder, int position) {
        User workmate = workmates.get(position);
        holder.updateUi(workmate);
        if (holder.itemView.getContext() instanceof MainActivity) {
            if (workmate.choseARestaurant()) {
                holder.itemView.setOnClickListener(v -> workmateItemClickCallback.onWorkmateItemClick(workmate.getLunchRestaurantPlaceId()));
            }
        }
    }

    @Override
    public int getItemCount() {
        return workmates.size();
    }

    public interface WorkmateItemClickCallback {
        void onWorkmateItemClick(String placeId);
    }
}