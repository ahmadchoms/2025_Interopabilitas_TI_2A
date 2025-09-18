import xmlrpc.client
import sys
import os
from typing import Optional, Tuple

class Colors:
    HEADER = '\033[95m'
    BLUE = '\033[94m'
    CYAN = '\033[96m'
    GREEN = '\033[92m'
    YELLOW = '\033[93m'
    RED = '\033[91m'
    ENDC = '\033[0m'
    BOLD = '\033[1m'
    UNDERLINE = '\033[4m'

class CalculatorClient:
    def __init__(self, server_url: str = "http://172.16.167.159:1717/RPC2"):
        self.server_url = server_url
        self.server = None
        self.operations = {
            '1': ('Penambahan', 'hitungPenjumlahan', '+'),
            '2': ('Pengurangan', 'hitungPengurangan', '-'),
            '3': ('Perkalian', 'hitungPerkalian', '×'),
            '4': ('Pembagian', 'hitungPembagian', '÷')
        }
        
    def connect(self) -> bool:
        try:
            self.server = xmlrpc.client.ServerProxy(self.server_url)
            return True
        except Exception as e:
            print(f"{Colors.RED}❌ Gagal terhubung ke server: {e}{Colors.ENDC}")
            return False
    
    def clear_screen(self):
        os.system('cls' if os.name == 'nt' else 'clear')
    
    def print_header(self):
        print(f"{Colors.CYAN}{Colors.BOLD}")
        print("╔" + "═" * 48 + "╗")
        print("║" + " " * 14 + "KLIEN KALKULATOR" + " " * 17 + "║")
        print("║" + " " * 10 + "Klien Terminal XML-RPC" + " " * 15 + "║")
        print("╚" + "═" * 48 + "╝")
        print(f"{Colors.ENDC}")
    
    def print_menu(self):
        print(f"{Colors.BLUE}Operasi yang Tersedia:{Colors.ENDC}")
        print(f"{Colors.YELLOW}1.{Colors.ENDC} Penambahan (+)")
        print(f"{Colors.YELLOW}2.{Colors.ENDC} Pengurangan (-)")
        print(f"{Colors.YELLOW}3.{Colors.ENDC} Perkalian (×)")
        print(f"{Colors.YELLOW}4.{Colors.ENDC} Pembagian (÷)")
        print(f"{Colors.YELLOW}5.{Colors.ENDC} Cek Kesehatan Server")
        print(f"{Colors.YELLOW}6.{Colors.ENDC} Ganti URL Server")
        print(f"{Colors.RED}0.{Colors.ENDC} Keluar")
        print("-" * 30)
    
    def get_number_input(self, prompt: str) -> Optional[int]:
        while True:
            try:
                value = input(f"{Colors.CYAN}{prompt}{Colors.ENDC}").strip()
                if value.lower() in ['q', 'quit', 'exit', 'keluar']:
                    return None
                return int(value)
            except ValueError:
                print(f"{Colors.RED}❌ Input harus berupa angka!{Colors.ENDC}")
            except KeyboardInterrupt:
                return None
    
    def perform_calculation(self, operation_key: str) -> bool:
        if operation_key not in self.operations:
            print(f"{Colors.RED}❌ Operasi tidak valid!{Colors.ENDC}")
            return False
        
        op_name, method_name, symbol = self.operations[operation_key]
        
        print(f"\n{Colors.GREEN}Operasi yang dipilih: {op_name} ({symbol}){Colors.ENDC}")
        print(f"{Colors.YELLOW}Tips: Ketik 'q' untuk keluar{Colors.ENDC}\n")
        
        x = self.get_number_input("Masukkan angka pertama: ")
        if x is None:
            return True
        
        y = self.get_number_input("Masukkan angka kedua: ")
        if y is None:
            return True
        
        if operation_key == '4' and y == 0:
            print(f"{Colors.RED}❌ Error: Tidak bisa membagi dengan nol!{Colors.ENDC}")
            input(f"\n{Colors.YELLOW}Tekan Enter untuk melanjutkan...{Colors.ENDC}")
            return True
        
        try:
            print(f"\n{Colors.YELLOW}⏳ Sedang menghitung...{Colors.ENDC}")
            
            method = getattr(self.server.server, method_name.replace('server.', ''))
            result = method(x, y)
            
            print(f"\n{Colors.GREEN}✅ Hasil:{Colors.ENDC}")
            print(f"{Colors.BOLD}{x} {symbol} {y} = {Colors.GREEN}{result}{Colors.ENDC}")
            
        except xmlrpc.client.Fault as e:
            print(f"{Colors.RED}❌ Error Server: {e.faultString}{Colors.ENDC}")
        except Exception as e:
            print(f"{Colors.RED}❌ Error Koneksi: {e}{Colors.ENDC}")
            print(f"{Colors.YELLOW}💡 Coba jalankan pemeriksaan kesehatan server untuk memverifikasi koneksi{Colors.ENDC}")
        
        input(f"\n{Colors.YELLOW}Tekan Enter untuk melanjutkan...{Colors.ENDC}")
        return True
    
    def health_check(self):
        print(f"\n{Colors.YELLOW}🔍 Memeriksa kesehatan server...{Colors.ENDC}")
        print(f"URL Server: {Colors.BLUE}{self.server_url}{Colors.ENDC}")
        
        try:
            if not self.server:
                self.connect()
            
            method = getattr(self.server, 'hitungPenjumlahan')
            test_result = method(1, 1)
            
            if test_result == 2:
                print(f"{Colors.GREEN}✅ Server sehat dan merespons dengan benar{Colors.ENDC}")
                print(f"{Colors.GREEN}✅ Perhitungan uji coba (1 + 1 = {test_result}) berhasil{Colors.ENDC}")
            else:
                print(f"{Colors.YELLOW}⚠️  Server merespons tetapi hasil tampaknya tidak benar{Colors.ENDC}")
                print(f"Diharapkan: 2, Diperoleh: {test_result}")
                
        except xmlrpc.client.Fault as e:
            print(f"{Colors.RED}❌ Kesalahan XML-RPC: {e.faultString}{Colors.ENDC}")
        except ConnectionError:
            print(f"{Colors.RED}❌ Tidak dapat terhubung ke server{Colors.ENDC}")
            print(f"{Colors.YELLOW}💡 Silakan periksa apakah server berjalan dan dapat diakses{Colors.ENDC}")
        except Exception as e:
            print(f"{Colors.RED}❌ Pemeriksaan kesehatan gagal: {e}{Colors.ENDC}")
        
        input(f"\n{Colors.YELLOW}Tekan Enter untuk melanjutkan...{Colors.ENDC}")
    
    def change_server_url(self):
        print(f"\n{Colors.BLUE}URL server saat ini: {Colors.YELLOW}{self.server_url}{Colors.ENDC}")
        new_url = input(f"{Colors.CYAN}Masukkan URL server baru (atau tekan Enter untuk membatalkan): {Colors.ENDC}").strip()
        
        if new_url:
            self.server_url = new_url
            print(f"{Colors.GREEN}✅ URL server berhasil diperbarui ke: {new_url}{Colors.ENDC}")
            
            print(f"{Colors.YELLOW}Menguji koneksi baru...{Colors.ENDC}")
            if self.connect():
                print(f"{Colors.GREEN}✅ Berhasil terhubung ke server baru{Colors.ENDC}")
            else:
                print(f"{Colors.RED}❌ Gagal terhubung ke server baru{Colors.ENDC}")
        else:
            print(f"{Colors.YELLOW}❌ URL server tidak diubah{Colors.ENDC}")
        
        input(f"\n{Colors.YELLOW}Tekan Enter untuk melanjutkan...{Colors.ENDC}")
    
    def run(self):
        if not self.connect():
            print(f"{Colors.RED}❌ Gagal membuat koneksi awal{Colors.ENDC}")
            print(f"{Colors.YELLOW}Anda masih dapat menggunakan aplikasi dan mengubah URL server dari menu{Colors.ENDC}")
            input(f"\n{Colors.YELLOW}Tekan Enter untuk melanjutkan...{Colors.ENDC}")
        
        while True:
            try:
                self.clear_screen()
                self.print_header()
                self.print_menu()
                
                choice = input(f"{Colors.CYAN}Masukkan pilihan Anda (0-6): {Colors.ENDC}").strip()
                
                if choice == '0':
                    print(f"\n{Colors.GREEN}👋 Terima kasih telah menggunakan Klien Kalkulator!{Colors.ENDC}")
                    break
                elif choice in ['1', '2', '3', '4']:
                    if not self.perform_calculation(choice):
                        break
                elif choice == '5':
                    self.health_check()
                elif choice == '6':
                    self.change_server_url()
                else:
                    print(f"{Colors.RED}❌ Pilihan tidak valid! Silakan pilih 0-6{Colors.ENDC}")
                    input(f"\n{Colors.YELLOW}Tekan Enter untuk melanjutkan...{Colors.ENDC}")
            
            except KeyboardInterrupt:
                print(f"\n\n{Colors.YELLOW}👋 Keluar dari Klien Kalkulator...{Colors.ENDC}")
                break
            except Exception as e:
                print(f"\n{Colors.RED}❌ Error tak terduga: {e}{Colors.ENDC}")
                input(f"\n{Colors.YELLOW}Tekan Enter untuk melanjutkan...{Colors.ENDC}")

def main():
    try:
        server_url = sys.argv[1] if len(sys.argv) > 1 else "http://172.16.167.159:1717/RPC2"
        
        print(f"{Colors.GREEN}Memulai Klien Kalkulator...{Colors.ENDC}")
        if len(sys.argv) > 1:
            print(f"Menggunakan URL server khusus: {Colors.BLUE}{server_url}{Colors.ENDC}")
        
        client = CalculatorClient(server_url)
        client.run()
        
    except Exception as e:
        print(f"{Colors.RED}❌ Gagal memulai aplikasi: {e}{Colors.ENDC}")
        sys.exit(1)

if __name__ == "__main__":
    main()