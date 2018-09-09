package com.actionmonitor.repository;

import com.actionmonitor.domain.ActiveMQMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@SuppressWarnings("unused")
@Repository
public interface MessageRepository extends JpaRepository<ActiveMQMessage, Long> {

}
