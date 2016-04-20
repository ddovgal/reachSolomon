package ua.reapersworkgroup.reachsolomon;

public class Main {
    public static void main(String[] args) {
        String path = "test.txt";
        int errors = 2;
        for (int i = 0; i < args.length; i++) {
            String s = args[i];
            switch (s) {
                case "-p": {
                    path = args[++i];
                    break;
                }
                case "-e": {
                    errors = Integer.parseInt(args[++i]);
                    break;
                }
            }
        }
        System.out.printf("Encoding file \"%s\" with %d errors...", path, errors);
        encodeFile(path, errors);
    }

    private static void encodeFile(String path, int errors) {
        try {
            RSCEncoder encoder = new RSCEncoder(8, errors);
            encoder.encodeFile(path);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
