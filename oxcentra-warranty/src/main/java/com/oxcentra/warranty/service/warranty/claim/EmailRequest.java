package com.oxcentra.warranty.service.warranty.claim;

import com.oxcentra.warranty.bean.warranty.claim.EmailRequestBean;
import com.oxcentra.warranty.util.varlist.CommonVarList;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

@Service
public class EmailRequest {

    @Autowired
    CommonVarList commonVarList;

        public String sentEmail(EmailRequestBean emailRequestBean) throws IOException, Exception {
            String response = "";
            String message = "";
            URL url = null;
            HttpURLConnection urlConnection = null;
            String urnDetail = "";
            String urlDetail = "";
            BufferedWriter bWriter = null;
            BufferedReader bReader = null;
            String request = "";
            try {

//                System.out.println("Url "+commonVarList.EMAIL_BASE_URL);

                urlDetail ="http://localhost:8082/v1.0/service/supplier/email/"+emailRequestBean.getToken()+"";

//                urlDetail = commonVarList.EMAIL_BASE_URL +emailRequestBean.getToken()+"";

                if(urlDetail.contains(" "))
                    urlDetail = urlDetail.replace(" ", "%20");

                System.out.println("url : "+ urlDetail);

                url = new URL(urlDetail);

                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("POST");
                urlConnection.setDoOutput(true);

                urlConnection.setRequestProperty("Content-Type", "application/json");
                urlConnection.setRequestProperty("Accept", "application/json");


                request = "{\n" +
                        "  \"claimOnSupplier\": "+emailRequestBean.getClaimOnSupplier().trim()+",\n" +
                        "  \"model\": \""+emailRequestBean.getModel().trim()+"\",\n" +
                        "  \"failureArea\": \""+emailRequestBean.getFailureArea().trim()+"\",\n" +
                        "  \"repairType\": \""+emailRequestBean.getRepairType().trim()+"\",\n" +
                        "  \"repairDescription\": \""+emailRequestBean.getRepairDescription().trim()+"\",\n" +
                        "  \"costDescription\": \""+emailRequestBean.getCostDescription().trim()+"\"\n" +
                        "}";

                System.out.println("request              : " + request);

                bWriter = new BufferedWriter(new OutputStreamWriter(urlConnection.getOutputStream()));
                bWriter.write(request);
                bWriter.flush();


                // Get the response
                bReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                String line;
                while (null != ((line = bReader.readLine()))) {
                    response += line;
                }

                System.out.println("reponse              : " + response);

                System.out.println(response.toString());

                message = getResponseToToken(response);


            } catch (IOException e) {
                message="Fail to create connection";
                System.err.println("Fail to create connection"+e.getMessage());
            } catch (Exception ex) {
                throw ex;
            }
            return message;
        }

    private String getResponseToToken(String switchResponse) throws Exception {
        String accInfoResponseError = "";
        try {

            JSONParser parser = new JSONParser();
            JSONObject obj = (JSONObject) parser.parse(switchResponse);

            String  tokenServiceResponse= ((String) obj.get("status"));

            if (tokenServiceResponse!=null && (tokenServiceResponse.equals("SUCCESS") )) {
                String message = (String)obj.get("message");

                System.out.println("Email Service Response : "+message);

            } else {
                accInfoResponseError = ((String) obj.get("errors"));
                System.out.println("Email Service Response : "+accInfoResponseError);
            }
        } catch (Exception e) {
            throw e;
        }
        return accInfoResponseError;
    }

}
