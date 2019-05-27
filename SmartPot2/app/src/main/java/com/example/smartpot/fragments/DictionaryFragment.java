package com.example.smartpot.fragments;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListView;

import com.example.smartpot.Dictionary;
import com.example.smartpot.DictionaryAdapter;
import com.example.smartpot.R;
import com.example.smartpot.enums.ServerURL;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class DictionaryFragment extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;
    private OnFragmentInteractionListener mListener;

    public DictionaryFragment() {
    }

    public static DictionaryFragment newInstance(String param1, String param2) {
        DictionaryFragment fragment = new DictionaryFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    phpdo task;

    private ListView dicList;
    private DictionaryAdapter dictionaryAdapter;
    private List<Dictionary> dictionaries;
    private List<Dictionary> searchDictionaries;
    private EditText searchText;
    static String searchName;
    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dictionary, container, false);

        task = new phpdo();
        task.execute();

        dicList = (ListView) view.findViewById(R.id.dicList);
        searchText = (EditText) view.findViewById(R.id.searchText);

        dictionaries = new ArrayList<Dictionary>();
        dictionaryAdapter = new DictionaryAdapter(getContext(), dictionaries);
        dicList.setAdapter(dictionaryAdapter);

        dicList.setTextFilterEnabled(true);
        searchText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                searchName = searchText.getText().toString();
                searchDictionaries = new ArrayList<Dictionary>();

                for(Dictionary dic : dictionaries)  {
                    if(dic.getName().contains(searchName))
                        searchDictionaries.add(dic);
                }
                dictionaryAdapter = new DictionaryAdapter(getContext(), searchDictionaries);
                dicList.setAdapter(dictionaryAdapter);

            }

            @Override
            public void afterTextChanged(Editable s) {

                if(searchText.getText().length() == 0)
                    dicList.clearTextFilter();

            }
        });
        return view;
    }

    private class phpdo extends AsyncTask<String, Void, String> {

        protected void onPreExecute() {
        }
        @Override
        protected String doInBackground(String... arg) {
            try{
                String link = ServerURL.URL.getUrl() + "/GetFlower.php";
                URL url = new URL(link);
                HttpClient client = new DefaultHttpClient();
                HttpGet request = new HttpGet();
                request.setURI(new URI(link));
                HttpResponse response = client.execute(request);

                StringBuffer sb = new StringBuffer("");
                String line = "";
                BufferedReader in = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));

                while((line = in.readLine()) != null) {
                    sb.append(line);
                    break;
                }

                in.close();
                return sb.toString();
            } catch (Exception e) {
                return new String("Exception" + e.getMessage());
            }
        }

        @Override
        protected void onPostExecute(String result) {

            try {
                JSONObject jsonObject = new JSONObject(result);
                JSONArray jsonArray = jsonObject.getJSONArray("response");
                int count = 0;
                String name, image, content;
                while (count < jsonArray.length()) {
                    JSONObject object = jsonArray.getJSONObject(count);
                    name = object.getString("name");
                    image = object.getString("image");
                    content = object.getString("content");
                    Dictionary dictionary = new Dictionary(name, image, content);
                    dictionaries.add(dictionary);

                    dictionaryAdapter.notifyDataSetChanged();
                    count++;
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

}