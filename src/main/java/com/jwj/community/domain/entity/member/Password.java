package com.jwj.community.domain.entity.member;

import com.jwj.community.domain.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

import static com.jwj.community.utils.CommonUtils.relativeMonthFromNow;

@Entity
@Table(name = "PASSWORD_TB")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Password extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "char(60)")
    private String password;

    private LocalDateTime beChangedDate; // 비밀번호가 변경되어야 하는 날짜

    @Setter
    @OneToOne
    @JoinColumn(referencedColumnName = "id")
    private Member member;

    @Builder
    public Password(String password) {
        this.password = password;
    }

    public void extendBeChangedDate() {
        this.beChangedDate = relativeMonthFromNow(3);
    }
}
