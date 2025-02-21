package userservice.feign.dto;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@Getter
public class PostIdListDto {
    private List<String> postIdList;

}
