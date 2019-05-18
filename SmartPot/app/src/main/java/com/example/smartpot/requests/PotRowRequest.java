package com.example.smartpot.requests;

import android.support.annotation.Nullable;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class PotRowRequest extends StringRequest {

    final static private String URL = "http://117.16.94.138/PotRow.php";
    private Map<String, String> parameters;

    public PotRowRequest(String userID, String potCode, Response.Listener<String> listener){
        super(Method.GET, URL, listener, null);
        parameters = new HashMap<>();
        parameters.put("userID", userID);
        parameters.put("potCode", potCode);
        System.out.println("parameters : " + parameters.toString());

    }

    @Override
    public Map<String, String> getParams(){
        return parameters;
    }
}