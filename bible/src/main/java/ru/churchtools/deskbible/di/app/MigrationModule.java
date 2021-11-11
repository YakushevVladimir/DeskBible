package ru.churchtools.deskbible.di.app;

import android.content.Context;

import com.BibleQuote.domain.controller.ILibraryController;

import dagger.Module;
import dagger.Provides;
import dagger.multibindings.IntoSet;
import ru.churchtools.deskbible.data.library.LibraryContext;
import ru.churchtools.deskbible.data.migration.MigrationReloadModules;
import ru.churchtools.deskbible.data.migration.MigrationUpdateBuiltinModules;
import ru.churchtools.deskbible.data.migration.MoveLibraryDatabaseFromSdcardMigration;
import ru.churchtools.deskbible.data.migration.TSKSourceMigration;
import ru.churchtools.deskbible.domain.migration.Migration;

/**
 * Dagger-модуль с классами, реализующими {@link Migration}
 *
 * @author Yakushev Vladimir <ru.phoenix@gmail.com>
 */
@Module
public interface MigrationModule {

    @IntoSet
    @Provides
    static Migration provideUpdateModulesMigration(LibraryContext libraryContext,
                                                   Context context) {
        return new MigrationUpdateBuiltinModules(libraryContext, context, 4);
    }

    @IntoSet
    @Provides
    static Migration provideReloadModulesMigration(ILibraryController libraryController) {
        return new MigrationReloadModules(libraryController, 5);
    }

    @IntoSet
    @Provides
    static Migration provideMoveDatabaseMigration(Context context) {
        return new MoveLibraryDatabaseFromSdcardMigration(context, 4);
    }

    @IntoSet
    @Provides
    static Migration provideTSKSourceMigration(LibraryContext libraryContext, Context context) {
        return new TSKSourceMigration(libraryContext, context, 4);
    }
}
