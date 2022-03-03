package valerie.myModel;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented //生成文档信息的时候保留注解，对类作辅助说明
public @interface PermissionAnnotation {
}
