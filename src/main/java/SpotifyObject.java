import java.util.List;

public class SpotifyObject {

    String name;

    public SpotifyObject(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}

class SpotifyAlbum extends SpotifyObject {

    private List<String> artists;
    private String spotifyUrl;

    public SpotifyAlbum(String name, List<String> artists, String spotifyUrl) {
        super(name);
        this.artists = artists;
        this.spotifyUrl = spotifyUrl;
    }

    @Override
    public String toString() {
        return super.getName() + "\n" + artists + "\n" + spotifyUrl + "\n";
    }
}

class SpotifyCategories extends SpotifyObject {
    private String id;

    public SpotifyCategories(String name, String id) {
        super(name);
        this.id = id;
    }

    @Override
    public String toString() {
        return super.getName();
    }

    public String getId() {
        return id;
    }
}


class SpotifyFeatured extends SpotifyObject {
    private String url;

    public SpotifyFeatured(String name, String url) {
        super(name);
        this.url = url;
    }

    @Override
    public String toString() {
        return super.getName() + "\n" + url + "\n";
    }

}


class SpotifyPlaylists extends SpotifyObject {
    private String url;

    public SpotifyPlaylists(String name, String url) {
        super(name);
        this.url = url;
    }

    @Override
    public String toString() {
        return super.getName() + "\n" + url + "\n";
    }
}
