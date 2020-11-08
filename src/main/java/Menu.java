import java.io.IOException;
import java.util.Scanner;

public class Menu {

    private static boolean auth = false;
    private static String accessCode = HttpUtils.getAccessUri() + "/authorize?client_id=f02476f99811465e9b77ab75c5292909&redirect_uri=http://localhost:8080&response_type=code";
    private static final View view = View.getInstance();

    public static void mainMenu() {

        Scanner scanner = new Scanner(System.in);
        String input;
        String playlistName = "";


        while (scanner.hasNext()) {
            input = scanner.nextLine();

            if (input.contains("playlists")) {
                // try {
                String[] arrayWithTwoWords = input.split(" ");
                StringBuilder sb = new StringBuilder();
                for (int i = 1; i < arrayWithTwoWords.length; i++) { // for multiple word playlists
                    if (i == arrayWithTwoWords.length - 1) {
                        sb.append(arrayWithTwoWords[i]);
                    } else {
                        sb.append(arrayWithTwoWords[i]).append(" ");
                    }
                    playlistName = sb.toString();
                }
                input = "playlists";

            }

            switch (input) {

                case "auth": {
                    if (auth) {
                        System.out.println("Already authenticated.");
                        break;
                    }
                    System.out.println("use this link to request the access code:");
                    System.out.println(accessCode);

                    HttpUtils.startServer();
                    try {
                        HttpUtils.getAccessToken();
                        auth = true;
                        System.out.println("Success!");
                    } catch (IOException | InterruptedException e) {
                        e.printStackTrace();
                    }
                    break;
                }

                case "new": {
                    if (auth()) break;
                    view.setContent(Model.getNew());
                    view.update();
                    break;
                }

                case "featured": {
                    if (auth()) break;
                    view.setContent(Model.getFeatured());
                    view.update();
                    break;
                }

                case "categories": {
                    if (auth()) break;
                    view.setContent(Model.getCategories());
                    view.update();
                    break;
                }

                case "playlists": {
                    if (auth()) break;
                    view.setContent(Model.getPlaylists(playlistName));
                    view.update();
                    break;
                }

                case "next": {
                    view.next();
                    break;
                }

                case "prev": {

                    view.prev();
                    break;
                }

                case "exit": {
                    System.out.println("---GOODBYE!---");
                    System.exit(0);
                }

                default:
                    System.out.println("Unknown command. Try again.");
                    break;
            }
        }
    }


    private static boolean auth() {
        if (!auth) {
            String accessNotGranted = "Please, provide access for application.\n";
            System.out.println(accessNotGranted);
            return true;
        }
        return false;
    }

}

