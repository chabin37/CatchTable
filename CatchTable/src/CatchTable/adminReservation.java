package CatchTable;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.time.LocalTime;
import java.util.*;

//실제 동작(읽고쓰기)(사장)
public class adminReservation {// 사장이 예약 관리
    database database;
    output output;
    Scanner scan = new Scanner(System.in);
    int flag;

    public adminReservation(database database) {
        this.database = database;
    }

    public void storeJoin(String ID) {// 매장 등록 수정 4/13 1:52
        String storeName, openTime, closeTime;

        try { // 파일 내 Scanner위치 초기화
            database.store = new Scanner(new File("store.txt"));
            while (database.store.hasNextLine()) {// 매장 등록 여부 확인
                String line = database.store.nextLine();
                String[] part = line.split("\t");
                // System.out.println(line);//출력확인
                if (part[1].equals(ID)) {
                    System.out.println("이미 등록된 매장이 있습니다.");
                    return;
                }
            }
        } catch (Exception e) {
            System.out.println("데이터베이스에 문제가 있습니다. 프로그램을 종료합니다.");
            System.exit(0);
        } finally {
            database.store.close();
        }

        System.out.println("\n[매장 등록]");
        System.out.println("새로운 매장 등록을 위해 매장 정보를 입력해주세요.");

        // 매장 중복 여부 확인
        Loop1:while(true) {
            System.out.print("매장 이름: ");
            storeName = scan.nextLine();
            storeName = storeName.trim();
            storeName = storeName.replaceAll("\\s", "");
            if (storeName.isEmpty()) {
                System.out.println("[오류] 매장 이름을 입력하세요.");
                continue Loop1;
            }

            try { // 파일 내 Scanner위치 초기화
                database.store = new Scanner(new File("store.txt"));
                while (database.store.hasNextLine()) {// 매장이름 중복 확인
                    String line = database.store.nextLine();
                    String[] part = line.split("\t");
                    // System.out.println(line);//출력확인
                    if(part[0].equals(storeName)) {
                        System.out.println("[오류] 이미 사용 중인 매장 이름입니다. 다른 이름을 사용해주세요.");
                        database.store.close();
                        continue Loop1;
                    }

                }
                database.store.close();
                break;
            } catch (Exception e) {
                System.out.println("데이터베이스에 문제가 있습니다. 프로그램을 종료합니다.");
                System.exit(0);
            }
        }


        System.out.println("사용 가능한 매장 이름입니다.");

        // 영업 시간 등록
        System.out.print("영업 시작 시간 (예: 10:00): ");
        openTime = scan.nextLine();
        openTime = openTime.replaceAll("\\s+", "");

        while (database.isValidTime(1, openTime)==false) {
            System.out.println("올바른 시간 형식이 아닙니다. 다시 입력해주세요.");
            System.out.print("영업 시작 시간 (예: 10:00): ");
            openTime = scan.nextLine();
        }

        System.out.print("영업 마감 시간 (예: 18:00): ");
        closeTime = scan.nextLine();
        closeTime = closeTime.replaceAll("\\s+", "");

        while (database.isValidTime(1, closeTime)==false) {
            System.out.println("올바른 시간 형식이 아닙니다. 다시 입력해주세요.");
            System.out.print("영업 마감 시간 (예: 18:00): ");
            closeTime = scan.nextLine();
        }

        System.out.print("해당 매장을 등록하시겠습니까? (Yes/No): ");
        String check = scan.nextLine();
        if (check.equals("No")) {
            System.out.println("등록이 취소되었습니다.");
            return;
        }

        // 매장 정보 파일에 해당 매장 쓰기
        try {
            database.storeWrite = new PrintWriter(new FileWriter("store.txt", true));
            database.storeWrite.println(storeName + "\t" + ID + "\t" + openTime + "\t" + closeTime);
        } catch (Exception e) {
            System.out.println("데이터베이스에 문제가 있습니다. 프로그램을 종료합니다.");
            System.exit(0);
        } finally {
            database.storeWrite.close();
        }

        System.out.println("\n등록되었습니다.");
    }




