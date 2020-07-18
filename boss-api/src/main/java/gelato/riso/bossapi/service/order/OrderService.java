package gelato.riso.bossapi.service.order;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.stereotype.Service;

import gelato.riso.bossapi.service.order.Order.State;
import gelato.riso.bossapi.support.exception.BaseException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;

    public Mono<List<Order>> getOrders(SecurityContext context) {
        String storeId = context.getAuthentication().getCredentials().toString();
        return orderRepository.findAllByStoreIdAndStateIsNot(storeId, State.FINISHED)
                              .collectList();
    }

    public Mono<Order> start(SecurityContext context, String orderId, String storeId) {
        String userId = context.getAuthentication().getCredentials().toString();
        return editOrderState(userId, orderId, storeId, State.NOT_STARTED, State.STARTED);
    }

    public Mono<Order> finish(SecurityContext context, String orderId, String storeId) {
        String userId = context.getAuthentication().getCredentials().toString();
        return editOrderState(userId, orderId, storeId, State.STARTED, State.FINISHED);
    }

    private Mono<Order> editOrderState(
            String userId, String orderId, String storeId, State currentState, State newState) {
        if (false == storeId.equals(userId)) {
            return Mono.error(new NotAllowedOrderEditException());
        }

        return orderRepository.findByIdAndState(new ObjectId(orderId), currentState)
                              .filter(order -> order.getStoreId().equals(storeId))
                              .switchIfEmpty(Mono.error(new Exception()))
                              .map(order -> order.withState(newState))
                              .flatMap(orderRepository::save);

    }
    private static class NotAllowedOrderEditException extends BaseException {
        private static final long serialVersionUID = 1143674303077608995L;

        @Getter
        private final HttpStatus httpStatus = HttpStatus.BAD_REQUEST;
    }

}
