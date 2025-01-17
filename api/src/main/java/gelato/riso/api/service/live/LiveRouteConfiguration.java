package gelato.riso.api.service.live;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class LiveRouteConfiguration {

    private final LiveHandler liveHandler;

    @Bean
    public RouterFunction<?> liveRouterFunction() {
        return RouterFunctions.route()
                              .GET("/live/start", request -> liveHandler.start())
                              .POST("/live/stop", liveHandler::stop)
                              .GET("/internal/live/list", request -> liveHandler.list())
                              .build();
    }

}
