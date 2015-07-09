package com.github.ldeitos.tarcius.audit.resolver;

import static com.github.ldeitos.tarcius.configuration.Constants.XML_FORMATTED_RESOLVER_ID;

import javax.enterprise.context.ApplicationScoped;

import com.github.ldeitos.tarcius.api.ParameterResolver;
import com.github.ldeitos.tarcius.qualifier.CustomResolver;

/**
 * Default implementation to {@link ParameterResolver} to formatted XML output.
 *
 * @author <a href=mailto:leandro.deitos@gmail.com>Leandro Deitos</a>
 *
 */
@ApplicationScoped
@CustomResolver(XML_FORMATTED_RESOLVER_ID)
public class DefaultFormattedXMLResolver extends DefaultXMLResolver {

	@Override
	protected boolean isOutputFormatted() {
		return true;
	}
}
