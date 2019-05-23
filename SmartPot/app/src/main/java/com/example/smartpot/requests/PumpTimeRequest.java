package com.example.smartpot.requests;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.example.smartpot.enums.ServerURL;

import java.util.HashMap;
import java.util.Map;

public class PumpTimeRequest extends StringRequest {

    final static private String URL = ServerURL.URL.getUrl() + "/PumpTimeUpdate.php";
    private Map<String, String> parameters;

    public PumpTimeRequest(String potCode, String manualPumpTime){
        super(Request.Method.POST, URL, null, null);
        parameters = new HashMap<>();
        parameters.put("potCode", potCode);
        parameters.put("manualPumpTime", manualPumpTime);

    }

    @Override
    public Map<String, String> getParams(){
        return parameters;
    }
}