package com.mercadopago.android.px.addons.validator.internal;

import androidx.annotation.NonNull;
import java.util.List;

public abstract class RuleSet<T> implements Rule<T> {

    @Override
    public final boolean apply(@NonNull final T data) {
        return apply(data, applyWithOperator());
    }

    private boolean apply(@NonNull final T data, @NonNull final Operator operator) {
        boolean validation = operator == Operator.AND;
        for (final Rule<T> rule : getRules()) {
            validation = operator.evaluate(validation, rule.apply(data));
        }
        return validation;
    }

    protected abstract Operator applyWithOperator();

    abstract List<Rule<T>> getRules();

    enum Operator {
        OR {
            @Override
            public boolean evaluate(final boolean evaluated, final boolean toEvaluate) {
                return evaluated || toEvaluate;
            }
        }, AND {
            @Override
            public boolean evaluate(final boolean evaluated, final boolean toEvaluate) {
                return evaluated && toEvaluate;
            }
        };

        public abstract boolean evaluate(final boolean evaluated, final boolean toEvaluate);
    }
}