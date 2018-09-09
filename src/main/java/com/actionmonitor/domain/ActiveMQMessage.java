package com.actionmonitor.domain;

import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@DynamicUpdate
@Table(name = "ACTIVEMQ_MSGS")
public class ActiveMQMessage implements Serializable {

    @Id
    private Long id;

    @Column(name = "container")
    private String container;

    @Column(name = "msgid_prod")
    private String msgidProd;

    @Column(name = "msgid_seq")
    private String msgidSeq;

    @Column(name = "expiration")
    private String expiration;

    @Lob
    @Column(name = "msg")
    private byte[] msg;

    @Column(name = "priority")
    private Long priority;

    @Column(name = "xid")
    private String xid;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getContainer() {
        return container;
    }

    public void setContainer(String container) {
        this.container = container;
    }

    public String getMsgidProd() {
        return msgidProd;
    }

    public void setMsgidProd(String msgidProd) {
        this.msgidProd = msgidProd;
    }

    public String getMsgidSeq() {
        return msgidSeq;
    }

    public void setMsgidSeq(String msgidSeq) {
        this.msgidSeq = msgidSeq;
    }

    public String getExpiration() {
        return expiration;
    }

    public void setExpiration(String expiration) {
        this.expiration = expiration;
    }

    public byte[] getMsg() {
        return msg;
    }

    public void setMsg(byte[] msg) {
        this.msg = msg;
    }

    public Long getPriority() {
        return priority;
    }

    public void setPriority(Long priority) {
        this.priority = priority;
    }

    public String getXid() {
        return xid;
    }

    public void setXid(String xid) {
        this.xid = xid;
    }
}
