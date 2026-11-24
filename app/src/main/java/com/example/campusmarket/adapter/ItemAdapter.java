package com.example.campusmarket.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.campusmarket.R;
import com.example.campusmarket.model.Item;

import java.util.List;
import java.util.Locale;

public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ItemViewHolder> {

    public interface OnItemClickListener {
        void onItemClick(Item item);
    }

    private final List<Item> items;
    private final OnItemClickListener listener;

    public ItemAdapter(List<Item> items, OnItemClickListener listener) {
        this.items = items;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_row_card, parent, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        Item item = items.get(position);
        holder.bind(item, listener);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvTitle;
        private final TextView tvPrice;
        private final TextView tvCategory;
        private final TextView tvCondition;
        private final TextView tvSeller;
        private final TextView tvSellerPhone;
        private final ImageView ivProductImage;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvPrice = itemView.findViewById(R.id.tvPrice);
            tvCategory = itemView.findViewById(R.id.tvCategory);
            tvCondition = itemView.findViewById(R.id.tvCondition);
            tvSeller = itemView.findViewById(R.id.tvSeller);
            tvSellerPhone = itemView.findViewById(R.id.tvSellerPhone);
            ivProductImage = itemView.findViewById(R.id.ivProductImage);
        }

        void bind(Item item, OnItemClickListener listener) {
            tvTitle.setText(item.getTitle() != null ? item.getTitle() : "Untitled Item");
            tvPrice.setText(String.format(Locale.US, "UGX %,.0f", item.getPrice()));

            String cat = item.getCategory() != null ? item.getCategory().toUpperCase(Locale.getDefault()) : "OTHER";
            tvCategory.setText(cat);

            String cond = item.getCondition() != null ? item.getCondition().toUpperCase(Locale.getDefault()) : "USED";
            tvCondition.setText(cond);

            tvSeller.setText(item.getSellerEmail() != null ? item.getSellerEmail() : "campus@student.ac.ug");

            String phone = item.getSellerPhone() != null ? item.getSellerPhone() : "+256 781 000 000";
            tvSellerPhone.setText("Call/WhatsApp: " + phone);

            String imageUrl = item.getImageUrl();
            if (imageUrl != null && !imageUrl.trim().isEmpty()) {
                ivProductImage.setVisibility(View.VISIBLE);
                Glide.with(itemView.getContext())
                        .load(imageUrl)
                        .into(ivProductImage);
            } else {
                ivProductImage.setVisibility(View.GONE);
            }

            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onItemClick(item);
                }
            });
        }
    }
}
