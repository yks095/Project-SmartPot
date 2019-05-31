package com.example.smartpot.requests;

import android.support.annotation.Nullable;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.example.smartpot.enums.ServerURL;

import java.util.HashMap;
import java.util.Map;

public class PotRowRequest extends StringRequest {

    final static private String URL = ServerURL.URL.getUrl() + "/PotRow.php";
    private Map<String, String> parameters;

    public PotRowRequest(String userID, String potCode, Response.Listener<String> listener){
        super(Method.GET, URL, listener, null);
        parameters = new HashMap<>();
        parameters.put("userID", userID);
        parameters.put("potCode", potCode);

    }

    @Override
    public Map<String, String> getParams(){
        return parameters;
    }
}