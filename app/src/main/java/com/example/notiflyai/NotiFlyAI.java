package com.example.notiflyai;

import okhttp3.*;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class NotiFlyAI {

    private static final String OPENAI_API_KEY = "YOUR_API_KEY"; // <-- ВСТАВЬТЕ СВОЙ КЛЮЧ
    private static final String OPENAI_URL = "https://api.openai.com/v1/chat/completions";

    private static OkHttpClient client = new OkHttpClient();

    public interface AICallback {
        void onResult(String text);
    }

    /**
     * Генерирует более развернутый ответ из короткой заготовки,
     * и добавляет фразу в конце: "(написано при помощи искусственного интеллекта)".
     */
    public static void generateExpandedReply(String shortDraft, final AICallback callback) {
        String prompt = "Expand this short message into a polite and friendly sentence in Russian. " +
                "Then add the phrase '(написано при помощи искусственного интеллекта)' at the end. " +
                "Short draft: \"" + shortDraft + "\"";
        doOpenAIRequest(prompt, callback);
    }

    /**
     * Вызывается при получении нового сообщения, если Busy Mode = ON.
     * AI решает, стоит ли уведомлять пользователя, учитывая приоритет автора, текст сообщения.
     * Возвращает "YES" или "NO".
     */
    public static void shouldNotifyBusyUser(int senderPriority, String messageText, AICallback callback) {
        String prompt = "You are a helpful assistant. " +
                "The user is currently in Busy Mode. The sender's priority is " + senderPriority + " (1=lowest,5=highest). " +
                "Message text: \"" + messageText + "\". " +
                "Should the user be notified? Return exactly 'YES' or 'NO'. " +
                "Criteria: If priority >= 3, or if the message seems urgent, answer YES. Otherwise NO.";

        doOpenAIRequest(prompt, callback);
    }

    // ------------------------------------------------------------------------
    // Вспомогательный метод запроса к OpenAI
    private static void doOpenAIRequest(String prompt, final AICallback callback) {
        JSONObject requestBodyJson = new JSONObject();
        try {
            requestBodyJson.put("model", "gpt-3.5-turbo");

            JSONArray messages = new JSONArray();
            JSONObject userMessage = new JSONObject();
            userMessage.put("role", "user");
            userMessage.put("content", prompt);
            messages.put(userMessage);

            requestBodyJson.put("messages", messages);
            requestBodyJson.put("temperature", 0.7);
        } catch (JSONException e) {
            e.printStackTrace();
            callback.onResult("AI Error: " + e.getMessage());
            return;
        }

        RequestBody body = RequestBody.create(
                requestBodyJson.toString(),
                MediaType.parse("application/json; charset=utf-8")
        );

        Request request = new Request.Builder()
                .url(OPENAI_URL)
                .header("Authorization", "Bearer " + OPENAI_API_KEY)
                .header("Content-Type", "application/json")
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                callback.onResult("AI Error: " + e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try (Response r = response) {
                    if(!r.isSuccessful()) {
                        callback.onResult("AI Error: " + r.message());
                        return;
                    }
                    String respBody = (r.body() != null) ? r.body().string() : "";
                    try {
                        JSONObject json = new JSONObject(respBody);
                        JSONArray choices = json.optJSONArray("choices");
                        if(choices != null && choices.length() > 0) {
                            JSONObject first = choices.getJSONObject(0);
                            JSONObject msgObj = first.optJSONObject("message");
                            String content = msgObj != null ? msgObj.optString("content") : "";
                            callback.onResult(content.trim());
                        } else {
                            callback.onResult("AI no answer");
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        callback.onResult("AI Error: " + e.getMessage());
                    }
                }
            }
        });
    }
}