package com.ocr.francois.go4lunch.ui.workmates;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.ocr.francois.go4lunch.R;
import com.ocr.francois.go4lunch.models.User;
import com.ocr.francois.go4lunch.ui.MainActivity;

import butterknife.BindView;
import butterknife.ButterKnife;

class WorkmatesViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.recycler_view_workmates_item_text_view)
    TextView textView;
    @BindView(R.id.recycler_view_workmates_item_picture_image_view)
    ImageView pictureImageView;
    private View view;

    WorkmatesViewHolder(@NonNull View itemView) {
        super(itemView);
        this.view = itemView;
        ButterKnife.bind(this, view);
    }

    void updateUi(User user) {

        String textToDisplay;

        if (view.getContext() instanceof MainActivity) {
            textToDisplay = user.getUserName() + view.getResources().getString(R.string.hasnt_decided_yet);
            if (user.choseARestaurant()) {
                textToDisplay = user.getUserName() + view.getResources().getString(R.string.is_eating_at) + user.getLunchRestaurantName();
            }
        } else {
            textToDisplay = user.getUserName() + view.getResources().getString(R.string.is_joining);
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
