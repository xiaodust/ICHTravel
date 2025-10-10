package com.icht.backfront.service.impl;

import com.icht.backfront.model.Notify;
import com.icht.backfront.service.NotifyConsumer;
import com.icht.backfront.service.NotifyHelper;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class NotifyConsumerImpl implements NotifyConsumer {
    @Autowired
    private KafkaTemplate<String, Notify> kafkaTemplate;
    @Autowired
    private NotifyHelper notifyHelper;

    @KafkaListener(topics = {"notify"})
    public void listen(ConsumerRecord<String, Notify> record) {
        Optional<Notify> kafkaMsg = Optional.ofNullable(record.value());
        if (kafkaMsg.isPresent()) {
            notifyHelper.sendNotify(kafkaMsg.get());
        }
    }
}
