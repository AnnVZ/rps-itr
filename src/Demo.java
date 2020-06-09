import javax.crypto.KeyGenerator;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.*;

public class Demo {
    public static void main(String[] args) {
        if (args.length % 2 == 0 || args.length < 3 || (new HashSet<>(Arrays.asList(args))).size() != args.length) {
            System.out.println("Input error");
            return;
        }

        int computerMove = 0;
        String sourceKey = "";

        System.out.print("HMAC: ");
        try {
            String algorithm = "HmacSHA256";
            SecureRandom secureRandom = new SecureRandom();
            KeyGenerator generator = KeyGenerator.getInstance(algorithm);
            generator.init(256, secureRandom);
            final Key key = generator.generateKey();
            sourceKey = toHexString(key.getEncoded());

            computerMove = Math.abs(secureRandom.nextInt()) % args.length;

            Mac hmacSHA256 = Mac.getInstance(algorithm);
            SecretKeySpec keySpec = new SecretKeySpec(key.getEncoded(), algorithm);
            hmacSHA256.init(keySpec);
            byte[] macData = hmacSHA256.doFinal(args[computerMove].getBytes(StandardCharsets.UTF_8));
            System.out.println(toHexString(macData));
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            System.out.println("error");
        } finally {
            System.out.println("Available moves:");
            for (int i = 0; i < args.length; i++) {
                System.out.println(i + 1 + " - " + args[i]);
            }
            System.out.println("0 - exit");

            Scanner in = new Scanner(System.in);
            boolean incorrectInput = true;
            int userMove = 0;
            do {
                System.out.print("Enter your move: ");
                String input = in.next();
                if (input.matches("[0-9]+")) {
                    userMove = Integer.parseUnsignedInt(input);
                    if (userMove <= args.length) {
                        if (userMove == 0) {
                            return;
                        }
                        incorrectInput = false;
                    }
                }
            } while (incorrectInput);

            System.out.println("Your move: " + args[--userMove]);
            System.out.println("Computer move: " + args[computerMove]);

            if (userMove == computerMove) {
                System.out.println("No winner");
            } else if (computerMove > userMove && computerMove - userMove <= (args.length - 1) / 2 ||
                    computerMove < userMove && userMove - computerMove > (args.length - 1) / 2) {
                System.out.println("Computer win!");
            } else {
                System.out.println("You win!");
            }

            System.out.println("HMAC key: " + sourceKey);
        }
    }

    private static String toHexString(byte[] bytes) {
        Formatter formatter = new Formatter();
        for (byte b : bytes) {
            formatter.format("%02x", b);
        }
        return formatter.toString();
    }
}