    public void reserveManAdmin(String ID) {// 예약 관리

        String storeName="";
        String openTime="";
        String closeTime="";

        // 매장 등록 여부 확인
        boolean IDflag=false; //해당 ID로 가게가 존재하는지 확인하는 플래그
        // 파일에서 매장 정보 가져오기
        try { // 파일 내 Scanner위치 초기화
            database.store = new Scanner(new File("store.txt"));
            while (database.store.hasNextLine()) {// 매장 등록 여부 확인
                String line = database.store.nextLine();
                String[] part = line.split("\t");
                // System.out.println(line);//출력확인
                if (part[1].equals(ID)) {
                    IDflag=true;
                    storeName=part[0];
                    openTime=part[2];
                    closeTime=part[3];
                    break;
                }
            }
            if(!IDflag) {
                System.out.println("등록된 매장이 없습니다. 매장을 먼저 등록하고 이용해주세요.");
                return;
            }
        } catch (Exception e) {
            System.out.println("데이터베이스에 문제가 있습니다. 프로그램을 종료합니다.");
            System.exit(0);
        } finally {
            database.store.close();
        }

        // 파일에서 예약 정보 가져오기


        // (출력,메뉴 1.예약 시간 및 인원 등록,2.예약 삭제,1과2 이외 return)반복-흐름도 확인
        while (true) {
            int manageNum=0;
            List<String> reserveData=new ArrayList<>();

            Map<String, Map<Integer, Integer>> currentTables = new HashMap<>();
            Map<String, Integer> peopleLeft = new HashMap<>();
            String select;
            try { // 파일 내 Scanner위치 초기화
                database.reserveManagement = new Scanner(new File("reserveManagement.txt"));
                while(database.reserveManagement.hasNextLine()) {
                    String line = database.reserveManagement.nextLine();
                    String[] parts = line.split("\t");

                    if(parts[0].equals(storeName)) {
                        int sumMax = 0;
                        String[] tables = parts[2].split(" ");

                        Map<Integer, Integer> currentTable = new HashMap<>();

                        for(int j = 0; j < tables.length; j += 2) {
                            int maxTable = Integer.parseInt(tables[j]);
                            int tableCnt = Integer.parseInt(tables[j + 1]);
                            currentTable.put(maxTable, tableCnt);
                            sumMax += maxTable * tableCnt;
                        }
                        currentTables.put(parts[1], currentTable);
                        peopleLeft.put(parts[1], sumMax);
                    }
                }
            } catch (Exception e) {
                System.out.println("데이터베이스에 문제가 있습니다. 프로그램을 종료합니다.");
                System.exit(0);
            } finally {
                database.reserveManagement.close();
            }

            // 출력
            System.out.println("[예약 관리]");
            System.out.println("매장 이름: "+storeName);
            System.out.println("영업 시간: "+openTime+" ~ "+closeTime);
            System.out.println();
            if(currentTables.isEmpty()) {
                System.out.println("현재 설정된 예약이 없습니다.\n");
            } else {
                System.out.println("현재 설정된 예약 목록");
                System.out.println("----------------------------------------");
                for(String time : currentTables.keySet()) {
                    System.out.print(time + " | 최대 인원: " + peopleLeft.get(time) + " ");
                    String print = "";
                    for(int maxTable : currentTables.get(time).keySet()) {
                        print += (String.valueOf(maxTable) + "인석: " + String.valueOf(currentTables.get(time).get(maxTable)) + ", ");
                    }
                    System.out.println("(" + print.substring(0, print.length() - 2) + ")");
                }
                System.out.println("----------------------------------------");
            }
            System.out.println("[메뉴]");
            System.out.println("1. 예약 시간 및 인원 등록");
            System.out.println("2. 예약 삭제");
            System.out.println("3. 돌아가기");
            System.out.print("메뉴를 선택해 주세요: ");
            // 입력
            select = scan.nextLine();

            if (!select.matches("^[0-9]+$")) {
                System.out.println("[오류] 해당하는 번호가 없습니다.");
                return;
            }

            try {
                flag = Integer.parseInt(select);
            }catch(Exception e) {
                return;
            }

            LocalTime open= LocalTime.parse(openTime);
            LocalTime close=LocalTime.parse(closeTime);
            switch (flag) {
                case 1 -> {
                    System.out.println("[예약 등록]");
                    System.out.print("등록할 시간과 테이블 정보를 입력해주세요 (예: 09:00 2 4): ");
                    String input = scan.nextLine();

                    Map<String, Map<Integer, Integer>> inputTables = new HashMap<>();
                    String firstTime = "";

                    try {
                        String[] parts = input.split(" ");
                        firstTime = parts[0];
                        for (int i = 0; i < parts.length; ) {
                            String time = parts[i++];

                            if(!database.isValidTime(1, time)) {
                                throw new Exception();
                            }
                            Map<Integer, Integer> tableInfo = new HashMap<>();
                            while (i < parts.length && !parts[i].contains(":")) {
                                int maxPeople = Integer.parseInt(parts[i++]);
                                int tableCount = Integer.parseInt(parts[i++]);
                                tableInfo.put(maxPeople, tableCount);
                            }
                            inputTables.put(time, tableInfo);
                        }
                    } catch (Exception e) {
                        System.out.println("[오류] 올바르지 않은 형식입니다");
                        return;
                    }

                    // 저장된 자료 자체 무결성 검사

                    for(String time : inputTables.keySet()) {
//						if(peopleLeft.containsKey(time)) {
//							System.out.println("[오류] 이미 등록된 시간을 포함하고 있습니다.");
//							return;
//						}
//
//						LocalTime inputTime = LocalTime.parse(time);
//						if (!(open.isBefore(close) && (inputTime.equals(open) || inputTime.isAfter(open)) && (inputTime.equals(close) || inputTime.isBefore(close)) ||
//								open.isAfter(close) && ((inputTime.equals(open) || inputTime.isAfter(open)) && (inputTime.isBefore(LocalTime.MAX)) ||
//										(inputTime.equals(close) || inputTime.isBefore(close)) && (inputTime.isAfter(LocalTime.MIN)||inputTime.equals(LocalTime.MIN))))) {
//							System.out.println("[오류] 예약 시간은 영업 시간 사이에 있어야 합니다.");
//							return;
//						}

                        for(int maxTable : inputTables.get(time).keySet()) {
                            if (!database.isValidInt(1, String.valueOf(maxTable)) || !database.isValidInt(1, String.valueOf(inputTables.get(time).get(maxTable)))) {
                                System.out.println("[오류] 테이블 수용 인원수와 해당 테이블의 개수는 1이상의 정수여야 합니다.");
                                return;
                            }
                        }
                    }

                    System.out.print(firstTime + "에 ");
                    String print = "";
                    for (int maxTable : inputTables.get(firstTime).keySet()) {
                        print += (String.valueOf(maxTable) + "인석 " + String.valueOf(inputTables.get(firstTime).get(maxTable)) + "테이블/");
                    }
                    System.out.print(print.substring(0, print.length() - 1) + "을 등록하시겠습니까? (Yes/No): ");

                    input = scan.nextLine();
                    if (input.equals("No")) {
                        System.out.println("등록이 취소되었습니다.");
                    }
                    else {
                        // reserveManagement.txt 파일에 바뀐 테이블 정보 추가
                        try {
                            FileWriter fw = new FileWriter(new File("reserveManagement.txt"), true);
                            PrintWriter writer = new PrintWriter(fw);

                            for(String time : inputTables.keySet()) {
                                String modifiedData = "";

                                modifiedData = (storeName + "\t" + time + "\t");
                                for (int maxTable : inputTables.get(time).keySet()) {
                                    modifiedData += (String.valueOf(maxTable) + " " + String.valueOf(inputTables.get(time).get(maxTable)) + " ");
                                }
                                writer.println(modifiedData.substring(0, modifiedData.length() - 1));
                            }

                            writer.close();
                        }  catch (Exception e) {
                            System.out.println("데이터베이스에 문제가 있습니다. 프로그램을 종료합니다.");
                            System.exit(0);
                        }

                        System.out.println("등록되었습니다");
                        return;
                    }

                }
                case 2 -> {
                    System.out.println("[예약 삭제]\n");

                    if(currentTables.isEmpty()) {
                        System.out.println("삭제할 수 있는 예약이 없습니다.\n");
                        return;
                    } else {
                        List<String> reserveList = new ArrayList<>();
                        reserveList.add(""); // 편의상 인덱스 1번부터.

                        System.out.println("\n현재 설정된 예약 목록");
                        System.out.println("----------------------------------------");
                        for(String time : currentTables.keySet()) {
                            System.out.print(reserveList.size() + ". " + time + " | 최대 인원: " + peopleLeft.get(time) + " ");
                            reserveList.add(time);

                            String print = "";
                            for(int maxTable : currentTables.get(time).keySet()) {
                                print += (String.valueOf(maxTable) + "인석: " + String.valueOf(currentTables.get(time).get(maxTable)) + ", ");
                            }
                            System.out.println("(" + print.substring(0, print.length() - 2) + ")");
                        }
                        System.out.println("----------------------------------------");

                        System.out.print("삭제할 예약 시간의 번호를 입력해 주세요 : ");
                        int index;

                        try {
                            String input = scan.nextLine();
                            input = input.replaceAll("\\s+", "");
                            if (input.contains(".")) {
                                index = Integer.parseInt(input.substring(0, input.indexOf(".")));
                            } else {
                                index = Integer.parseInt(input);
                            }

                            if (!(index > 0 && index <= reserveList.size())) {
                                System.out.println("[오류] 존재하지 않는 항목입니다.");
                                return;
                            }
                        } catch (NumberFormatException e) {
                            System.out.println("[오류] 입력 형식이 올바르지 않습니다.");
                            return;
                        }

                        System.out.println();
                        System.out.print(reserveList.get(index) +"을 삭제하시겠습니까? (Yes/No): ");
                        String input=scan.nextLine();
                        if (input.equals("No")) {
                            System.out.println("삭제가 취소되었습니다.");
                            return;
                        } else {
                            String str = storeName+"\t"+reserveList.get(index)+"\t";
                            for (int maxTable : currentTables.get(reserveList.get(index)).keySet()) {
                                str += (String.valueOf(maxTable) + " " + String.valueOf(currentTables.get(reserveList.get(index)).get(maxTable)) + " ");
                            }
                            removeReserve(idxRm(str,"reserveManagement.txt"),"reserveManagement.txt");
                            System.out.println("예약삭제가 완료되었습니다.");
                            return;
                        }
                    }



                } // 예약 삭제(Index 입력 -검사 if(!문자열.matches("^[0-9]+$"),범위)
                default -> {
                    return;
                }
            }
        }
    }

