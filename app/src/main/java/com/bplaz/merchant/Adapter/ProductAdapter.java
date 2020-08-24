package com.bplaz.merchant.Adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bplaz.merchant.Class.ProductClass;
import com.bplaz.merchant.Connection.BasedURL;
import com.bplaz.merchant.R;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> implements Filterable {

    private List<ProductClass> mData;
    private Context mCtx;
    public static List<ProductClass> jobByMonthList;
    private onClickJobByMonth mListener;
    Activity activity;
    private List<ProductClass> mDataListFiltered;

    public ProductAdapter(Context mCtx, List<ProductClass> jobByMonthList, onClickJobByMonth mListener, Activity activity) {
        this.mCtx = mCtx;
        this.mData = jobByMonthList;
        this.jobByMonthList = jobByMonthList;
        this.mListener = mListener;
        this.activity = activity;
    }

    @Override
    public ProductViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mCtx);
        View view = inflater.inflate(R.layout.adapter_product_list, null,false);
        RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        view.setLayoutParams(lp);
        return new ProductViewHolder(view);


    }

    @Override
    public void onBindViewHolder(final ProductViewHolder holder, final int position) {
        final ProductClass jobByMonthClass = jobByMonthList.get(position);


        //LOAD IMAGE PRODUCT
        holder.imageView_product_image.setBackground(null);
        if(jobByMonthClass.getImage_product().contains("uploads")) {
            Picasso.get().load(BasedURL.ROOT_URL_IMAGE + jobByMonthClass.getImage_product()).into(holder.imageView_product_image);
        }else{
            Drawable customs = mCtx.getDrawable(R.drawable.no_image_product);
            holder.imageView_product_image.setBackground(customs);
        }

        //SHOW ICON APPROVAL OR PENDING BPLAZ

        holder.imageView_status.setVisibility(View.GONE);
        holder.imageView_status.setBackground(null);
        if(jobByMonthClass.getStatus().equals("0")){ //NOT SUBMIT
            holder.imageView_status.setVisibility(View.GONE);
        }else if(jobByMonthClass.getStatus().equals("1")){ //PENDING APPROVAL
            holder.imageView_status.setVisibility(View.VISIBLE);
            Drawable custom = mCtx.getDrawable(R.drawable.icon_bplaz_pending);
            holder.imageView_status.setBackground(custom);
        }else if(jobByMonthClass.getStatus().equals("2")){ //APPROVE
            holder.imageView_status.setVisibility(View.VISIBLE);
            Drawable custom = mCtx.getDrawable(R.drawable.icon_bplaz_approve);
            holder.imageView_status.setBackground(custom);
        }else {
            holder.imageView_status.setVisibility(View.GONE);
        }

        holder.textView_price.setText("RM "+jobByMonthClass.getRsp_price());
        holder.textView_product.setText(jobByMonthClass.getProduct_name());
    }

    @Override
    public int getItemCount() {
        return jobByMonthList.size();
    }

    class ProductViewHolder extends RecyclerView.ViewHolder{

        TextView textView_product,textView_price;
        ImageView imageView_product_image,imageView_status;

        public ProductViewHolder(View itemView) {
            super(itemView);

            textView_product = itemView.findViewById(R.id.textView_product);
            textView_price = itemView.findViewById(R.id.textView_price);
            imageView_product_image = itemView.findViewById(R.id.imageView_product_image);
            imageView_status = itemView.findViewById(R.id.imageView_status);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mListener.onClick(jobByMonthList.get(getAdapterPosition()));
                }
            });

        }
    }
    public interface onClickJobByMonth {
        void onClick(ProductClass jobByMonthClass);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                List<ProductClass> filteredList = new ArrayList<>();
                if (charString.isEmpty()) {
                    filteredList.addAll(mData);

                    mDataListFiltered = filteredList;
                } else {
                    for (ProductClass row : mData) {
                        if (row.getProduct_name().toLowerCase().contains(charString.toLowerCase())) {
                            filteredList.add(row);
                        }
                    }

                    mDataListFiltered = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = mDataListFiltered;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                jobByMonthList = (ArrayList<ProductClass>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }
}