package userservice.feign;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@Getter
@AllArgsConstructor
public class PostCreateDateAndIdListDto {
    private List<String> postCreateDateAndIdList;

}
