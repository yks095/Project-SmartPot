package com.example.smartpot.fragments;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;

import com.example.smartpot.Dictionary;
import com.example.smartpot.DictionaryAdapter;
import com.example.smartpot.R;
import com.example.smartpot.activity.InfoActivity;
import com.example.smartpot.activity.LoginActivity;
import com.example.smartpot.activity.MainActivity;
import com.example.smartpot.activity.UpdateMemberActivity;
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
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class DictionaryFragment extends Fragment{
    //    ManageFragment manageFragment;
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;
    private OnFragmentInteractionListener mListener;

    phpdo task;
    private ListView dicList;
    private DictionaryAdapter dictionaryAdapter;
    private List<Dictionary> dictionaries;
    private List<Dictionary> searchDictionaries;
    private EditText searchText;
    static String searchName;
    private ImageButton cancelButton;

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

    @Override   // 프래그먼트 생성시 호출, 일시정지되거나 중단되었다가 재개되었을 때 유지하고자 하는 것을 초기화
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }


    @Override   // 프래그먼트가 자신의 UI를 처음으로 그릴 시간이 되면 호출
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dictionary, container, false);

        dicList = (ListView) view.findViewById(R.id.dicList);
        searchText = (EditText) view.findViewById(R.id.searchText);
        cancelButton = (ImageButton) view.findViewById(R.id.cancelButton);

        return view;
    }


    @Override   // 프래그먼트가 화면에 완전히 그렸으며, 사용자와 상호작용 가능
    public void onResume() {

        task = new phpdo();
        task.execute();

        searchText.setText(null);

        dicList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent dicIntent = new Intent(getContext(), InfoActivity.class);
                dicIntent.putExtra("name", dictionaries.get(position).getName());
                dicIntent.putExtra("image", dictionaries.get(position).getImage());
//                dicIntent.putExtra("content", dictionaries.get(position).getContent());

                getContext().startActivity(dicIntent);

            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchText.setText(null);
                dicList.clearTextFilter();
            }
        });
        dictionaries = new ArrayList<Dictionary>();
        dictionaryAdapter = new DictionaryAdapter(getContext(), dictionaries);
        dicList.setAdapter(dictionaryAdapter);
        dicList.setTextFilterEnabled(true);

        searchText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            /* searchText의 값이 변할 때 마다 searchDictionary를 변경 시켜 adapter에 적용
               searchText의 문자가 포함된 식물을 searchDictionary에 add시킴
            */
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

                dicList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Intent dicIntent = new Intent(getContext(), InfoActivity.class);
                        dicIntent.putExtra("name", searchDictionaries.get(position).getName());
                        dicIntent.putExtra("image", searchDictionaries.get(position).getImage());

                        getContext().startActivity(dicIntent);

                    }
                });
            }

            @Override
            public void afterTextChanged(Editable s) {
                if(searchText.getText().length() == 0)
                    dicList.clearTextFilter();
            }
        });

        super.onResume();
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
                String name, image;
                while (count < jsonArray.length()) {
                    JSONObject object = jsonArray.getJSONObject(count);

                    name = object.getString("name");
                    image = object.getString("image");


                    Dictionary dictionary = new Dictionary(name, image);
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

    @Override   // 프래그먼트 상태를 완전히 종료 할 수 있도록 호출
    public void onDestroy() {
        super.onDestroy();
    }

    @Override   // 프래그먼트가 액티비티와 연결이 완전히 끊기기 직전에 호출
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

}