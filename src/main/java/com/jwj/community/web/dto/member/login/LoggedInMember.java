package com.jwj.community.web.dto.member.login;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LoggedInMember {

    private final String email;
}
