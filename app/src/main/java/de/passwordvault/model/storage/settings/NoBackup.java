package de.passwordvault.model.storage.settings;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * Annotation can be used with fields of type {@link de.passwordvault.model.storage.settings.items.GenericItem}
 * within {@link Config} to indicate that a specific setting shall not be included within backups.
 *
 * @author  Christian-2003
 * @version 3.6.0
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface NoBackup {

}
