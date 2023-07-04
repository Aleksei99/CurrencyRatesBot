package com.smuraha.service.impl;

import com.smuraha.model.AppUser;
import com.smuraha.model.dto.UpdateWithUserDto;
import com.smuraha.service.UpdateProducer;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;

@Service
@RequiredArgsConstructor
public class UpdateProducerImpl implements UpdateProducer {

    private final RabbitTemplate rabbitTemplate;

    @Override
    public void produce(String queue, Update update) {
        rabbitTemplate.convertAndSend(queue, update);
    }

    @Override
    public void produce(String queue, Update update, AppUser user) {
        UpdateWithUserDto dto = new UpdateWithUserDto(update, user);
        rabbitTemplate.convertAndSend(queue, dto);
    }
}
