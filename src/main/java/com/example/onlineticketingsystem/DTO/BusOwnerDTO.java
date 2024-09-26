package com.example.onlineticketingsystem.DTO;

import com.example.onlineticketingsystem.entity.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BusOwnerDTO {
    private int userID;
    private String name;
    private String email;
    private String contactNo;
//    private String password;
    private Role role;
    private int registrationNo;
    private String ownedBuses;
}
