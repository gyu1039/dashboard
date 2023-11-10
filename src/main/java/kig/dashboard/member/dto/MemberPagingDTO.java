package kig.dashboard.member.dto;

import kig.dashboard.member.entity.Member;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
public class MemberPagingDTO {

    private int totalPageCount;
    private int currentPageNum;
    private long totalElementCount;
    private int currentPageElementCount;

    private List<MemberInfoDTO> memberList = new ArrayList<>();

    public MemberPagingDTO(Page<Member> searchResults) {

        this.totalPageCount = searchResults.getTotalPages();
        this.currentPageNum = searchResults.getNumber();
        this.totalElementCount = searchResults.getTotalElements();
        this.currentPageElementCount = searchResults.getNumberOfElements();
        memberList = searchResults.getContent().stream().map(MemberInfoDTO::new).collect(Collectors.toList());
    }
}
