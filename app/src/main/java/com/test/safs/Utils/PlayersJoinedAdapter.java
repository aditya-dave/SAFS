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
import com.test.safs.models.UserAccountSettings;

import java.util.ArrayList;

import static android.support.constraint.Constraints.TAG;

public class PlayersJoinedAdapter extends RecyclerView.Adapter<PlayersJoinedAdapter.MyViewHolder>{

    Context context;
    CustomItemClickListener listener;
    ArrayList<UserAccountSettings> mDataset;
    Dialog myDialog;
    FirebaseMethods mFireBasemethods;

    // Provide a suitable constructor (depends on the kind of dataset)
    public PlayersJoinedAdapter(Context mContext, ArrayList<UserAccountSettings> data, CustomItemClickListener listener) {
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
        public CoordinatorLayout item_playersjoined;

        public de.hdodenhof.circleimageview.CircleImageView profilephoto;
        public TextView name;

        public MyViewHolder(final View itemView) {
            super(itemView);
            item_playersjoined = (CoordinatorLayout) itemView.findViewById(R.id.item_playersjoined);
            this.profilephoto = (de.hdodenhof.circleimageview.CircleImageView) itemView.findViewById(R.id.profilephoto);
            this.name = (TextView) itemView.findViewById(R.id.name);
        }
    }



    // Create new views (invoked by the layout manager)
    @Override
    public PlayersJoinedAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent,
                                                                int viewType) {
        // create a new view
        View view =  LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_playersjoined_listitem, parent, false);
        final MyViewHolder vh = new MyViewHolder(view);

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

        UserAccountSettings UserAccountSettings = mDataset.get(position);
        String imgUrl = mDataset.get(position).getprofilephoto();
        Log.d(TAG, "onBindViewHolder: "+imgUrl);
        UniversalImageLoader.setImage(imgUrl,holder.profilephoto,null,"");
        holder.name.setText(mDataset.get(position).getname());
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