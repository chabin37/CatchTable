package CatchTable;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.io.PrintWriter;
public class database {
    //텍스트 파일 읽기
    Scanner account;
    Scanner store;
    Scanner reserveManagement;
    Scanner reserve;
    //텍스트 파일 쓰기
    PrintWriter accountWrite;
    PrintWriter storeWrite;
    PrintWriter reserveManagementWrite;
    PrintWriter reserveWrite;
    public database(){//아마 이 생성자에서, 데이터 무결성을 검사해야 할 것으로 보임
        //이때, 데이터 무결성 검사 클래스를 따로 빼서 구현하는것을 추천
        try {
            account = new Scanner(new File("account.txt"));
            store = new Scanner(new File("store.txt"));
            reserveManagement = new Scanner(new File("reserveManagement.txt"));
            reserve = new Scanner(new File("reserve.txt"));

            accountWrite = new PrintWriter("account.txt");
            storeWrite=new PrintWriter("store.txt");
            reserveManagementWrite=new PrintWriter("reserveManagement.txt");
            reserveWrite=new PrintWriter("reserve.txt");

        } catch (FileNotFoundException e) {
            System.out.println("데이터베이스에 문제가 있습니다. 프로그램을 종료합니다.");
            System.exit(0);
        }
        //account, store, reserveManagement, reserve 순으로 입출력 전체 관리3435
        isVaildElement(3,account);

        isVaildElement(4,store);
        isVaildElement(3,reserveManagement);
        isVaildElement(5,reserve);
    }
    private boolean isVaildElement(int count,Scanner sc){
        //구성요소의 갯수가 count만큼 있는가
        while(sc.hasNextLine()){
            String line = sc.nextLine();
            String[]part=line.split("\t");
            if(part.length!=count) {
                completionCode();
            }
            else{//갯수만큼 있는 경우
                return true;
            }
        }
        return true;
    }
    private boolean isVaildString(int index, Scanner sc){//'단어'조건에 맞는가 (띄어쓰기가 없이 이어진 문자열)
        while(sc.hasNextLine()){
            String line=sc.nextLine();
        }

        if (.contains(" ")) {
            completionCode();
        }else return true;
    }
    private boolean isVaildDate(String s){//날짜 문자열이 조건에 맞는가 2024/04/28

    }
    private boolean isVaildTime(String s){//시간 문자열이 조건에 맞는가 10:00

    }
    private void completionCode(){//모든 읽고쓰는 객체를 닫고 프로그램 강제 종료
        System.out.println("데이터베이스에 문제가 있습니다. 프로그램을 종료합니다.");
        this.account.close();
        this.reserve.close();
        this.reserveManagement.close();
        this.store.close();
        this.accountWrite.close();
        this.reserveWrite.close();
        this.reserveManagementWrite.close();
        this.storeWrite.close();
        System.exit(0);
    }
}
