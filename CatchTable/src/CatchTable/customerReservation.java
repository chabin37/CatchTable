package CatchTable;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.Scanner;

public class customerReservation {
    database database;
    Scanner scan = new Scanner(System.in);
    int flag;

    public customerReservation(database database) {
        this.database = database;
    }

    //1.예약하기
    public void reserve(String ID) {
        int i=1; //1.김길동 돈까스, 2... 출력할때의 index
        try {
            // 파일에서 예약 정보 가져오기
            database.reserveManagement = new Scanner(new File("reserveManagement.txt"));
            // 매장 정보 출력 (예약 가능한 매장)
            System.out.println("[예약하기]");
            System.out.println();
            System.out.println("현재 예약 가능 매장");
            System.out.println("-".repeat(40));
            while (database.reserveManagement.hasNextLine()) {
                System.out.println(i+"."+database.reserveManagement.nextLine());
                i++;
            }
            System.out.println("-".repeat(40));
            database.store.close();

        } catch (Exception e) {
            System.out.println("데이터베이스에 문제가 있습니다. 프로그램을 종료합니다.");
            System.exit(0);
        }

        // 매장 선택 및 예약 날짜 입력
        System.out.print("예약 희망하는 매장 번호를 입력하세요: ");
        int storeIndex = Integer.parseInt(scan.nextLine());


        //만약 사용자가 입력한 매장 번호가 양의 정수인지 아닌지 검사
        if(database.isValidInt(1,Integer.toString(storeIndex)) != true) {
            System.out.println("[오류] 해당하는 매장이 없습니다.");
            return;
        }
        //사용자가 선택한 매장 이름 출력
        try {
            // 파일에서 매장 정보 가져오기
            database.reserveManagement = new Scanner(new File("reserveManagement.txt"));
            int currentIndex = 0;
            // 매장 정보 출력 및 이름 찾기
            while (database.reserveManagement.hasNextLine()) {
                String storeInfo = database.reserveManagement.nextLine();
                if (currentIndex+1 == storeIndex) {
                    // 매장 정보 파싱 및 이름 출력
                    String[] storeData = storeInfo.split("\t"); // 매장 정보 데.파.는 \t로되있음.
                    String storeName = storeData[0]; // 첫 번째 요소인 매장 이름 추출
                    System.out.println(storeName);
                    break;
                }
                currentIndex++;
            }
            database.store.close();
        } catch (Exception e) {
            System.out.println("데이터베이스에 문제가 있습니다. 프로그램을 종료합니다.");
            System.exit(0);
        }

        // 예약 날짜 입력
        System.out.print("예약 희망하는 날짜를 입력하세요: ");
        String reservationDate = scan.nextLine();
        //날짜 형식이 yyyy/mm/dd인지 아닌지 검사
        if(database.isValidDate(1,reservationDate) != true) {
            System.out.println("[오류] 해당하는 매장이 없습니다.");
            return;
        }
        System.out.println("-".repeat(40));
        System.out.println("예약 가능한 날짜입니다.");
        System.out.println("예약 가능한 시간대");
        int i2=0; //1. 12:00 | 최대 인원: 20 <-여기에서 1. 담당할 애
        while (database.reserveManagement.hasNextLine()) {
            String reserveInfo = database.reserveManagement.nextLine();
            String[] reserveData= reserveInfo.split("\t"); // 매장 정보 데.파.는 \t로되있음.
            String reserveTime = reserveData[1]; // 두 번째 요소인 매장 이름 추출(첫번쨰는 매장이름)
            String maxReserve = reserveData[2];
            System.out.println(i2+". "+reserveTime+" | 최대 인원: "+maxReserve);
            i2++;
        }
        System.out.print("희망하는 번호와 인원을 입력해주세요: ");
        String reserveHope = scan.nextLine();

        // 입력된 문자열에서 공백(" ")의 위치를 찾음
        int spaceIndex = reserveHope.indexOf(' ');
        if (spaceIndex == -1) { //희망하는 번호와 인원수를  ‘␣’으로 구별하지 않은 경우
            System.out.println("[오류] 번호와 인원을 구분할 수 없습니다. ");
            return;
        }
        String[] reserveInfo= reserveHope.split(" ");
        String FinalreserveIndex= reserveInfo[0]; //예약 희망 번호
        String FinalreserveNum = reserveInfo[1]; //예약 희망인원

        String FinalStoreName = null;
        String FinalReserveTime = null;
        String FinalReserveMax = null;

        while (database.reserveManagement.hasNextLine()) {
            int i3=1;
            String reserveInfo2 = database.reserveManagement.nextLine();
            String[] reserveData= reserveInfo2.split("\t"); // 매장 정보 데.파.는 \t로되있음.
            String reserveStoreName = reserveData[0]; // 첫 번째 요소인 매장 이름 추출
            String reserveTime = reserveData[1]; // 두 번째 요소인 매장 시간 추출
            String maxReserve = reserveData[2];//세번째 요소인 최대 인원 추출
            if(i3==Integer.parseInt(FinalreserveIndex) ){ //마참내 만약 사용자가 입력한 index가게 찾으면!!!
                FinalStoreName = reserveStoreName; // Final변수에 그 index의 여러값 각각 대입
                FinalReserveTime = reserveTime;
                FinalReserveMax = maxReserve;
                break;
            }
            i3++;
        }

        if(Integer.parseInt(FinalreserveIndex) >=i2 ||
                database.isValidInt(1,FinalreserveIndex)!=true) { //예약 희망 번호가 목록에 있는 번호보다 크거나 애초에 0이상의 정수가 아닌지 검사
            System.out.println("[오류] 목록에 없는 번호입니다.");
            return;
        }
        if(database.isValidInt(1, FinalreserveNum)) { //아니 애초에 예약 희망 인원수가 양의 정수가 맞느지 검사
            System.out.println("[오류] 인원수는 1 이상의 정수값이어야 합니다.");
            return;
        }
        if(Integer.parseInt(FinalreserveNum) >=Integer.parseInt(FinalReserveMax) ) { //예약 희망인원이 목록에 있는 인원보다 클떄
            System.out.println("[오류] 인원수가 최대 인원을 넘어섰습니다.");
            return;
        }
        //예약 재확인 질문
        System.out.print(FinalreserveIndex+"번 항목에 "+FinalreserveNum+"명을 예약하시겠습니까? (Yes/No):");
        String lastAnswer=scan.nextLine();
        if(lastAnswer.equals("No")) {
            System.out.println("등록 취소되었습니다.");
            return;
        }else {
            System.out.println("예약되었습니다");
        }

        // 예약 성공 출력 및 예약 정보 파일에 쓰기
        try {
            PrintWriter reserveWrite = new PrintWriter(new FileWriter("reserve.txt", true));
            database.reserveWrite = new PrintWriter(new FileWriter("reserve.txt", true));
            System.out.println("[예약완료]");
            System.out.println("-".repeat(20));
            System.out.println("예약 날짜 및 시간: "+reservationDate+" "+FinalReserveTime);
            System.out.println("예약인원: "+FinalreserveNum);
            System.out.println("-".repeat(20));
            // 파일에 예약 정보 쓰기
            reserveWrite.println("가게 이름: " + FinalStoreName);
            reserveWrite.println("예약 날짜 및 시간: " + reservationDate + " " + FinalReserveTime);
            reserveWrite.println("예약인원: " + FinalreserveNum);
            reserveWrite.println("-".repeat(20));
            return;

        } catch (Exception e) {
            System.out.println("데이터베이스에 문제가 있습니다. 프로그램을 종료합니다.");
            System.exit(0);
        } finally {
            database.reserveWrite.close();
        }
    }

