package com.intellias.bot;

import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.User;

import java.util.Scanner;

public class Main {

    private static final String KEY_NAME = "TELEGRAM_BOT_KEY";

    public static void main(String[] args) {

        String key = System.getenv(KEY_NAME);
        Scanner scanner = new Scanner(System.in);
        TelegramBotService telegramBotService = new TelegramBotService(key);
        String menu = new StringBuilder()
                .append("1 - Send message\n")
                .append("2 - Send song\n")
                .append("3 - send picture\n")
                .append("4 - send sticker \n")
                .append("5 - edit last message")
                .toString();

        while (true) {
            System.out.println(menu);
            System.out.println("Choose: ");
            int number = scanner.nextInt();
            switch (number) {
                case 1:
                    scanner.nextLine();
                    System.out.println("Enter text to send: ");
                    String text = scanner.nextLine();
                    telegramBotService.sendMessageForAll(text);
                    break;
                case 2:
                    telegramBotService.sendAudio("music.mp3");
                    break;
                case 3:
                    telegramBotService.sendPhoto("picture.png");
                    break;
                case 4:
                    telegramBotService.sendSticker("CAACAgIAAxkBAAEFl7Ni_Uzgtgvwj4vS_EsLx9ORwBR-_AACjgEAAiteUwswRKMveY_xOykE");
                    break;
                case 5:
                    User user = telegramBotService.getLastUser();

                    System.out.println("Last user received: " + user);

                    telegramBotService.sendUserProfilePhotos(user);
                    break;
            }
        }
    }
}
