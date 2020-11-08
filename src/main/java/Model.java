import com.google.gson.*;

import java.util.ArrayList;
import java.util.List;


public class Model {

    private static final ApiRequestSender sender = new ApiRequestSender();

    public static SpotifyCollection<SpotifyObject> getNew() {
        sender.setMethod(new NewApiRequest());
        return sender.sendRequest(null);
    }

    public static SpotifyCollection<SpotifyObject> getFeatured() {
        sender.setMethod(new FeaturedApiRequest());
        return sender.sendRequest(null);
    }

    public static SpotifyCollection<SpotifyObject> getCategories() {
        sender.setMethod(new CategoriesApiRequest());
        return sender.sendRequest(null);
    }

    public static SpotifyCollection<SpotifyObject> getPlaylists(String playlist) {
        sender.setMethod(new PlaylistsApiRequest());
        return sender.sendRequest(playlist);
    }
}


class ApiRequestSender {

    ApiRequestMethod method;

    public void setMethod(ApiRequestMethod method) {
        this.method = method;
    }

    /**
     * Uses Strategy design pattern to send a request to the Spotify API to desired endpoint
     * defined by the ApiRequestMethod child class.
     *
     * @param playlist should only be used for PlaylistApiRequest method, can be null otherwise.
     * @return A SpotifyCollection of objects, which is a collection of String's
     */
    public SpotifyCollection<SpotifyObject> sendRequest(String playlist) {
        return this.method.sendRequest(playlist);
    }
}

interface ApiRequestMethod {
    SpotifyCollection<SpotifyObject> sendRequest(String playlist);
}

class NewApiRequest implements ApiRequestMethod {

    @Override
    public SpotifyCollection<SpotifyObject> sendRequest(String playlist) {
        String json = HttpUtils.getFromApi("new", null);
        if (json == null) {
            return null;
        }
        JsonArray items = JsonParser.parseString(json)
                .getAsJsonObject()
                .getAsJsonObject("albums")
                .getAsJsonArray("items");

        String name;
        List<String> artistsList;
        String spotifyUrl;
        List<SpotifyObject> albums = new ArrayList<>();

        for (JsonElement item : items) {
            // Get the album name
            name = item.getAsJsonObject().get("name").getAsString();
            // Get artist(s) from the JsonArray object
            JsonArray artists = item.getAsJsonObject().getAsJsonArray("artists");
            artistsList = new ArrayList<>(); // new object to prevent reusing the same reference?
            for (JsonElement artistElement : artists) {
                JsonObject artist = artistElement.getAsJsonObject();
//                System.out.println(artist.get("name"));
                artistsList.add(artist.get("name").getAsString());
            }

            // Get the external Spotify URL for the album
//            System.out.println(externalUrl.get("spotify"));
            spotifyUrl = item.getAsJsonObject().getAsJsonObject("external_urls").get("spotify").getAsString();

            // Create a SpotifyAlbum object for each album and add to List of SpotifyObjects
            SpotifyObject spotifyAlbum = new SpotifyAlbum(name, artistsList, spotifyUrl);
//            System.out.println(spotifyAlbum.toString());
            albums.add(spotifyAlbum);

        }
        return new SpotifyCollection<>(albums);
    }
}

class CategoriesApiRequest implements ApiRequestMethod {

    @Override
    public SpotifyCollection<SpotifyObject> sendRequest(String playlist) {
        //        System.out.println(json);
        String json = HttpUtils.getFromApi("categories", null);
        JsonArray items = JsonParser.parseString(json)
                .getAsJsonObject()
                .getAsJsonObject("categories")
                .getAsJsonArray("items");

        String name;
        String id;
        List<SpotifyObject> categories = new ArrayList<>();
        for (JsonElement item : items) {
            name = item.getAsJsonObject().get("name").getAsString();
            id = item.getAsJsonObject().get("id").getAsString();
            SpotifyObject spotifyObject = new SpotifyCategories(name, id);
            categories.add(spotifyObject);
        }
        return new SpotifyCollection<>(categories);
    }
}

class FeaturedApiRequest implements ApiRequestMethod {

    @Override
    public SpotifyCollection<SpotifyObject> sendRequest(String playlist) {
        String json = HttpUtils.getFromApi("featured", null);
        JsonArray items = JsonParser.parseString(json)
                .getAsJsonObject()
                .getAsJsonObject("playlists")
                .getAsJsonArray("items");

        String url;
        String name;
        List<SpotifyObject> featured = new ArrayList<>();
        for (JsonElement item : items) {
            name = item.getAsJsonObject().get("name").getAsString();
            url = item.getAsJsonObject().getAsJsonObject("external_urls").get("spotify").getAsString();
            featured.add(new SpotifyFeatured(name, url));
        }
        return new SpotifyCollection<>(featured);
    }
}

class PlaylistsApiRequest implements ApiRequestMethod {

    @Override
    public SpotifyCollection<SpotifyObject> sendRequest(String playlist) {
        // Request categories so we can compare to the String playlist
        ApiRequestSender categoriesRequest = new ApiRequestSender();
        categoriesRequest.setMethod(new CategoriesApiRequest());
        SpotifyCollection<SpotifyObject> categories = categoriesRequest.sendRequest(null);

        int index;
        if ((index = categories.contains(playlist)) >= 0) {
            SpotifyCategories object = (SpotifyCategories) categories.get(index);
            playlist = object.getId();

        }
        String json = HttpUtils.getFromApi("playlists", playlist);

        try {
            JsonArray items = JsonParser.parseString(json)
                    .getAsJsonObject()
                    .getAsJsonObject("playlists")
                    .getAsJsonArray("items");

            String name;
            String url;
            List<SpotifyObject> playlistsList = new ArrayList<>();

            for (JsonElement item : items) {
                name = item.getAsJsonObject().get("name").getAsString();
                url = item.getAsJsonObject().getAsJsonObject("external_urls").get("spotify").getAsString();
                playlistsList.add(new SpotifyPlaylists(name, url));
            }
            return new SpotifyCollection<>(playlistsList);
        } catch (JsonSyntaxException | NullPointerException e) {
            // Gets the Spotify error message from JSON and returns it.
            System.out.println(JsonParser.parseString(json)
                    .getAsJsonObject()
                    .get("error")
                    .getAsJsonObject()
                    .get("message").getAsString());
            return null;
        }
    }
}


