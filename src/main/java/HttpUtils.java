import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;


public class HttpUtils {


    private static HttpServer server;
    private static String query = ""; // I need to initialize query for my condition in startServer()
    private static String spotifyCode = ""; // I need to initialize spotifyCode for my condition in startServer()
    private static String accessToken;
    private static String accessUri = "https://accounts.spotify.com"; //default value
    private static String resourceUri = "https://api.spotify.com"; //default value
    private static final String CLIENT_ID = "f02476f99811465e9b77ab75c5292909";
    private static final String CLIENT_SECRET = "9e7d9a9234114d0898e3d6b3c3780ef0";
    private static final String REDIRECT_ID = "http://localhost:8080";


    public static void startServer() {
        try {
            server = HttpServer.create(new InetSocketAddress("localhost", 8080), 0);
            server.start();

            server.createContext("/", new HttpHandler() {
                @Override
                public void handle(HttpExchange exchange) throws IOException {
                    query = exchange.getRequestURI().getQuery();
                    System.out.println("waiting for code...\ncode received");
                    String msg;
                    if (query != null && query.contains("code")) {
                        spotifyCode = query.substring(5);
                        msg = "Got the code. Return back to your program.";
                    } else {
                        msg = "Authorization code not found. Try again.";
                    }
                    exchange.sendResponseHeaders(200, msg.length());
                    exchange.getResponseBody().write(msg.getBytes());
                    exchange.getResponseBody().close();
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }

        while (spotifyCode.equals("")) {
            try {
                Thread.sleep(10);
            } catch (InterruptedException ie) {
                ie.printStackTrace();
            }
        }
        server.stop(10);
    }


    public static String getAccessToken() throws IOException, InterruptedException {

        System.out.println("Making http request for access token...");

        HttpRequest requestForAccessToken = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(
                        "&client_id=" + CLIENT_ID
                                + "&client_secret=" + CLIENT_SECRET
                                + "&grant_type=authorization_code"
                                + "&code=" + spotifyCode
                                + "&redirect_uri=" + REDIRECT_ID))
                .header("Content-type", "application/x-www-form-urlencoded")
                .uri(URI.create(accessUri + "/api/token"))
                .build();

        HttpResponse<String> responseWithAccessToken = HttpClient
                .newBuilder()
                .build()
                .send(requestForAccessToken,
                        HttpResponse.BodyHandlers.ofString());

        //TODO JUST ADDED
        if (responseWithAccessToken.statusCode() > 200) {
            return String.valueOf(false);
        }
        String fullToken = responseWithAccessToken.body();
        return parseAccessToken(fullToken);
    }

    public static String parseAccessToken(final String bearerToken) {
        JsonObject jo = JsonParser.parseString(bearerToken).getAsJsonObject();
        accessToken = jo.get("access_token").getAsString();
        return accessToken;
    }


    public static String getFromApi(String getType, String playlist) {

        String pathForEachCase = "";
        switch (getType) {
            case "new":
                pathForEachCase = "/v1/browse/new-releases";
                break;
            case "categories":
                pathForEachCase = "/v1/browse/categories?limit=50";
                break;
            case "featured":
                pathForEachCase = "/v1/browse/featured-playlists";
                break;
            case "playlists":
                pathForEachCase = String.format("/v1/browse/categories/%s/playlists", playlist);
                break;

        }
        String responseJson = "";

        try {
            HttpClient client = HttpClient.newBuilder().build();
            HttpRequest request = HttpRequest.newBuilder()
                    .header("Authorization", "Bearer " + accessToken)
                    .uri(URI.create(resourceUri + pathForEachCase))
                    .GET()
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            responseJson = response.body();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        return responseJson;
    }

    public static void setAccessUri(String accessUri) {
        HttpUtils.accessUri = accessUri;
    }

    public static void setResourceUri(String resourceUri) {
        HttpUtils.resourceUri = resourceUri;
    }

    public static String getAccessUri() {
        return accessUri;
    }

    public static String getResourceUri() {
        return resourceUri;
    }
}

