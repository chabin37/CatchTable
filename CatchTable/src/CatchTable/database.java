package CatchTable;

import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class database {
    //텍스트 파일 읽기
    Scanner account;
    Scanner store;
    Scanner reserveManagement;
    Scanner reserve;
    Scanner waiting;
    //텍스트 파일 쓰기
    PrintWriter accountWrite;
    PrintWriter storeWrite;
    PrintWriter reserveManagementWrite;
    PrintWriter reserveWrite;
    PrintWriter waitingWrite;

    Set<String> IdSet = new HashSet<>();
    Set<String> StoreSet = new HashSet<>();
    Map<String, Integer> StoreTime = new HashMap<>();
    Map<String, Map<Integer, ArrayList<Integer>>> ReserveTimeandPeople = new HashMap<>();
    //매장명 - 시간, [테이블최대인원, 테이블수, 테이블최대인원, 테이블수], 시간, [테이블최대인원, 테이블수...
    Map<String, String> IdOwnerCustomer = new HashMap<>();//ID, 사장고객
    Map<String, Map<String, Map<String, ArrayList<Integer>>>> StoreReserveDateTime = new HashMap<>();

    public database() {
        try {
            accountWrite = new PrintWriter(new OutputStreamWriter(new FileOutputStream("account.txt", true), "UTF-8"));
            storeWrite = new PrintWriter(new OutputStreamWriter(new FileOutputStream("store.txt", true), "UTF-8"));
            reserveManagementWrite = new PrintWriter(new OutputStreamWriter(new FileOutputStream("reserveManagement.txt", true), "UTF-8"));
            reserveWrite = new PrintWriter(new OutputStreamWriter(new FileOutputStream("reserve.txt", true), "UTF-8"));
            waitingWrite = new PrintWriter(new OutputStreamWriter(new FileOutputStream("waiting.txt", true), "UTF-8"));
            // 필요한 쓰기 작업을 수행합니다.

            // 파일 읽기 작업을 UTF-8 인코딩으로 설정
            account = new Scanner(new InputStreamReader(new FileInputStream("account.txt"), "UTF-8"));
            store = new Scanner(new InputStreamReader(new FileInputStream("store.txt"), "UTF-8"));
            reserveManagement = new Scanner(new InputStreamReader(new FileInputStream("reserveManagement.txt"), "UTF-8"));
            reserve = new Scanner(new InputStreamReader(new FileInputStream("reserve.txt"), "UTF-8"));
            waiting = new Scanner(new InputStreamReader(new FileInputStream("waiting.txt"), "UTF-8"));
        } catch (Exception e) {
            System.out.println("데이터베이스에 문제가 있습니다. 프로그램을 종료합니다.");
            System.exit(0);
        }
        //account, store, reserveManagement, reserve 순으로 입출력 전체 관리3435
        isValidAccount(3, account);
        isValidStore(store);
        isValidReserveManagement(reserveManagement);
        isValidReserve(reserve);
        isValidWaiting(waiting);
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
                isValidString(0, part[0]);
                isValidString(0, part[1]);
                isValidCustomerOwner(0, part[2]);
                isDuplicate(IdSet, part[0]);
                this.IdSet.add(part[0]);
                this.IdOwnerCustomer.put(part[0], part[2]);
            }
        }
        return true;
    }

    private boolean isValidStore(Scanner sc) {
        //[매장 이름]+[\t]+[ID]+[\t]+[영업 시작 시간]+[\t]+[영업 종료 시간]+[\t]+[노쇼 고객 id]+[␣]+[노쇼 횟수]+…[\n]
        Set<String> set = new HashSet<>();
        while (sc.hasNextLine()) {
            String line = sc.nextLine();
            String[] part = line.split("\t");
            if (part.length <= 3) {//최소갯수만큼 없는경우
                completionCode();
            } else {//갯수만큼 있는 경우
                isValidStringStore(0, part[0]);
                isValidString(0, part[1]);
                isValidTime(0, part[2]);
                isValidTime(0, part[3]);
                isDuplicate(StoreSet, part[0]);
                isValidSet(IdSet, part[1]);
                isDuplicateStoreId(set, part[1]);
                String sT = part[2].replace(":", "");
                String eT = part[3].replace(":", "");
                int StartTime = Integer.parseInt(sT);
                int EndTime = Integer.parseInt(eT);
                this.StoreTime.put(part[0], StartTime * 10000 + EndTime);//가게이름,10001800 으로저장
                if (!this.IdOwnerCustomer.get(part[1]).equals("사장")) completionCode();
                //노쇼 id 고객 무결성검사, 노쇼가 1명 이상 존재 [노쇼 고객 id]+[␣]+[노쇼 횟수]+[␣]...가 됨
                if (part.length >= 5) {
                    String ns = part[4];
                    String[] noShow = ns.split(" ");
                    if (noShow.length % 2 != 0) {
                        completionCode();
                    } else {
                        for (int i = 0; i < noShow.length / 2; i++) {
                            isValidSet(IdSet,noShow[i*2]);
                            isValidInt(0, noShow[i * 2 + 1]);
                        }
                    }
                }
            }
        }
        return true;
    }

    private boolean isValidReserveManagement(Scanner sc) {
        //[매장 이름]+[\t]+[시간]+[\t]+[테이블 최대인원]+[␣]+[테이블 수]+[␣]...+[\n]
        Set<Map> set = new HashSet<>();
        while (sc.hasNextLine()) {
            String line = sc.nextLine();
            String[] part = line.split("\t");
            if (part.length <= 2) {//갯수만큼 없는 경우
                completionCode();
            } else {//갯수만큼 있는 경우
                isValidStringStore(0, part[0]);
                isValidTime(0, part[1]);
                isDuplicateReserveManagement(set, part[0], part[1]);
                String time = part[1].replace(":", "");
                int t = Integer.parseInt(time);
                int storeTime = this.StoreTime.get(part[0]);
                if ((storeTime / 10000) < (storeTime % 10000)) {
                    if (storeTime / 10000 > t || storeTime % 10000 < t) {
                        completionCode();
                    }
                } else {
                    if (storeTime / 10000 > t && storeTime % 10000 < t) { // 2200    0100    0300   2100
                        completionCode();
                    }
                }//테이블 무결성검사, [테이블 최대인원]+[␣]+[테이블 수]+[␣]...
                String p = part[2];
                String[] s = p.split(" ");
                for (String tp : s) {
                    isValidInt(0, tp);
                }
                if (s.length % 2 != 0) completionCode();
                //테이블 인원이랑 수를 정확히 그대로 입력함. 5 3 2 1
                if (this.ReserveTimeandPeople.containsKey(part[0])) {//매장이 이미 있으면, 업데이트
                    var temp = ReserveTimeandPeople.get(part[0]);//시간, 테이블배열 map
                    ArrayList<Integer> tt = new ArrayList<>();
                    for (String a : s) {
                        int t2 = Integer.parseInt(a);
                        tt.add(t2);
                    }
                    temp.put(t, tt);
                    this.ReserveTimeandPeople.put(part[0], temp);
                } else {//매장이 없으면, 추가
                    Map<Integer, ArrayList<Integer>> tm = new HashMap<>();
                    ArrayList<Integer> arr = new ArrayList<>();
                    for (String a : s) {
                        int t2 = Integer.parseInt(a);
                        arr.add(t2);
                    }
                    tm.put(t, arr);
                    this.ReserveTimeandPeople.put(part[0], tm);//매장명-예약시간과 테이블 쌍 추가, [홍길동가게:[1400:[5,2,3,3,2,3]]]
                }
            }
        }
        return true;
    }

    private boolean isValidReserve(Scanner sc) {
        //[매장 이름]+[\t]+[ID]+[\t]+[날짜]+[\t]+[시간]+[\t]+[테이블 최대인원]+[␣]+[테이블 수]+[␣]...+[\n]
        Set<String> set = new HashSet<>();
        while (sc.hasNextLine()) {
            String line = sc.nextLine();
            String[] part = line.split("\t");
            if (part.length <= 3) {//갯수만큼 없는 경우
                completionCode();
            } else {//갯수만큼 있는 경우
                isValidStringStore(0, part[0]);
                isValidString(0, part[1]);
                isValidDate(0, part[2]);
                isValidTime(0, part[3]);
                isValidStoreNameandStoreId(part[0], part[1]);//매장이름과 아이디 무결성검사
                String time = part[3].replace(":", "");
                int t = Integer.parseInt(time);
                int storeTime = this.StoreTime.get(part[0]);
                if ((storeTime / 10000) < (storeTime % 10000)) {
                    if (storeTime / 10000 > t || storeTime % 10000 < t) {
                        completionCode();
                    }
                } else {
                    if (storeTime / 10000 > t && storeTime % 10000 < t) {
                        completionCode();
                    }
                }
                //테이블 무결성검사, [테이블 최대인원]+[␣]+[테이블 수]+[␣]...
                String tb = part[4];
                String[] s = tb.split(" ");//테이블인원,테이블수,테이블인원,테이블수...저장된 문자열 리스트
                for (String tp : s) {
                    isValidInt(0, tp);
                }
                int[] ita = Arrays.stream(s).mapToInt(Integer::parseInt).toArray();
                ArrayList<Integer> inta = new ArrayList<>(Arrays.stream(ita).boxed().collect(Collectors.toList()));
                if (s.length % 2 != 0) completionCode();
                var temparr = this.ReserveTimeandPeople.get(part[0]);
                int q = 0;
                for (Integer a : temparr.keySet()) {//시간대 존재여부 무결성검사(예약한 시간이 가계예약에 존재하는지여부)
                    if (a == t) q++;//시간대가 존재하면, q를 증가
                }
                if (q == 0) completionCode();//시간대가 없으면 종료
                if (this.IdOwnerCustomer.get(part[1]).equals("고객")) {//map 구조 : {매장이름:{날짜:{시간:테이블최대인원, 테이블수...}}}
                    if (this.StoreReserveDateTime.containsKey(part[0])) {
                        if (this.StoreReserveDateTime.get(part[0]).containsKey(part[2])) {
                            if (this.StoreReserveDateTime.get(part[0]).get(part[2]).containsKey(part[3])) {//만일 해당시간대 예약이 이미 존재한다면
                                ArrayList<Integer> temp = this.StoreReserveDateTime.get(part[0]).get(part[2]).get(part[3]);
                                int si = temp.size();
                                for (int i = 0; i < si / 2; i++) {
                                    for (int j = 0; j < inta.size() / 2; j++) {//추가하고, inta에서 추가한부분은 지워야함
                                        if (temp.get(i * 2) == inta.get(j * 2)) {//s의 테이블최대인원이 temp에 존재한다면
                                            temp.set(i * 2 + 1, temp.get(i * 2 + 1) + inta.get(j * 2 + 1));//그매장 그날 그시간 테이블 수 최신화
                                        } else {//존재하지 않는다면
                                            temp.add(inta.get(j * 2));//for문 i 순회횟수증가
                                            temp.add(inta.get(j * 2 + 1));//for문 i 순회횟수증가
                                        }
                                    }
                                }
                                this.StoreReserveDateTime.get(part[0]).get(part[2]).put(part[3], temp);
                            } else
                                this.StoreReserveDateTime.get(part[0]).get(part[2]).put(part[3], inta);
                        } else {
                            Map<String, ArrayList<Integer>> tempmap1 = new HashMap<>();
                            tempmap1.put(part[3], inta);
                            this.StoreReserveDateTime.get(part[0]).put(part[2], tempmap1);
                        }
                    } else {
                        Map<String, Map<String, ArrayList<Integer>>> tempmap1 = new HashMap<>();
                        Map<String, ArrayList<Integer>> tempmap2 = new HashMap<>();
                        tempmap2.put(part[3], inta);
                        tempmap1.put(part[2], tempmap2);
                        this.StoreReserveDateTime.put(part[0], tempmap1);
                    }
                } else completionCode();
            }


        }//해당 매장의 매장이름으로 예약 시간에 맞는 인원대가 설정됬는지 확인
        for (Map.Entry<String, Map<String, Map<String, ArrayList<Integer>>>> storeEntry : StoreReserveDateTime.entrySet()) {
            String storeName = storeEntry.getKey();//매장명
            Map<String, Map<String, ArrayList<Integer>>> dates = storeEntry.getValue();

            for (Map.Entry<String, Map<String, ArrayList<Integer>>> dateEntry : dates.entrySet()) {
                Map<String, ArrayList<Integer>> times = dateEntry.getValue();

                for (Map.Entry<String, ArrayList<Integer>> timeEntry : times.entrySet()) {//시간:테이블인원테이블수..인 map
                    String time = timeEntry.getKey();//시간
                    Map<Integer, ArrayList<Integer>> timeTableMap = this.ReserveTimeandPeople.get(storeName);//매장이름으로 [시간:[테이블인원,테이블수..]]map가져오기
                    int timeint = Integer.parseInt(time.replace(":", ""));
                    if (timeTableMap.containsKey(timeint)) {//시간대가내부에 존재한다면(존재안하면 데이터베이스오류) 테이블수가 최대 가용 가능한 테이블수를 안넘는지 확인
                        ArrayList<Integer> statictablenum = timeTableMap.get(timeint);//tablenum과 timeEntry의 value랑 비교
                        ArrayList<Integer> tablenum = timeEntry.getValue();//[테이블인원,테이블수..]
                        int staticLength = statictablenum.size();
                        int tLength = tablenum.size();
                        for (int i = 0; i < staticLength / 2; i++) {
                            for (int j = 0; j < tLength / 2; j++) {
                                if (statictablenum.get(i * 2) == tablenum.get(j * 2)) {//테이블인원이 같은경우, 테이블 수 확인
                                    if (statictablenum.get(i * 2 + 1) < tablenum.get(j * 2 + 1)) {
                                        completionCode();
                                    }
                                }
                            }
                        }
                    } else completionCode();
                }
            }
        }
        return true;
    }

    private void isValidWaiting(Scanner sc) {
        //[매장 이름]+[\t]+[ID]+[\t]+[순서]+[\t]+[웨이팅 인원]+[\n]
        Map<String,Integer> temp = new HashMap<>();
        while (sc.hasNextLine()) {
            String line = sc.nextLine();
            String[] part = line.split("\t");
            if (part.length <= 3) {//최소갯수만큼 없는경우
                completionCode();
            } else {//갯수만큼 있는 경우
                isValidStringStore(0, part[0]);
                isValidString(0, part[1]);
                isValidInt(0,part[2]);
                isValidInt(0,part[3]);
                if(Integer.parseInt(part[3])>1000)completionCode();//1000명이상이면 X
                int a=0;
                if(temp.containsKey(part[0])){
                    a=temp.get(part[0])+1;
                }else{
                    a=1;
                }
                if(Integer.parseInt(part[2]) != a)completionCode();//순서무결성검사
                isValidStoreNameandStoreId(part[0],part[1]);
                temp.put(part[0],a);
            }

        }
    }


    private boolean isValidStringStore(int a, String s) {
        if (a == 0) {//무결성 검사파트
            if (!s.contains(" ")) {
                return true;
            } else {
                completionCode();
            }
        } else {//입력이 정확한지 확인
            return !s.contains(" ") && s.length() <= 15;
        }
        return false;
    }

    //int a=0인 경우 데이터 무결성 검사용, int a!=0인 경우 입력받는 데 사용.
    public boolean isValidString(int a, String s) {//'단어'조건에 맞는가 (띄어쓰기가 없이 이어진 문자열)
        if (a == 0) {//무결성 검사파트
            if (!s.contains(" ")) {
                if (s.length() <= 15 && s.matches("^[a-zA-Z0-9\\p{Punct}]+$")) {
                    return true;
                } else completionCode();
            } else return true;
        } else {//입력이 정확한지 확인
            return !s.contains(" ") && s.length() <= 15 && s.matches("^[a-zA-Z0-9\\p{Punct}]+$");
        }
        return true;
    }

    public boolean isValidCustomerOwner(int a, String s) {
        if (a == 0) {//무결성 검사파트
            if (s.equals("사장") || s.equals("고객")) {
                return true;
            } else completionCode();
        } else {
            return s.equals("사장") || s.equals("고객");
        }
        return true;
    }

    public boolean isValidDate(int a, String s) {//날짜 문자열이 조건에 맞는가 2024/04/28
        String regex = "^20(2[4-9]|[3-9][0-9]|2[1-9][0-9]{2}|[3-9][0-9]{3})/(0[1-9]|1[0-2])/(0[1-9]|[1-2][0-9]|3[0-1])$";//정규표현식
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(s);
        if (a == 0) {//무결성 검사파트
            if (!matcher.matches()) {
                completionCode();
            } else {
                if (!isLeapYear(s)) {//윤년조건 만족하지 않는 경우
                    completionCode();
                } else return true;
            }
        } else {//입력이 정확한지 확인
            if (!matcher.matches()) {
                return false;
            } else return isLeapYear(s);//윤년조건 만족하지 않는 경우
        }
        return true;
    }

    public boolean isValidTime(int a, String s) {//시간 문자열이s 조건에 맞는가 10:00
        String regex = "^([01]{1}[0-9]|2[0-3]):[0-5][0-9]$";//정규표현식
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

    public boolean isValidInt(int a, String s) {//정수형이 제대로 입력됬는가
        if (a == 0) {//무결성 검사파트
            if (!s.matches("^[1-9][0-9]*$")) {
                completionCode();
            } else {
                try {
                    Integer.parseInt(s);
                } catch (Exception e) {
                    completionCode();
                }
                return true;
            }
        } else {//입력이 정확한지 확인
            if (!s.matches("^[1-9][0-9]*$")) {
                return false;
            } else {
                try {
                    Integer.parseInt(s);
                } catch (Exception e) {
                    return false;
                }
                return true;
            }
        }
        return true;
    }

    private boolean isLeapYear(String s) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd");
        LocalDate date = LocalDate.parse(s, formatter);//자동으로 day값이 보정되므로, 연도로 isLeapYear만 하는걸로 함.
        String daySt = s.substring(8, 10);
        int month = date.getMonthValue();
        int day = Integer.parseInt(daySt);
        if (date.isLeapYear()) {//윤년이라면
            if (month == 2) {//2월이라면
                return day <= 29;
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
        } else {//윤년이 아니라면
            if (month == 2) {
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

    private void isValidSet(Set set,String s) {
        if (!set.contains(s)) {
            completionCode();
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
        }
        if (this.IdSet.add(object)) {
            completionCode();
        }
    }

    private void isDuplicateReserveManagement(Set set, String key, String value) {
        Map<String, String> map = new HashMap<>();
        map.put(key, value);
        if (!set.add(map) || !this.StoreSet.contains(key)) {
            completionCode();
        }
        if (this.StoreSet.add(key)) {
            completionCode();
        }
    }

    private void isValidStoreNameandStoreId(String name, String id) {
        if (this.StoreSet.add(name)) {
            completionCode();
        }
        if (this.IdSet.add(id)) {
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
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        System.exit(0);
    }
}
