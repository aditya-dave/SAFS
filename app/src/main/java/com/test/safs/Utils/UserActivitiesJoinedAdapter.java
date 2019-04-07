package com.test.safs.Utils;

import android.app.Dialog;
import android.content.Context;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.test.safs.R;
import com.test.safs.models.Activity;
import com.test.safs.models.UserActivitiesJoined;

import java.util.ArrayList;

import static android.support.constraint.Constraints.TAG;

public class UserActivitiesJoinedAdapter extends RecyclerView.Adapter<UserActivitiesJoinedAdapter.MyViewHolder>{

    Context context;
    CustomItemClickListener listener;
    ArrayList<UserActivitiesJoined> mDataset;
    Dialog myDialog;
    FirebaseMethods mFireBasemethods;

    // Provide a suitable constructor (depends on the kind of dataset)
    public UserActivitiesJoinedAdapter(Context mContext, ArrayList<UserActivitiesJoined> data, CustomItemClickListener listener) {
        this.context = mContext;
        this.mDataset = data;
        this.listener = listener;
        setHasStableIds(true);
    }



    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class MyViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public CoordinatorLayout item_mainfeed;

        public de.hdodenhof.circleimageview.CircleImageView profilephoto;
        public ImageView mProfilephoto;
        public TextView host_name;
        public TextView sport_name;
        public TextView time;
        public TextView date;
        public TextView location;
        public TextView key;


        public MyViewHolder(final View itemView) {
            super(itemView);
            item_mainfeed = (CoordinatorLayout) itemView.findViewById(R.id.item_mainfeed);
            this.profilephoto = (de.hdodenhof.circleimageview.CircleImageView) itemView.findViewById(R.id.profilephoto);
            this.host_name = (TextView) itemView.findViewById(R.id.host_name);
            this.sport_name = (TextView) itemView.findViewById(R.id.sport_name);
            this.time = (TextView) itemView.findViewById(R.id.time);
            this.date = (TextView) itemView.findViewById(R.id.date);
            this.location = (TextView) itemView.findViewById(R.id.location);
            this.key = (TextView) itemView.findViewById(R.id.key);
            this.mProfilephoto = (ImageView) itemView.findViewById(R.id.profilephoto);

        }
    }



    // Create new views (invoked by the layout manager)
    @Override
    public UserActivitiesJoinedAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent,
                                                                       int viewType) {
        // create a new view
        View view =  LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_mainfeed_listitem, parent, false);
        final MyViewHolder vh = new MyViewHolder(view);


        //Dialong initialization
        /*myDialog = new Dialog(context);
        myDialog.setContentView(R.layout.dialog_join_activity);
        myDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));*/
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onItemClick(view,vh.getAdapterPosition());
            }
        });

        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element

        UserActivitiesJoined userActivitiesJoined = mDataset.get(position);
        ImageView mProfilephoto = (ImageView) holder.mProfilephoto;
        String imgUrl = mDataset.get(position).getProfilephoto();
        Log.d(TAG, "onBindViewHolder: "+imgUrl);
        UniversalImageLoader.setImage(imgUrl,holder.mProfilephoto,null,"");
        holder.host_name.setText(mDataset.get(position).getName());
        holder.sport_name.setText(mDataset.get(position).getSport_name());
        holder.time.setText(mDataset.get(position).getTime());
        holder.date.setText(mDataset.get(position).getDate());
        holder.location.setText(mDataset.get(position).getLocation());
        holder.key.setText(mDataset.get(position).getKey());
        holder.key.setVisibility(View.GONE);

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        Log.d(TAG, "getItemCount: "+ mDataset.size());
        return mDataset.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

}