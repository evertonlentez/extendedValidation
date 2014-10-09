package com.github.ldeitos.validators;

import java.math.BigDecimal;

import com.github.ldeitos.constraint.Digits;

public class DigitsValidatorImpl extends BigDecimalComparativeValidator<Digits> {
	private int integerPart;
	
	private int fractionPart;
	
	public void initialize(Digits constraintAnnotation) {
		integerPart = constraintAnnotation.integer();
		fractionPart = constraintAnnotation.fraction();
	}

	@Override
	protected boolean compareValid(BigDecimal n) {
		int integerPartLength = n.precision() - n.scale();
		int fractionPartLength = n.scale() < 0 ? 0 : n.scale();

		return ( integerPart >= integerPartLength && fractionPart >= fractionPartLength );
	}

}
