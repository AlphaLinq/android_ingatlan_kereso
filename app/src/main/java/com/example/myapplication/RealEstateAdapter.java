package com.example.myapplication;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;


public class RealEstateAdapter extends RecyclerView.Adapter<RealEstateAdapter.ViewHolder> implements Filterable {
    private ArrayList<RealEstateItem> mRealEstatesData;
    private ArrayList<RealEstateItem> mRealEstatesDataAll;
    private Context mContext;
    private int lastPosition = -1;

    RealEstateAdapter(Context context, ArrayList<RealEstateItem> itemData) {
        this.mContext = context;
        this.mRealEstatesData = itemData;
        this.mRealEstatesDataAll = new ArrayList<>(itemData);
    }

    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(mContext).inflate(R.layout.list_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RealEstateAdapter.ViewHolder holder, int position) {
        RealEstateItem currentItem = mRealEstatesData.get(position);

        holder.bindTo(currentItem);

        if (holder.getAdapterPosition() > lastPosition){
            Animation animation = AnimationUtils.loadAnimation(mContext, R.anim.slide_in_row);
            holder.itemView.startAnimation(animation);
            lastPosition = holder.getAdapterPosition();
        }
    }

    @Override
    public int getItemCount() {
        return mRealEstatesData.size();
    }

    @Override
    public Filter getFilter() {
        return realEstateFilter;
    }
    private Filter realEstateFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            ArrayList<RealEstateItem> filteredList = new ArrayList<>();
            FilterResults results = new FilterResults();
            if (constraint == null || constraint.length() == 0) {
                results.count = mRealEstatesDataAll.size();
                results.values = mRealEstatesDataAll;
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();

                for (RealEstateItem item : mRealEstatesDataAll) {
                    if (item.getBaseArea().toLowerCase().contains(filterPattern)) {
                        filteredList.add(item);
                    }
                }

                results.count = filteredList.size();
                results.values = filteredList;
            }

            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            mRealEstatesData = (ArrayList) results.values;
            notifyDataSetChanged();
        }
    };

    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView mAddress;
        private TextView mBaseArea;
        private TextView mInfoText;
        private TextView mPriceText;
        private ImageView mItemImage;
        private TextView mRoomsText;

        private TextView mPhoneNumber;

        public ViewHolder(View itemView) {
            super(itemView);
            mAddress = itemView.findViewById(R.id.itemAddress);
            mBaseArea = itemView.findViewById(R.id.itemBaseArea);
            mInfoText = itemView.findViewById(R.id.itemDescription);
            mPriceText = itemView.findViewById(R.id.price);
            mItemImage = itemView.findViewById(R.id.itemImage);
            mRoomsText = itemView.findViewById(R.id.rooms);
            mPhoneNumber = itemView.findViewById(R.id.phoneNumber);

        }

        public void bindTo(RealEstateItem currentItem) {
            mAddress.setText(currentItem.getAddress());
            mBaseArea.setText(currentItem.getBaseArea());
            mInfoText.setText(currentItem.getDescription());
            mPriceText.setText(currentItem.getPrice());
            mRoomsText.setText(currentItem.getRooms());
            mPhoneNumber.setText(currentItem.getPhoneNumber());

            Glide.with(mContext).load(currentItem.getImageUrl()).into(mItemImage);
            itemView.findViewById(R.id.delete).setOnClickListener(view -> ((RealEstateListActivity)mContext).deleteItem(currentItem));
        }
    }
}

