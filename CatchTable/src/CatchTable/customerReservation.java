package CatchTable;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.*;
import java.util.stream.Collectors;

public class customerReservation {
    database database;
    Scanner scan = new Scanner(System.in);
    int flag;

    public customerReservation(database database) {
        this.database = database;
    }


    //1.예약하기
    public void reserve(String ID) {
        // 중복 제거를 위한 List
        List <String> temp = new ArrayList<>();
        int i=0; //1.김길동 돈까스, 2... 출력할때의 index temp.size()로 대체
        try {
            // 파일에서 예약 정보 가져오기
            database.reserveManagement = new Scanner(new File("reserveManagement.txt"));
            //파일에서 노쇼횟수 불러오기
            database.store = new Scanner(new File("store.txt"));

            // 매장 정보 출력 (예약 가능한 매장)
            System.out.println("[예약하기]");
            System.out.println();
            System.out.println("현재 예약 가능 매장");
            System.out.println("-".repeat(40));

            Loop:while (database.reserveManagement.hasNextLine()) {
                String str[]=database.reserveManagement.nextLine().split("\t");
                String StoreName=str[0];
                if(temp.contains(StoreName))
                    continue Loop;

                temp.add(StoreName);
                System.out.println(temp.size()+"."+StoreName);
            }
            System.out.println("-".repeat(40));


        } catch (Exception e) {
            System.out.println("데이터베이스에 문제가 있습니다. 프로그램을 종료합니다.");
            System.exit(0);
        }finally {
            database.reserveManagement.close();
        }

        // 매장 선택 및 예약 날짜 입력
        System.out.print("예약 희망하는 매장 번호를 입력하세요: ");
        String ans = scan.nextLine();

        //고객이 블랙리스트 고객인지 아닌지 확인
        Map<String, Integer> noShowCustomers = new HashMap<>();
        Loop:while (database.store.hasNextLine()) {
            String str[]=database.store.nextLine().split("\t|\s");
            for (int i1 = 4; i1 < str.length; i1 += 2) {
                String userId = str[i1];
                int count = Integer.parseInt(str[i1 + 1]);
                noShowCustomers.put(userId, count);
            }
            for (Map.Entry<String, Integer> entry : noShowCustomers.entrySet()) {
                if(ID.equals(entry.getKey()))  {
                    System.out.println("'"+ID+"'님은 김길동돈까스의 블랙리스트 입니다. 다른 매장을 선택해주세요.\n"
                    );
                    return;
                }
            }

            //만약 사용자가 입력한 매장 번호가 양의 정수인지 아닌지 검사
            if(!ans.matches("^[0-9]+$")) {
                System.out.println("[오류] 해당하는 매장이 없습니다.");
                return;
            }
            int storeIndex; // 사용자가 입력한 매장 번호
            try {
                storeIndex = Integer.parseInt(ans);
            }catch(Exception e) {
                System.out.println("[오류] 해당하는 매장이 없습니다.");
                return;
            }

            if(database.isValidInt(1,Integer.toString(storeIndex)) != true||storeIndex>temp.size() ) {
                System.out.println("[오류] 해당하는 매장이 없습니다.");
                return;
            }



            //사용자가 선택한 매장 이름 출력
            System.out.println(temp.get(storeIndex-1));

            // 예약 날짜 입력
            System.out.print("예약 희망하는 날짜를 입력하세요: ");
            String reservationDate = scan.nextLine();
            //날짜 형식이 yyyy/mm/dd인지 아닌지 검사
            if(database.isValidDate(1,reservationDate) != true) {
                System.out.println("[오류] 입력이 올바르지 않습니다.");
                return;
            }
            System.out.println("-".repeat(40));
            System.out.println("예약 가능한 날짜입니다.");
            System.out.println("예약 가능한 시간대");

            // 간략한 자료 구조 설명
            // currentTables : ("12:00", [(1, 4), (2, 2), (4, 3))]) << 12시에 1인 테이블 4명, 2인 테이블 2명, 4인 테이블 3명 남았다는 뜻.
            // peopleLeft : ("12:00", 20) << 12시에 20명 남았다는 뜻

            Map<String, Map<Integer, Integer>> currentTables = new HashMap<>();
            Map<String, Integer> peopleLeft = new HashMap<>();
            try {
                database.reserveManagement = new Scanner(new File("reserveManagement.txt"));
                while(database.reserveManagement.hasNextLine()) {
                    String line = database.reserveManagement.nextLine();
                    String[] parts = line.split("\t");

                    if(temp.get(storeIndex-1).equals(parts[0])) {
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

            ArrayList<String> me = new ArrayList<>(); // 사용자가 이미 예약한 시간이 담겨 있음

            try { 						//사장이 상정해둔 예약 최대인원-현예약인원=실제 예약가능한 인원 구하는 부분
                database.reserve = new Scanner(new File("reserve.txt"));
                while(database.reserve.hasNextLine()) {
                    String line = database.reserve.nextLine();
                    String[] parts = line.split("\t");
                    if(temp.get(storeIndex-1).equals(parts[0]) && parts[2].equals(reservationDate)) { // 선택한 매장의 선택한 날짜인 경우!
                        String[] tables = parts[4].split(" ");

                        if(ID.equals(parts[1])) { // 사용자가 선택했을 경우 시간 추가!
                            me.add(parts[3]);
                        }

//                        Map<Integer, Integer> currentTable = currentTables.get(parts[3]); // 현재 시간의 테이블 정보를 구함.
//                        int currentPeopleLeft = peopleLeft.get(parts[3]); // 현재 시간의 인원 총합을 구함.
//                        for(int j = 0; j < tables.length; j += 2) {
//                            int maxTable = Integer.parseInt(tables[j]);
//                            int tableCnt = Integer.parseInt(tables[j + 1]);
//
//                            currentTable.compute(maxTable, (k, val) -> val - tableCnt);
//                            currentPeopleLeft -= (maxTable * tableCnt);
//                        }
//
//                        currentTables.put(parts[3], currentTable); // 계산 후 변경 사항 저장
//                        peopleLeft.put(parts[3], currentPeopleLeft);
                    }
                }
            } catch (Exception e) {
                System.out.println("데이터베이스에 문제가 있습니다. 프로그램을 종료합니다.");
                System.exit(0);
            } finally {
                database.reserve.close();
            }

            int cnt = 1;
            ArrayList<String> availableTime = new ArrayList<>(); // 이 리스트는 1번 인덱스부터 시작함 (편의를 위해)
            availableTime.add("");
            for(String time : currentTables.keySet()) {
                System.out.println((cnt)+". "+ time +" | 최대 인원: "+ peopleLeft.get(time));
                availableTime.add(time);
                cnt++;
            }

            System.out.print("희망하는 번호와 인원을 입력해주세요: ");
            String reserveHope = scan.nextLine();


            String[] tokens = reserveHope.split(" ");
            if (tokens.length != 2 ) {
                System.out.println("[오류] 번호와 인원을 구분할 수 없습니다.");
                return;
            }

            int userIndex = Integer.parseInt(tokens[0]);
            String userChosenTime;
            try {
                userChosenTime = availableTime.get(userIndex); // 유저가 고른 시간

                if(userIndex == 0) { // 리스트가 1부터 시작하기 때문에, 0은 if문으로 걸러야 함.
                    System.out.println("[오류] 목록에 없는 번호입니다.");
                    return;
                }
            } catch(Exception e) {
                System.out.println("[오류] 목록에 없는 번호입니다.");
                return;
            }
            String FinalreserveNum = tokens[1]; //예약 희망 인원


            //-입력 검사
//        if(!FinalreserveNum.matches("^[0-9]+$")) { //아니 애초에 예약 희망 인원수가 양의 정수가 맞느지 검사
//            System.out.println("[오류] 인원수는 1 이상의 정수값이어야 합니다.");
//            return;
//        }
            if (!FinalreserveNum.matches("^[1-9][0-9]*$")) { // 0을 제외한 양의 정수 확인
                System.out.println("[오류] 인원수는 1 이상의 정수값이어야 합니다.");
                return;
            }

            int reservationPeople = Integer.parseInt(FinalreserveNum); // 예약 희망 인원

            for(String time : me) {
                if(userChosenTime.equals(time)){
                    System.out.println("[오류] 이미 예약한 시간대입니다.");
                    return;
                }
            }

            int maxPeople = peopleLeft.get(userChosenTime);

            if(reservationPeople > maxPeople) { //예약 희망인원이 목록에 있는 인원보다 클떄
                System.out.println("[오류] 인원수가 최대 인원을 넘어섰습니다.");
                return;
            }


            //예약 재확인 질문
            System.out.print(userIndex+"번 항목에 "+reservationPeople+"명을 예약하시겠습니까? (Yes/No):");
            String lastAnswer=scan.nextLine();
            if(lastAnswer.equals("No")) {
                System.out.println("등록 취소되었습니다.");
                return;
            }else { // Yes라고 답할 시
                // 테이블 배정 알고리즘
                Map<Integer, Integer> tableMap = currentTables.get(userChosenTime);

                // 테이블 인원수를 내림차순으로 정렬한다.
                List<Integer> table = tableMap.keySet().stream()
                        .sorted(Comparator.reverseOrder())
                        .collect(Collectors.toList());

                // 시험용 코드
//                for(int t : table) {
//                    System.out.println(t + "명 테이블 : " + tableMap.get(t) + "개 남음");
//                }

                // 알고리즘 설명
                // 6인 예약 가정 :    8인    4인    2인    1인 테이블
                //                        pos (<= 6인)

                Map<Integer, Integer> userTable = new HashMap<>();
                while(reservationPeople > 0) {
                    int pos = table.size() - 1;
                    for(int j = 0; j < table.size(); j++) {
                        if(table.get(j) < reservationPeople && tableMap.get(table.get(j)) > 0) { // pos의 위치를 잡음 (테이블 수 맞추기, 1개 이상 테이블 남음)
                            pos = j;
                            break;
                        }
                    }
                    if(pos != 0 && table.get(pos) != reservationPeople) { // 인원수와 맞거나 더 큰 테이블이 있다면..
                        if(tableMap.get(table.get(pos - 1)) > 0)
                            pos--;
                    }

                    reservationPeople -= table.get(pos);
                    if(userTable.computeIfPresent(table.get(pos), (k, v) -> v + 1) == null) {
                        userTable.put(table.get(pos), 1);
                    }
                    tableMap.compute(table.get(pos), (k, v) -> v - 1);
                }

                currentTables.put(userChosenTime, tableMap);

                // 시험용 코드
//                for(int t : table) {
//                    System.out.println(t + "명 테이블 : " + tableMap.get(t) + "개 남음");
//                }

                // reserve.txt 파일에 예약 정보 입력
                try {
                    FileWriter fw = new FileWriter(new File("reserve.txt"), true);
                    PrintWriter writer = new PrintWriter(fw);
                    String newReservationInfo = temp.get(storeIndex-1) + "\t" + ID + "\t" + reservationDate + "\t" + userChosenTime + "\t";

                    for(int k : userTable.keySet()) {
                        newReservationInfo += (String.valueOf(k) + " " + String.valueOf(userTable.get(k)) + " ");
                    }
                    writer.println(newReservationInfo);

                    writer.close();
                }  catch (Exception e) {
                    System.out.println("데이터베이스에 문제가 있습니다. 프로그램을 종료합니다.");
                    System.exit(0);
                }

                // reserveManagement.txt 파일에 바뀐 테이블 정보 재입력
                // 먼저 전체 스캔하고, 해당 부분만 수정해서 다시 덮어쓰는 방식
                try {
                    database.reserveManagement = new Scanner(new File("reserveManagement.txt"));

                    String modifiedData = "";
                    while(database.reserveManagement.hasNextLine()) {
                        String line = database.reserveManagement.nextLine();
                        String[] parts = line.split("\t");
                        if(parts[0].equals(temp.get(storeIndex-1)) && parts[1].equals(userChosenTime)) { // 가게명 시간 같은 부분 고치면 됨
                            modifiedData += (parts[0] + "\t" + parts[1] + "\t");
                            for(int t : table) {
                                modifiedData += (String.valueOf(t) + " " + String.valueOf(tableMap.get(t)) + " ");
                            }
                            modifiedData += "\n";
                        } else {
                            modifiedData += line + "\n";
                        }
                    }
                    FileWriter fw = new FileWriter(new File("reserveManagement.txt"));
                    PrintWriter writer = new PrintWriter(fw);
                    writer.print(modifiedData);

                    writer.close();
                }  catch (Exception e) {
                    System.out.println("데이터베이스에 문제가 있습니다. 프로그램을 종료합니다.");
                    System.exit(0);
                }

                System.out.println("예약되었습니다");
            }
        }

    }

    //2. 예약관리
    public void reserveManCustomer(String ID) {
        String storeName = ""; // 매장 이름
        String dataID = ""; // ID
        String reserveDate = ""; // 예약 날짜
        String reserveTime = ""; // 예약 시간
        String reserveNum = ""; // 예약 인원
        int index = 0; // 예약 현황 인덱스

        try {
            // 파일에서 예약 현황 가져오기
            database.reserve = new Scanner(new File("reserve.txt"));

            System.out.println("\n[예약관리]");
            System.out.println("-".repeat(40));
            System.out.println("예약 매장");

            // 예약 현황 출력
            while (database.reserve.hasNextLine()){
                String line = database.reserve.nextLine();
                String[] part = line.split("\t");

                // 예약 내역이 있을 때
                if(part[1].equals(ID)) {
                    storeName = part[0];
                    reserveDate = part[2];
                    reserveTime = part[3];
                    reserveNum = part[4];

                    System.out.println((index + 1) + ". " + storeName + " " + reserveDate + " " + reserveTime);
                    index++;
                }
            }

            System.out.println("-".repeat(40));
        } catch (Exception e) {
            System.out.println("데이터베이스에 문제가 있습니다. 프로그램을 종료합니다.");
            System.exit(0);
        }

        // 예약 삭제 또는 돌아가기 선택
        System.out.println("1. 예약 삭제");
        System.out.println("2. 돌아가기");
        System.out.print("원하는 메뉴를 선택해 주세요: ");
        String inputMenu = scan.nextLine(); // 메뉴 선택 입력값

        if (!inputMenu.matches("^[0-9]+$")) {
            return;
        }

        int reservationIndex ;

        try {
            reservationIndex= Integer.parseInt(inputMenu);
        }catch(Exception e) {
            return;
        }
//
//        if(reservationIndex == 2) { // 메뉴 선택 입력값이 2일때
//            return;
//        }
//        if(reservationIndex!= 1 || reservationIndex!=2) {
//            System.out.println("[오류] 올바르지 않은 입력입니다.");
//            return;
//        }


        //       기존 기획서대로, 1아닌 값이기만 하면 오류출력없이 돌아감.
        if(reservationIndex != 1) {
            return;
        }

        // 예약 삭제 작업 수행
        System.out.println();
        System.out.println("[예약삭제]");
        System.out.print("예약을 삭제하려는 항목의 번호를 입력하세요: ");
        String inputDelete = scan.nextLine(); // 예약 삭제 항목 번호 입력값

        if (!inputDelete.matches("^[0-9]+$")) { // 숫자가 입력되지 않았을 때
            System.out.println("[오류] 올바르지 않은 입력입니다.");
            return;
        }

        int deletionIndex ;

        try {
            deletionIndex= Integer.parseInt(inputDelete);
        }catch(Exception e) {
            System.out.println("[오류] 해당하는 번호가 존재하지 않습니다.");
            return;
        }

        if(deletionIndex < 1 || deletionIndex > index) { // 예약 매장 목록에 없는 항목의 번호를 입력한 경우
            System.out.println("[오류] 해당하는 번호가 존재하지 않습니다.");
            return;
        }

        System.out.print("정말로 삭제하시겠습니까? (Yes/No): "); // 예약 매장 목록에 있는 항목 번호 입력한 경우
        String input = scan.nextLine();

        if(input.equals("No")) { // No를 입력한 경우
            System.out.println("예약 삭제가 취소되었습니다.");
            return;
        }

        try {
            database.reserve = new Scanner(new File("reserve.txt"));
            ArrayList<String> updatedReservations = new ArrayList<>();

            while (database.reserve.hasNextLine()) {
                String line = database.reserve.nextLine();
                String[] part = line.split("\t");

                if (part[1].equals(ID)) {
                    updatedReservations.add(line); // 일단 모든 예약을 저장
                }
            }

            if (deletionIndex > 0 && deletionIndex <= updatedReservations.size()) {
                updatedReservations.remove(deletionIndex - 1); // 삭제할 예약 제거
            } else {
                System.out.println("[오류] 해당하는 번호가 존재하지 않습니다.");
                return;
            }

            database.reserveWrite = new PrintWriter(new File("reserve.txt"));
            for (String reservation : updatedReservations) {
                database.reserveWrite.println(reservation); // 수정된 예약 목록을 파일에 씀
            }
        } catch (Exception e) {
            System.out.println("데이터베이스에 문제가 있습니다. 프로그램을 종료합니다.");
            System.exit(0);
        } finally {
            database.reserve.close();
            if (database.reserveWrite != null) {
                database.reserveWrite.close();
            }
        }
        System.out.println("예약 삭제되었습니다.");
        return;
    }

}