package com.example.smartpot.requests;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.example.smartpot.enums.ServerURL;

import java.util.HashMap;
import java.util.Map;

public class PotRequest extends StringRequest {

    final static private String URL = ServerURL.URL.getUrl() + "/PotRegister.php";
    private Map<String, String> parameters;

    public PotRequest(String potCode, String potName, String userID, Response.Listener<String> listener){
        super(Method.POST, URL, listener, null);
        parameters = new HashMap<>();
        parameters.put("potCode", potCode);
        parameters.put("potName", potName);
        parameters.put("userID", userID);
    }

    @Override
    public Map<String, String> getParams(){
        return parameters;
    }
}
