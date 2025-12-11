package me.basiqueevangelist.blazingagenda;

public class BlazingAgendaUtil {
    public static boolean looksLikePng(byte[] data) {
        return data.length > 8 &&
            (data[0] & 0xff) == 0x89 && // 0x89
            data[1] == 0x50 && // P
            data[2] == 0x4E && // N
            data[3] == 0x47 && // G
            data[4] == 0x0D && // CR
            data[5] == 0x0A && // LF
            data[6] == 0x1A && // EOF
            data[7] == 0x0A;   // LF
    }
}
