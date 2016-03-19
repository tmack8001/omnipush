package com.github.tmack8001.vertx;

import java.util.concurrent.TimeUnit;

import com.github.michalboska.vertx3.gcm.GcmNotification;
import com.github.michalboska.vertx3.gcm.GcmResponse;
import com.github.michalboska.vertx3.gcm.GcmService;
import com.github.michalboska.vertx3.gcm.GcmServiceConfig;
import com.github.tmack8001.push.BatchRequest;
import com.github.tmack8001.push.NotificationJob;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;

/**
 * Main {@link AbstractVerticle} for starting the Ommnipush Service
 *
 * @author Trevor Mack (drummer8001@gmail.com)
 * @since 1.0.0
 */
public class OmnipushVerticle extends AbstractVerticle {

    private static final String GCM_APIKEY = "gcm.apiKey";
    private static final String GCM_PACKAGE_NAME = "gcm.packageName";
    private static final String HTTP_PORT = "http.port";

    // TODO: integrate with redis store to schedule delivery of notifications
    private GcmService gcmService;

    @Override
    public void start(Future<Void> future) throws Exception {
        gcmService = GcmService.create(vertx, new GcmServiceConfig(config().getString(GCM_APIKEY)));

        Router router = Router.router(vertx);
        router.route().handler(BodyHandler.create());
        router.post("/batch/push").handler(this::batchPush);

        // Create the HTTP server and pass the "accept" method to the request handler.
        vertx
                .createHttpServer()
                .requestHandler(router::accept)
                .listen(
                        // Retrieve the port from the configuration,
                        // default to 8080.
                        config().getInteger(HTTP_PORT, 8080),
                        result -> {
                            if (result.succeeded()) {
                                future.complete();
                            } else {
                                future.fail(result.cause());
                            }
                        }
                );
    }

    /**
     * This is designed to load a JsonArray from the request body with the following list of values:
     * <code>
     * {
     *   "id":"id-of-batch",
     *   "dryRun":[
     *     optional
     *   ]  true  |false,
     *   "gcm":[
     *     {
     *       "devices": [
     *         "gcm-registration-id1",
     *         "gcm-registration-id2"
     *       ],
     *       "payload": { // any JsonObject for delivery to devices }
     *     },
     *     ...
     *   ]
     * }
     * </code>
     * <p/>
     *
     * @param routingContext - Server routing context object
     */
    private void batchPush(RoutingContext routingContext) {
        // Read the request's content and create an instance of BatchRequest.
        final BatchRequest batchRequest = Json.decodeValue(routingContext.getBodyAsString(),
                BatchRequest.class);

        for (NotificationJob job : batchRequest.getGcm()) {
            JsonObject payload = new JsonObject(job.payload);
            GcmNotification notification = new GcmNotification(job.devices, batchRequest.getId(), payload,
                    true, TimeUnit.DAYS.toSeconds(1), config().getString(GCM_PACKAGE_NAME, ""), batchRequest.getDryRun());

            gcmService.sendNotification(notification, ar -> {
                if (ar.succeeded()) {
                    GcmResponse result = ar.result();
                    Integer failureCount = result.getFailureCount();

                    routingContext.response()
                            .setStatusCode(failureCount == 0 ? HttpResponseStatus.CREATED.code() : HttpResponseStatus.INTERNAL_SERVER_ERROR.code())
                            .putHeader("content-type", "application/json; charset=utf-8")
                            .end(Json.encodePrettily(result));
                } else {
                    Throwable cause = ar.cause();
                    //error handling
                    routingContext.response()
                            .setStatusCode(HttpResponseStatus.INTERNAL_SERVER_ERROR.code())
                            .putHeader("content-type", "application/json; charset=utf-8")
                            .end(Json.encodePrettily(cause));
                }
            });
        }
    }
}
