package com.example.smartpot.requests;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.example.smartpot.enums.ServerURL;

import java.util.HashMap;
import java.util.Map;

public class MemberRequest extends StringRequest {

    final static private String URL = ServerURL.URL.getUrl() + "/UpdateMember.php";
    private Map<String, String> parameters;

    public MemberRequest(String userID, String userEmail, String potName, Response.Listener<String> listener){
        super(Request.Method.POST, URL, listener, null);
        parameters = new HashMap<>();
        parameters.put("userID", userID);
        parameters.put("userEmail", userEmail);
        parameters.put("potName", potName);
    }

    @Override
    public Map<String, String> getParams(){
        return parameters;
    }
}