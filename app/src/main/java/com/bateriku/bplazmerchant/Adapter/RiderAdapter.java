package com.bateriku.bplazmerchant.Adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bateriku.bplazmerchant.Class.RiderClass;
import com.bateriku.bplazmerchant.R;

import java.util.ArrayList;
import java.util.List;

public class RiderAdapter extends RecyclerView.Adapter<RiderAdapter.ProductViewHolder> implements Filterable {

    private List<RiderClass> mData;
    private Context mCtx;
    public static List<RiderClass> jobByMonthList;
    private onClickJobByMonth mListener;
    Activity activity;
    private List<RiderClass> mDataListFiltered;

    public RiderAdapter(Context mCtx, List<RiderClass> jobByMonthList, onClickJobByMonth mListener, Activity activity) {
        this.mCtx = mCtx;
        this.mData = jobByMonthList;
        this.jobByMonthList = jobByMonthList;
        this.mListener = mListener;
        this.activity = activity;
    }

    @Override
    public ProductViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mCtx);
        View view = inflater.inflate(R.layout.adapter_list_staff, null,false);
        RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        view.setLayoutParams(lp);
        return new ProductViewHolder(view);


    }

    @Override
    public void onBindViewHolder(final ProductViewHolder holder, final int position) {
        final RiderClass jobByMonthClass = jobByMonthList.get(position);


        holder.textView_name.setText(jobByMonthClass.getName());
        holder.textView_phone.setText(jobByMonthClass.getTelephone_number());
        holder.textView_status.setText(jobByMonthClass.getStaff_status());

    }

    @Override
    public int getItemCount() {
        return jobByMonthList.size();
    }

    class ProductViewHolder extends RecyclerView.ViewHolder{

        TextView textView_name,textView_phone,textView_status;

        public ProductViewHolder(View itemView) {
            super(itemView);

            textView_name = itemView.findViewById(R.id.textView_name);
            textView_phone = itemView.findViewById(R.id.textView_phone);
            textView_status = itemView.findViewById(R.id.textView_status);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mListener.onClick(jobByMonthList.get(getAdapterPosition()));
                }
            });

        }
    }
    public interface onClickJobByMonth {
        void onClick(RiderClass jobByMonthClass);
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
                List<RiderClass> filteredList = new ArrayList<>();
                if (charString.isEmpty()) {
                    filteredList.addAll(mData);

                    mDataListFiltered = filteredList;
                } else {
                    for (RiderClass row : mData) {
                        if (row.getName().toLowerCase().contains(charString.toLowerCase())) {
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
                jobByMonthList = (ArrayList<RiderClass>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }
}