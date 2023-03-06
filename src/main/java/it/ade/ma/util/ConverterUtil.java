package it.ade.ma.util;

import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;

@Component
public class ConverterUtil {

    private final SimpleDateFormat mmss = new SimpleDateFormat("mm:ss");

    public String mmss(long millis) {
        try {
            return mmss.format(new Timestamp(millis));
        } catch (Exception e) {
            return null;
        }
    }

}
