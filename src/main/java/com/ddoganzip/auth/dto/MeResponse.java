package com.ddoganzip.auth.dto;

import com.ddoganzip.auth.entity.MemberGrade;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MeResponse {
    private Long id;
    private String email;
    private String name;
    private String address;
    private String phone;
    private MemberGrade memberGrade;
    private Integer orderCount;
}
