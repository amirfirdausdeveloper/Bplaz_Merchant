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

import com.bateriku.bplazmerchant.Class.RiderAssignClass;
import com.bateriku.bplazmerchant.R;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

public class RiderAssignAdapter extends RecyclerView.Adapter<RiderAssignAdapter.ProductViewHolder> implements Filterable {

    private List<RiderAssignClass> mData;
    private Context mCtx;
    public static List<RiderAssignClass> jobByMonthList;
    private onClickJobByMonth mListener;
    Activity activity;
    private List<RiderAssignClass> mDataListFiltered;

    public RiderAssignAdapter(Context mCtx, List<RiderAssignClass> jobByMonthList, onClickJobByMonth mListener, Activity activity) {
        this.mCtx = mCtx;
        this.mData = jobByMonthList;
        this.jobByMonthList = jobByMonthList;
        this.mListener = mListener;
        this.activity = activity;
    }

    @Override
    public ProductViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mCtx);
        View view = inflater.inflate(R.layout.adapter_assign_rider, null,false);
        RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        view.setLayoutParams(lp);
        return new ProductViewHolder(view);


    }

    @Override
    public void onBindViewHolder(final ProductViewHolder holder, final int position) {
        final RiderAssignClass jobByMonthClass = jobByMonthList.get(position);


        holder.textView_name.setText(jobByMonthClass.getName());
        holder.textView_phone.setText(jobByMonthClass.getTelephone_number());

        NumberFormat nf = new DecimalFormat("##.##");
        holder.textView_distance.setText(nf.format(Double.parseDouble(jobByMonthClass.getDistance()))+" KM");

    }

    @Override
    public int getItemCount() {
        return jobByMonthList.size();
    }

    class ProductViewHolder extends RecyclerView.ViewHolder{

        TextView textView_name,textView_phone,textView_distance;

        public ProductViewHolder(View itemView) {
            super(itemView);

            textView_name = itemView.findViewById(R.id.textView_name);
            textView_phone = itemView.findViewById(R.id.textView_phone);
            textView_distance = itemView.findViewById(R.id.textView_distance);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mListener.onClick(jobByMonthList.get(getAdapterPosition()));
                }
            });

        }
    }
    public interface onClickJobByMonth {
        void onClick(RiderAssignClass jobByMonthClass);
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
                List<RiderAssignClass> filteredList = new ArrayList<>();
                if (charString.isEmpty()) {
                    filteredList.addAll(mData);

                    mDataListFiltered = filteredList;
                } else {
                    for (RiderAssignClass row : mData) {
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
                jobByMonthList = (ArrayList<RiderAssignClass>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }
}