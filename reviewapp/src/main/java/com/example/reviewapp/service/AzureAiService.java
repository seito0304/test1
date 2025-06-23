package com.example.reviewapp.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class AzureAiService {

    @Value("${azure.api.key}")
    private String apiKey;

    @Value("${azure.api.endpoint}")
    private String endpoint;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 感情分析
     */
    public Map<String, Object> analyzeSentiment(String text) throws Exception {
        String url = endpoint + "/language/:analyze-text?api-version=2023-04-01";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Ocp-Apim-Subscription-Key", apiKey);

        Map<String, Object> payload = Map.of(
            "kind", "SentimentAnalysis",
            "parameters", Map.of("modelVersion", "latest"),
            "analysisInput", Map.of(
                "documents", List.of(Map.of(
                    "id", "1",
                    "language", "ja",
                    "text", text
                ))
            )
        );

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(payload, headers);
        ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);

        JsonNode root = objectMapper.readTree(response.getBody());
        JsonNode docNode = root.path("results").path("documents").get(0);

        String sentiment = docNode.get("sentiment").asText();
        double score = docNode.get("confidenceScores").get(sentiment.toLowerCase()).asDouble();

        return Map.of(
            "sentiment", sentiment,
            "score", score
        );
    }

    /**
     * キーワード抽出
     */
    public List<String> extractKeywords(String text) throws Exception {
        String url = endpoint + "/language/:analyze-text?api-version=2023-04-01";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Ocp-Apim-Subscription-Key", apiKey);

        Map<String, Object> payload = Map.of(
            "kind", "KeyPhraseExtraction",
            "parameters", Map.of("modelVersion", "latest"),
            "analysisInput", Map.of(
                "documents", List.of(Map.of(
                    "id", "1",
                    "language", "ja",
                    "text", text
                ))
            )
        );

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(payload, headers);
        ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);

        JsonNode root = objectMapper.readTree(response.getBody());
        JsonNode keyPhrasesNode = root.path("results").path("documents").get(0).path("keyPhrases");

        return objectMapper.convertValue(keyPhrasesNode, List.class);
    }
}

