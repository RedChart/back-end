package userservice.feign.dto;


import java.util.List;

public class ServerPostDto {
    private List<String> createDateAndId;
    public ServerPostDto(List<String> postList){
        this.createDateAndId = postList;
    }
}
