package com.example.rex.myapplication;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import java.util.ArrayList;

/**
 * Created by Rex on 7/06/2017.
 */

public class SearchResultsFragment extends Fragment {

    ShowSelectedSearchResultListener mCallback;
    private ListView mHillList;
    private ArrayList<Hill> hills;
    private HillListAdapter adapter;


    public SearchResultsFragment(){
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState){
        Bundle args = getArguments();
        View view = inflater.inflate(R.layout.hill_list_view,container,false);
        mHillList = (ListView) view.findViewById(R.id.hillList);
        hills = args.getParcelableArrayList("Hill");
        adapter = new HillListAdapter(this.getContext(),hills);
        mHillList.setAdapter(adapter);
        mHillList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> arg0, View view, int position, long id) {
                //set review object to review at adapter position
                Hill hill = adapter.getItem(position);
                //run the review detail dialog fragment
                mCallback.ShowSelectedSearchResultListener(hill);
            }
        });

        return view;
    }

    public interface ShowSelectedSearchResultListener {
        public void ShowSelectedSearchResultListener(Hill hill);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mCallback = (ShowSelectedSearchResultListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement AddHillToDataBaseListener");
        }
    }

}
