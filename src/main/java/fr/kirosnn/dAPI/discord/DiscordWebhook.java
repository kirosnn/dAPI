package fr.kirosnn.dAPI.discord;

import com.google.gson.Gson;
import org.jetbrains.annotations.NotNull;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DiscordWebhook {
    private final String webhookUrl;
    private final Map<String, Object> payload;

    /**
     * Initialise un DiscordWebhook.
     *
     * @param webhookUrl L'URL du webhook Discord.
     */
    public DiscordWebhook(@NotNull String webhookUrl) {
        this.webhookUrl = webhookUrl;
        this.payload = new HashMap<>();
    }

    /**
     * Définit le contenu principal du message.
     *
     * @param content Le texte du message.
     */
    public void setContent(@NotNull String content) {
        payload.put("content", content);
    }

    /**
     * Définit le nom personnalisé de l'expéditeur.
     *
     * @param username Le nom d'utilisateur affiché.
     */
    public void setUsername(@NotNull String username) {
        payload.put("username", username);
    }

    /**
     * Définit l'avatar de l'expéditeur.
     *
     * @param avatarUrl L'URL de l'avatar.
     */
    public void setAvatarUrl(@NotNull String avatarUrl) {
        payload.put("avatar_url", avatarUrl);
    }

    /**
     * Définit une embarcation (embed) pour enrichir le message.
     *
     * @param embed Un objet Map représentant l'embed.
     */
    public void addEmbed(@NotNull Map<String, Object> embed) {
        payload.computeIfAbsent("embeds", k -> new ArrayList<Map<String, Object>>());
        ((List<Map<String, Object>>) payload.get("embeds")).add(embed);
    }

    /**
     * Envoie le webhook.
     *
     * @throws Exception Si une erreur survient lors de l'envoi.
     */
    public void send() throws Exception {
        URL url = new URL(webhookUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        connection.setDoOutput(true);
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setRequestProperty("User-Agent", "WebhookBot");
        connection.setConnectTimeout(5000);
        connection.setReadTimeout(5000);

        Gson gson = new Gson();
        String jsonPayload = gson.toJson(payload);

        try (OutputStream os = connection.getOutputStream()) {
            os.write(jsonPayload.getBytes());
            os.flush();
        }

        int responseCode = connection.getResponseCode();
        if (responseCode != 204) {
            throw new Exception("Failed to send webhook: HTTP " + responseCode);
        }
    }
}