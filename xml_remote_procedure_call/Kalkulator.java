public class Kalkulator {
    int hasil = 0;

    public int hitungPenjumlahan(int x, int y) {
        hasil = x + y;
        System.out.println("Operasi Penjumlahan");
        System.out.println(x + " + " + y + " = " + hasil);
        return hasil;
    }

    public int hitungPengurangan(int x, int y) {
        hasil = x - y;
        System.out.println("Operasi Pengurangan");
        System.out.println(x + " - " + y + " = " + hasil);
        return hasil;
    }

    public int hitungPerkalian(int x, int y) {
        hasil = x * y;
        System.out.println("Operasi Perkalian");
        System.out.println(x + " * " + y + " = " + hasil);
        return hasil;
    }

    public int hitungPembagian(int x, int y) {
        hasil = x / y;
        System.out.println("Operasi Pembagian");
        System.out.println(x + " / " + y + " = " + hasil);
        return hasil;
    }
}
