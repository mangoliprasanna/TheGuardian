package com.example.mango.theguardian.fragments;



import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.mango.theguardian.JSONLoader;
import com.example.mango.theguardian.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class SectionFragment extends Fragment implements LoaderManager.LoaderCallbacks<String> {

    private final int LOADER_ID = 1;
    private  ListView sectionListView;

    // Simple Section Object Class.
    class Section {
        public String sectionId;
        public String sectionName;
        public Section(String sectionId, String sectionName){
            this.sectionId = sectionId;
            this.sectionName = sectionName;
        }
    }

    class SectionAdapter extends ArrayAdapter<Section>{

        public SectionAdapter(Context context, List<Section> objects) {
            super(context, 0, objects);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            // If view is empty, Infalting View.
            if(convertView == null){
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.layout_section_list, parent, false);
            }

            // Get section data from section object list.
            Section curruntSection = getItem(position);

            // Assign data for TextView for given section.
            ((TextView) convertView.findViewById(R.id.sectionName)).setText(curruntSection.sectionName);
            ((TextView) convertView.findViewById(R.id.sectionId)).setText(curruntSection.sectionId);
            ((TextView) convertView.findViewById(R.id.sectionLetter)).setText(String.valueOf(curruntSection.sectionName.charAt(0)));

            View containerView = (View) convertView.findViewById(R.id.container);

            containerView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                }
            });

            return convertView;
        }
    }

    public SectionFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_section, container, false);

        // Attaching listview to listview object.
        sectionListView = (ListView) view.findViewById(R.id.sectionList) ;

        // Loading all sections from gurdian servers.
        Bundle b = new Bundle();
        b.putString("URL", "http://content.guardianapis.com/sections?api-key=" + getString(R.string.API_KEY));

        // initialize loader.
        getLoaderManager().initLoader(LOADER_ID, b, this);

        return view;
    }

    @Override
    public Loader<String> onCreateLoader(int i, Bundle bundle) {
        return new JSONLoader(getContext(), bundle.getString("URL"));
    }

    @Override
    public void onLoadFinished(Loader<String> loader, String s) {
        try {
            // Getting result array from json by converting string to JSONobject,
            JSONArray resultArray =  new JSONObject(s).getJSONObject("response").getJSONArray("results");

            // Creating empty list for the collection of section.
            List<Section> sectionList = new ArrayList<>();

            // Looping through each jsonObject in resultArray and extract Section information.
            for(int i = 0; i < resultArray.length(); i++)
                sectionList.add(new Section(resultArray.getJSONObject(i).getString("id"), resultArray.getJSONObject(i).getString("webTitle")));

            // Custom Adater for sectionListView.
            sectionListView.setAdapter(new SectionAdapter(getContext(), sectionList));

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onLoaderReset(Loader<String> loader) {

    }
}
