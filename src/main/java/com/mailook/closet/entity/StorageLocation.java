package com.mailook.closet.entity;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data 
@AllArgsConstructor 
@NoArgsConstructor 
public class StorageLocation {
    private long storage_location_id;
    private long user_id;
    private String name;
    private Date deleted_at;
    private Date created_at;





}
