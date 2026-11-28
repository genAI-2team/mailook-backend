package com.mailook.closet.entity;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data 
@AllArgsConstructor 
@NoArgsConstructor 
public class StorageLocation {
    private long storageLocationId;
    private long userId;
    private String name;
    private Date deletedAt;
    private Date createdAt;





}
