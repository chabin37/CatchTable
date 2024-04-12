package CatchTable;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.time.LocalDate;
import java.time.Year;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Scanner;
import java.io.PrintWriter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

    public database() {
        try {
            account = new Scanner(new File("account.txt"));
            store = new Scanner(new File("store.txt"));
            reserveManagement = new Scanner(new File("reserveManagement.txt"));
            reserve = new Scanner(new File("reserve.txt"));

            accountWrite = new PrintWriter(new FileWriter("account.txt", true));
            storeWrite = new PrintWriter(new FileWriter("store.txt", true));
            reserveManagementWrite = new PrintWriter(new FileWriter("reserveManagement.txt", true));
            reserveWrite = new PrintWriter(new FileWriter("reserve.txt", true));

        } catch (Exception e) {
            System.out.println("데이터베이스에 문제가 있습니다. 프로그램을 종료합니다.");
            System.exit(0);
        }
        //account, store, reserveManagement, reserve 순으로 입출력 전체 관리3435
        isVaildAccount(3, account);
        isvaildStore(4, store);
        isvaildReserveManagement(3, reserveManagement);
        isvaildReserve(5, reserve);
    }
    //아래의 isVaildAccount를 이용해서, 요소가 count만큼 있는지 확인, 이후 isVaild String Time Date 확인

    private boolean isVaildAccount(int count, Scanner sc) {
        //구성요소의 갯수가 count만큼 있는가
        //[ID]+[\t]+[PW]+[\t]+[사장 고객 여부]+[\n]
        while (sc.hasNextLine()) {
            String line = sc.nextLine();
            String[] part = line.split("\t");
            if (part.length != count) {//갯수만큼 없는 경우
                completionCode();
            } else {//갯수만큼 있는 경우
                isVaildString(0,part[0]);
                isVaildString(0,part[1]);
                isVaildCustomerOwner(0,part[2]);
            }
        }
        return true;
    }

    private boolean isvaildStore(int count, Scanner sc) {
        //[매장 이름]+[\t]+[ID]+[\t]+[영업 시작 시간]+[\t]+[영업 종료 시간]+[\n]
        while (sc.hasNextLine()) {
            String line = sc.nextLine();
            String[] part = line.split("\t");
            if (part.length != count) {//갯수만큼 없는 경우
                completionCode();
            } else {//갯수만큼 있는 경우
                isVaildString(0,part[0]);
                isVaildString(0,part[1]);
                isVaildTime(0,part[2]);
                isVaildTime(0, part[3]);
            }
        }
        return true;
    }

    private boolean isvaildReserveManagement(int count, Scanner sc) {
        //[매장 이름]+[\t]+[시간]+[\t]+[예약 가능 인원]+[\n]
        while (sc.hasNextLine()) {
            String line = sc.nextLine();
            String[] part = line.split("\t");
            if (part.length != count) {//갯수만큼 없는 경우
                completionCode();
            } else {//갯수만큼 있는 경우
                isVaildString(0,part[0]);
                isVaildTime(0,part[1]);
                isvaildInt(0,part[2]);
            }
        }
        return true;
    }

    private boolean isvaildReserve(int count, Scanner sc) {
        //[매장 이름]+[\t]+[ID]+[\t]+[날짜]+[\t]+[시간]+[\t]+[예약 인원]+[\n]
        while (sc.hasNextLine()) {
            String line = sc.nextLine();
            String[] part = line.split("\t");
            if (part.length != count) {//갯수만큼 없는 경우
                completionCode();
            } else {//갯수만큼 있는 경우
                isVaildStringStore(0,part[0]);
                isVaildString(0,part[1]);
                isVaildDate(0,part[2]);
                isVaildTime(0,part[3]);
                isvaildInt(0,part[4]);
            }
        }
        return true;
    }

    private boolean isVaildStringStore(int a, String s) {
        if (a == 0) {//무결성 검사파트
            if (!s.contains(" ")) {
                if(s.length()<=15){
                    return true;
                }else completionCode();
            }else return true;
        } else {//입력이 정확한지 확인
            return !s.contains(" ")&&s.length() <= 15;
        }return true;
    }

    //int a=0인 경우 데이터 무결성 검사용, int a!=0인 경우 입력받는 데 사용.
    public boolean isVaildString(int a, String s) {//'단어'조건에 맞는가 (띄어쓰기가 없이 이어진 문자열)
        if (a == 0) {//무결성 검사파트
            if (!s.contains(" ")) {
                if(s.length()<=15&&s.matches("^[a-zA-Z0-9\\p{Punct}]+$")){
                    return true;
                }else completionCode();
            }else return true;
        } else {//입력이 정확한지 확인
                return !s.contains(" ")&&s.length() <= 15&&s.matches("^[a-zA-Z0-9\\p{Punct}]+$");
        }return true;
    }
    public boolean isVaildCustomerOwner(int a, String s){
        if (a == 0) {//무결성 검사파트
            if (s.equals("사장")||s.equals("고객")) {
                return true;
            }else completionCode();
        }else{
            return s.equals("사장") || s.equals("고객");
        }return true;
    }

    public boolean isVaildDate(int a, String s) {//날짜 문자열이 조건에 맞는가 2024/04/28
        String regex = "^20(2[3-9]|[3-9][0-9]|2[1-9][0-9]{2}|[3-9][0-9]{3})/(0[1-9]|1[0-2])/(0[1-9]|[1-2][0-9]|3[0-1])$";//정규표현식
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(s);
        if (a == 0) {//무결성 검사파트
            if (!matcher.matches()) {
                completionCode();
            } else {
                if (!isLeapYear(s)){//윤년조건 만족하지 않는 경우
                    completionCode();
                }else return true;
            }
        } else {//입력이 정확한지 확인
            if(!matcher.matches()) {
                return false;
            }else isLeapYear(s);//윤년조건 만족하지 않는 경우
        } return true;
    }

    public boolean isVaildTime(int a, String s) {//시간 문자열이s 조건에 맞는가 10:00
        String regex = "^([01]?[0-9]|2[0-3]):[0-5][0-9]$";//정규표현식
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(s);
        if (a == 0) {//무결성 검사파트
            if (!matcher.matches()) {
                completionCode();
            } else return true;
        } else {//입력이 정확한지 확인
            return matcher.matches();
        }
        return true;
    }
    public boolean isvaildInt(int a, String s){//예약인원(정수형)이 제대로 입력됬는가
        if (a == 0) {//무결성 검사파트
            if(!s.matches("^[1-9][0-9]*$")){
                completionCode();
            }else return true;
        }else{//입력이 정확한지 확인
            return s.matches("^[1-9][0-9]*$");
        }
        return true;
    }
    private boolean isLeapYear(String s){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd");
        LocalDate date = LocalDate.parse(s, formatter);//자동으로 day값이 보정되므로, 연도로 isLeapYear만 하는걸로 함.
        String daySt=s.substring(8,10);
        int month =date.getMonthValue();
        int day = Integer.parseInt(daySt);
        if(date.isLeapYear()){//윤년이라면
            if(month==2){//2월이라면
                return day <= 29;
            }
        }
        else{//윤년이 아니라면
            if(month==2){
                return day <= 28;
            }
        }return true;
    }
    private void completionCode() {//모든 읽고쓰는 객체를 닫고 프로그램 강제 종료
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