import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.io.PrintWriter;
public class database {
    //텍스트 파일 읽기
    Scanner account;
    Scanner reserve;
    Scanner reserveManagement;
    Scanner store;
    Scanner waiting;
    //텍스트 파일 쓰기
    PrintWriter accountWrite;
    PrintWriter reserveWrite;
    PrintWriter reserveManagementWrite;
    PrintWriter storeWrite;
    PrintWriter waitingWrite;
    {
        try {
            account = new Scanner(new File("account.txt"));
            reserve = new Scanner(new File("reserve.txt"));
            reserveManagement = new Scanner(new File("reserveManagement.txt"));
            store = new Scanner(new File("store.txt"));
            waiting = new Scanner(new File("waiting.txt"));
            accountWrite=new PrintWriter("account.txt");
            reserveWrite=new PrintWriter("reserve.txt");
            reserveManagementWrite=new PrintWriter("reserveManagement.txt");
            storeWrite=new PrintWriter("store.txt");
            waitingWrite=new PrintWriter("waitingWrite.txt");

        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
