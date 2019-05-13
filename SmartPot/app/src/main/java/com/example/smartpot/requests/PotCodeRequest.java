package com.example.smartpot.requests;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class PotCodeRequest extends StringRequest {

    final static private String URL = "http://117.16.94.138/PotCodeValidate.php";
    private Map<String, String> parameters;

    public PotCodeRequest(String potCode, Response.Listener<String> listener){
        super(Method.POST, URL, listener, null);
        parameters = new HashMap<>();
        parameters.put("potCode", potCode);
    }

    @Override
    public Map<String, String> getParams(){
        return parameters;
    }
}
