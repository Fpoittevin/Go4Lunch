package com.ocr.francois.go4lunch.ui.workmates;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.ocr.francois.go4lunch.R;
import com.ocr.francois.go4lunch.models.User;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class WorkmatesAdapter extends RecyclerView.Adapter<WorkmatesAdapter.WorkmatesViewHolder> {

    private List<User> workmates;
    private WorkmateItemClickCallback workmateItemClickCallback;

    WorkmatesAdapter(List<User> users, WorkmateItemClickCallback workmateItemClickCallback) {
        this.workmates = users;
        this.workmateItemClickCallback = workmateItemClickCallback;
    }


    void updatesWorkmates(List<User> workmates) {
        this.workmates.clear();
        this.workmates.addAll(workmates);
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
        if (workmate.choseARestaurant()) {
            holder.view.setOnClickListener(v -> workmateItemClickCallback.onWorkmateItemClick(workmate.getLunchRestaurantPlaceId()));
        }
    }

    @Override
    public int getItemCount() {
        return workmates.size();
    }

    public interface WorkmateItemClickCallback {
        void onWorkmateItemClick(String placeId);
    }

    static class WorkmatesViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.recycler_view_workmates_item_text_view)
        TextView textView;
        @BindView(R.id.recycler_view_workmates_item_picture_image_view)
        ImageView pictureImageView;
        View view;

        WorkmatesViewHolder(@NonNull View itemView) {
            super(itemView);
            this.view = itemView;
            ButterKnife.bind(this, view);
        }

        void updateUi(User user) {

            String textToDisplay = user.getUserName() + " " + view.getResources().getString(R.string.hasnt_decided_yet);

            if (user.choseARestaurant()) {
                textToDisplay = user.getUserName() + " " + view.getResources().getString(R.string.is_eating_at) + " " + user.getLunchRestaurantName();
            }

            textView.setText(textToDisplay);

            if (user.getUrlPicture() != null) {
                Glide.with(view)
                        .load(user.getUrlPicture())
                        .apply(RequestOptions.circleCropTransform())
                        .into(pictureImageView);
            }
        }
    }
}