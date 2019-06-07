package com.kavita.quiztest.Adapter;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.DrawableContainer;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.load.engine.Resource;
import com.kavita.quiztest.Models.ListItem;
import com.kavita.quiztest.R;
import com.kavita.quiztest.UI.Fragments.DashboardFragment;
import com.kavita.quiztest.UI.Fragments.QuizFragment;

import java.util.List;

public class ListAdapter extends RecyclerView.Adapter<ListAdapter.ViewHolder> {
    private  List<ListItem> listItems;
    private Context context;

    public ListAdapter(Context context, List<ListItem> listItems) {
        this.listItems =  listItems;
        this.context = context;
    }

    @NonNull
    @Override
    public ListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_list,parent,false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ListAdapter.ViewHolder holder, int position) {
        ListItem listItem = listItems.get(position);
        holder.tv.setText(listItem.getTest_name());
        if(position%2==0)
        {
            holder.linearLayout.setBackgroundResource(R.drawable.text_gradient);
        }
        else
        {
            holder.linearLayout.setBackgroundResource(R.drawable.text_gradient1);
        }
    }

    @Override
    public int getItemCount() {
        return listItems.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tv;
        private LinearLayout linearLayout;

        private ViewHolder(@NonNull final View view) {
            super(view);
            tv = view.findViewById(R.id.text_list);
            linearLayout=view.findViewById(R.id.Linear);
            tv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Bundle bundle = new Bundle();
                    bundle.putString("gid",listItems.get(getAdapterPosition()).getGid());
                    bundle.putString("test_id",listItems.get(getAdapterPosition()).getTest_id());
                    bundle.putString("test_marks",listItems.get(getAdapterPosition()).getTest_marks());
                    FragmentManager fm = ((FragmentActivity)context).getSupportFragmentManager();
                    FragmentTransaction ft = fm.beginTransaction();
                    Fragment lif = new QuizFragment();
                    lif.setArguments(bundle);
                    ft.replace(R.id.fragment_container, lif)
                            .add(new DashboardFragment(),"dash")
                            .addToBackStack("dash")
                            .commit();

                }
            });
        }
    }
}
