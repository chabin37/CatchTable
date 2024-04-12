package CatchTable;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.Scanner;

//실제 동작(읽고쓰기)(사장)
public class adminReservation {// 사장이 예약 관리
    database database;
    Scanner scan = new Scanner(System.in);
    int flag;

    public adminReservation(database database) {
        this.database = database;
    }

    public void storeJoin() {// 매장 등록
        String storeName, openTime, closeTime;

        System.out.println("\n[매장 등록]");
        System.out.println("새로운 매장 등록을 위해 매장 정보를 입력해주세요.");
        System.out.print("매장 이름: ");
        storeName = scan.nextLine();

        // 매장 등록 여부 확인
        try { // 파일 내 Scanner위치 초기화
            database.store = new Scanner(new File("store.txt"));
            while (database.store.hasNextLine()) {// 매장이름 중복 확인
                String line = database.store.nextLine();
                String[] part = line.split("\t");
                // System.out.println(line);//출력확인
                if (part[0].equals(storeName)) {
                    System.out.println("[오류] 이미 사용 중인 매장 이름입니다. 다른 이름을 사용해주세요.");
                    return;
                }
            }
        } catch (Exception e) {
            System.out.println("데이터베이스에 문제가 있습니다. 프로그램을 종료합니다.");
            System.exit(0);
        } finally {
            database.store.close();
        }

        System.out.println("사용 가능한 매장 이름입니다.");

        // 영업 시간 등록
        System.out.println("영업 시작 시간 (예: 10:00): ");
        openTime = scan.nextLine();
        System.out.println("영업 마감 시간 (예: 18:00): ");
        closeTime = scan.nextLine();

        if (!database.isValidTime(1,openTime) || !database.isValidTime(1,closeTime)) {
            System.out.println("올바른 시간 형식이 아닙니다. 다시 입력해주세요.");
            return;
        }

        // 매장 정보 파일에 해당 매장 쓰기
        try {
            database.storeWrite = new PrintWriter(new FileWriter("store.txt", true));
            database.storeWrite.println(storeName + "\t" + openTime + "\t" + closeTime);
        } catch (Exception e) {
            System.out.println("데이터베이스에 문제가 있습니다. 프로그램을 종료합니다.");
            System.exit(0);
        } finally {
            database.storeWrite.close();
        }

        System.out.println("\n*등록되었습니다.");
    }



    public void reserveManAdmin() {// 예약 관리
        // 매장 등록 여부 확인

        // 파일에서 매장 정보 가져오기
        try { // 파일 내 Scanner위치 초기화
            database.store = new Scanner(new File("store.txt"));

            // 코드 작성

        } catch (Exception e) {
            System.out.println("데이터베이스에 문제가 있습니다. 프로그램을 종료합니다.");
            System.exit(0);
        } finally {
            database.store.close();
        }

        // 파일에서 예약 정보 가져오기
        try { // 파일 내 Scanner위치 초기화
            database.reserveManagement = new Scanner(new File("reserveManagement.txt"));

            // 코드 작성

        } catch (Exception e) {
            System.out.println("데이터베이스에 문제가 있습니다. 프로그램을 종료합니다.");
            System.exit(0);
        } finally {
            database.reserveManagement.close();
        }

        // (출력,메뉴 1.예약 시간 및 인원 등록,2.예약 삭제,1과2 이외 return)반복-흐름도 확인
        while (true) {
            String select;
            // 출력

            // 입력
            select = scan.nextLine();

            if (!select.matches("^[0-9]+$"))
                return;

            flag = Integer.parseInt(select);
            switch (flag) {
                case 1 -> {
                } // 예약 시간 및 인원 등록(시간 ,최대인원)
                case 2 -> {
                } // 예약 삭제(Index 입력 -검사 if(!문자열.matches("^[0-9]+$"),범위)
                default -> {
                    return;
                }
            }
        }
    }

    public void reserveNow() {// 예약 현황
        // 매장 등록 여부 확인

        // 파일에서 예약 현황 가져오기
        try { // 파일 내 Scanner위치 초기화
            database.reserve = new Scanner(new File("reserve.txt"));

            // 코드 작성

        } catch (Exception e) {
            System.out.println("데이터베이스에 문제가 있습니다. 프로그램을 종료합니다.");
            System.exit(0);
        } finally {
            database.reserve.close();
        }
        // 예약 현황 출력

        // 메뉴(1.예약허가 , 1이외 return)
        while (true) {
            String select;
            // 출력

            // 입력
            select = scan.nextLine();

            if (!select.matches("^[0-9]+$"))
                return;

            flag = Integer.parseInt(select);
            switch (flag) {
                case 1 -> {
                } // 예약허가(Index 입력 -검사 if(!문자열.matches("^[0-9]+$"),범위)
                default -> {
                    return;
                }
            }
        }
    }

}