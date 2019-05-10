package com.example.smartpot.requests;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class WaterRequest extends StringRequest {

    final static private String URL = "http://222.97.212.74/WaterRequest.php";
    private Map<String, String> parameters;

    public WaterRequest(String auto, String potCode, Response.Listener<String> listener)   {
        super(Method.POST, URL, listener, null);
        parameters = new HashMap<>();
        parameters.put("auto", auto);
        parameters.put("potCode", potCode);
    }

    @Override
    public Map<String, String> getParams()  {
        return parameters;
    }
}