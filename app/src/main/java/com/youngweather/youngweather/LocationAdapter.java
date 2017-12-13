package com.youngweather.youngweather;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.TextView;

import java.util.List;

import static com.youngweather.youngweather.R.id.location_name;

/**
 * Created by 17670 on 2017/12/11.
 * 地址适配器
 */

public class LocationAdapter extends ArrayAdapter<String> {

    private int resourceId;
//    private CardView cardView;
//    private TextView locationName;

    public LocationAdapter(@NonNull Context context, int textViewResourceId, @NonNull List<String> objects) {
        super(context, textViewResourceId, objects);
        resourceId = textViewResourceId;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        String name = getItem(position);
        View view ;
        ViewHolder viewHolder;
        if (convertView == null){
            view  = LayoutInflater.from(parent.getContext()).inflate(resourceId,parent,false);
            viewHolder = new ViewHolder();
            viewHolder.cardView = (CardView) view;
            viewHolder.locationName = view.findViewById(R.id.location_name);
            view.setTag(viewHolder);
        }else {
            view = convertView;
            viewHolder = (ViewHolder) view.getTag();
        }
        viewHolder.locationName.setText(name);
        return view;
    }

    class ViewHolder{
        CardView cardView;
        TextView locationName;
    }

    //    private Context mContext;
//
//    private List<String> mList;
//
//    static class ViewHolder extends RecyclerView.ViewHolder{
//
//        CardView cardView;
//        TextView locationName;
//        public ViewHolder(View itemView) {
//            super(itemView);
//            cardView = (CardView) itemView;
//            locationName = itemView.findViewById(R.id.location_name);
//        }
//    }
//
//    public LocationAdapter(List<String> list){
//        mList = list;
//    }
//
//    @Override
//    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//        if (mContext==null){
//            mContext = parent.getContext();
//        }
//        View view = LayoutInflater.from(mContext).inflate(R.layout.location_item,parent,false);
//        return new ViewHolder(view);
//    }
//
//    @Override
//    public void onBindViewHolder(ViewHolder holder, int position) {
//        String locationName = mList.get(position);
//        holder.locationName.setText(locationName);
//    }
//
//    @Override
//    public int getItemCount() {
//        return mList.size();
//    }
}
