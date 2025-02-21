package postservice.feign.dto;


import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@Getter
public class PostIdListDto {
    private List<String> postIdList;
    public PostIdListDto(List<String> postIdList){
        this.postIdList = postIdList;
    }
}
