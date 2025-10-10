package com.icht.backfront.service;

import com.icht.backfront.model.Notify;
import org.apache.kafka.clients.consumer.ConsumerRecord;

public interface NotifyConsumer {
    void listen(ConsumerRecord<String, Notify> record);
}
