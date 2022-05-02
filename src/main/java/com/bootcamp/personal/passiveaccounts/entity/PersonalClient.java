package com.bootcamp.personal.passiveaccounts.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document
public class PersonalClient {

    @Id
    private String id;

    private String documentType;
    private String documentNumber;
    private String firstName;
    private String lastName;
    private String profile;
    private String residenceAddress;
    private short registrationStatus;
    private Date insertionDate;
    private String fk_insertionUser;
    private String insertionTerminal;
}
