package org.cheonyakplanet.be.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SubscriptionDTO {
    private String bsnsMbyNm; // 사업주체명 (시행사)
    private String cnstrctEntrpsNm; // 건설업체명 (시공사)

    private LocalDate cntrctCnclsBgnde; // 계약시작일
    private LocalDate cntrctCnclsEndde; // 계약종료일

    private LocalDate gnrlRnk1CrspareaEndde; // 1순위 해당지역 접수종료일
    private LocalDate gnrlRnk1CrspareaRcptde; // 1순위 해당지역 접수시작일

    private LocalDate gnrlRnk1EtcAreaEndde; // 1순위 기타지역 접수종료일
    private LocalDate gnrlRnk1EtcAreaRcptde; // 1순위 기타지역 접수시작일

    private LocalDate gnrlRnk1EtcGgEndde; // 1순위 경기지역 접수종료일
    private LocalDate gnrlRnk1EtcGgRcptde; // 1순위 경기지역 접수시작일

    private LocalDate gnrlRnk2CrspareaEndde; // 2순위 해당지역 접수종료일
    private LocalDate gnrlRnk2CrspareaRcptde; // 2순위 해당지역 접수시작일

    private LocalDate gnrlRnk2EtcAreaEndde; // 2순위 기타지역 접수종료일
    private LocalDate gnrlRnk2EtcAreaRcptde; // 2순위 기타지역 접수시작일

    private LocalDate gnrlRnk2EtcGgEndde; // 2순위 경기지역 접수종료일
    private LocalDate gnrlRnk2EtcGgRcptde; // 2순위 경기지역 접수시작일

    private String hmpgAdres; // 홈페이지 주소

    private String houseDtlSecd; // 주택상세구분코드
    private String houseDtlSecdNm; // 주택상세구분코드명

    private String houseManageNo; // 주택관리번호
    private String houseNm; // 주택명

    private String houseSecd; // 주택구분코드
    private String houseSecdNm; // 주택구분코드명

    private String hssplyAdres; // 공급위치
    private String hssplyZip; // 공급위치 우편번호

    private String imprmnBsnsAt; // 정비사업 여부
    private String lrsclBldlndAt; // 대규모 택지개발지구 여부

    private String mdatTrgetAreaSecd; // 조정대상지역 여부
    private String mdhsTelno; // 문의처 전화번호

    private String mvnPrearngeYm; // 입주예정월

    private String nplnPrvoprPublicHouseAt; // 수도권 내 민영 공공주택지구 여부
    private String parcprcUlsAt; // 분양가상한제 적용 여부

    private String pblancNo; // 공고번호
    private String pblancUrl; // 모집공고 상세 URL

    private LocalDate przwnerPresnatnDe; // 당첨자 발표일

    private String publicHouseEarthAt; // 공공주택지구 여부
    private String publicHouseSpclwApplcAt; // 공공주택 특별법 적용 여부

    private LocalDate rceptBgnde; // 청약 접수 시작일
    private LocalDate rceptEndde; // 청약 접수 종료일

    private LocalDate rcritPblancDe; // 모집공고일

    private String rentSecd; // 분양구분코드
    private String rentSecdNm; // 분양구분코드명

    private String specltRdnEarthAt; // 투기과열지구 여부

    private LocalDate spsplyRceptBgnde; // 특별공급 접수 시작일
    private LocalDate spsplyRceptEndde; // 특별공급 접수 종료일

    private String subscrptAreaCode; // 공급지역코드
    private String subscrptAreaCodeNm; // 공급지역명

    private Integer totSuplyHshldco; // 공급규모

    private String Region;
    private String city;
    private String district;
    private String detail;

}
