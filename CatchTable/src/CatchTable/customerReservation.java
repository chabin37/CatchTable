package CatchTable;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.Scanner;

//실제 동작(읽고쓰기)(고객)
public class customerReservation {//고객이 예약 관리
    database database;
    Scanner scan = new Scanner(System.in);
    int flag;

    public customerReservation(database database) {
        this.database= database;
    }

    public void reserve() {//예약하기

        //파일에서 매장 정보 가져오기
        try { // 파일 내 Scanner위치 초기화
            database.store = new Scanner(new File("store.txt"));

            //코드 작성

        } catch (Exception e) {
            System.out.println("데이터베이스에 문제가 있습니다. 프로그램을 종료합니다.");
            System.exit(0);
        } finally {
            database.store.close();
        }

        //코드작성(예약 가능 매장 출력,매장 index 입력 -검사 if(!문자열.matches("^[0-9]+$"),범위  ,예약 날짜입력 isVaildDate(1,String s) )

        //파일에서 예약 정보 가져오기
        try { // 파일 내 Scanner위치 초기화
            database.reserveManagement = new Scanner(new File("reserveManagement.txt"));

            //코드 작성

        } catch (Exception e) {
            System.out.println("데이터베이스에 문제가 있습니다. 프로그램을 종료합니다.");
            System.exit(0);
        } finally {
            database.reserveManagement.close();
        }

        //코드 작성(예약 가능 시간, 인원-남은인원 출력  , 예약 Index 입력 -검사 if(!문자열.matches("^[0-9]+$"),범위)

        //예약 성공 (출력)
        //예약현황 파일에 해당 예약 쓰기
        try {
            database.reserveWrite = new PrintWriter(new FileWriter("reserve.txt", true));
            database.reserveWrite.println( /*쓸 값 지정*/);
        } catch (Exception e) {
            System.out.println("데이터베이스에 문제가 있습니다. 프로그램을 종료합니다.");
            System.exit(0);
        } finally {
            database.reserveWrite.close();
        }
    }

    public void reserveManCustomer() {//예약관리

        //파일에서 예약 현황 가져오기
        try { // 파일 내 Scanner위치 초기화
            database.reserve = new Scanner(new File("reserve.txt"));

            //코드 작성(예약한 매장 저장 -ID 이용)

        } catch (Exception e) {
            System.out.println("데이터베이스에 문제가 있습니다. 프로그램을 종료합니다.");
            System.exit(0);
        } finally {
            database.reserve.close();
        }

        //예약한 매장 출력

        //1.예약삭제,1이외.돌아가기  ,검사-if(!문자열.matches("^[0-9]+$"))
        while (true) {
            String select;
            //출력

            //입력
            select=scan.nextLine();

            if(!select.matches("^[0-9]+$"))
                return;

            flag = Integer.parseInt(select);
            switch (flag) {
                case 1 -> {}//예약삭제(Index 입력 -검사 if(!문자열.matches("^[0-9]+$"),범위)
                default -> {return;}
            }
        }

    }
}