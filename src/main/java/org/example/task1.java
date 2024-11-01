package org.example;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.Random;

/*
 * Игра Виселица. Ввод 1 буквы за раз. Если не угадал, то -1 жизнь
 * Всего жизней = 10 (стойка, перекладина, распорка, веревка, голова, тело, 2 руки и 2 ноги)
 * Обяз вывод загадываемого слова с пробелами и кол-во оставшихся жизней
 * Случайный выбор слова из списка слов-констант внутри программы.
 * */
public class task1 {
    //    public static final byte MAX_TRYES = 3;
//    public static final String ALFAVIT="[a-zA-Z]+";
    public static void main(String[] args) {
        byte MAX_TRYES = 3;
        String ALFAVIT="[a-zA-Z]+";
//        String ALFAVIT="[а-яА-ЯёЁ]+";
        byte lives=10;
        byte CLEAR_METHOD=3;    // значение для clearScr: 1 - Для Unix, 2 - для Win, 3 - псевдоочистка, иначе ANSI
        String[] arrWords = {
                "Dance",
                "cat",
                "mushrom",
                "bobr",
                "monday",
                "saturday"
        };
        // получение hideWord из массива псевдо-случайным образом
        Random rnd = new Random();
        int rndIndex = rnd.nextInt(arrWords.length);
        String hideWord = arrWords[rndIndex];

        hideWord = hideWord.toLowerCase();
//        System.out.print("Слово = \""+hideWord+"\"\n");
        byte size = 0; size += hideWord.length();
        boolean[] status = new boolean[size];   // по умолчанию значение false

        Scanner cin = new Scanner(System.in);
        clearScr(CLEAR_METHOD);
        System.out.print("Игра Висилица. Алфавит = \""+ALFAVIT+"\"\nТекущее слово:\n");
        while(true){
            if(checkWin(status)){   // Победа
                System.out.print(" *** Вы выиграли! ***\n Отгаданное слово целиком:\n\t");
                printWord(hideWord, status);
                break;
            }
            else if(lives < 1){     // Поражение
                System.out.print(" *** У вас не осталось жизней! Вы проиграли ***\n");
                break;
            }
            printWord(hideWord, status);
            System.out.println("Осталось жизней: "+lives);
            // получение нового символа в нижнем регистре
            String tempStr = "";
            tempStr = checkInChar(MAX_TRYES, cin, ALFAVIT);
            if ( tempStr.length() < 1 ){
                System.out.print("Вы исчерпали количество попыток. Завершение игры ...\n");
                break;
            }
            byte index = 0; index += hideWord.indexOf(tempStr);
            // Если символ не найден, то вернется -1
            if ( index >= 0 && status[index]==false ){
                // прочитанный символ найден в слове и он не открыт
                clearScr(CLEAR_METHOD);
                System.out.print(" Такая буква(\""+tempStr+"\") в слове есть. Открытие данной буквы ...\n");
                openAll(hideWord, tempStr, (byte)0, status);
            }
            else {
                clearScr(CLEAR_METHOD);
                System.out.print(" Такой буквы(\""+tempStr+"\") в слове нет! Минус жизнь ...\n");
                // минус жизнь
                lives--;
            }
        }
    }

    // открытие букв(ы) в слове
    public static void openAll(String str, String needCh, byte curIndex, boolean[] answers){
        if( curIndex < str.length()){
            byte nextIndex = 0; nextIndex+= str.indexOf(needCh, curIndex) + 1;
//            System.out.println("\tPosit= "+ (nextIndex-1));
            answers[nextIndex-1] = true;
            if ( nextIndex-1 == str.lastIndexOf(needCh) ) return;
            openAll(str, needCh, nextIndex, answers);
        }
    }

    //Проверка ввода от пользователя с ограничениями
    public static String checkInChar(byte tryes, Scanner input, String Alfavit){
        System.out.print("  Введите букву для проверки ответа:\t");
        String ansStr="";
        String tempStr = input.nextLine().toLowerCase();    //перевод ввода в нижний регистр
        // использование регулярного выражение для поиска подходящего символа
        Pattern pattern = Pattern.compile(Alfavit);
        Matcher matcher = pattern.matcher(tempStr);
        for (byte i = 0; i < tryes ; ++i) {
            if (tempStr.length() > 1)
                System.out.println("\tТ.к. длина ввода > 1 , будет использован первый подходящий символ");
            matcher = pattern.matcher(tempStr);
            if(matcher.find()) {
                // Подходящий символ найден
//                System.out.print("\t\tВведенный символ: \""+tempStr+"\"\n");
                ansStr += tempStr.charAt(matcher.start() );
                break;
            }
            else {
                System.out.println("\tНет подходящих значений! Попыток ввода осталось: " + (tryes - i - 1));
                System.out.print("\n  Введите букву для проверки ответа:\t");
                tempStr = "";
                tempStr = input.nextLine().toLowerCase();
            }
        }
        tempStr = "";   // Тип очистка переменной
        return ansStr;
    }

    // Функция проверки на победу
    public static boolean checkWin(boolean[] bukvi){
        if(bukvi.length < 1) {
            System.out.print("\tfunc_checkWin: Error input arguments\n");
            return false;
        }
        boolean answer = true;  // Нулевая гипотеза
        for(byte i=0; i < bukvi.length; i++){
            if(bukvi[i] == false){
                answer = false;
                break;
            }
        }
        return answer;
    }

    // Функция очистки консоли от предыдущего вывода
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

    // Функция вывода слова из String с "маской" в виде bool массива (false = закрыта буква)
    public static void printWord (String word, boolean[] bukvi){
        if(word.isEmpty() || word.length()<1 || bukvi.length!=word.length()) {
            System.out.print("\tfunc_printWord: Error input arguments\n");
            return;
        }
        for(byte i=0; i < word.length(); ++i){
            System.out.print("| ");
            if(bukvi[i] == true)
                System.out.print(word.charAt(i)+" ");
            else System.out.print("_ ");
        }
        System.out.print("|\n");
    }
}