import java.util.List;

public class SpotifyCollection<T extends SpotifyObject> {

    List<T> spotifyCollection;

    public SpotifyCollection(List<T> spotifyCollection) {
        this.spotifyCollection = spotifyCollection;
    }

    public void printItems(int n, int pos) {
        if (pos + n > spotifyCollection.size() - 1) {
            n -= (pos + n) - (spotifyCollection.size() - 1);
        }
        for (int i = pos; i < pos + n; i++) {
            System.out.println(spotifyCollection.get(i).toString());
        }

        if (pos == spotifyCollection.size() - 1) {   // This is for last element in CategoryList !
            System.out.println(spotifyCollection.get(pos).toString());
        }
    }

    public int contains(String name) {
        for (int i = 0; i < spotifyCollection.size(); i++) {
            if (spotifyCollection.get(i).getName().toLowerCase().equals(name.toLowerCase())) {
                return i;
            }
        }
        return -1;
    }

    public T get(int index) {
        return spotifyCollection.get(index);
    }

    public int size() {
        return spotifyCollection.size();
    }

}
