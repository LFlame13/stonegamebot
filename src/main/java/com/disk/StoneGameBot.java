package com.disk.stonegm;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.CallbackQuery;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import com.pengrad.telegrambot.request.SendMessage;

import java.util.Random;

public class StoneGameBot {
    // Инициализация бота с вашим токеном
    private final TelegramBot bot = new TelegramBot("BOT API");

    public static void main(String[] args) {
        new StoneGameBot().start();
    }

    public void start() {
        // Настройка обработки обновлений
        bot.setUpdatesListener(updates -> {
            for (Update update : updates) {
                handleUpdate(update);
            }
            return UpdatesListener.CONFIRMED_UPDATES_ALL;
        });
    }

    private void handleUpdate(Update update) {
        if (update.message() != null) {
            handleMessage(update.message());
        } else if (update.callbackQuery() != null) {
            handleCallback(update.callbackQuery());
        }
    }

    private void handleMessage(Message message) {
        String text = message.text();
        long chatId = message.chat().id();

        if ("/start".equals(text)) {
            bot.execute(new SendMessage(chatId, "Добро пожаловать в игру 'Камень, ножницы, бумага'! Через 5 секунд начнём.")
                    .replyMarkup(new InlineKeyboardMarkup()));  // Пустая клавиатура для скрытия предыдущих кнопок

            // Таймер
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            sendGameOptions(chatId);
        }
    }

    private void sendGameOptions(long chatId) {
        InlineKeyboardMarkup keyboard = new InlineKeyboardMarkup(
                new InlineKeyboardButton("\uD83E\uDEA8").callbackData("Камень"),
                new InlineKeyboardButton("✂\uFE0F").callbackData("Ножницы"),
                new InlineKeyboardButton("\uD83D\uDCDD").callbackData("Бумага")
        );

        bot.execute(new SendMessage(chatId, "Сделай свой выбор:").replyMarkup(keyboard));
    }

    private void handleCallback(CallbackQuery callbackQuery) {
        String userChoice = callbackQuery.data();
        long chatId = callbackQuery.message().chat().id();

        String[] options = {"Камень", "Ножницы", "Бумага"};
        String botChoice = options[new Random().nextInt(options.length)];

        String result;
        if (userChoice.equals(botChoice)) {
            result = "Ничья!";
        } else if (
                (userChoice.equals("Камень") && botChoice.equals("Ножницы")) ||
                        (userChoice.equals("Ножницы") && botChoice.equals("Бумага")) ||
                        (userChoice.equals("Бумага") && botChoice.equals("Камень"))
        ) {
            result = "Ты выиграл!";
        } else {
            result = "Бот выиграл!";
        }

        bot.execute(new SendMessage(chatId, "Ты выбрал: " + userChoice + "\n" +
                "Бот выбрал: " + botChoice + "\n" +
                "Результат: " + result));
    }
}
