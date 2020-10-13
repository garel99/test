package test;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

class Calculator implements TelephoneBillCalculator{


    /**
     * calculate
     * @param csv
     * @return
     */
    public  BigDecimal calculate(String csv) {

    	BigDecimal total = new BigDecimal(0);
        
        
        String[] lines = csv.split("\n");

        HashMap<String, Integer[]> phonesTime = new HashMap<>();

        Arrays.stream(lines).map(s -> s.split(",")).forEach(line -> {
            String phone = line[0];
            int  startTime = timeToSecs(line[1]);
            int  endTime = timeToSecs(line[2]);            
            int time = endTime - startTime;
            int rate;
            if(isTimeWith_in_Interval(line[1])) {
            	rate = 1;
            } else {
            	rate = 2;
            }
            Integer[] vals = {time, rate};
            phonesTime.put(phone, vals);
            
        });
        
        HashMap<String, Integer> phonesCount = new HashMap<>();
        
        for (Map.Entry<String, Integer[]> entry : phonesTime.entrySet()) {
            String key = entry.getKey();
            
            if(phonesCount.containsKey(key)) {
            	phonesCount.put(key, phonesCount.get(key)+1);
            }else {
            	phonesCount.put(key, 1);
            }
        }
        
        String maxkey = Collections.max(phonesCount.entrySet(), Map.Entry.comparingByValue()).getKey();
        phonesTime.remove(maxkey);
        
        for (Map.Entry<String, Integer[]> entry : phonesTime.entrySet()) {
            
            Integer[] value = entry.getValue();
            total = total.add(getTotalByRate(value[0], value[1]));
        }        

        return total;
    }

    /**
     * Business logic
     * @param sec
     * @return
     */
    private BigDecimal getTotalByRate(int time, int rate) {
    	
    	BigDecimal val = new BigDecimal(0);
    	
    	float calc = Math.round(time/60);
    	float calc2;
        if(rate==1) {
        	if(calc>5) {
        	    calc2 = (float) (5+(calc-5)*0.2);
        	} else {
        		calc2 = calc;
            }
        	val = BigDecimal.valueOf(calc2);
        }else {
        	if(calc>5) {
        	    calc2 = (float) ((5*0.5)+(calc-5)*0.2);
        	} else {
        		calc2 = (float) (calc*0.5);
            }
        }
        val = BigDecimal.valueOf(calc2);
        return val;
    }

    /**
     *  timeToSecs
     * @param time
     * @return
     */
    private static int timeToSecs(String  time) {
        DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
        Date date = null;
        Date reference = null;
        long seconds = 0;
        try {
            reference = dateFormat.parse("00:00:00");
            String[] t = time.split(" ");
            date = dateFormat.parse(t[1]);
            seconds = (date.getTime() - reference.getTime()) / 1000L;
        }catch ( Exception ex){
        	System.err.println(ex);
        }
        
        return Math.toIntExact(seconds);

    }
    
    public static boolean isTimeWith_in_Interval(String valueToCheck) {
        boolean isBetween = false;
        String startTime = "08:00:00";
        String endTime = "16:00:00";
        try {
            Date time1 = new SimpleDateFormat("HH:mm:ss").parse(startTime);

            Date time2 = new SimpleDateFormat("HH:mm:ss").parse(endTime);
            
            String[] t = valueToCheck.split(" ");

            Date d = new SimpleDateFormat("HH:mm:ss").parse(t[1]);

            if (time1.before(d) && time2.after(d)) {
                isBetween = true;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return isBetween;
    }

    public static void main(String[] args) {
        String S = "420774577453,13-01-2020 18:10:15,13-01-2020 18:12:57\n" + 
        		"420776562353,18-01-2020 08:59:20,18-01-2020 09:10:00";
        Calculator s = new Calculator();

        BigDecimal bill = s.calculate(S);

        System.out.println(bill);
    }


}
