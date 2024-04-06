package CatchTable;
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
    //텍스트 파일 쓰기
    PrintWriter accountWrite;
    PrintWriter reserveWrite;
    PrintWriter reserveManagementWrite;
    PrintWriter storeWrite;
    public database(){//아마 이 생성자에서, 데이터 무결성을 검사해야 할 것으로 보임
        //이때, 데이터 무결성 검사 클래스를 따로 빼서 구현하는것을 추천
        try {
            account = new Scanner(new File("account.txt"));
            reserve = new Scanner(new File("reserve.txt"));
            reserveManagement = new Scanner(new File("reserveManagement.txt"));
            store = new Scanner(new File("store.txt"));

            accountWrite = new PrintWriter("account.txt");
            reserveWrite=new PrintWriter("reserve.txt");
            reserveManagementWrite=new PrintWriter("reserveManagement.txt");
            storeWrite=new PrintWriter("store.txt");

        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
