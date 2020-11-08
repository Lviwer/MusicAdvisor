public class Main {


    public static void main(String[] args) {


        if (args.length > 1) {
            for (int i = 0; i < args.length; i++) {
                if (args[i].contains("-access")) {
                    HttpUtils.setAccessUri(args[i + 1]);
                }
                if (args[i].contains("-resource")) {
                    HttpUtils.setResourceUri(args[i + 1]);
                }


                if (args[i].contains("-page")) {
                    View view = View.getInstance();
                    try {
                        view.numberOfItems = Integer.parseInt(args[i + 1]);
                    } catch (ClassCastException e) {
                        view.numberOfItems = 5;  //set default if given String can't be parsed
                    }
                }
            }
        }

        Menu.mainMenu();
    }
}

