package com.intellias.bot;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.PhotoSize;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.User;
import com.pengrad.telegrambot.model.request.*;
import com.pengrad.telegrambot.request.*;
import com.pengrad.telegrambot.response.*;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class TelegramBotService {
    private String key;
    private TelegramBot telegramBot;
    private List<Long> chatIds = new ArrayList<>();

    public TelegramBotService(String key) {
        this.key = key;
        telegramBot = new TelegramBot(key);

        GetUpdates getUpdates = new GetUpdates().limit(1).offset(0);
        GetUpdatesResponse updatesResponse = telegramBot.execute(getUpdates);
        List<Update> updates = updatesResponse.updates();

        updates.forEach(update -> {
            long chatId = update.message().chat().id();
            chatIds.add(chatId);
        });

        System.out.println("Initialized chat ids - " + chatIds);
    }

    public void sendMessageForAll(String text) {
        for (Long chatId : chatIds) {

            GetChat getChat = new GetChat(chatId);
            GetChatResponse getChatResponse = telegramBot.execute(getChat);
            String firstName =  getChatResponse.chat().firstName();

            SendMessage request = new SendMessage(chatId, text + " " + firstName)
                    .parseMode(ParseMode.HTML)
                    .disableWebPagePreview(true)
                    .disableNotification(true)
                    .replyMarkup(new ForceReply());

            SendResponse sendResponse = telegramBot.execute(request);
            System.out.println("Message response " + sendResponse.message());
        }
    }

    public void sendUserProfilePhotos(User user) {
        System.out.println("Send photos for user " + user.firstName());

        for (Long chatId: chatIds) {
            GetUserProfilePhotos getUserPhotos = new GetUserProfilePhotos(user.id());
            GetUserProfilePhotosResponse response = telegramBot.execute(getUserPhotos);
            PhotoSize photoSize = response.photos().photos()[0][0];

            System.out.println("Found user photo " + photoSize);

            SendPhoto sendPhoto = new SendPhoto(chatId, photoSize.fileId())
                    .caption("Your profile photo");

            SendResponse sendResponse = telegramBot.execute(sendPhoto);
            System.out.println("Received response from user photo " + sendResponse);
        }
    }

    public User getLastUser() {
        System.out.println("Getting last user from chats");

        GetUpdates getUpdates = new GetUpdates().limit(100).offset(0);
        GetUpdatesResponse updatesResponse = telegramBot.execute(getUpdates);
        List<Update> updates = updatesResponse.updates();
        for (Update update : updates) {
            return update.message().from();
        }

        return null;
    }

    public void deleteMessage(){

    }

    public void sendAudio(String audioPath) {
        File file = getFileFromResource(audioPath);
        for (Long chatId : chatIds) {
           SendAudio sendAudio = new SendAudio(chatId, file)
                   .title("Great song")
                   .caption("Listen to our music");

           SendResponse sendResponse = telegramBot.execute(sendAudio);

            System.out.println("Audio response " + sendResponse.message());
        }
    }

    public void sendPhoto(String photoPath) {
        File file = getFileFromResource(photoPath);
        for (Long chatId : chatIds) {
            SendPhoto sendPhoto = new SendPhoto(chatId,file);
            SendResponse sendResponse = telegramBot.execute(sendPhoto);
            System.out.println("Photo response " + sendResponse.message());
        }
    }

    public void sendSticker(String stickerId) {
        for (Long chatId : chatIds) {
            SendSticker sendSticker = new SendSticker(chatId, stickerId);
            SendResponse sendResponse = telegramBot.execute(sendSticker);
            System.out.println("Sticker response " + sendResponse);
        }
    }

    private File getFileFromResource(String path) {
        URL url = TelegramBotService.class.getClassLoader().getResource(path);
        if(url == null) {
            throw new IllegalArgumentException("File was not found by path");
        }
        return new File(url.getFile());
    }


}
