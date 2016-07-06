package org.cook_team.wn2nac;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

/**
 * Created by Alexander on 7/5/2016.
 */
public class FragmentLanguageSelect extends Fragment {
    private ListView listv;
    private Context fcontext;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View v = inflater.inflate(R.layout.fragment_language_select, container, false);
        listv = (ListView)v.findViewById(R.id.lang_list);
        listv.setVisibility(View.VISIBLE);
        String[] values = {getResources().getString(R.string.lang_en_us),
                getResources().getString(R.string.lang_zh_tw)};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_list_item_1, android.R.id.text1, values);
        listv.setAdapter(adapter);
        return v;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        fcontext = context;
    }

    @Override
    public void onResume() {
        super.onResume();
        final WnApp a = ((WnApp)getActivity().getApplication());
        if (a.getShow_screen()) {
            ((ActivityMain)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            listv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    int itemPosition = position;
                    String itemValue = (String) listv.getItemAtPosition(position);
                    if (itemValue.equals(fcontext.getString(R.string.lang_en_us))) {
                        a.setUse_en();
                        a.initializeStrings();
                        exitLangSelect();
                    } else if (itemValue.equals(fcontext.getString(R.string.lang_zh_tw))) {
                        a.setUse_zh();
                        a.initializeStrings();
                        exitLangSelect();
                    }
                }
            });
        } else {
            exitLangSelect();
        }
    }

    private void exitLangSelect() {
        ((ActivityMain)getActivity()).exitFrag(this, WnApp.getInstance().getShow_screen());
    }
}
