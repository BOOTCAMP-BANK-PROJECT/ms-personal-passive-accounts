package com.bootcamp.personal.passiveaccounts.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document
public class AccountType {

    @Id
    private String id;
    private String description;
    private String abbreviation;
    private String isoCurrencyCode;
    private BigDecimal interesRate;
    private Integer transactionsNumber;
    private Double mainteanceCost;
    private short registrationStatus;
    private Date insertionDate;
    private String fk_insertionUser;
    private String insertionTerminal;

}
