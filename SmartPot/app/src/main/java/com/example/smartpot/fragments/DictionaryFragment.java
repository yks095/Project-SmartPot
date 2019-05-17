package com.example.smartpot.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.smartpot.R;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.Dictionary;

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

//    public void onAttach(Context context) {
//        super.onAttach(context);
//    }

    TextView[] flowerDic = new TextView[4];

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dictionary, container, false);


        flowerDic[0] = (TextView) view.findViewById(R.id.content);
        flowerDic[1] = (TextView) view.findViewById(R.id.content1);
        flowerDic[2] = (TextView) view.findViewById(R.id.content2);
        flowerDic[3] = (TextView) view.findViewById(R.id.content3);

        new DictionaryAsynTask(flowerDic).execute("https://terms.naver.com/list.nhn?cid=42526&categoryId=42527");

        return view;
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

    static class DictionaryAsynTask extends AsyncTask<String, Void, String[]>   {

        private TextView[] textView;
        private final String SELECTOR ="div[class = subject], p[class = desc __ellipsis is-truncated], div[class = related], div[class = related v2]  ";

        public DictionaryAsynTask(TextView[] textView)  {
            this.textView = textView;
        }

        @Override
        protected String[] doInBackground(String... params) {

            String URL = params[0];
            String[] result = new String[4];

            int count = 0;

            try {
                Document document = Jsoup.connect(URL).get();
                Elements elements = document.select(SELECTOR);

                for (Element element : elements) {
                    if (element.text().trim().length() == 1)    {
                        result[count] = "no";
                    }
                    else {
                        if (count == 2)result[count] = element.text() + "\n";
                        else if (count == 3) result[count] = element.text() + "\n";
                        else result[count] = element.text() + "\n";
                    }
                    System.out.println(element.toString());
                    count++;
                }
                return result;
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }
        @Override
        protected void onPostExecute(String[] s) {
            super.onPostExecute(s);

            for (int i = 0; i < 4; i++) {
                textView[i].setText(s[i]);
            }

        }
    }
}