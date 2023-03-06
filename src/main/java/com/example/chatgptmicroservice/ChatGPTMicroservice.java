package com.example.chatgptmicroservice;

import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

@RestController
public class ChatGPTMicroservice {

    //  API key
    private static final String API_KEY = "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";

    //  the path of  CSV file
    private static final String CSV_PATH = "C:\\Users\\JemaliAyoub\\Desktop\\file.csv";

    // the model that we use
    private static final String MODEL = "text-davinci-002";

    @PostMapping("/chatgpt")
    public ResponseEntity<String> chatGPT(@RequestBody String input, HttpServletResponse response) {
        try {
            // Send the input to the ChatGPT API and get the response
            String responseStr = getChatGPTResponse(input);

            // Parse the response to get the answer
            String answer = parseChatGPTResponse(responseStr);

            // Write the question and answer to the CSV file
            writeQuestionAnswerToCSV(input, answer);

            // Return the answer to the user
            return new ResponseEntity<>(answer, HttpStatus.OK);
        } catch (IOException e) {
            // Handle exception
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @GetMapping("/hy")
    public String func(){
        return "hyhy";
    }
    private void writeQuestionAnswerToCSV(String input, String answer) {

    }

    private String getChatGPTResponse(String input) throws IOException {
        // Set up the HTTP request to the ChatGPT API
        URL url = new URL("https://api.openai.com/v1/completions");
        String payload = null;
        try {
            payload = new JSONObject()
                    .put("model", MODEL)
                    .put("prompt", input)
                    .put("max_tokens", 4000)
                    .put("temperature", 1.0)
                    .toString();
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        byte[] postData = payload.getBytes(StandardCharsets.UTF_8);
        int postDataLength = postData.length;
        String authHeader = "Bearer " + API_KEY;

        // Send the HTTP request and get the response
        // You can use any HTTP library you like, this is just an example
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setDoOutput(true);
        conn.setInstanceFollowRedirects(false);
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setRequestProperty("Authorization", authHeader);
        conn.setRequestProperty("charset", "utf-8");
        conn.setRequestProperty("Content-Length", Integer.toString(postDataLength));
        conn.setUseCaches(false);
        try (DataOutputStream wr = new DataOutputStream(conn.getOutputStream())) {
            wr.write(postData);
        }
        String responseStr = new String(conn.getInputStream().readAllBytes(), StandardCharsets.UTF_8);

        return responseStr;
    }

    private String parseChatGPTResponse(String responseStr) {
        // Parse the JSON response to get the answer
        JSONObject responseObj = null;
        try {
            responseObj = new JSONObject(responseStr);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        JSONObject choice = null;
        try {
            choice = responseObj.getJSONArray("choices").getJSONObject(0);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        try {
            String answer = choice.getString("text").trim();
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        return responseStr;
    }
    }
