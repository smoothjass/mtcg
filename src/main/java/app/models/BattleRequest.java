package app.models;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class BattleRequest {
    @JsonAlias({"id"})
    Integer battle_id;

    @JsonAlias({"requestingUser"})
    String username;

    public BattleRequest(String username) {
        setUsername(username);
    }
}
