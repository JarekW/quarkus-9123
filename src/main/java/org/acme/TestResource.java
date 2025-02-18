package org.acme;

import io.quarkus.logging.Log;
import io.quarkus.narayana.jta.runtime.TransactionConfiguration;
import io.smallrye.common.annotation.Blocking;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.net.NetClient;
import io.vertx.core.net.NetClientOptions;
import io.vertx.core.net.NetSocket;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

@Path("/")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class TestResource {

    @Inject
    PostgresRepository repository;

    @GET
    @Transactional
    @TransactionConfiguration(timeout = 10)
    public Multi<TestEntity> findAll() {
        String entityId = "1";

        return Multi.createFrom().ticks().every(Duration.ofMillis(1000))
            .select()
            .first(3)
            .onOverflow().drop()
            .map(aLong -> {
                TestEntity entity = repository.findOne(entityId);
                if (entity == null) {
                    throw new RuntimeException("No entity found with id " + entityId);
                }

                List<String> names = Arrays.asList("mary", "jane");
                Random rand2 = new Random();
                entity.setName(names.get(rand2.nextInt(names.size())));

                repository.save(entity);

                return entity;
            })
        ;
    }

    @Inject
    SocketService socketService;

    @GET
    @Path("/test")
    @Blocking
    public Uni<Void> testConnectionClose() {
        return socketService.send();
    }
}
