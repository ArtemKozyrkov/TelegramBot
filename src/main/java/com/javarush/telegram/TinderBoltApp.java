package com.javarush.telegram;

import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import java.util.ArrayList;
import java.util.List;

public class TinderBoltApp extends MultiSessionTelegramBot {
    public static final String TELEGRAM_BOT_NAME = "";
    public static final String TELEGRAM_BOT_TOKEN = "";
    public static final String OPEN_AI_TOKEN = "";

    private final ChatGPTService chatGPTService = new ChatGPTService(OPEN_AI_TOKEN);
    private DialogMode currentMode = null;
    private final List<String> messages = new ArrayList<>();
    private UserInfo userInfo;
    private UserInfo she;
    private int questionsCount;

    public TinderBoltApp() {
        super(TELEGRAM_BOT_NAME, TELEGRAM_BOT_TOKEN);
    }

    @Override
    public void onUpdateEventReceived(Update update) {
        String message = getMessageText();

        if (message.equals("/start")) {
            currentMode = DialogMode.MAIN;
            sendPhotoMessage("main");
            sendTextMessage(loadMessage("main"));

            showMainMenu("Main", "/start",
                    "Generate Tinder-profile \uD83D\uDE0E", "/profile",
                    "Generate open message for tinder \uD83E\uDD70", "/opener",
                    "Generate answer \uD83D\uDE08", "/message",
                    "Chat with celebrities \uD83D\uDD25", "/date",
                    "Ask Chat-GPT \uD83E\uDDE0", "/gpt");
            return;
        }

        //Command GPT
        if (message.equals("/gpt")) {
            currentMode = DialogMode.GPT;
            sendPhotoMessage("gpt");
            sendTextMessage(loadMessage("gpt"));
            return;
        }

        if (currentMode == DialogMode.GPT && !isMessageCommand()) {
            String prompt = loadPrompt("gpt");
            Message msg = sendTextMessage("Please wait - ChatGPT \uD83E\uDDE0 is thinking...");
            String answer = chatGPTService.sendMessage(prompt, message);
            updateTextMessage(msg, answer);
            return;
        }

        //Command DATE
        if (message.equals("/date")) {
            currentMode = DialogMode.DATE;
            sendPhotoMessage("date");
            sendTextButtonsMessage(loadMessage("date"),
                    "Ariana Grande", "date_grande",
                    "Margo Robbie", "date_robbie",
                    "Zendaya", "date_zendaya",
                    "Ryan Gosling", "date_gosling",
                    "Tom Hardy", "date_hardy");
            return;
        }

        if (currentMode == DialogMode.DATE && !isMessageCommand()) {
            String query = getCallbackQueryButtonKey();
            System.out.println(query);
            if (query.startsWith("date_")) {
                sendPhotoMessage(query);
                sendTextMessage(" Good choose! \uD83D\uDE05 \nYour mission is invite opponent on date \uFE0F in 5 messages. *\nYou first:");
                chatGPTService.setPrompt(loadPrompt(query));
                return;
            }

            Message msg = sendTextMessage("Please wait your opponent is typing...");
            String answer = chatGPTService.addMessage(message);
            updateTextMessage(msg, answer);
            return;
        }

        //Command MESSAGE
        if (message.equals("/message")) {
            currentMode = DialogMode.MESSAGE;
            sendPhotoMessage("message");
            sendTextButtonsMessage(loadMessage("message"),
                    "Next message", "message_next",
                    "Invite to date", "message_date");
            return;
        }

        if (currentMode == DialogMode.MESSAGE && !isMessageCommand()) {
            String query = getCallbackQueryButtonKey();
            if (query.startsWith("message_")) {
                Message msg = sendTextMessage("Please wait - ChatGPT \uD83E\uDDE0 is thinking...");
                String answer = chatGPTService.sendMessage(loadPrompt(query), String.join("\n\n", messages));
                updateTextMessage(msg, answer);
            }

            messages.add(message);
            return;
        }

        //Command PROFILE
        if (message.equals("/profile")) {
            currentMode = DialogMode.PROFILE;
            sendPhotoMessage("profile");
            userInfo = new UserInfo();
            questionsCount = 0;
            sendTextMessage(loadMessage("profile"));
            sendTextMessage("Age?");
            return;
        }

        if (currentMode == DialogMode.PROFILE && !isMessageCommand()) {
            switch (questionsCount) {
                case 0 -> {
                    userInfo.age = message;
                    questionsCount = 1;
                    sendTextMessage("Job?");
                    return;
                }
                case 1 -> {
                    userInfo.occupation = message;
                    questionsCount = 2;
                    sendTextMessage("Hobby?");
                    return;
                }
                case 2 -> {
                    userInfo.hobby = message;
                    questionsCount = 3;
                    sendTextMessage("Thing you don't like in people?");
                    return;
                }
                case 3 -> {
                    userInfo.annoys = message;
                    questionsCount = 4;
                    sendTextMessage("Purpose of message?");
                    return;
                }
                case 4 -> {
                    userInfo.goals = message;
                    String aboutMe = userInfo.toString();
                    Message msg = sendTextMessage("Please wait - ChatGPT \uD83E\uDDE0 is thinking...");
                    String answer = chatGPTService.sendMessage(loadPrompt("profile"), aboutMe);
                    updateTextMessage(msg, answer);
                    return;
                }
            }

            return;
        }

        //OPENER
        if (message.equals("/opener")) {
            currentMode = DialogMode.OPENER;
            sendPhotoMessage("opener");
            sendTextMessage(loadMessage("opener"));
            she = new UserInfo();
            questionsCount = 0;
            sendTextMessage("Name?");
            return;
        }

        if (currentMode == DialogMode.OPENER && !isMessageCommand()) {
            switch (questionsCount) {
                case 0 -> {
                    she.name = message;
                    questionsCount = 1;
                    sendTextMessage("Age?");
                    return;
                }
                case 1 -> {
                    she.age = message;
                    questionsCount = 2;
                    sendTextMessage("Hobby?");
                    return;
                }
                case 2 -> {
                    she.hobby = message;
                    questionsCount = 3;
                    sendTextMessage("Job?");
                    return;
                }
                case 3 -> {
                    she.occupation = message;
                    questionsCount = 4;
                    sendTextMessage("Purpose of message?");
                    return;
                }
                case 4 -> {
                    she.goals = message;
                    String about = she.toString();
                    Message msg = sendTextMessage("Please wait - ChatGPT \uD83E\uDDE0 is thinking...");
                    String answer = chatGPTService.sendMessage(loadPrompt("opener"), about);
                    updateTextMessage(msg, answer);
                    return;
                }
            }
            return;
        }


        sendTextMessage("*Hi*");
        sendTextMessage("_Hi_");

        sendTextMessage("Message: " + message);

        sendTextButtonsMessage("Select mode", "Start", "start", "Stop", "stop");
    }

    public static void main(String[] args) throws TelegramApiException {
        TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
        telegramBotsApi.registerBot(new TinderBoltApp());
    }
}
