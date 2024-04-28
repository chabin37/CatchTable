package CatchTable;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
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

    Set<String> IdSet = new HashSet<>();
    Set<String> StoreSet = new HashSet<>();
    Map<String,Integer> StoreTime=new HashMap<>();
    Map<String,ArrayList<Integer>> ReserveTimeandPeople=new HashMap<>();
    public database() {
        try {
            accountWrite = new PrintWriter(new FileWriter("account.txt", true));
            storeWrite = new PrintWriter(new FileWriter("store.txt", true));
            reserveManagementWrite = new PrintWriter(new FileWriter("reserveManagement.txt", true));
            reserveWrite = new PrintWriter(new FileWriter("reserve.txt", true));

            account = new Scanner(new File("account.txt"));
            store = new Scanner(new File("store.txt"));
            reserveManagement = new Scanner(new File("reserveManagement.txt"));
            reserve = new Scanner(new File("reserve.txt"));
        } catch (Exception e) {
            System.out.println("데이터베이스에 문제가 있습니다. 프로그램을 종료합니다.");
            System.exit(0);
        }
        //account, store, reserveManagement, reserve 순으로 입출력 전체 관리3435
        isValidAccount(3, account);
        isValidStore(4, store);
        isValidReserveManagement(3, reserveManagement);
        isValidReserve(5, reserve);
        this.account.close();
        this.store.close();
        this.reserveManagement.close();
        this.reserve.close();
    }
    //아래의 isValidAccount를 이용해서, 요소가 count만큼 있는지 확인, 이후 isValid String Time Date 확인

    private boolean isValidAccount(int count, Scanner sc) {
        //구성요소의 갯수가 count만큼 있는가
        //[ID]+[\t]+[PW]+[\t]+[사장 고객 여부]+[\n]
        while (sc.hasNextLine()) {
            String line = sc.nextLine();
            String[] part = line.split("\t");
            if (part.length != count) {//갯수만큼 없는 경우
                completionCode();
            } else {//갯수만큼 있는 경우
                isValidString(0,part[0]);
                isValidString(0,part[1]);
                isValidCustomerOwner(0,part[2]);
                isDuplicate(IdSet, part[0]);
            }
        }
        return true;
    }

    private boolean isValidStore(int count, Scanner sc) {
        //[매장 이름]+[\t]+[ID]+[\t]+[영업 시작 시간]+[\t]+[영업 종료 시간]+[\n]
        Set<String> set = new HashSet<>();
        while (sc.hasNextLine()) {
            String line = sc.nextLine();
            String[] part = line.split("\t");
            if (part.length != count) {//갯수만큼 없는 경우
                completionCode();
            } else {//갯수만큼 있는 경우
                isValidStringStore(0,part[0]);
                isValidString(0,part[1]);
                isValidTime(0,part[2]);
                isValidTime(0, part[3]);
                isDuplicate(StoreSet, part[0]);
                isDuplicateStoreId(set, part[1]);
                String sT=part[2].replace(":","");
                String eT=part[3].replace(":","");
                int StartTime=Integer.parseInt(sT);
                int EndTime=Integer.parseInt(eT);
                this.StoreTime.put(part[0],StartTime*10000+EndTime);//가게이름,10001800 으로저장
            }
        }
        return true;
    }

    private boolean isValidReserveManagement(int count, Scanner sc) {
        //[매장 이름]+[\t]+[시간]+[\t]+[예약 가능 인원]+[\n]
        Set<Map> set = new HashSet<>();
        while (sc.hasNextLine()) {
            String line = sc.nextLine();
            String[] part = line.split("\t");
            if (part.length != count) {//갯수만큼 없는 경우
                completionCode();
            } else {//갯수만큼 있는 경우
                isValidStringStore(0,part[0]);
                isValidTime(0,part[1]);
                isValidInt(0,part[2]);
                isDuplicateReserveManagement(set, part[0],part[1]);
                String time=part[1].replace(":","");
                int t=Integer.parseInt(time);
                int storeTime=this.StoreTime.get(part[0]);
                if(storeTime/10000>t||storeTime%10000<t){
                    completionCode();
                }
                String people=part[2].replace(":","");
                int p=Integer.parseInt(people);
                if(this.ReserveTimeandPeople.containsKey(part[0])){//이미 있으면, 업데이트
                    var temp=ReserveTimeandPeople.get(part[0]);
                    temp.add(t);
                    temp.add(p);
                    ReserveTimeandPeople.put(part[0],temp);
                }else {
                    ArrayList<Integer> arr=new ArrayList();
                    arr.add(t);
                    arr.add(p);
                    this.ReserveTimeandPeople.put(part[0], arr);//매장명-예약시간과 인원 추가
                }
            }
        }
        return true;
    }

    private boolean isValidReserve(int count, Scanner sc) {
        //[매장 이름]+[\t]+[ID]+[\t]+[날짜]+[\t]+[시간]+[\t]+[예약 인원]+[\n]
        Set<String> set = new HashSet<>();
        while (sc.hasNextLine()) {
            String line = sc.nextLine();
            String[] part = line.split("\t");
            if (part.length != count) {//갯수만큼 없는 경우
                completionCode();
            } else {//갯수만큼 있는 경우
                isValidStringStore(0,part[0]);
                isValidString(0,part[1]);
                isValidDate(0,part[2]);
                isValidTime(0,part[3]);
                isValidInt(0,part[4]);
                isVaildStoreNameandStoreId(part[0], part[1]);
                String time=part[3].replace(":","");
                int t=Integer.parseInt(time);
                int storeTime=this.StoreTime.get(part[0]);
                if(storeTime/10000>t||storeTime%10000<t){
                    completionCode();
                }//3이 시간 4가 인원
                var temparr=this.ReserveTimeandPeople.get(part[0]);
                int temp=-1;
                for(int i=0;i<temparr.size();i+=2){//시간 확인, 시간은 1000 1230 이렇게 저장
                    if(temparr.get(i)==t){
                        temp=i;
                    }
                }if(temp==-1){
                    completionCode();
                }//temp가 예약 날짜이므로, 그 날짜의 최대인원보다 적은지 확인
                if(temparr.get(temp+1)<Integer.parseInt(part[4])){
                    completionCode();
                }
            }
        }
        return true;
    }

    private boolean isValidStringStore(int a, String s) {
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
    public boolean isValidString(int a, String s) {//'단어'조건에 맞는가 (띄어쓰기가 없이 이어진 문자열)
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
    public boolean isValidCustomerOwner(int a, String s){
        if (a == 0) {//무결성 검사파트
            if (s.equals("사장")||s.equals("고객")) {
                return true;
            }else completionCode();
        }else{
            return s.equals("사장") || s.equals("고객");
        }return true;
    }

    public boolean isValidDate(int a, String s) {//날짜 문자열이 조건에 맞는가 2024/04/28
        String regex = "^20(2[4-9]|[3-9][0-9]|2[1-9][0-9]{2}|[3-9][0-9]{3})/(0[1-9]|1[0-2])/(0[1-9]|[1-2][0-9]|3[0-1])$";//정규표현식
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
            }else return isLeapYear(s);//윤년조건 만족하지 않는 경우
        } return true;
    }

    public boolean isValidTime(int a, String s) {//시간 문자열이s 조건에 맞는가 10:00
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
    public boolean isValidInt(int a, String s){//예약인원(정수형)이 제대로 입력됬는가
        if (a == 0) {//무결성 검사파트
            if(!s.matches("^[1-9][0-9]*$")){
                completionCode();
            }else{
                try {
                    Integer.parseInt(s);
                }catch (Exception e){
                    completionCode();
                }return true;
            }
        }else{//입력이 정확한지 확인
            if(!s.matches("^[1-9][0-9]*$")){
                return false;
            }else{
                try{
                    Integer.parseInt(s);
                }catch(Exception e){
                    return false;
                }return true;
            }
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
            }switch (month) {
                case 4:
                case 6:
                case 9:
                case 11:
                    return day <= 30; // 4, 6, 9, 11월은 30일까지 있음
                default:
                    return day <= 31; // 나머지 달은 31일까지 있음
            }
        }
        else{//윤년이 아니라면
            if(month==2){
                return day <= 28;
            }
            switch (month) {
                case 4:
                case 6:
                case 9:
                case 11:
                    return day <= 30; // 4, 6, 9, 11월은 30일까지 있음
                default:
                    return day <= 31; // 나머지 달은 31일까지 있음
            }
        }
    }
    private void isDuplicate(Set set, String object) {
        if (!set.add(object)) {
            completionCode();
        }
    }
    private void isDuplicateStoreId(Set set, String object) {
        if (!set.add(object)) {
            completionCode();
        }if(this.IdSet.add(object)){
            completionCode();
        }
    }
    private void isDuplicateReserveManagement(Set set, String key, String value) {
        Map<String, String> map= new HashMap<>();
        map.put(key, value);
        if(!set.add(map)||!this.StoreSet.contains(key)){
            completionCode();
        }if(this.StoreSet.add(key)){
            completionCode();
        }
    }
    private void isVaildStoreNameandStoreId(String name, String id) {
        if (this.StoreSet.add(name)) {
            completionCode();
        }if(this.IdSet.add(id)){
            completionCode();
        }
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
