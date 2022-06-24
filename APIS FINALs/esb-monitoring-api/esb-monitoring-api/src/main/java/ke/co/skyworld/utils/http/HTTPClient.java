package ke.co.skyworld.utils.http;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class HTTPClient {
	public String httpGet(String strUrl, String strData, int intConnectionTimeoutSeconds)
	{
		String returnString = null;
		URL url = null;
		URLConnection conn = null;
		OutputStreamWriter wr = null;
		BufferedReader rd = null;
		try {
			  
			strUrl = strUrl + "?" + strData;
			//System.out.println("Get URL: " + strUrl + "\n");
			// Send data
			url = new URL(strUrl);
			conn = url.openConnection();
			conn.setConnectTimeout(intConnectionTimeoutSeconds * 1000); //Setting HTTP Connection Timeout
			conn.setReadTimeout(intConnectionTimeoutSeconds * 1000); 	//Setting HTTP Read Timeout
			conn.setDoOutput(true);
			wr = new OutputStreamWriter(conn.getOutputStream());
			wr.write("");
			wr.flush();
			
			// Get the response
			rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			
			returnString = "";
			String line = "";
			while ((line = rd.readLine()) != null) {
//			System.out.println(line.toString());
			returnString = returnString + line;
			}
			
		} 
		catch (Exception e) {
			System.err.println ("httpGet(strUrl,strData): " + e.getMessage());
		}
		finally
		{			
			try{wr.close();}catch(Exception e){}
			try{rd.close();}catch(Exception e){}
			
			url = null;
			conn = null;
			wr = null;
			rd = null;
		}
		
		return returnString;
	}
	
	
	public String httpPost(String strUrl, String strData, int intConnectionTimeoutSeconds)
	{
		String returnString = null;
		URL url = null;
		URLConnection conn = null;
		OutputStreamWriter wr = null;
		BufferedReader rd = null;
		try {
		   
			// Send data
			url = new URL(strUrl);
			//System.out.println("Post URL: " + strUrl + "\n");
			conn = url.openConnection();
			conn.setConnectTimeout(intConnectionTimeoutSeconds * 1000); //Setting HTTP Connection Timeout
			conn.setReadTimeout(intConnectionTimeoutSeconds * 1000); 	//Setting HTTP Read Timeout
			conn.setDoOutput(true);
			wr = new OutputStreamWriter(conn.getOutputStream());
			wr.write(strData);
			wr.flush();
			
			// Get the response
			rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			returnString = "";
			String line = "";
			while ((line = rd.readLine()) != null) {
				returnString = returnString + line;
			}
		} 
		catch (Exception e) {
			System.err.println ("httpPost(strUrl,strData): " + e.getMessage());
		}
		finally
		{			
			try{wr.close();}catch(Exception e){}
			try{rd.close();}catch(Exception e){}
			
			url = null;
			conn = null;
			wr = null;
			rd = null;
		}
		
		return returnString;
				
	}

	public String formPost(String urlStr, HashMap<String, String> params, Integer connectionTimeout){

        byte[] postData = getParametersString(params).getBytes(StandardCharsets.UTF_8);
        int postDataLength = postData.length;

        String returnString = null;
        URL url = null;
        HttpURLConnection conn = null;
        DataOutputStream wr = null;
        BufferedReader rd = null;

        try{
            url = new URL(urlStr);
            conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(connectionTimeout * 1000); //Setting HTTP Connection Timeout
            conn.setReadTimeout(connectionTimeout * 1000); 	//Setting HTTP Read Timeout
            conn.setDoOutput(true);
            conn.setInstanceFollowRedirects(false);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            conn.setRequestProperty("charset", "utf-8");
            conn.setRequestProperty("Content-Length", Integer.toString(postDataLength ));
            conn.setUseCaches(false);

            wr = new DataOutputStream(conn.getOutputStream());
            wr.write(postData);
            wr.flush();

            // Get the response
            rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            returnString = "";
            String line = "";
            while ((line = rd.readLine()) != null) {
                returnString = returnString + line;
            }

        }catch (Exception e){
            e.printStackTrace();
        }finally
        {
            try{wr.close();}catch(Exception e){}
            try{rd.close();}catch(Exception e){}

            url = null;
            conn = null;
            wr = null;
            rd = null;
        }

        return returnString;
    }

    private String getParametersString(HashMap<String, String> params){
        StringBuilder result = new StringBuilder();
        boolean first = true;
        for(Map.Entry<String, String> entry : params.entrySet()){
            if (first)
                first = false;
            else
                result.append("&");
            try{
                result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
                result.append("=");
                result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
            }catch (UnsupportedEncodingException e){
                e.printStackTrace();
            }
        }
        return result.toString();
    }

}
