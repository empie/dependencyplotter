package net.empuly.maven.plugin.dependencyplotter;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Arrays;

import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.MultipleFailureException;
import org.junit.runners.model.Statement;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

public class MockitoRule implements TestRule {

	final Object testClass;

	public MockitoRule(Object testClass) {
		checkNotNull(testClass);
		this.testClass = testClass;
	}

	public Statement apply(final Statement base, final Description description) {
		return new Statement()
		{
			@Override
			public void evaluate() throws Throwable {
				MockitoAnnotations.initMocks(testClass);
				Throwable throwable = null;
				try {
					base.evaluate();
				} catch (final Throwable t) {
					throwable = t;
					throw t;
				} finally {
					try {
						Mockito.validateMockitoUsage();
					} catch (final Throwable t) {
						if (throwable != null) {
							throw new MultipleFailureException(Arrays.asList(new Throwable[] { throwable, t }));
						}
						else {
							throw t;
						}
					}
				}
			}
		};
	}
}
