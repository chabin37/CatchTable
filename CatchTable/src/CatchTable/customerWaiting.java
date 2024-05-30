package CatchTable;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Scanner;

public class customerWaiting {
    database database;
    output output;
    Scanner scan = new Scanner(System.in);

    public customerWaiting(database database) {
        this.database = database;
    }

    public void isWaiting(String ID) {
        String storeName = ""; // 매장 이름
        int waitingNum; // 웨이팅 순서(int)
        String select; // 재차질문 YES/No

        try {
            database.waiting = new Scanner(new File("waiting.txt"));
            boolean isWaiting = false;

            while (database.waiting.hasNextLine()) {
                String line = database.waiting.nextLine();
                String[] part = line.split("\t");
                if (ID.equals(part[1])) { // 사용자가 웨이팅하고 있는 매장이 있는 경우
                    isWaiting = true;
                    storeName = part[0];
                    waitingNum = Integer.parseInt(part[2]);
                    System.out.println(ID + " 님은 현재 " + storeName + " 매장에 웨이팅하고 있습니다.");
                    System.out.println();
                    System.out.println(ID + " 님의 현재 웨이팅 순서는 " + (waitingNum + 1) + " 번입니다.");
                    System.out.println();
                    System.out.println("[경고] 다른 매장에 웨이팅하고자 할 경우, 현재 웨이팅은 취소됩니다.");
                    System.out.print("다른 매장에 웨이팅하겠습니까? (Yes/No): ");

                    select = scan.nextLine();

                    if (select.equals("No")) {
                        return;
                    } else {
                        waiting(ID);
                        return;
                    }
                }
            }

            // 사용자가 웨이팅하고 있는 매장이 없는 경우
            if (!isWaiting) {
                System.out.println(ID + " 님은 현재 웨이팅하고 있는 매장이 없습니다.");
                waiting(ID);
            }

        } catch (Exception e) {
            System.out.println("데이터베이스에 문제가 있습니다. 프로그램을 종료합니다.");
            System.exit(0);
        } finally {
            database.waiting.close();
        }
    }

    public void waiting(String ID) {
        ArrayList<String[]> waitingStores = new ArrayList<>();
        String num; // 매장 항목 번호(str)
        int storeNum; // 매장 항목 번호(int)
        int peopleNum; // 웨이팅할 총 인원(int)
        String select; // 재차질문 YES/No
        int waitingNum = 0; // 웨이팅 순서(int), 데베 저장은 0부터, 출력은 1부터

        try {
            database.store = new Scanner(new File("store.txt"));
            while (database.store.hasNextLine()) {
                String line = database.store.nextLine();
                String[] part = line.split("\t");
                waitingStores.add(part);
            }

            System.out.println("현재 웨이팅 가능한 매장 목록");
            System.out.println("----------------------------------------");
            for (int i = 0; i < waitingStores.size(); i++) {
                String[] storeInfo = waitingStores.get(i);
                String storeName = storeInfo[0];
                System.out.println(i + ". " + storeName);
            }
            System.out.println("----------------------------------------");
            System.out.print("웨이팅 희망하는 매장 번호를 입력하세요: ");
            num = scan.nextLine();

            if (!num.matches("^[0-9]+$")) {
                System.out.println("[오류] 입력 형식이 올바르지 않습니다.");
                return;
            }

            storeNum = Integer.parseInt(num);
            if (storeNum < 0 || storeNum >= waitingStores.size()) {
                System.out.println("[오류] 해당하는 매장이 없습니다.");
                return;
            }

            System.out.print("웨이팅할 총 인원을 입력하세요: ");
            num = scan.nextLine();

            if (!num.matches("^[0-9]+$")) {
                System.out.println("[오류] 입력 형식이 올바르지 않습니다.");
                return;
            }

            peopleNum = Integer.parseInt(num);
            if (peopleNum < 1) {
                System.out.println("[오류] 총 인원수는 1 이상의 정수여야 합니다.");
                return;
            }
            else if (peopleNum > 1000) {
                System.out.println("[오류] 총 인원수는 1000 이하의 정수여야 합니다.");
                return;
            }

            String[] storeInfo = waitingStores.get(storeNum);
            String storeName = storeInfo[0];
            System.out.print(storeName + " 매장에 " + peopleNum + " 명이 웨이팅하겠습니까? (YES/No): ");
            select = scan.nextLine();

            if (select.equalsIgnoreCase("No")) {
                return;
            }

            ArrayList<String[]> waitingList = new ArrayList<>();
            database.waiting = new Scanner(new File("waiting.txt"));
            while (database.waiting.hasNextLine()) {
                String line = database.waiting.nextLine();
                String[] part = line.split("\t");
                waitingList.add(part);
                if (storeName.equals(part[0])) {
                    waitingNum++;
                }
            }

            waitingList.add(new String[]{storeName, ID, String.valueOf(waitingNum), String.valueOf(peopleNum)});
            PrintWriter writer = new PrintWriter(new FileWriter("waiting.txt"));
            for (String[] entry : waitingList) {
                writer.println(String.join("\t", entry));
            }
            writer.close();

            System.out.println(ID + " 님의 현재 웨이팅 순서는 " + (waitingNum + 1) + " 번입니다.");

        } catch (Exception e) {
            System.out.println("데이터베이스에 문제가 있습니다. 프로그램을 종료합니다.");
            System.exit(0);
        } finally {
            database.store.close();
            database.waiting.close();
        }
    }
}