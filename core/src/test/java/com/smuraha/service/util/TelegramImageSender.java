package com.smuraha.service.util;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.net.URL;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;
import javax.imageio.ImageIO;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

public class TelegramImageSender {
    public static void main(String[] args) throws IOException {

        Map<LocalDate, List<Double>> map = new TreeMap<>();
        map.put(LocalDate.of(2023, 4, 1), List.of(2.5, 2.6));
        map.put(LocalDate.of(2023, 4, 5), List.of(2.6, 2.7));
        map.put(LocalDate.of(2023, 4, 6), List.of(2.7, 3.0));
        map.put(LocalDate.of(2023, 4, 4), List.of(2.4, 2.8));
        map.put(LocalDate.of(2023, 4, 3), List.of(2.2, 2.5));
        map.put(LocalDate.of(2023, 4, 8), List.of(2.8, 2.9));
        map.put(LocalDate.of(2023, 4, 10), List.of(2.6, 3.01));
        map.put(LocalDate.of(2023, 4, 12), List.of(2.7, 3.1));
        map.put(LocalDate.of(2023, 4, 11), List.of(2.8, 2.9));
        map.put(LocalDate.of(2023, 4, 13), List.of(2.6, 2.8));
        map.put(LocalDate.of(2023, 4, 15), List.of(2.7, 2.8));
        map.put(LocalDate.of(2023, 4, 14), List.of(2.8, 2.9));
        map.put(LocalDate.of(2023, 4, 16), List.of(2.85, 2.92));
        map.put(LocalDate.of(2023, 4, 17), List.of(2.81, 2.99));
        map.put(LocalDate.of(2023, 4, 18), List.of(2.75, 2.88));
        map.put(LocalDate.of(2023, 4, 19), List.of(2.80, 2.91));
        map.put(LocalDate.of(2023, 4, 20), List.of(2.8, 2.9));
        map.put(LocalDate.of(2023, 4, 21), List.of(2.8, 2.9));
        map.put(LocalDate.of(2023, 4, 22), List.of(2.8, 2.9));

        // Create an image
        BufferedImage image = drawGraph(map);

        // Convert the image to bytes
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(image, "png", baos);
        byte[] imageBytes = baos.toByteArray();

        // Send the image to the Telegram bot
        sendImageToTelegramBot(imageBytes);
    }

    private static void sendImageToTelegramBot(byte[] imageBytes) throws IOException {
        // Telegram bot API endpoint
        String botToken = "6252219537:AAFNRK_SsDMYQq00Vsy3LoGWM8JtMEgFAt4";
        String botApiUrl = "https://api.telegram.org/bot" + botToken + "/sendPhoto";

        // Create an HTTP client
        CloseableHttpClient httpClient = HttpClients.createDefault();

        // Create an HTTP POST request with the image data
        HttpPost httpPost = new HttpPost(botApiUrl);
        ByteArrayBody imageBody = new ByteArrayBody(imageBytes, ContentType.IMAGE_PNG, "image.png");

        // Build the multipart/form-data request entity
        HttpEntity requestEntity = MultipartEntityBuilder.create()
                .addPart("chat_id", new StringBody("926023839", ContentType.TEXT_PLAIN))
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

    private static BufferedImage drawGraph(Map<LocalDate, List<Double>> data) {
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

        List<Double> listOfValues = data.values().stream().flatMap(List::stream).collect(Collectors.toList());
        double maxValue = listOfValues.stream().max(Double::compareTo).get();
        double minValue = listOfValues.stream().min(Double::compareTo).get();
        List<BigDecimal> setOfValues = new ArrayList<>();
        for (BigDecimal i = new BigDecimal(minValue).setScale(2, RoundingMode.FLOOR); i.compareTo(new BigDecimal(maxValue).setScale(2,RoundingMode.FLOOR)) <= 0; i=i.add(new BigDecimal("0.05").setScale(2,RoundingMode.FLOOR))) {
            setOfValues.add(i);
        }

        // Определяем количество точек на графике
        int numPoints = data.size();

        // Определяем шаг по оси X
        double xStep = (double) graphWidth / (numPoints - 1);

        // Определяем шаг по оси Y
        double yStep = (double) graphHeight / (maxValue - minValue);

        // Рисуем оси
        g2d.drawLine(padding, height - padding, padding, padding - 40);
        g2d.drawLine(padding, height - padding, width - padding, height - padding);

        // пдписываем ось y
        for (BigDecimal value : setOfValues) {
            int y = height - 30 - padding - ((value.add(BigDecimal.valueOf(-minValue))).multiply(new BigDecimal(yStep))).intValue();
            g2d.drawLine(padding - 5, y, padding + 5, y);
            g2d.drawString(value.toString(), padding - 30, y);
        }

        // Рисуем точки и соединяем их линиями
        int x = padding;
        int yBuyPrev = 0;
        int ySellPrev = 0;
        for (Map.Entry<LocalDate, List<Double>> entry : data.entrySet()) {
            String key = entry.getKey().toString().substring(5);
            double buy = entry.getValue().get(0);
            double sell = entry.getValue().get(1);

            // Рисуем точку
            int yBuy = height - 30 - padding - (int) ((buy - minValue) * yStep);
            int ySell = height - 30 - padding - (int) ((sell - minValue) * yStep);
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

        return image;
    }
}
