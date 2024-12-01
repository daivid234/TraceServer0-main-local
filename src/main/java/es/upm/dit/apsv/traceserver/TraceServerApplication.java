package es.upm.dit.apsv.traceserver;

import java.util.function.Consumer;

import org.slf4j.Logger;

import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.SpringApplication;

import org.springframework.boot.autoconfigure.SpringBootApplication;

import org.springframework.context.annotation.Bean;

import org.springframework.web.client.HttpClientErrorException;

import org.springframework.web.client.RestTemplate;

import es.upm.dit.apsv.traceserver.model.Trace;

import es.upm.dit.apsv.traceserver.model.TransportationOrder;

import es.upm.dit.apsv.traceserver.repository.TraceRepository;

@SpringBootApplication

public class TraceServerApplication {

        

        public static final Logger log = LoggerFactory.getLogger(TraceServerApplication.class);

        private final TraceRepository tr;

        public TraceServerApplication(TraceRepository tr) {

                this.tr = tr;

        }

        public static void main(String[] args) {

                SpringApplication.run(TraceServerApplication.class, args);

                log.info("Prueba consumer arrancando...");

        }

        @Bean("consumer")

        public Consumer<Trace> checkTrace() {

                return t -> {

                        t.setTraceId(t.getTruck() + t.getLastSeen());
                        tr.save(t);
                        String uri = "http://localhost:8080/transportationorders/";
                        RestTemplate restTemplate = new RestTemplate();

                        TransportationOrder result = null;

                        try {                        

                          result = restTemplate.getForObject(uri

                           + t.getTruck(), TransportationOrder.class);

                        } catch (HttpClientErrorException.NotFound ex)   {

                                result = null;

                        }

                        if (result != null && result.getSt() == 0) {

                                result.setLastDate(t.getLastSeen());

                                result.setLastLat(t.getLat());

                                result.setLastLong(t.getLng());

                                if (result.distanceToDestination() < 10)

                                        result.setSt(1);

                                restTemplate.put(uri, result, TransportationOrder.class);

                                log.info("Order updated: "+ result);

                        }

                };

        }

}