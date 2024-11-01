package org.example;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/*
*********** Курс валют.
* У пользователя запрашивать сумму в float/double и конвертировать из одной валюты в другую
* Курсы тоже хранить в float/double (Пяти валют достаточно). Можно внутри программы, можно запрашивать
* Возможность постоянного запуска и изменения режима .
* */
public class task2 {
    public static void main(String[] args) {
//        final String ALFAVIT="[a-zA-Z]+";
        final String FLOATS="[0-9.,]+";
//        final byte CLEAR_METHOD=3;    // значение для clearScr: 1 - Для Unix, 2 - для Win, 3 - псевдоочистка, иначе ANSI
        final byte MAX_TRYES = 3;   // если указать 0, то будет бесконечное кол-во попыток
        final byte MIN_LEN = 1;
        tableMoneyChange temp = new tableMoneyChange();
        temp.addElem(64.6, "usd", "rub");
        temp.addElem(0.01547988, "rub", "usd");
        temp.addElem(102.4, "eur", "rub");
        temp.addElem(0.00976563, "rub", "eur");
        temp.addElem(0.63085938, "usd", "eur");
        temp.addElem(1.58513932, "eur", "usd");
        Scanner cin = new Scanner(System.in);
        menu(MAX_TRYES, temp, cin, FLOATS);
        cin.close();
    }

    public static class tableMoneyChange {
        //снизу структура 1-ой записи {float|String|String}
        private class elemTable{
            private double numbChange;
            private String fromMoney;
            private String toMoney;

            public elemTable(){ //Конструктор по умолчанию
                this.numbChange = 0.0;
                this.fromMoney = "";
                this.toMoney = "";
            }
            public elemTable(double numbCh, String fromM, String toM){
                this.numbChange = numbCh;
                this.fromMoney = fromM;
                this.toMoney = toM;
            }
            public double getNumbCh(){
                return this.numbChange;
            }
            public String getFromM(){
//                String newStr = new String(this.fromMoney);return newStr; // возвращение копии строки
                return this.fromMoney;
            }
            public String getToM(){
//                String newStr = new String(this.toMoney);return newStr; // возвращение копии строки
                return this.toMoney;
            }
//            public void printEl(){
//                System.out.printf("| %-5s | %-5s | %-5s |%n", this.numbChange, this.fromMoney, this.toMoney);
//            }
        }
        //        private elemTable[] table;
        private LinkedList<elemTable> table;
        private int countElem;

        public tableMoneyChange(){
            this.countElem = 0;
            this.table = null;
        }
        public int getLen(){
            return this.countElem;
        }
        public void addElem(double curs, String fromX, String toY){
            if (this.table == null){
                this.table = new LinkedList<elemTable>();
            }
            if(fromX.isEmpty() || toY.isEmpty() ){
//            if(fromX.isEmpty() || toY.isEmpty() || curs <= 0.0001f){
                System.out.println("tableMoneyChange::addElem: Error, Bad input arguments! Element not created\n");
                return;
            }
            elemTable temp = new elemTable(curs, fromX, toY);
            this.table.add(temp);
            this.countElem++;
            return;
        }

