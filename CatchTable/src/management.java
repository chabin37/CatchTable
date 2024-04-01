public class management {
    database database;
    adminReservation adminReservation;
    waiting
    public management() {
        this.database=new database();
        this.adminReservation=new adminReservation(this.database.reserveManagement);
    }
}