    //2. 예약관리
    //2. 예약관리
    public void reserveManCustomer(String ID) {
        String storeName = ""; // 매장 이름
        String dataID = ""; // ID
        String reserveDate = ""; // 예약 날짜
        String reserveTime = ""; // 예약 시간
        String reserveNum = ""; // 예약 인원
        boolean IDflag = false; // 해당 ID로 예약한 가게가 존재하는지 확인하는 플래그
        int index = 1; // 예약 현황 인덱스

        try {
            // 파일에서 예약 현황 가져오기
            database.reserve = new Scanner(new File("reserve.txt"));
            boolean first = true; // 첫 번째 예약을 확인하기 위한 플래그

            // 예약 현황 출력
            while (database.reserve.hasNextLine()){
                String line = database.reserve.nextLine();
                String[] part = line.split("\t");

                // 예약 내역이 있을 때
                if(part[1].equals(ID)) {
                    IDflag = true;
                    storeName = part[0];
                    reserveDate = part[2];
                    reserveTime = part[3];
                    reserveNum = part[4];

                    if(first) { // 첫 번째 예약인 경우만 메시지 출력
                        System.out.println("[예약관리]");
                        System.out.println("-".repeat(40));
                        System.err.println("예약 매장");
                        first = false;
                    }

                    System.out.println(index + ". " + storeName + " " + reserveDate + " " + reserveTime);
                    index++;
                }
            }

            // 예약 내역이 없을 때
            if(!IDflag) {
                System.out.println("예약 내역이 없습니다. 예약을 먼저 하고 이용해주세요.");
                return;
            }

            System.out.println("-".repeat(40));
        } catch (Exception e) {
            System.out.println("데이터베이스에 문제가 있습니다. 프로그램을 종료합니다.");
            System.exit(0);
        }

        // 예약 삭제 또는 돌아가기 선택
        System.out.println("-".repeat(40));
        System.out.println("1. 예약 삭제");
        System.out.println("2. 돌아가기");
        System.out.print("원하는 메뉴를 선택해 주세요: ");
        String inputMenu = scan.nextLine(); // 메뉴 선택 입력값

        if (!inputMenu.matches("^[0-9]+$")) { // 숫자가 입력되지 않았을 때
            System.out.println("[오류] 올바르지 않은 입력입니다.");
            return;
        }

        int reservationIndex = Integer.parseInt(inputMenu);

        if(reservationIndex != 1) { // 메뉴 선택 입력값이 1이 아닐 때
            return;
        }

        // 예약 삭제 작업 수행
        System.out.println();
        System.out.println("[예약삭제]");
        System.out.print("예약을 삭제하려는 항목의 번호를 입력하세요: ");
        String inputDelete = scan.nextLine(); // 예약 삭제 항목 번호 입력값

        if (!inputMenu.matches("^[0-9]+$")) { // 숫자가 입력되지 않았을 때
            System.out.println("[오류] 올바르지 않은 입력입니다.");
            return;
        }

        int deletionIndex = Integer.parseInt(inputDelete);

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

        try { // No를 입력하지 않은 모든 경우 (즉, 예약 삭제가 이루어지는 경우)
            PrintWriter reserveWrite = new PrintWriter(new FileWriter("reserve.txt", true));

            while (database.reserve.hasNextLine()){
                String line = database.reserve.nextLine();
                String[] part = line.split("\t");

                if(part[1].equals(ID)) {
                    for(int i = 0; i < index; i++) {
                        if(i == deletionIndex) {
                            continue;
                        }

                        reserveWrite.println(line);
                    }
                }
                reserveWrite.println(line);
            }
        } catch (Exception e) {
            System.out.println("데이터베이스에 문제가 있습니다. 프로그램을 종료합니다.");
            System.exit(0);
        } finally {
            database.reserveWrite.close();
        }
        System.out.println("예약 삭제되었습니다.");
        return;
    }

    //3. 로그아웃?
}