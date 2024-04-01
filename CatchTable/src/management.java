public class management {
    database database;
    adminReservation adminReservation;
    customerReservation customerReservation;
    waiting waiting;
    public management() {
        this.database=new database();
        this.adminReservation=new adminReservation(this.database.getReserveManagement());
        this.customerReservation=new customerReservation(this.database.getReserve());
        this.waiting=new waiting(this.database.getWaiting());

    }
}
