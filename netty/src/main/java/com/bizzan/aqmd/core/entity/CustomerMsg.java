package com.bizzan.aqmd.core.entity;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 
 * </p>
 *
 * @author sanfeng
 * @since 2020-04-04
 */

public class CustomerMsg implements Serializable {

    protected static final long serialVersionUID = 1L;


    protected Long id;
    protected Date updateTime;
    protected String createdBy;
    protected Date creationTime;
    protected String customerName;
    protected String email;
    /**
     * 0 身份证 1护照
     */
    protected Integer cardType;
    protected String idCardNo;
    /**
     * 银行卡主键id
     */
    protected Long bankDetailId;
    /**
     * 0锁定  1正常
     */
    protected Integer locked;
    protected String nickName;
    protected String password;
    protected String phone;
    protected String salt;
    protected String sex;
    /**
     * 代理商id和机构id
     */
    protected Long agentId;
    protected Long organId;

    /**
     * 操作备注
     */
    protected String opNote;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public Date getCreationTime() {
        return creationTime;
    }

    public void setCreationTime(Date creationTime) {
        this.creationTime = creationTime;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Integer getCardType() {
        return cardType;
    }

    public void setCardType(Integer cardType) {
        this.cardType = cardType;
    }

    public String getIdCardNo() {
        return idCardNo;
    }

    public void setIdCardNo(String idCardNo) {
        this.idCardNo = idCardNo;
    }

    public Integer getLocked() {
        return locked;
    }

    public void setLocked(Integer locked) {
        this.locked = locked;
    }


    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getOpNote() {
        return opNote;
    }

    public void setOpNote(String opNote) {
        this.opNote = opNote;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public Long getBankDetailId() {
        return bankDetailId;
    }

    public void setBankDetailId(Long bankDetailId) {
        this.bankDetailId = bankDetailId;
    }

    public Long getAgentId() {
        return agentId;
    }

    public void setAgentId(Long agentId) {
        this.agentId = agentId;
    }

    public Long getOrganId() {
        return organId;
    }

    public void setOrganId(Long organId) {
        this.organId = organId;
    }

    @Override
    public String toString() {
        return "CustomerMsg{" +
        ", id=" + id +
        ", updateTime=" + updateTime +
        ", createdBy=" + createdBy +
        ", creationTime=" + creationTime +
        ", customerName=" + customerName +
        ", email=" + email +
        ", cardType=" + cardType +
        ", idCardNo=" + idCardNo +
        ", locked=" + locked +
        ", nickName=" + nickName +
        ", password=" + password +
        ", phone=" + phone +
        ", salt=" + salt +
        ", sex=" + sex +
        ", opNote=" + opNote +
        "}";
    }
}