        public double getNumb(int index){
            if(this.countElem < 1 || this.table == null){
                return -0.1f;
            }
            return table.get(index).getNumbCh();
        }
        public void printElem(int index) {
            if (this.countElem < 1 || this.table == null || index < 0) return;
            if (index > countElem + 1) {

            }
            elemTable temp = table.get(index);
            //            temp.printEl();
            System.out.printf("| %-3s | %-10.5f | %-5s | %-5s |%n",
                    index,
                    temp.getNumbCh(),
                    temp.getFromM(),
                    temp.getToM()
            );
        }
        public void printAll() {
            if (this.countElem < 1 || this.table == null){
                System.out.print("Записей нет\n");
                return;
            }
            for (int i=0; i < this.countElem; ++i) {
                this.printElem(i);
            }
        }
    }
    public static void menu(byte tryes, tableMoneyChange table, Scanner cin, String floatAlfav){
        int mode=0;
        double answer = 0.0f;
        do {
            System.out.println("\n1 - Вывести таблицу курсов обмена валют\n2 - Ввести сумму и валюту для пересчёта денег"
                    +"\n3 - Вывести справку\n4 - Выход");
//            System.out.print("Выберите режим работы программы, введя соответствующую цифру:\t");
            String intAlfav = floatAlfav.replaceAll("[.,]", floatAlfav);
            mode = needRetry(0, 4, tryes, cin, intAlfav);
            switch (mode) {
            case (1): {
                System.out.println("|----------------------------|");
                System.out.printf("| %-10s | %-5s | %-5s |%n", "Курс", "Из", "В");
                System.out.println("|----------------------------|");
                table.printAll();
                System.out.println("|----------------------------|");
                break;
            }

            case (2): {
                double fTable = 0.0f;
                int innd = -1;
                table.printAll();
                System.out.println("\nВыберите валюту для обмена:");
                innd = needRetry(0, table.getLen(), tryes, cin, intAlfav);
                if( innd >= 0 )
                    fTable = table.getNumb(innd);
//                    System.out.println("\nВыберите валюту для обмена:");
                boolean infinityCycle = false;
                if (tryes == 0) {   //режим бесконечных попыток
                    infinityCycle = true;
                    tryes = 3;
                }

                int method = 2;
                if(!(answer == 0.0f)) {
                    System.out.println("\nТекущее значение суммы: "+answer);
                    System.out.println("1 - Использовать старое значение\n2 - Ввести новое значение\n");
                    method = needRetry(1, 2, tryes, cin, intAlfav);
                }
                if (method == 1) {
                    System.out.printf("\n\n| Сумма =  %-5.5f * %-5.5f = %-8.5f|%n", fTable, answer, (answer *fTable));
                }
                else if (method == 2) {
                    float fTemp = -1f;
                    for (byte i = 0; i < tryes; ) {
                        System.out.print("Введите желаемую сумму:\t");
                        fTemp = Math.abs((checkFloatIn(cin, floatAlfav)));
                        if (fTemp < 0.00001f) {
                            if (!infinityCycle) {
                                System.out.println("\tНет подходящих значений! Попыток ввода осталось: " + (tryes - i - 1));
                                ++i;
                            } else
                                System.out.println("\tНет подходящих значений! Попробуйте еще раз");
                        } else break;
                    }
                    answer = fTemp;
                    System.out.printf("\n\n| Сумма =  %-5.5f * %-5.5f = %-8.5f|%n", fTable, fTemp, (fTemp *fTable));
                }

                break;
            }
            case (3):{
                System.out.println("\nУ пользователя запрашивается сумма в float и конвертируется из одной валюты в другую"+
                "\nЕсли пользователь вводит что-то неправильное, то  будут взяты первые валидные данные из всего ввода"+
                "\nНо если введены абсолютно неверные значения(только символы вместо цифр), то количество попыток ограничено");
                break;
            }
            case (4):
                System.out.print("Выход из программы...\n");
                break;
            default:
                System.out.println("\nВы нашли посхалку. Возьмите с полки пирожок\n");
                break;
            }
        } while (mode != 4);
        return;
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
    public static float checkFloatIn(Scanner input, String Alfavit){
        float ansFloat = -1f;
        if(input == null || Alfavit.isEmpty()) {
            System.out.print("checkFloatIn:Error , Bad input arguments! End...\n");
            return ansFloat;
        }
        String ansStr="";
        String tempStr = input.nextLine().toLowerCase();    //перевод ввода в нижний регистр
        // использование регулярного выражение для поиска подходящего символа
        Pattern pattern = Pattern.compile(Alfavit);
        Matcher matcher = pattern.matcher(tempStr);
        int j = -1;
        for ( ; matcher.find(); j++) { //извлечение найденных по шаблону символов
            if (j == -1)
                j = matcher.start();  //начал значение первый индекс найденного вхождения
            if(j != matcher.start()) break; //отсечение отдельной группы символов
            ansStr += matcher.group();
        }
        tempStr = ansStr; ansStr="";
        boolean checkDot = false;   //0-гипотеза, что '.' или ',' нет в вводе
        char ch=0;
        //проверка на только одну точку или запятую
        for(byte i=0; i < tempStr.length(); ++i) {
            ch = tempStr.charAt(i);
            if (checkDot == true && (ch== '.' || ch== ','))
                break;
            else if (checkDot == false && (ch== '.' || ch== ','))
                checkDot = true;
            if (ch==',')    //замена на точку. P.s. можно было и в самом начале в String заменить на точки
                ansStr += '.';
            else
                ansStr += ch;
        }
        if(ansStr.length() > 0)
            ansFloat = Float.parseFloat(ansStr);
        tempStr = "";   // Тип очистка переменной
        return ansFloat;
    }
    public static void clearScr (byte method){
        switch (method) {
            case (1):
                try { //Очистка для Unix
                    //  Runtime.getRuntime().exec("clear");
                    new ProcessBuilder("/bin/bash", "-c", "clear").inheritIO().start().waitFor();
                } catch (Exception E) {
                    System.out.println(E);
                }
                break;
            case (2):
                try { //Очистка для Win
                    new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
                } catch (Exception E) {
                    System.out.println(E);
                }
                break;
            case (3):
                // псевдо-очистка (с помощью КОСТЫЛЯ)
                byte endlCount = 10;
                char[] cls= new char[endlCount];
                Arrays.fill(cls, '\n');
                System.out.print(cls);
                break;
            default:
                //Очистка с помощью управлеющего ANSI-кода
                System.out.print("\033[H\033[J");
                // System.out.print("\u001b[2J");
                System.out.flush();
                break;
        }
        return;
    }
}