    private int idxRm(String str, String fileName) {//삭제할 위치
        int n=-1;
        String search;
        try { // 파일 내 Scanner위치 초기화
            database.reserveManagement = new Scanner(new File(fileName));
            while(database.reserveManagement.hasNextLine()) {
                n++;
                search=database.reserveManagement.nextLine();
                if(search.equals(str))
                    break;
            }

        } catch (Exception e) {
            System.out.println("데이터베이스에 문제가 있습니다. 프로그램을 종료합니다.");
            System.exit(0);
        } finally {
            database.reserveManagement.close();
        }
        return n;
    }

    private void removeReserve(int c, String fileName) {
        List<String> temp = new ArrayList<>();

        try { // 파일 내 Scanner위치 초기화
            database.reserveManagement = new Scanner(new File(fileName));
            while(database.reserveManagement.hasNextLine()) {
                temp.add(database.reserveManagement.nextLine());
            }
        } catch (Exception e) {
            System.out.println("데이터베이스에 문제가 있습니다. 프로그램을 종료합니다.");
            System.exit(0);
        } finally {
            database.reserveManagement.close();
        }

        temp.remove(c);

        try {
            database.reserveManagementWrite = new PrintWriter(new FileWriter(fileName, false));
            for(int i=0;i<temp.size();i++)
                database.reserveManagementWrite.println(temp.get(i));
        } catch (Exception e) {
            System.out.println("데이터베이스에 문제가 있습니다. 프로그램을 종료합니다.");
            System.exit(0);
        } finally {
            database.reserveManagementWrite.close();
        }
    }



