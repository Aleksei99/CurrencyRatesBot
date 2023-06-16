package com.smuraha.service.util;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
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
        double minValue = 2.8;
        double maxValue = 3.2;
        LocalDate startDate = LocalDate.of(2023, 6, 10);
        LocalDate endDate = LocalDate.now();

        // Calculate the number of days between the start and end dates
        long days = ChronoUnit.DAYS.between(startDate, endDate);

        // Calculate the height and width of the image based on the number of days
        int width = 800;
        int height = (int) (days * 20) + 100;

        // Create an image
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = image.createGraphics();
        g2d.setColor(Color.WHITE);
        g2d.fillRect(0, 0, width, height);
        g2d.setColor(Color.BLACK);
        g2d.setFont(new Font("Arial", Font.BOLD, 12));

        // Draw the Y-axis labels (dates)
        LocalDate currentDate = endDate;
        int y = 20;
        while (currentDate.isAfter(startDate) || currentDate.isEqual(startDate)) {
            String dateLabel = currentDate.toString();
            g2d.drawString(dateLabel, 10, y);
            currentDate = currentDate.minusDays(1);
            y += 20;
        }

        // Draw the X-axis labels (numbers)
        int x1 = 50;
        int x2 = width - 50;
        int y1 = height - 50;
        int y2 = height - 50;
        g2d.drawLine(x1, y1, x2, y2);
        g2d.setFont(new Font("Arial", Font.PLAIN, 10));
        double step = (maxValue - minValue) / 10.0;
        double value = minValue;
        int x = x1;
        while (value <= maxValue) {
            String valueLabel = String.format("%.2f", value);
            g2d.drawString(valueLabel, x - 10, y1 + 15);
            g2d.drawLine(x, y1 - 3, x, y1 + 3);
            value += step;
            x += (x2 - x1) / 10;
        }

        g2d.dispose();

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
                .addPart("chat_id", new StringBody("926023839",ContentType.TEXT_PLAIN))
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
