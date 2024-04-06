package CatchTable;

import CatchTable.adminReservation;
import CatchTable.customerReservation;

public class management {
    database database;
    CatchTable.adminReservation adminReservation;
    CatchTable.customerReservation customerReservation;

    public management() {//웨이팅, 사장예약, 고객예약 클래스에 database 객체를 한번에 넘겨줌. 개별로는 너무 복잡
        this.database=new database();
        this.adminReservation=new adminReservation(this.database);
        this.customerReservation=new customerReservation(this.database);

    }
}
