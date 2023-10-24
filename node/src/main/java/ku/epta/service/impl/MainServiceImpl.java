package ku.epta.service.impl;

import ku.epta.dao.AppUserDAO;
import ku.epta.dao.RowDataDAO;
import ku.epta.entity.AppDocument;
import ku.epta.entity.AppPhoto;
import ku.epta.entity.AppUser;
import ku.epta.entity.RowData;
import ku.epta.exceptions.UploadFileException;
import ku.epta.service.AppUserService;
import ku.epta.service.FileService;
import ku.epta.service.MainService;
import ku.epta.service.ProducerService;
import ku.epta.service.enums.LinkType;
import ku.epta.service.enums.ServiceCommands;
import lombok.extern.log4j.Log4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;

import static ku.epta.entity.UserState.BASIC_STATE;
import static ku.epta.entity.UserState.WAIT_FOR_EMAIL_STATE;
import static ku.epta.service.enums.ServiceCommands.*;

@Service
@Log4j
public class MainServiceImpl implements MainService {
    private final RowDataDAO rowDataDAO;
    private final ProducerService producerService;
    private final AppUserDAO appUserDAO;
    private final FileService fileService;
    private final AppUserService appUserService;

    public MainServiceImpl(RowDataDAO rowDataDAO, ProducerService producerService, AppUserDAO appUserDAO, FileService fileService, AppUserService appUserService) {
        this.rowDataDAO = rowDataDAO;
        this.producerService = producerService;
        this.appUserDAO = appUserDAO;
        this.fileService = fileService;
        this.appUserService = appUserService;
    }

    @Override
    public void processTextMessage(Update update) {
        saveRowData(update);
        var appUser = findOrSaveAppUser(update);
        var userState = appUser.getState();
        var text = update.getMessage().getText();
        var output = "";

        var serviceCommand = ServiceCommands.fromValue(text);
        if (CANCEL.equals(serviceCommand)) {
            output = cancelProcess(appUser);
        } else if (BASIC_STATE.equals(userState)) {
            output = processServiceCommand(appUser, text);
        } else if (WAIT_FOR_EMAIL_STATE.equals(userState)) {
            output = appUserService.setEmail(appUser, text);
        } else {
            log.error("Unknown user state: " + userState);
            output = "Незвестная ошибка введите /cancel и попробуйте снова!";
        }

        var chatId = update.getMessage().getChatId();
        sendAnswer(output, chatId);


    }

    @Override
    public void processDocMessage(Update update) {
        saveRowData(update);
        var appUser = findOrSaveAppUser(update);
        var chatId = update.getMessage().getChatId();
        if (isNotAllowToSendContent(chatId, appUser)) {
            return;
        }

        try {
            AppDocument appDocument = fileService.processDoc(update.getMessage());
            var link = fileService.generateLink(appDocument.getId(), LinkType.GET_DOC);
            var answer = "Документ успешно загружен! Ссылка для скачивания: " + link;
            sendAnswer(answer, chatId);
        } catch (UploadFileException ex) {
            log.error(ex);
            var answer = "К сожилению, загрузка файла не удалась. Повторите попытку позже";
            sendAnswer(answer, chatId);
        }
    }

    @Override
    public void processPhotoMessage(Update update) {
        saveRowData(update);
        var appUser = findOrSaveAppUser(update);
        var chatId = update.getMessage().getChatId();
        if (isNotAllowToSendContent(chatId, appUser)) {
            return;
        }

        try {
            AppPhoto appPhoto = fileService.processPhoto(update.getMessage());
            var link = fileService.generateLink(appPhoto.getId(), LinkType.GET_PHOTO);
            var answer = "Фото успешно загружен! Ссылка для скачивания: " + link;
            sendAnswer(answer, chatId);
        } catch (UploadFileException ex) {
            log.error(ex);
            var answer = "К сожилению, загрузка фото не удалась. Повторите попытку позже";
            sendAnswer(answer, chatId);
        }
    }

    private boolean isNotAllowToSendContent(Long chatId, AppUser appUser) {
        var userState = appUser.getState();
        if (!appUser.getIsActive()) {
            var error = "Зарегитрируйтесь или активируйте свою учетную запись для загрузки контента";
            sendAnswer(error, chatId);
            return true;
        } else if (!BASIC_STATE.equals(userState)) {
            var error = "Отмените текущую команды с помощю /cancel для отправки файлов";
            sendAnswer(error, chatId);
            return true;
        }
        return false;
    }

    private void sendAnswer(String output, Long chatId) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText(output);
        producerService.producerAnswer(sendMessage);
    }

    private String processServiceCommand(AppUser appUser, String cmd) {
        if (REGISTRATION9.equals(cmd)) {
            return appUserService.registerUser(appUser);
        } else if (HELP.equals(cmd)) {
            return help();
        } else if (START.equals(cmd)) {
            return "Привествую! Чтобы посмотреть список команд введи /help";
        } else {
            return "Неизвестная команда! Чтобы посмотреть список команд введи /help";
        }
    }

    private String help() {
        return "Список доступных команд: \n"
                + "/cancel - отмена выполнения текущей команды\n"
                + "/registration - регистрация пользователя";
    }

    private String cancelProcess(AppUser appUser) {
        appUser.setState(BASIC_STATE);
        appUserDAO.save(appUser);
        return "Команда отменена";
    }

    private AppUser findOrSaveAppUser(Update update) {
        User telegramUser = update.getMessage().getFrom();
        var optionalAppUser = appUserDAO.findByTelegramUserId(telegramUser.getId());
        if (optionalAppUser.isEmpty()) {
            AppUser transientAppUser = AppUser.builder()
                    .telegramUserId(telegramUser.getId())
                    .firstName(telegramUser.getFirstName())
                    .lastName(telegramUser.getLastName())
                    .userName(telegramUser.getUserName())
                    .isActive(false)
                    .state(BASIC_STATE)
                    .build();

            return appUserDAO.save(transientAppUser);
        }
        return optionalAppUser.get();
    }

    private void saveRowData(Update update) {
        RowData rowData = RowData.builder()
                .event(update)
                .build();
        rowDataDAO.save(rowData);
    }
}
