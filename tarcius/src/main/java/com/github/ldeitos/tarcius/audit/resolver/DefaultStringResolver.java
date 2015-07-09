package com.github.ldeitos.tarcius.audit.resolver;

import static com.github.ldeitos.tarcius.configuration.Constants.STRING_RESOLVER_ID;

import javax.enterprise.context.ApplicationScoped;

import com.github.ldeitos.tarcius.api.ParameterResolver;
import com.github.ldeitos.tarcius.qualifier.CustomResolver;

/**
 * Default implementation to {@link ParameterResolver} to {@link String#valueOf}
 * output.
 *
 * @author <a href=mailto:leandro.deitos@gmail.com>Leandro Deitos</a>
 *
 */
@ApplicationScoped
@CustomResolver(STRING_RESOLVER_ID)
public class DefaultStringResolver implements ParameterResolver<Object> {

	@Override
	public String resolve(Object input) {
		return String.valueOf(input);
	}

}
