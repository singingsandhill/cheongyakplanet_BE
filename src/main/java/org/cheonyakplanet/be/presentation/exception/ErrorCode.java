package org.cheonyakplanet.be.presentation.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    // 계정
    SIGN001("SIGN001","일치하는 이메일 없음"),
    SIGN002("SIGN002","중복된 이메일 존재"),
    SIGN003("SIGN003","관리자 가입 토큰 불일치"),
    SIGN004("SIGN004","로그인 정보 불일치"),

    // JWT
    AUTH001("AUTH001","유효하지 않은 JWT 서명"),
    AUTH002("AUTH002","만료된 토큰"),
    AUTH003("AUTH003","지원되지 않는 토큰"),
    AUTH004("AUTH004","claim is empty"),
    AUTH005("AUTH005","유효하지 않은 토큰"),
    AUTH010("AUTH010","토큰 없음"),


    // Other
    OTHER001("OTHER001","필터 부분 에러"),


    UNKNOWN_ERROR("UNK000","알 수 없는 에러 발생");

    private final String code;
    private final String message;

}
