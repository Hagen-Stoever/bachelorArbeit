FROM quay.apps.cloud.internal/150106-frame/cbk-frame-springboot-jre11
COPY target/heartbeat-api-provider.jar /var/run/app/app.jar
RUN mkdir -p /logs && chmod 777 /logs
RUN echo "Docker build finished."