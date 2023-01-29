package com.jwj.community.web.dto.member.auth.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class MemberRolesCreate {

    private Long memberId;
    private Long roleId;

}
