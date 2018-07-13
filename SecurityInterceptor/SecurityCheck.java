package br.com.sisprog.auth.security;

import br.com.sisprog.auth.entity.SisgerOperacaoEnum;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 *
 * @author César Júnior
 */

@Retention(RUNTIME)
@Target({METHOD, TYPE})
public @interface SecurityCheck {
    @Deprecated String Entity() default "";
    String CodMenu() default "000";
    SisgerOperacaoEnum Operacao() default SisgerOperacaoEnum.CONSULTAR;
}
