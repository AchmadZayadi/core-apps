package com.sesolutions.http;

import org.apache.http.client.methods.HttpPost;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class HttpRequestVO {
    public String url;
    public String requestMethod = HttpPost.METHOD_NAME;
    public Map<String, Object> params;
    //public Map<String, Object> jsonParams;
//	public Map<String, String> urlParams;
    public Map<String, String> headres;
    public ArrayList<String> files;
    // public String sslCertificate;

    public HttpRequestVO(String url) {
        params = new HashMap<String, Object>();
        //urlParams = new HashMap<String, String>();
        //jsonParams = new HashMap<String, Object>();
        headres = new HashMap<String, String>();
        this.url = url;
    }

    public boolean addFile(String file) {
        if (files == null) {
            files = new ArrayList<String>();
        }
        return files.add(file);
    }

    public ArrayList<String> getFiles() {
        return files;
    }
}
