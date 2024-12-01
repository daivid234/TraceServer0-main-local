package es.upm.dit.apsv.traceserver.restcontroller;

import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import es.upm.dit.apsv.traceserver.repository.TraceRepository;
import es.upm.dit.apsv.traceserver.model.Trace;

@RestController

public class TraceController {

  private final TraceRepository tr;

  public TraceController(TraceRepository tr) {

    this.tr = tr;

  }

  @GetMapping("/traces")

  List<Trace> all() {

    return (List<Trace>) tr.findAll();

  }

  @PostMapping("/traces")

  Trace newTraze(@RequestBody Trace newTrace) {

    return tr.save(newTrace);

  }

  @GetMapping("/traces/{id}")

  Trace one(@PathVariable String id) {

    return tr.findById(id).orElseThrow();

  }

  @PutMapping("/traces/{id}")

  Trace replaceTraze(@RequestBody Trace newTrace, @PathVariable String id) {

    return tr.findById(id).map(Trace -> {

      Trace.setTraceId(newTrace.getTraceId());

      Trace.setTruck(newTrace.getTruck());

      Trace.setLastSeen(newTrace.getLastSeen());

      Trace.setLat(newTrace.getLat());

      Trace.setLng(newTrace.getLng());

      return tr.save(Trace);

    }).orElseGet(() -> {

      newTrace.setTraceId(id);

      return tr.save(newTrace);

    });

  }

  @DeleteMapping("/traces/{id}")

  void deleteTraze(@PathVariable String id) {

    tr.deleteById(id);

  }

}