package com.example.smartpot.requests;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class PumpTimeRequest extends StringRequest {

    final static private String URL = "http://117.16.94.138/android/PumpTimeUpdate.php";
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

