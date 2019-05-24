package com.example.smartpot.requests;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.example.smartpot.enums.ServerURL;

import java.util.LinkedHashMap;
import java.util.Map;

public class FlowerRegisterRequest extends StringRequest {

    final static private String URL = ServerURL.URL.getUrl() + "/FlowerRegister.php";
    private Map<String, String> parameters;

    public FlowerRegisterRequest(String flower, String userID, Response.Listener<String> listener)   {
        super(Method.POST, URL, listener, null);
        parameters = new LinkedHashMap<>();

        parameters.put("flower", flower);
        parameters.put("userID", userID);


        System.out.println(parameters.toString());
    }

    @Override
    public Map<String, String> getParams()  {
        return parameters;
    }
}
