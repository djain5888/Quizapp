package com.kavita.quiztest.Adapter;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.kavita.quiztest.Models.ListGroup;
import com.kavita.quiztest.R;
import com.kavita.quiztest.UI.Fragments.DashboardFragment;
import com.kavita.quiztest.UI.Fragments.ListFragment;

import java.util.List;

import static com.android.volley.VolleyLog.TAG;

public class GroupAdapter extends RecyclerView.Adapter<GroupAdapter.ViewHolder> {


    private List<ListGroup> groupItems;
    private Context context;

    public GroupAdapter(Context context, List<ListGroup> groupItems) {
        this.groupItems = groupItems;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_group, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        ListGroup groupItem = groupItems.get(position);
        holder.name.setText(groupItem.getGroup_name());
        if (position == 0) {
            holder.cardView.setCardBackgroundColor(Color.BLUE);
        }
        if (position == 1) {
            holder.cardView.setCardBackgroundColor(Color.RED);
        }if (position == 2) {
            holder.cardView.setCardBackgroundColor(Color.GREEN);
        }

        RequestOptions options = new RequestOptions()
                .centerCrop()
                .placeholder(R.mipmap.ic_launcher_round)
                .error(R.mipmap.ic_launcher_round);
        Glide.with(context).load(groupItem.getGroup_icon()).apply(options).into(holder.iv);
    }

    @Override
    public int getItemCount() {
        return groupItems.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView name;
        public ImageView iv;
        private CardView cardView;
        public View view;

        public ViewHolder(final View view) {
            super(view);
            this.view = view;
            name = view.findViewById(R.id.group);
            iv = view.findViewById(R.id.imageGroup);
            cardView = view.findViewById(R.id.group_card);
            cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.i(TAG, "onClick: Clicked" + getAdapterPosition());

                    Bundle bundle = new Bundle();
                    bundle.putString("gid", groupItems.get(getAdapterPosition()).getId());
                    FragmentManager fm = ((FragmentActivity) context).getSupportFragmentManager();
                    FragmentTransaction ft = fm.beginTransaction();
                    Fragment lif = new ListFragment();
                    lif.setArguments(bundle);
                    ft.replace(R.id.fragment_container, lif)
                            .add(new DashboardFragment(), "dash")
                            .addToBackStack("dash")
                            .commit();
                }
            });

        }
    }
}
