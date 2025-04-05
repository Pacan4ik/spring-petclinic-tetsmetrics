package org.springframework.samples.petclinic;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.Assertions;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assumptions.assumeTrue;

@ExtendWith(TestMetricWatcher.class)
@SpringBootTest
public class WithMetricTests {

	@Test
	void successfulTest() {
		Assertions.assertTrue(true);
	}

	@Test
	@DisplayName("Successful")
	void successfulWithDisplayNameTest() {
		Assertions.assertTrue(true);
	}

	@Test
	void failedTest() {
		Assertions.fail("This test is intentionally failing for metrics.");
	}

	@Test
	void testWithRuntimeException() {
		throw new RuntimeException("This is a test exception.");
	}

	@Test
	void testWithAnotherRuntimeException() {
		throw new RuntimeException("This is a test exception.");
	}

	@Test
	void testWithYetAnotherRuntimeException() {
		throw new RuntimeException("This is a test exception.");
	}

	@Test
	void testWithException() throws Exception {
		throw new Exception("This is a test exception.");
	}

	@Test
	void skippedTest() {
		assumeTrue(false);
	}
}

