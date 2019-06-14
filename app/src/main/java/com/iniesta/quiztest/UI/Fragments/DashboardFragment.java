package com.iniesta.quiztest.UI.Fragments;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.iniesta.quiztest.Adapter.GroupAdapter;
import com.iniesta.quiztest.Helpers.JsonSingleton;
import com.iniesta.quiztest.Helpers.Url;
import com.iniesta.quiztest.Models.ListGroup;
import com.iniesta.quiztest.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class DashboardFragment extends Fragment {

    private final String TAG = getClass().getSimpleName();
    private RecyclerView recyclerView;
    private List<ListGroup> data_group;
    private GroupAdapter adapter;
    public final static String TAG_DASHBOARD = "Dashboard";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivity().setTitle(this.TAG_DASHBOARD);

    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_dashboard, container, false);
        recyclerView = view.findViewById(R.id.recycler);
        doInServer();
        return view;


    }

    private void doInServer() {
        final ProgressDialog progress;
        progress = new ProgressDialog(getActivity());
        progress.setTitle("Loading....");
        progress.setCancelable(false);
        progress.show();


        StringRequest stringRequest = new StringRequest(Request.Method.GET, Url.URL_DASH, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                progress.dismiss();


                try {

                    Log.d(TAG, "onResponse: JSON" + response);

                    JSONObject obj = new JSONObject(response);
                    data_group = new ArrayList();


                    JSONArray dataArray = obj.getJSONArray("groups");

                    for (int i = 0; i < dataArray.length(); i++) {
                        JSONObject dataobj = dataArray.getJSONObject(i);
                        ListGroup myData = new ListGroup(dataobj.getString("gid"), dataobj.getString("group_name"), dataobj.getString("group_icon"));
                        Log.d(TAG, "onResponse: ListGroup" + myData.toString());
                        data_group.add(myData);

                    }
                    adapter = new GroupAdapter(getContext(), data_group);
                    RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
                    recyclerView.setLayoutManager(layoutManager);
                    recyclerView.setItemAnimator(new DefaultItemAnimator());
                    recyclerView.setAdapter(adapter);


                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });


        JsonSingleton.getInstance(getContext()).addToQueue(stringRequest);
    }

}