package CatchTable;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class adminWaiting {
    database database;
    output output;
    Scanner scan = new Scanner(System.in);
    public adminWaiting(database database) {
        this.database = database;
    }

    public void waitingManage(String ID) {
        String storeName="";
        ArrayList<String[]> waitingCustomers = new ArrayList<>();
        boolean IDflag=false;
        String select; //사용자의 선택 저장
        int flag; //선택을 정수로 변환
        try { // 파일 내 Scanner위치 초기화
            database.store = new Scanner(new File("store.txt"));
            database.waiting = new Scanner(new File("waiting.txt"));
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

            while (database.waiting.hasNextLine()) {// 매장 등록 여부 확인
                String line = database.waiting.nextLine();
                String[] part = line.split("\t");
                // System.out.println(line);//출력확인
                if (part[0].equals(storeName)) {
                    waitingCustomers.add(part);
                }
            }
        } catch (Exception e) {
            System.out.println("aijei");
            System.out.println("데이터베이스에 문제가 있습니다. 프로그램을 종료합니다.");
            System.exit(0);
        } finally {
            database.store.close();
            database.waiting.close();
        }
        System.out.println("[웨이팅현황]");
        System.out.println("현재 웨이팅 고객 목록");
        System.out.println("----------------------------------------");

        for (int i = 0; i < waitingCustomers.size(); i++) {
            String[] customerInfo = waitingCustomers.get(i);
            String customerID = customerInfo[1];
            String waitingCount = customerInfo[3];
            System.out.println((i+1) + ". " + customerID + " " + waitingCount + "명");
        }
        System.out.println("----------------------------------------");
        System.out.println("1. 입장허가");
        System.out.println("2. 돌아가기");
        System.out.print("메뉴를 선택하세요: ");
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

        switch (flag) {
            case 1 -> {
                String first=waitingCustomers.get(0)[1];
                System.out.println(first+"고객을 입장시키겠습니까? (YES/No):");
                List<String> temp = new ArrayList<>();
                select=scan.nextLine();
                if (select.equals("No")) {
                    System.out.println("입장 허가가 취소되었습니다.");
                    return;
                } else {

                    try {
                        database.waiting = new Scanner(new File("waiting.txt"));
                        while (database.waiting.hasNextLine()) {
                            String line = database.waiting.nextLine();
                            String[] part = line.split("\t");
                            if (part[0].equals(storeName)) {
                                int order = Integer.parseInt(part[2]);
                                if (order > 1) {
                                    order--;
                                    temp.add(part[0] + "\t" + part[1] + "\t" + order + "\t" + part[3]);
                                }
                            } else {
                                temp.add(line);
                            }
                        }

                    } catch (Exception e) {
                        System.out.println("데이터베이스에 문제가 있습니다. 프로그램을 종료합니다.");
                        System.exit(0);
                    } finally {
                        database.waiting.close();
                    }
                    try {
                        database.waitingWrite = new PrintWriter(new FileWriter("waiting.txt", false));

                        for(int i=0;i<temp.size();i++)
                            database.waitingWrite.println(temp.get(i));

                    } catch (Exception e) {
                        System.out.println("데이터베이스에 문제가 있습니다. 프로그램을 종료합니다.");
                        System.exit(0);
                    } finally {
                        database.waitingWrite.close();
                    }
                    System.out.println(first+"고객의 입장이 완료되었습니다.");

                    return;
                }
            }
            case 2 ->{
                return;
            }
            default -> {
                return;
            }
        }



    }

}