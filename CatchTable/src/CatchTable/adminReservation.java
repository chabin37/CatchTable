package CatchTable;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

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

        while (database.isVaildTime(1, openTime)==false) {
            System.out.println("올바른 시간 형식이 아닙니다. 다시 입력해주세요.");
            System.out.print("영업 시작 시간 (예: 10:00): ");
            openTime = scan.nextLine();
        }

        System.out.print("영업 마감 시간 (예: 18:00): ");
        closeTime = scan.nextLine();

        while (database.isVaildTime(1, closeTime)==false) {
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




    public void reserveManAdmin() {// 예약 관리
        String storeName="";
        String openTime="";
        String closeTime="";
        int manageNum=0;
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
                if (part[1].equals(output.ID)) {
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
        try { // 파일 내 Scanner위치 초기화
            database.reserveManagement = new Scanner(new File("reserveManagement.txt"));
            while (database.reserveManagement.hasNextLine()) {
                String line = database.reserveManagement.nextLine();
                String[] part = line.split("\t");
                // System.out.println(line);//출력확인
                if (part[0].equals(storeName)) {
                    manageNum++;
                    reserveData.add(part[1]);
                    reserveData.add(part[2]);
                }
            }

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
            System.out.println("[예약 관리}");
            System.out.println("매장 이름: "+storeName);
            System.out.println("영업 시간: "+openTime+"~"+closeTime);
            System.out.println();
            if(manageNum==0) {
                System.out.println("현재 설정된 예약이 없습니다.");
            }else {
                System.out.println("현재 설정된 예약 목록");
                System.out.println("----------------------------------------");
                for(int i=0; i<manageNum*2; i=i+2) {
                    System.out.println(reserveData.get(i)+"|최대 인원: "+reserveData.get(i+1));
                }
                System.out.println("----------------------------------------");
            }
            System.out.println("[메뉴]");
            System.out.println("1. 예약 등록");
            System.out.println("2. 예약 삭제");
            System.out.println("3. 돌아가기");
            System.out.print("메뉴를 선택해 주세요:");
            // 입력
            select = scan.nextLine();

            if (!select.matches("^[0-9]+$"))
                return;

            flag = Integer.parseInt(select);
            switch (flag) {
                case 1 -> {
                    System.out.println("[예약 등록]");
                    System.out.print("등록할 시간과 최대 인원을 입력해주세요 (예: 09:00 15):");
                    String input = scan.nextLine();
                    String[] tokens = input.split(" ");
                    if (tokens.length % 2 != 0) {
                        System.out.println("[오류] 올바르지 않은 형식입니다");
                        return;
                    }
                    for (int i = 0; i < tokens.length; i += 2) {
                        String time = tokens[i];
                        String maxCapacityStr = tokens[i + 1];

                        // 표준 시간 형식이 맞는지 확인
                        if (!time.matches("\\d{2}:\\d{2}")) {
                            System.out.println("[오류] 올바르지 않은 형식입니다.");
                            return;
                        }
                        for(int j=0; j<manageNum*2; j=j+2) {
                            if(time.equals(reserveData.get(j))) {
                                System.out.println("[오류] 이미 등록된 시간을 포함하고 있습니다.");
                            }
                        }

                        // 최대 인원이 양의 정수인지 확인
                        try {
                            int maxCapacity = Integer.parseInt(maxCapacityStr);
                            if (maxCapacity <= 0) {
                                System.out.println("[오류] 최대 인원은 양의 정수여야 합니다.");
                                return;
                            }
                        } catch (NumberFormatException e) {
                            System.out.println("[오류] 최대 인원은 양의 정수여야 합니다.");
                            return;
                        }
                    }

                    for(int i=0; i<tokens.length; i=i+2) {
                        if(!(i==tokens.length-1)) {
                            System.out.print(tokens[i]+"에 "+tokens[i+1]+",");
                        }else {
                            System.out.print(tokens[i]+"에 "+tokens[i+1]);
                        }
                    }
                    System.out.println("을 예약 시간으로 등록하시겠습니까? (Yes/No)");
                    input = scan.nextLine();
                    if(input.equals("No")) {
                        System.out.println("등록이 취소되었습니다.");
                    }else {
                        try {
                            database.reserveManagementWrite = new PrintWriter(new FileWriter("reserveManagement.txt", true));
                            for(int i=0; i<tokens.length; i=i+2) {
                                database.reserveManagementWrite.println(storeName + "\t" + tokens[i] + "\t" + tokens[i+1]);
                            }
                        } catch (Exception e) {
                            System.out.println("데이터베이스에 문제가 있습니다. 프로그램을 종료합니다.");
                            System.exit(0);
                        } finally {
                            database.reserveManagementWrite.close();
                        }
                        System.out.println("등록되었습니다.");
                    }

                } // 예약 시간 및 인원 등록(시간 ,최대인원)
                case 2 -> {
                    System.out.println("[예약 삭제}");
                    System.out.println();

                    if(manageNum==0) {
                        System.out.println("삭제할 수 있는 예약이 없습니다.");
                        return;
                    }else {
                        System.out.println("현재 설정된 예약 목록");
                        System.out.println("----------------------------------------");
                        for(int i=0; i<manageNum*2; i=i+2) {
                            System.out.println((i/2+1)+". "+reserveData.get(i)+"|최대 인원: "+reserveData.get(i+1));
                        }
                        System.out.println("----------------------------------------");
                    }
                    System.out.println("예약 시간의 번호를 입력해 주세요:");
                    int index;
                    try {
                        String input= scan.nextLine();
                        index = Integer.parseInt(input);

                        // 양수이고, 1부터 manageNum까지의 범위에 속하는지 확인
                        if (!(index > 0 && index <= manageNum)) {
                            System.out.println("[오류] 존재하지 않는 항목입니다.");
                            return;
                        }
                    } catch (NumberFormatException e) {
                        System.out.println("올바른 숫자를 입력하세요.");
                        return;
                    }
                    System.out.println();
                    System.out.println(reserveData.get((index-1)*2)+"을 삭제하시겠습니까? (Yes/No):");
                    String input=scan.nextLine();
                    if(input.equals("No")) {
                        System.out.println("등록이 취소되었습니다.");
                    }else {
                        try {
                            //이 부분에 데이터 삭제 추가해야함.
                        } catch (Exception e) {
                            System.out.println("데이터베이스에 문제가 있습니다. 프로그램을 종료합니다.");
                            System.exit(0);
                        } finally {
                            database.reserveManagementWrite.close();
                        }
                        System.out.println("등록되었습니다.");
                    }

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