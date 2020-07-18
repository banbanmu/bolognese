package gelato.riso.bossapi.service.order;

import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class OrderHandler {

    private final OrderService orderService;

    public Mono<ServerResponse> getOrders() {
        return ReactiveSecurityContextHolder
                .getContext()
                .flatMap(orderService::getOrders)
                .flatMap(orders -> ServerResponse.ok().bodyValue(orders));
    }

    public Mono<ServerResponse> start(ServerRequest request) {
        return Mono.zip(ReactiveSecurityContextHolder.getContext(),
                        request.bodyToMono(OrderStart.Request.class))
                   .map(tuple -> {
                       SecurityContext context = tuple.getT1();
                       OrderStart.Request param = tuple.getT2();
                       return orderService.start(context, param.orderId, param.storeId);
                   }).flatMap(order -> ServerResponse.ok().bodyValue(order));
    }

    public Mono<ServerResponse> finish(ServerRequest request) {
        return Mono.zip(ReactiveSecurityContextHolder.getContext(),
                        request.bodyToMono(OrderFinish.Request.class))
                   .map(tuple -> {
                       SecurityContext context = tuple.getT1();
                       OrderFinish.Request param = tuple.getT2();
                       return orderService.finish(context, param.orderId, param.storeId);
                   }).flatMap(order -> ServerResponse.ok().bodyValue(order));
    }

    private static class OrderStart {
        @Value
        @Builder
        private static class Request {
            String storeId;
            String orderId;
        }
    }

    private static class OrderFinish {
        @Value
        @Builder
        private static class Request {
            String storeId;
            String orderId;
        }
    }
}