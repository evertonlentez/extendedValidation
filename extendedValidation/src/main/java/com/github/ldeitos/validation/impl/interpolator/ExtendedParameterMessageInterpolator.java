package com.github.ldeitos.validation.impl.interpolator;

import static javax.validation.Validation.byDefaultProvider;

import java.util.Locale;

import javax.enterprise.context.ApplicationScoped;
import javax.validation.MessageInterpolator;

import com.github.ldeitos.validation.MessagesSource;

/**
 * ExtendedValidation default {@link MessageInterpolator} implementation.<br/>
 * Obtain the requested message from {@link MessagesSource} and send to default
 * interpolator from concrete BeanValidation API implementation in use.
 *
 * @author <a href=mailto:leandro.deitos@gmail.com>Leandro Deitos</a>
 *
 * @see MessagesSource
 * @see ExtendedParameterContext
 */
@ApplicationScoped
public class ExtendedParameterMessageInterpolator extends BaseInterpolator implements MessageInterpolator {

	/**
	 * Default interpolator from concrete BeanValidation API implementation in
	 * use.
	 */
	private MessageInterpolator delegate = byDefaultProvider().configure().getDefaultMessageInterpolator();

	/**
	 * Get message, using messageTemplate, in {@link MessagesSource} and
	 * delegate to default interpolator to parameters resolution.
	 */
	@Override
	public String interpolate(String messageTemplate, Context context) {
		String message = getMessageSource().getMessage(messageTemplate);

		return delegate.interpolate(message, new ExtendedParameterContext(context));
	}

	/**
	 * Get message , using messageTemplate, in {@link MessagesSource} and
	 * delegate to default interpolator to parameters resolution.
	 */
	@Override
	public String interpolate(String messageTemplate, Context context, Locale locale) {
		String message = getMessageSource().getMessage(messageTemplate, locale);

		return delegate.interpolate(message, new ExtendedParameterContext(context), locale);
	}
}
