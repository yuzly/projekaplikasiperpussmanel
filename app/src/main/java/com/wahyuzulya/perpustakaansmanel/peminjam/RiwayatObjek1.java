package com.wahyuzulya.perpustakaansmanel.peminjam;

public class RiwayatObjek1 {
    String nomorBuku=" ";
    String judul=" ";
    String tanggalPinjam=" ";
    String tanggalKembali=" ";
    String denda=" ";
    String img=" ";
    String statusBuku="";

    public RiwayatObjek1(String nomorBuku, String judul, String tanggalPinjam, String tanggalKembali, String denda, String img,String statusBuku) {
        this.nomorBuku = nomorBuku;
        this.judul = judul;
        this.tanggalPinjam = tanggalPinjam;
        this.tanggalKembali = tanggalKembali;
        this.denda = denda;
        this.img = img;
        this.statusBuku = statusBuku;
    }

    public String getStatusBuku(){
        return statusBuku;
    }
    public String getImg(){
        return img;
    }
    public String getNomorBuku() {
        return nomorBuku;
    }

    public String getJudul() {
        return judul;
    }

    public String getTanggalPinjam() {
        return tanggalPinjam;
    }

    public String getTanggalKembali() {
        return tanggalKembali;
    }

    public String getDenda() {
        return denda;
    }
}
