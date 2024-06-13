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

    public void isWaiting(String ID) { // 사용자 웨이팅 여부 판별
        String storeName = ""; // 매장 이름
        int waitingNum; // 웨이팅 순서(int)
        String select; // 재차질문 Yes/No

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
                    System.out.println();
                    System.out.println(ID + " 님은 현재 " + storeName + " 매장에 웨이팅하고 있습니다.");
                    System.out.println();
                    System.out.println(ID + " 님의 현재 웨이팅 순서는 " + waitingNum + " 번입니다.");
                    System.out.println();
                    System.out.println("[경고] 새로운 웨이팅을 진행할 경우, 현재 웨이팅은 취소됩니다.");
                    System.out.print("새로운 웨이팅을 진행하겠습니까? (Yes/No): ");

                    select = scan.nextLine();

                    if (select.equals("No")) {
                        return;
                    }
                    else {
                        waiting(ID, true, storeName, waitingNum);
                        return;
                    }
                }
            }

            // 사용자가 웨이팅하고 있는 매장이 없는 경우
            if (!isWaiting) {
                System.out.println();
                System.out.println(ID + " 님은 현재 웨이팅하고 있는 매장이 없습니다.");
                waiting(ID, false, null, 0);
            }

        } catch (Exception e) {
            System.out.println("데이터베이스에 문제가 있습니다. 프로그램을 종료합니다.");
            System.exit(0);
        } finally {
            if (database.waiting != null) {
                database.waiting.close();
            }
        }
    }

    public void waiting(String ID, boolean isUpdating, String oldStoreName, int oldWaitingNum) {
        ArrayList<String[]> waitingStores = new ArrayList<>();
        String num; // 매장 항목 번호(str)
        int storeNum; // 매장 항목 번호(int)
        int peopleNum; // 웨이팅할 총 인원(int)
        String select; // 재차질문 Yes/No
        int waitingNum = 1; // 웨이팅 순서(int)

        try {
            database.store = new Scanner(new File("store.txt"));
            while (database.store.hasNextLine()) {
                String line = database.store.nextLine();
                String[] part = line.split("\t");
                waitingStores.add(part);
            }

            System.out.println();
            System.out.println("현재 웨이팅 가능한 매장 목록");
            System.out.println("----------------------------------------");
            for (int i = 0; i < waitingStores.size(); i++) {
                String[] storeInfo = waitingStores.get(i);
                String storeName = storeInfo[0];
                System.out.println((i + 1) + ". " + storeName);
            }
            System.out.println("----------------------------------------");
            System.out.print("웨이팅 희망하는 매장 번호를 입력하세요: ");
            num = scan.nextLine();

            if (!num.matches("^-?[0-9]+$")) {
                System.out.println("[오류] 입력 형식이 올바르지 않습니다.");
                return;
            }

            try {
                storeNum = Integer.parseInt(num);
            } catch(NumberFormatException e) {
                System.out.println("[오류] 해당하는 매장이 없습니다.");
                return;
            }

            if (Integer.parseInt(num) < 1 || Integer.parseInt(num) > waitingStores.size()) {
                System.out.println("[오류] 해당하는 매장이 없습니다.");
                return;
            }

            System.out.print("웨이팅할 총 인원을 입력하세요: ");
            num = scan.nextLine();

            if (num.matches("^[a-zA-Z]+$")) {
                System.out.println("[오류] 입력 형식이 올바르지 않습니다.");
                return;
            }
            else if (!num.matches("^-?[0-9]+$")) {
                System.out.println("[오류] 숫자만 입력하세요.");
                return;
            }
            else if(num.matches("-\\d+$")) {
                System.out.println("[오류] 총 인원수는 1 이상의 정수여야 합니다.");
                return;
            }

            try {
                peopleNum = Integer.parseInt(num);
            } catch(NumberFormatException e) {
                System.out.println("[오류] 총 인원수는 1000 이하의 정수여야 합니다.");
                return;
            }

            if (peopleNum < 1) {
                System.out.println("[오류] 총 인원수는 1 이상의 정수여야 합니다.");
                return;
            }
            else if (peopleNum > 1000) {
                System.out.println("[오류] 총 인원수는 1000 이하의 정수여야 합니다.");
                return;
            }

            String[] storeInfo = waitingStores.get(storeNum - 1);
            String storeName = storeInfo[0];
            System.out.println();
            System.out.print(storeName + " 매장에 " + peopleNum + " 명이 웨이팅하겠습니까? (Yes/No): ");
            select = scan.nextLine();

            if (select.equals("No")) {
                return;
            }
            else {
                if (isUpdating) {
                    updateWaitingOrder(ID, oldStoreName, oldWaitingNum);
                }
                ArrayList<String[]> waitingList = new ArrayList<>();
                database.waiting = new Scanner(new File("waiting.txt"));
                while (database.waiting.hasNextLine()) {
                    String line = database.waiting.nextLine();
                    String[] part = line.split("\t");
                    if (part[0].equals(storeName)) {
                        waitingNum++;
                    }
                    waitingList.add(part);
                }

                waitingList.add(new String[]{storeName, ID, String.valueOf(waitingNum), String.valueOf(peopleNum)});
                PrintWriter writer = new PrintWriter(new FileWriter("waiting.txt"));
                for (String[] entry : waitingList) {
                    writer.println(String.join("\t", entry));
                }
                writer.close();
                System.out.println();
                System.out.println(ID + " 님의 현재 웨이팅 순서는 " + waitingNum + " 번입니다.");
            }
        } catch (Exception e) {
            System.out.println("데이터베이스에 문제가 있습니다. 프로그램을 종료합니다.");
            System.exit(0);
        } finally {
            if (database.store != null) {
                database.store.close();
            }
            if (database.waiting != null) {
                database.waiting.close();
            }
        }
    }

    private void updateWaitingOrder(String ID, String storeName, int waitingNum) { // 사용자가 원래 웨이팅하던 매장에 다시 웨이팅할 때 사용자보다 순서 뒤인 사람 순서 1씩 줄임.
        try {
            ArrayList<String[]> waitingList = new ArrayList<>();
            database.waiting = new Scanner(new File("waiting.txt"));
            while (database.waiting.hasNextLine()) {
                String line = database.waiting.nextLine();
                String[] part = line.split("\t");
                if (part[0].equals(storeName) && Integer.parseInt(part[2]) > waitingNum) {
                    part[2] = String.valueOf(Integer.parseInt(part[2]) - 1);
                }
                if (!part[1].equals(ID)) {
                    waitingList.add(part);
                }
            }
            PrintWriter writer = new PrintWriter(new FileWriter("waiting.txt"));
            for (String[] entry : waitingList) {
                writer.println(String.join("\t", entry));
            }
            writer.close();
        } catch (Exception e) {
            System.out.println("데이터베이스에 문제가 있습니다. 프로그램을 종료합니다.");
            System.exit(0);
        } finally {
            if (database.waiting != null) {
                database.waiting.close();
            }
        }
    }
}
