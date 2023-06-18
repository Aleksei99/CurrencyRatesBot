package com.smuraha.service.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.smuraha.model.Bank;
import com.smuraha.model.CurrencyRate;
import com.smuraha.service.dto.CustomCallBack;
import com.smuraha.service.enums.CallBackKeys;
import com.smuraha.service.enums.CallBackParams;
import lombok.RequiredArgsConstructor;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.smuraha.service.enums.CallBackKeys.CB;
import static com.smuraha.service.enums.CallBackParams.P;

@Service
@RequiredArgsConstructor
public class TelegramUIImpl implements TelegramUI {

    private final JsonMapper jsonMapper;

    @Override
    public SendMessage getMessageWithButtons(List<List<InlineKeyboardButton>> currencyButtons, String text) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setParseMode(ParseMode.HTML);
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        inlineKeyboardMarkup.setKeyboard(currencyButtons);
        sendMessage.setReplyMarkup(inlineKeyboardMarkup);
        sendMessage.setText(text);
        return sendMessage;
    }

    @Override
    public String getBankFormedRates(Bank bank) {
        StringBuilder builder = new StringBuilder();
        CurrencyRate rate = bank.getRates().get(0);
        builder.append("<b>").append(bank.getBankName()).append("</b> \uD83D\uDCB0").append("\n")
                .append("❌ Сдать ").append(rate.getCurrency()).append(" : ").append(rate.getRateBuy()).append("\n")
                .append("✅ Купить ").append(rate.getCurrency()).append(" : ").append(rate.getRateSell()).append("\n")
                .append("\uD83D\uDD57").append(" ").append(rate.getLastUpdate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))).append("\n")
                .append("\n");
        return builder.toString();
    }

    @Override
    public List<InlineKeyboardButton> getCustomPager(CallBackKeys key, Map<CallBackParams, String> params, int page,int totalPages) throws JsonProcessingException {
        List<InlineKeyboardButton> pager = new ArrayList<>();
        if(page>0) {
            InlineKeyboardButton prev = new InlineKeyboardButton();
            prev.setText("←");
            params.put(P, (page - 1) + "");
            prev.setCallbackData(jsonMapper.writeCustomCallBackAsString(
                    new CustomCallBack(key, params)
            ));
            pager.add(prev);
        }
        InlineKeyboardButton cur = new InlineKeyboardButton();
        cur.setText(""+(page+1)+"/"+totalPages);
        cur.setCallbackData("IGNORE");
        pager.add(cur);
        if(page<totalPages-1){
            InlineKeyboardButton next = new InlineKeyboardButton();
            next.setText("→");
            params.put(P, (page + 1) + "");
            next.setCallbackData(jsonMapper.writeCustomCallBackAsString(
                    new CustomCallBack(key, params)
            ));
            pager.add(next);
        }
        return pager;
    }

    @Override
    public void drawChart(Map<LocalDate, List<BigDecimal>> data, String chatId) throws IOException {
        int width = 800;
        int height = 600;

        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = image.createGraphics();

        // Улучшаем качество отрисовки
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g2d.setColor(Color.WHITE);
        g2d.fillRect(0, 0, width, height);
        g2d.setColor(Color.BLACK);
        g2d.setFont(new Font("Arial", Font.PLAIN, 12));

        int padding = 70;
        int graphWidth = width - 2 * padding;
        int graphHeight = height - 2 * padding;

        // Определяем минимальное и максимальное значение на оси Y

        List<BigDecimal> listOfValues = data.values().stream().flatMap(List::stream).collect(Collectors.toList());
        BigDecimal maxValue = listOfValues.stream().max(BigDecimal::compareTo).get();
        BigDecimal minValue = listOfValues.stream().min(BigDecimal::compareTo).get();
        List<BigDecimal> setOfValues = new ArrayList<>();
        for (BigDecimal i = minValue; i.compareTo(maxValue) <= 0; i=i.add(new BigDecimal("0.05"))) {
            setOfValues.add(i);
        }

        // Определяем количество точек на графике
        int numPoints = data.size();

        // Определяем шаг по оси X
        double xStep = (double) graphWidth / (numPoints - 1);

        // Определяем шаг по оси Y
        double yStep = (double) graphHeight / (maxValue.subtract(minValue)).doubleValue();

        // Рисуем оси
        g2d.drawLine(padding, height - padding, padding, padding - 40);
        g2d.drawLine(padding, height - padding, width - padding, height - padding);

        // пдписываем ось y
        for (BigDecimal value : setOfValues) {
            int y = height - 30 - padding - ((value.subtract(minValue)).multiply(new BigDecimal(yStep))).intValue();
            g2d.drawLine(padding - 5, y, padding + 5, y);
            g2d.drawString(value.toString(), padding - 30, y);
        }

        // Рисуем точки и соединяем их линиями
        int x = padding;
        int yBuyPrev = 0;
        int ySellPrev = 0;
        for (Map.Entry<LocalDate, List<BigDecimal>> entry : data.entrySet()) {
            String key = entry.getKey().toString().substring(5);
            double buy = entry.getValue().get(0).doubleValue();
            double sell = entry.getValue().get(1).doubleValue();

            // Рисуем точку
            int yBuy = height - 30 - padding - (int) ((buy - minValue.doubleValue()) * yStep);
            int ySell = height - 30 - padding - (int) ((sell - minValue.doubleValue()) * yStep);
            g2d.fillOval(x - 2, yBuy - 2, 4, 4);
            g2d.fillOval(x - 2, ySell - 2, 4, 4);
            if (yBuyPrev != 0 && ySellPrev != 0) {
                g2d.setColor(Color.BLUE);
                g2d.drawLine((int) (x - xStep), yBuyPrev, x, yBuy);
                g2d.setColor(Color.RED);
                g2d.drawLine((int) (x - xStep), ySellPrev, x, ySell);
            }
            g2d.setColor(Color.BLACK);
            yBuyPrev = yBuy;
            ySellPrev = ySell;

            // Рисуем подпись по оси X (ключ из мапы)
            g2d.drawString(key, x - 10, height - padding + 25);
            g2d.drawLine(x, height - padding - 5, x, height - padding + 5);

            // Переходим к следующей точке по оси X
            x += xStep;
        }

        g2d.dispose();

        // Convert the image to bytes
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(image, "png", baos);
        byte[] imageBytes = baos.toByteArray();

        // Send the image to the Telegram bot
        sendImageToTelegramBot(imageBytes,chatId);
    }

    private void sendImageToTelegramBot(byte[] imageBytes,String chatId) throws IOException {
        // Telegram bot API endpoint
        String botToken = System.getenv("BOT_TOKEN");
        String botApiUrl = "https://api.telegram.org/bot" + botToken + "/sendPhoto";

        // Create an HTTP client
        CloseableHttpClient httpClient = HttpClients.createDefault();

        // Create an HTTP POST request with the image data
        HttpPost httpPost = new HttpPost(botApiUrl);
        ByteArrayBody imageBody = new ByteArrayBody(imageBytes, ContentType.IMAGE_PNG, "image.png");

        // Build the multipart/form-data request entity
        HttpEntity requestEntity = MultipartEntityBuilder.create()
                .addPart("chat_id", new StringBody(chatId, ContentType.TEXT_PLAIN))
                .addPart("photo", imageBody)
                .build();

        // Set the request entity
        httpPost.setEntity(requestEntity);

        // Execute the request
        CloseableHttpResponse response = httpClient.execute(httpPost);

        // Close the response and the HTTP client
        response.close();
        httpClient.close();
    }
}