    public void reserveNow(String ID) {// 예약 현황
        String storeName="";
        int reserveNum=0;
        List<String> reserveData=new ArrayList<>();
        // 매장 등록 여부 확인
        boolean IDflag=false; //해당 ID로 가게가 존재하는지 확인하는 플래그
        // 파일에서 매장 정보 가져오기
        try { // 파일 내 Scanner위치 초기화
            database.store = new Scanner(new File("store.txt"));
            while (database.store.hasNextLine()) {// 매장 등록 여부 확인
                String line = database.store.nextLine();
                String[] part = line.split("\t");
                // System.out.println(line);//출력확인
                if (part[1].equals(ID)) {
                    IDflag=true;
                    storeName=part[0];
                    break;
                }
            }
            if(!IDflag) {
                System.out.println("등록된 매장이 없습니다. 매장을 먼저 등록하고 이용해주세요.");
                return;
            }
        } catch (Exception e) {
            System.out.println("데이터베이스에 문제가 있습니다. 프로그램을 종료합니다.");
            System.exit(0);
        } finally {
            database.store.close();
        }

        // 파일에서 예약 현황 가져오기
        List<String> listID = new ArrayList<>();
        List<String> listDate = new ArrayList<>();
        List<Integer> listPeople = new ArrayList<>();
        listID.add("");
        listDate.add("");
        listPeople.add(0);

        try { // 파일 내 Scanner위치 초기화
            database.reserveManagement = new Scanner(new File("reserve.txt"));
            while(database.reserveManagement.hasNextLine()) {
                String line = database.reserveManagement.nextLine();
                String[] parts = line.split("\t");

                if(parts[0].equals(storeName)) {
                    int sumMax = 0;
                    String[] tables = parts[4].split(" ");

                    for(int j = 0; j < tables.length; j += 2) {
                        int maxTable = Integer.parseInt(tables[j]);
                        int tableCnt = Integer.parseInt(tables[j + 1]);
                        sumMax += maxTable * tableCnt;
                    }
                    listID.add(parts[1]);
                    listDate.add(parts[2] + " " + parts[3]);
                    listPeople.add(sumMax);
                }
            }
        } catch (Exception e) {
            System.out.println("데이터베이스에 문제가 있습니다. 프로그램을 종료합니다.");
            System.exit(0);
        } finally {
            database.reserveManagement.close();
        }
        // 예약 현황 출력
        System.out.println("[예약관리]");

        System.out.println();
        System.out.println("예약 매장");
        System.out.println("----------------------------------------");
        System.out.println("["+storeName+"]");
        for(int i=1; i < listID.size(); i++) {
            System.out.println(i + ". " + listID.get(i) + " " + listDate.get(i) + " " + listPeople.get(i));
        }
        System.out.println("----------------------------------------");

        // 메뉴(1.예약허가 , 1이외 return)
        while (true) {
            String select;
            // 출력
            System.out.println("");
            System.out.println("1. 예약 허가");
            System.out.println("2. 노쇼 등록");
            System.out.println("3. 돌아가기");
            System.out.print("원하는 메뉴를 선택해 주세요:");
            // 입력
            select = scan.nextLine();
            try {
                flag = Integer.parseInt(select);
            }catch(Exception e) {
                return;
            }
            switch (flag) {
                case 1 -> {
                    System.out.print("예약 허가할 번호를 입력하세요:");
                    select = scan.nextLine();

                    if (!select.matches("^[0-9]+$")) {
                        System.out.println("[오류] 해당하는 번호가 없습니다.");
                        return;
                    }
                    int input;
                    try {
                        input=Integer.parseInt(select);
                    }catch(Exception e) {
                        System.out.println("[오류] 해당하는 번호가 없습니다.");
                        return;
                    }

                    if(input < 1 || input>=listID.size()) {
                        System.out.println("[오류] 해당하는 번호가 없습니다.");
                        return;
                    }
                    System.out.print(input+"번을 예약허가 하시겠습니까?(Yes/No):"); // 예약 매장 목록에 있는 항목 번호 입력한 경우
                    select = scan.nextLine();
                    if (select.equals("No")) {
                        System.out.println("예약 허가가 취소되었습니다.");
                        return;
                    } else {
                        String str=storeName+"\t"+reserveData.get((input-1)*4)+"\t"+reserveData.get((input-1)*4+1)+"\t"+reserveData.get((input-1)*4+2)+"\t"+reserveData.get((input-1)*4+3);
                        removeReserve(idxRm(str,"reserve.txt"),"reserve.txt");
                        System.out.println("예약 허가되었습니다.");
                        return;
                    }


                } // 예약허가(Index 입력 -검사 if(!문자열.matches("^[0-9]+$"),범위)

                case 2 -> {
                    System.out.print("노쇼 등록할 번호를 입력하세요: ");

                    String inputNum = scan.nextLine();

                    if (!inputNum.matches("^[1-9]+$")) { // 숫자가 입력되지 않았을 때
                        System.out.println("[오류] 올바르지 않은 입력입니다.");
                        return;
                    }

                    int index = Integer.parseInt(inputNum);

                    if(index < 1 && index >= listID.size()) {
                        System.out.println("[오류] 해당하는 번호가 없습니다.");
                        return;
                    }

                    System.out.print(index + "번을 노쇼 등록 하시겠습니까?(Yes/No): ");
                    select = scan.nextLine();
                    if (select.equals("No")) {
                        System.out.println("노쇼 등록이 취소 되었습니다.");
                        return;
                    } else {
                        try {
                            String data = "";
                            String userName = listID.get(index);

                            database.store = new Scanner(new File("store.txt"));
                            while(database.store.hasNextLine()) {
                                String line = database.store.nextLine();
                                String[] parts = line.split("\t");
                                if(parts[0].equals(storeName)) {
                                    if(parts.length == 5) { // 이미 노쇼 고객정보가 있을 경우
                                        data += (String.join("\t", parts[0], parts[1], parts[2], parts[3]) + "\t");
                                        String[] noShowInfo = parts[4].split(" ");

                                        boolean isUserExist = false;
                                        for(int i = 0; i < noShowInfo.length; i += 2) {
                                            if(noShowInfo[i].equals(userName)) {
                                                isUserExist = true;
                                                int noShowCnt = Integer.parseInt(noShowInfo[i + 1]);
                                                noShowCnt++;
                                                noShowInfo[i + 1] = String.valueOf(noShowCnt);
                                            }

                                            data += (noShowInfo[i] + " " + noShowInfo[i + 1] + " ");
                                        }

                                        if(!isUserExist) { // 노쇼고객 정보는 있는데 유저가 없음.
                                            data += (userName + " 1 ");
                                        }
                                        data = data.substring(0, data.length() - 1) + "\n";

                                    } else { // 노쇼 고객 정보가 없다면!
                                        String userPart = listID.get(index) + " 1";
                                        data += (String.join("\t", parts[0], parts[1], parts[2], parts[3]) + "\t" + userPart + "\n");
                                    }
                                } else {
                                    data += (line + "\n");
                                }

                                database.storeWrite = new PrintWriter(new FileWriter("store.txt"));
                                database.storeWrite.write(data);

                            }
                        } catch (Exception e) {
                            System.out.println("데이터베이스에 문제가 있습니다. 프로그램을 종료합니다.");
                            System.exit(0);
                        } finally {
                            database.store.close();
                            database.storeWrite.close();
                        }
                    }

                    System.out.println("노쇼 등록 되었습니다.");
                    return;
                }
                default -> {
                    return;
                }
            }
        }
    }

}