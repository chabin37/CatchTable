package CatchTable;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
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
        //중복 제거를 위한 List
        List <String> temp = new ArrayList<>();
        int i=0; //1.김길동 돈까스, 2... 출력할때의 index temp.size()로 대체
        try {
            // 파일에서 예약 정보 가져오기
            database.reserveManagement = new Scanner(new File("reserveManagement.txt"));
            // 매장 정보 출력 (예약 가능한 매장)
            System.out.println("[예약하기]");
            System.out.println();
            System.out.println("현재 예약 가능 매장");
            System.out.println("-".repeat(40));
            Loop:while (database.reserveManagement.hasNextLine()) {
                String str[]=database.reserveManagement.nextLine().split("\t");
                String StoreName=str[0];
                for(int j=0;j<temp.size();j++) {
                    if(StoreName.equals(temp.get(j)))
                        continue Loop;
                }

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

        //만약 사용자가 입력한 매장 번호가 양의 정수인지 아닌지 검사
        if(!ans.matches("^[0-9]+$")) {
            System.out.println("[오류] 해당하는 매장이 없습니다.");
            return;
        }
        int storeIndex;
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
        int i2=0; //1. 12:00 | 최대 인원: 20 <-여기에서 1. 담당할 애

        List<String> time =new ArrayList<>();
        List<Integer> max =new ArrayList<>();
        try {
            database.reserveManagement = new Scanner(new File("reserveManagement.txt"));
            while(database.reserveManagement.hasNextLine()) {
                String line = database.reserveManagement.nextLine();
                String[] parts = line.split("\t");
                if(temp.get(storeIndex-1).equals(parts[0])) {
                    time.add(parts[1]);
                    max.add(Integer.parseInt(parts[2]));
                }
            }
        } catch (Exception e) {
            System.out.println("데이터베이스에 문제가 있습니다. 프로그램을 종료합니다.");
            System.exit(0);
        } finally {
            database.reserveManagement.close();
        }
        List<String> me =new ArrayList<>();//이미 내가 예약한 시간
        try { 						//사장이 상정해둔 예약 최대인원-현예약인원=실제 예약가능한 인원 구하는 부분
            database.reserve = new Scanner(new File("reserve.txt"));
            while(database.reserve.hasNextLine()) {
                String line = database.reserve.nextLine();
                String[] parts = line.split("\t");
                if(temp.get(storeIndex-1).equals(parts[0])) {
                    for(int j=0;j<time.size();j++) {
                        if(parts[3].equals(time.get(j))&&reservationDate.equals(parts[2])){
                            max.set(j, max.get(j)-Integer.parseInt(parts[4]));
                            if(parts[1].equals(ID))
                                me.add(time.get(j));
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("데이터베이스에 문제가 있습니다. 프로그램을 종료합니다.");
            System.exit(0);
        } finally {
            database.reserve.close();
        }

        for(int j=0;j<time.size();j++) {
            System.out.println((j+1)+". "+time.get(j)+" | 최대 인원: "+max.get(j));
        }


        System.out.print("희망하는 번호와 인원을 입력해주세요: ");
        String reserveHope = scan.nextLine();


        String[] tokens = reserveHope.split(" ");
        if (tokens.length != 2 ) {
            System.out.println("[오류] 번호와 인원을 구분할 수 없습니다.");
            return;
        }
        String FinalreserveIndex= tokens[0]; //예약 희망 번호
        String FinalreserveNum = tokens[1]; //예약 희망인원


        //-입력 검사
//        if(!FinalreserveNum.matches("^[0-9]+$")) { //아니 애초에 예약 희망 인원수가 양의 정수가 맞느지 검사
//            System.out.println("[오류] 인원수는 1 이상의 정수값이어야 합니다.");
//            return;
//        }
        if (!FinalreserveNum.matches("^[1-9][0-9]*$")) { // 0을 제외한 양의 정수 확인
            System.out.println("[오류] 인원수는 1 이상의 정수값이어야 합니다.");
            return;
        }
        try {
            Integer.parseInt(FinalreserveIndex);
        }catch(Exception e) {
            System.out.println("[오류] 목록에 없는 번호입니다.");
            return;
        }
        if(Integer.parseInt(FinalreserveIndex) >time.size()||!FinalreserveIndex.matches("^[0-9]+$")) { //예약 희망 번호가 목록에 있는 번호보다 큰지 검사
            System.out.println("[오류] 목록에 없는 번호입니다.");
            return;
        }

        for(int j=0;j<me.size();j++) {
            if(time.get(Integer.parseInt(FinalreserveIndex)-1).equals(me.get(j))){
                System.out.println("[오류] 이미 예약한 시간대입니다.");
                return;
            }
        }
        try {
            Integer.parseInt(FinalreserveNum);
        }catch(Exception e) {
            System.out.println("[오류] 인원수가 최대 인원을 넘어섰습니다.");
            return;
        }
        if(Integer.parseInt(FinalreserveNum) > max.get(Integer.parseInt(FinalreserveIndex)-1) ) { //예약 희망인원이 목록에 있는 인원보다 클떄
            System.out.println("[오류] 인원수가 최대 인원을 넘어섰습니다.");
            return;
        }
       /*
        try {
            CatchTable.database.reserveManagement = new Scanner(new File("reserveManagement.txt"));
            CatchTable.database.reserveManagementWrite = new PrintWriter(new FileWriter("reserveManagement.txt", true));
        } catch (IOException e) {
            System.out.println("야 왜 안되냐");
            System.exit(0);
        }

        while (CatchTable.database.reserveManagement.hasNextLine()) {
            int i3=1;
            String reserveInfo2 = CatchTable.database.reserveManagement.nextLine();
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




        int IntFinalReserveMax =0;

        if(FinalReserveMax.isEmpty()) {
            IntFinalReserveMax=0;
        }else {
            IntFinalReserveMax = Integer.parseInt(FinalReserveMax);
        }
        if(Integer.parseInt(FinalreserveNum) > IntFinalReserveMax ) { //예약 희망인원이 목록에 있는 인원보다 클떄
            System.out.println("[오류] 인원수가 최대 인원을 넘어섰습니다.");
            return;
        }*/
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
            database.reserveWrite = new PrintWriter(new FileWriter("reserve.txt", true));
            System.out.println("[예약완료]");
            System.out.println("-".repeat(20));
            System.out.println(temp.get(storeIndex-1));
            System.out.println("예약 날짜 및 시간: "+reservationDate+" "+time.get(Integer.parseInt(FinalreserveIndex)-1));
            System.out.println("예약인원: "+FinalreserveNum);
            System.out.println("-".repeat(20));
            // 파일에 예약 정보 쓰기
            database.reserveWrite.println(temp.get(storeIndex-1) + "\t" + ID + "\t" + reservationDate + "\t" + time.get(Integer.parseInt(FinalreserveIndex)-1) + "\t" + FinalreserveNum);
            return;

        } catch (Exception e) {
            System.out.println("데이터베이스에 문제가 있습니다. 프로그램을 종료합니다.");
            System.exit(0);
        } finally {
            database.reserveWrite.close();
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