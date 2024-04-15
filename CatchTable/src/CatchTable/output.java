package CatchTable;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.Scanner;

//메뉴출력위주+회원가입
public class output {
    CatchTable.management management;
    int startFlag, mainFlag;
    Scanner scan = new Scanner(System.in);
    String ID, PW; //로그인시 사용

    public output() {
        this.management = new management();
        // 시작메뉴,메인메뉴
        startMenu();
    }

    // 시작 메뉴
    private void startMenu() {
        while (true) {
            String select;
            //입력
            System.out.println("\n[Catch Table]");
            System.out.println("1.로그인");
            System.out.println("2.회원가입");
            System.out.println("3.종료");
            System.out.print("메뉴를 선택해 주세요:");
            select=scan.nextLine();
            if(!select.matches("^[0-9]+$"))
            {
                errPrint(7);
                continue;
            }
            startFlag = Integer.parseInt(select);
            switch (startFlag) {
                case 1 -> login();
                case 2 -> join();
                case 3 -> quit();
                default -> errPrint(8);
            }
        }
    }

    // 시작메뉴-로그인
    private void login() {
        //입력
        String distinguish = "";
        System.out.println("\n[로그인]");
        System.out.print("ID: ");
        ID = scan.nextLine();
        System.out.print("PW: ");
        PW = scan.nextLine();
        // ----검사----
        try { // 파일 내 Scanner위치 초기화
            management.database.account = new Scanner(new File("account.txt"));
            if (!management.database.account.hasNextLine()) {// 파일 비었을 때
                errPrint(1);
                return;
            }

            while (management.database.account.hasNextLine()) {// ID 중복
                String line = management.database.account.nextLine();
                String[] part = line.split("\t");
                // System.out.println(line);//출력확인
                if (!part[0].equals(ID) && !management.database.account.hasNextLine()) {
                    errPrint(1);
                    return;
                } else if (part[0].equals(ID)) {
                    if (part[1].equals(PW)) {
                        distinguish = part[2];
                        break;
                    } else {
                        errPrint(1);
                        return;
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("데이터베이스에 문제가 있습니다. 프로그램을 종료합니다.");
            System.exit(0);
        } finally {
            management.database.account.close();
        }

        // 로그인 완료
        if (distinguish.compareTo("사장") == 0) {
            System.out.println("\n*" + ID + " 계정에 로그인합니다(사장)");
            mainMenuAdmin();
        } else if (distinguish.compareTo("고객") == 0) {
            System.out.println("\n*" + ID + " 계정에 로그인합니다(고객)");
            mainMenuCustomer();
        } else
            System.out.println("오류");
    }

    // 시작메뉴-회원가입
    private void join() {
        // 입력
        String joinID, joinPW, distinguish;
        System.out.println("\n[회원가입]");
        System.out.print("ID: ");
        joinID = scan.nextLine();
        System.out.print("PW: ");
        joinPW = scan.nextLine();
        System.out.print("회원 유형을 선택하세요(사장,고객): ");
        distinguish = scan.nextLine();
        boolean errflag=false;

        // ----검사----
        if (joinID.length() > 15 || joinPW.length() > 15) {// 15자리
            errPrint(2);
            errflag=true;
        }
        if (joinID.contains(" ") || joinPW.contains(" ") || joinID.contains("\t") || joinPW.contains("\t")) {// 공백포함
            errPrint(3);
            errflag=true;
        }
        if (!joinID.matches("^[a-zA-Z0-9\\p{Punct}]+$") || !joinPW.matches("^[a-zA-Z0-9\\p{Punct}]+$")) {// 영어,숫자,특수문자만
            errPrint(4);
            errflag=true;
        }
        if (!distinguish.equals("사장") && !distinguish.equals("고객")) {// 회원유형
            errPrint(5);
            errflag=true;
        }



        try { // 파일 내 Scanner위치 초기화
            management.database.account = new Scanner(new File("account.txt"));
            while (management.database.account.hasNextLine()) {// ID 중복
                String line = management.database.account.nextLine();
                String[] part = line.split("\t");
                // System.out.println(line);//출력확인
                if (part[0].equals(joinID)) {
                    errPrint(6);
                    errflag=true;
                }
            }
        } catch (Exception e) {
            System.out.println("데이터베이스에 문제가 있습니다. 프로그램을 종료합니다.");
            System.exit(0);
        } finally {
            management.database.account.close();
        }

        if(errflag)
            return;

        // 회원가입완료
        try {
            management.database.accountWrite = new PrintWriter(new FileWriter("account.txt", true));
            management.database.accountWrite.println(joinID + "\t" + joinPW + "\t" + distinguish);
        } catch (Exception e) {
            System.out.println("데이터베이스에 문제가 있습니다. 프로그램을 종료합니다.");
            System.exit(0);
        } finally {
            management.database.accountWrite.close();
        }
        System.out.println("\n*회원 가입이 완료되었습니다. 시작 메뉴로 돌아갑니다.");

    }

    // 시작메뉴-종료
    private void quit() {
        System.out.println("\n프로그램을 종료합니다.");
        scan.close();
        System.exit(0);
    }

    private void mainMenuAdmin() {// 사장 메인메뉴
        while (true) {
            String select;
            //입력
            System.out.println("\n[메인메뉴]");
            System.out.println("1.매장등록");
            System.out.println("2.예약관리");
            System.out.println("3.예약현황");
            System.out.println("4.로그아웃");
            System.out.print("메뉴를 선택해 주세요:");
            select=scan.nextLine();
            //----검사-----
            if(!select.matches("^[0-9]+$"))
            {
                errPrint(7);
                continue;
            }

            mainFlag = Integer.parseInt(select);
            switch (mainFlag) {
                case 1 -> management.adminReservation.storeJoin(ID);// 매장등록
                case 2 -> management.adminReservation.reserveManAdmin(ID);// 예약관리
                case 3 -> management.adminReservation.reserveNow(ID);// 예약현황
                case 4 -> {
                    System.out.println("\n*로그아웃합니다.");// 로그아웃
                    return;
                }
                default -> errPrint(8);
            }
        }
    }

    private void mainMenuCustomer() {// 고객 메인메뉴
        while (true) {
            String select;
            //입력
            System.out.println("\n[메인메뉴]");
            System.out.println("1.예약하기");
            System.out.println("2.예약관리");
            System.out.println("3.로그아웃");
            System.out.print("메뉴를 선택해 주세요:");
            select=scan.nextLine();
            //----검사-----
            if(!select.matches("^[0-9]+$"))
            {
                errPrint(7);
                continue;
            }
            mainFlag = Integer.parseInt(select);
            switch (mainFlag) {
                case 1 -> management.customerReservation.reserve(ID);// 예약하기
                case 2 -> management.customerReservation.reserveManCustomer(ID);// 예약관리
                case 3 -> {
                    System.out.println("\n*로그아웃합니다.");// 로그아웃
                    return;
                }
                default -> errPrint(8);
            }
        }
    }

    // output.java 에러출력
    private void errPrint(int errflag) {
        switch (errflag) {
            case 1 -> System.out.println("[오류] 존재하지 않는 계정입니다.");
            case 2 -> System.out.println("[오류] ID와 PW는 15자리를 넘지 않고 공백을 포함하지 않는 단어입니다.");
            case 3 -> System.out.println("[오류] ID와 PW는 띄어쓰기를 포함할 수 없습니다.");
            case 4 -> System.out.println("[오류] ID와 PW는 영어,숫자,특수문자만 사용가능합니다.");
            case 5 -> System.out.println("[오류] 올바른 회원 유형을 입력하세요.");
            case 6 -> System.out.println("[오류] 중복된 ID입니다.");
            case 7 -> System.out.println("[오류] 숫자를 입력해 주세요.");
            case 8 -> System.out.println("[오류] 올바른 번호를 입력해주세요.");
        }

    }
}
