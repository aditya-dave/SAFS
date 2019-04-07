package com.test.safs.Utils;

import android.content.Context;
import android.provider.Contacts;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.test.safs.R;
import com.test.safs.models.Activity;
import com.test.safs.models.User;
import com.test.safs.models.UserAccountSettings;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainFeedListAdapter extends ArrayAdapter<Activity> {

    private static final String TAG = "MainFeedListAdapter";

    private LayoutInflater mInflater;
    private int mLayoutResource;
    private Context mContext;
    private DatabaseReference mReference;
    private String currenthost_name="";

    public MainFeedListAdapter(@NonNull Context context, int resource, @NonNull List<Activity> objects) {
        super(context, resource, objects);
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mLayoutResource = resource;
        this.mContext = context;
    }

    static class ViewHolder{
        CircleImageView mProfileImage;
        TextView host_name,sport_name,date,time,location;

        UserAccountSettings settings = new UserAccountSettings();
        User user = new User();
        Activity activity = new Activity();
        StringBuilder users;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        final ViewHolder holder;

        if (convertView==null){
            convertView= mInflater.inflate(mLayoutResource,parent,false);
            holder = new ViewHolder();

            holder.host_name = (TextView) convertView.findViewById(R.id.name);
            holder.sport_name = (TextView) convertView.findViewById(R.id.sport_name);
            holder.date = (TextView) convertView.findViewById(R.id.date);
            holder.time = (TextView) convertView.findViewById(R.id.time);
            holder.location = (TextView) convertView.findViewById(R.id.location);
            holder.mProfileImage = (CircleImageView) convertView.findViewById(R.id.profilephoto);
            holder.users = new StringBuilder();

            convertView.setTag(holder);
        }
        else{
            holder = (ViewHolder) convertView.getTag();
        }

        //Get the current users name
        getCurrentDisplayname();


        //set the profile Image
        final ImageLoader imageLoader = ImageLoader.getInstance();

        return convertView;
    }

    private void getCurrentDisplayname(){
        Log.d(TAG, "getCurrentUsername: retrieving user account settings");
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        Query query = reference
                .child(mContext.getString(R.string.dbname_user_account_settings))
                .orderByChild(mContext.getString(R.string.field_userID))
                .equalTo(FirebaseAuth.getInstance().getCurrentUser().getUid());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot singleSnapshot : dataSnapshot.getChildren()){
                    currenthost_name = singleSnapshot.getValue(UserAccountSettings.class).getname();
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
