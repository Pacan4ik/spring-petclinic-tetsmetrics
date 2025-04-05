package org.springframework.samples.petclinic;

import io.prometheus.metrics.core.metrics.Gauge;
import io.prometheus.metrics.exporter.pushgateway.PushGateway;
import org.junit.jupiter.api.extension.AfterTestExecutionCallback;
import org.junit.jupiter.api.extension.BeforeTestExecutionCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

import java.io.IOException;
import java.time.Instant;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class TestMetricWatcher implements BeforeTestExecutionCallback, AfterTestExecutionCallback {
	private static final ConcurrentMap<String, Instant> startTimes = new ConcurrentHashMap<>();

	private static PushGateway pushGateway = PushGateway.builder()
		.address("localhost:9091")
		.job("test")
		.build();

	private static Gauge gauge = Gauge.builder().name("test_execution_time")
		.help("Time taken to execute a test")
		.labelNames("name", "status", "exception", "test_class", "short_class_name")
		.register();

	@Override
	public void beforeTestExecution(ExtensionContext context) {
		startTimes.put(context.getUniqueId(), Instant.now());
	}

	@Override
	public void afterTestExecution(ExtensionContext context) {
		Instant startTime = startTimes.remove(context.getUniqueId());
		if (startTime == null) return;

		long duration = Instant.now().toEpochMilli() - startTime.toEpochMilli();
		String testName = context.getDisplayName();

		Optional<Throwable> optionalExecutionException = context.getExecutionException();
		String status = optionalExecutionException.map(throwable -> "failed")
			.orElse("succeeded");

		String exception = optionalExecutionException.map(throwable -> throwable.getClass().getSimpleName())
			.orElse("none");

//		Timer.builder("test_execution_time")
//			.description("Time taken to execute a test")
//			.tags("name", testName,
//				"status", status,
//				"exception", exception)
//			.register()
//			.record(duration, TimeUnit.MILLISECONDS);


		gauge
			.labelValues(testName,
				status,
				exception,
				context.getTestClass().get().getName(),
				context.getTestClass().get().getSimpleName())
			.set(duration);


		try {
			pushGateway.push();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

	}
}
