package gelato.riso.api.service.order;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Builder;
import lombok.Value;
import lombok.With;

@Value
@Builder
@Document
public class Order {
    @Id
    ObjectId id;
    Integer storeId;
    String userId;
    String address;
    String phoneNumber;
    String menuName;
    @With
    State state;

    public String getId() {
        return id.toHexString();
    }

    public enum State {
        NOT_STARTED,
        STARTED,
        FINISHED
    }
}
