package com.ocr.francois.go4lunch.ui.workmates;

import android.content.Context;
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
import com.ocr.francois.go4lunch.utils.DateTool;

import java.util.List;

import bolts.Task;
import butterknife.BindView;
import butterknife.ButterKnife;

public class WorkmatesAdapter extends RecyclerView.Adapter<WorkmatesAdapter.WorkmatesViewHolder> {
    private Context context;
    private List<User> workmates;

    public WorkmatesAdapter(Context context, List<User> users) {
        this.context = context;
        this.workmates = users;
    }

    @NonNull
    @Override
    public WorkmatesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.recycler_view_workmates_item, parent, false);

        return new WorkmatesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WorkmatesViewHolder holder, int position) {

        User workmate = workmates.get(position);

        holder.updateUi(context, workmate);
    }

    @Override
    public int getItemCount() {
        return workmates.size();
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

        void updateUi(Context context, User user) {

            String textToDisplay = user.getUserName() + " " + context.getResources().getString(R.string.hasnt_decided_yet);

            if (user.getLunchTimestamp() != null && user.getLunchTimestamp().compareTo(DateTool.getTodayMidnightTimestamp()) == 1) {
                if (user.getLunchRestaurantName() != null) {
                    textToDisplay = user.getUserName() + " " + context.getResources().getString(R.string.is_eating_at) + " " + user.getLunchRestaurantName();
                }
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
