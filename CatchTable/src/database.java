import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class database {

    Scanner account;
    Scanner reserve;
    Scanner reserveManagement;
    Scanner store;
    Scanner waiting;

    {
        try {
            account = new Scanner(new File("account.txt"));
            reserve = new Scanner(new File("reserve.txt"));
            reserveManagement = new Scanner(new File("reserveManagement.txt"));
            store = new Scanner(new File("store.txt"));
            waiting = new Scanner(new File("waiting.txt"));

        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

}
