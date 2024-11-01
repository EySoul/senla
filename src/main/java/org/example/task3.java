package org.example;

import java.security.DrbgParameters;
import java.security.SecureRandom;
import java.security.Security;
import java.util.HashSet;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.security.DrbgParameters.Capability.PR_AND_RESEED;

public class task3 {
    public static void main(String[] args) {
//        String AllChars = RUS + ENG + DIGITS;
        final String DIGITS="[0-9]+";
        final byte MAX_TRYES = 3;   // если указать 0, то будет бесконечное кол-во попыток
        String AllChars = "qwertyuiopasdfghjklzxcvbnmQWERTYUIOPASDFGHJKLZXCVBNM0123456789!@#$%^&*()_+-=:;<>.,/|";
//        Security.setProperty("securerandom.drbg.config", "Hash_DRBG, SHA-512");
//        SecureRandom rnd = SecureRandom.getInstance(
//                "DRBG",
//                DrbgParameters.instantiation(256, PR_AND_RESEED, null)
//        );
        //для Linux использование источника рандома
        int lenPass = -1;
        byte kolvoPass = 5;

        Scanner cin = new Scanner(System.in);
        lenPass = needRetry(12, 18, MAX_TRYES, cin, DIGITS);
        if (lenPass <0) {
            System.out.print("Будет использована длина по-умолчанию = 18\n");
            lenPass = 18;
        }
        String os = System.getProperty("os.name").toLowerCase();
        if (os.contains("nix") || os.contains("nux") || os.contains("aix"))
            Security.setProperty("securerandom.source", "file:/dev/urandom");
        SecureRandom rnd = new SecureRandom();
        System.out.println("Алгоритм по-умочланию: "+rnd.getAlgorithm());

        System.out.println("\nВывод "+kolvoPass+" сгенерированных паролей");
        for(byte i =0; i < kolvoPass; ++i) {
            String pass = genPass(rnd, lenPass, AllChars);
            double entropy = calcEntropy(pass);
            System.out.print("Пароль (" + lenPass + ") = \"" + pass + "\"\n");
            System.out.printf("\tЭнтропия = %-6.3f %n", entropy);
        }
        return;
    }

    public static String genPass(SecureRandom rnd, int len, String Alfavit){
        StringBuffer pass = new StringBuffer(len);
        for(byte i=0; i < len; i++){
            int index = rnd.nextInt(Alfavit.length());
            pass.append(Alfavit.charAt(index));
        }
        return pass.toString();
    }

public static double calcEntropy(String password){
        int len = password.length();
        int chSetSize = getChSet(password);
        return Math.log(chSetSize) / Math.log(2)*len;
}

//получение кол-ва символов в пароле
public static int getChSet(String password){
    HashSet<Character> chSet = new HashSet<>();
    for (char i : password.toCharArray()){
        chSet.add(i);
    }
    return chSet.size();
}


    public static int needRetry(int minV, int maxV, byte tryes, Scanner input, String Alfavit){
        if( tryes < 0 || input == null || Alfavit.isEmpty()) {
            System.out.print("checkIntIn:Error , Bad input arguments! End...\n");
            return -1;
        }
        boolean infinityCycle = false;
        if (tryes == 0) {   //режим бесконечных попыток
            infinityCycle = true;
            tryes = 3;
        }
        int iTemp = -1;
        for(byte i =0; i < tryes; ) {
            System.out.print("  Введите необходимое число:\t");
            iTemp = checkIntegIn(input, Alfavit);
            if(iTemp < minV || iTemp > maxV) {
                if(!infinityCycle) {
                    System.out.println("\tНет подходящих значений! Попыток ввода осталось: " + (tryes - i - 1));
                    ++i;
                }
                else
                    System.out.println("\tНет подходящих значений! Попробуйте еще раз");
            }
            else break;
        }
//        System.out.println("\tБудет использовано= "+temp);
        return iTemp;
    }
    public static int checkIntegIn(Scanner input, String Alfavit){
        int ansInt = -1;
        if( input == null || Alfavit.isEmpty()) {
            System.out.print("checkIntIn:Error , Bad input arguments! End...\n");
            return ansInt;
        }
        String ansStr="";
        String tempStr = input.nextLine().toLowerCase();    //перевод ввода в нижний регистр
        // использование регулярного выражение для поиска подходящего символа
        Pattern pattern = Pattern.compile(Alfavit);
        Matcher matcher = pattern.matcher(tempStr);
        int i = -1;
        for ( ; matcher.find(); i++) { //извлечение найденных по шаблону символов
            if (i == -1)
                i = matcher.start();//начал значение первый индекс найденного вхождения
            if(i != matcher.start()) break; //отсечение отдельной группы символов
            ansStr += matcher.group();
        }
        if(ansStr.length() > 0)
            ansInt = Integer.parseInt(ansStr);
        tempStr = "";   // Тип очистка переменной
        return ansInt;
    }
}
