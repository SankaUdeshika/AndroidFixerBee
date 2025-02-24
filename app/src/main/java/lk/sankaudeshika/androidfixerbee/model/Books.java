package lk.sankaudeshika.androidfixerbee.model;

public class Books {
    String description;
    String status;
    String time;
    String date;
    String vendor_id;
    String vendorMobile;

    public Books(String description, String status, String time, String date, String vendor_id, String vendorMobile) {
        this.description = description;
        this.status = status;
        this.time = time;
        this.date = date;
        this.vendor_id = vendor_id;
        this.vendorMobile = vendorMobile;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getVendor_id() {
        return vendor_id;
    }

    public void setVendor_id(String vendor_id) {
        this.vendor_id = vendor_id;
    }

    public String getVendorMobile() {
        return vendorMobile;
    }

    public void setVendorMobile(String vendorMobile) {
        this.vendorMobile = vendorMobile;
    }
}
