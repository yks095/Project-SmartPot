package com.example.smartpot.requests;

import android.content.res.Resources;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.example.smartpot.enums.ServerURL;

import java.util.HashMap;
import java.util.Map;

public class WaterRequest extends StringRequest {

    final static private String URL = ServerURL.URL.getUrl() + "/WaterRequest.php";
    private Map<String, String> parameters;

    public WaterRequest(String auto, String manual, String potCode, Response.Listener<String> listener)   {
        super(Method.POST, URL, listener, null);
        parameters = new HashMap<>();
        parameters.put("auto", auto);
        parameters.put("manual", manual);
        parameters.put("potCode", potCode);
    }

    @Override
    public Map<String, String> getParams()  {
        return parameters;
    }
}