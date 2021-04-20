package ch.epfl.sdp.appart.hilt;

import android.content.Context;
import android.location.Criteria;
import android.location.LocationManager;

import androidx.annotation.Nullable;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import javax.inject.Qualifier;
import javax.inject.Singleton;

import ch.epfl.sdp.appart.location.AndroidLocationService;
import ch.epfl.sdp.appart.location.LocationService;
import dagger.Binds;
import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.android.qualifiers.ApplicationContext;
import dagger.hilt.components.SingletonComponent;

@Module
@InstallIn(SingletonComponent.class)
public abstract class LocationModule {

    @Qualifier
    @Retention(RetentionPolicy.RUNTIME)
    public @interface LocationProvider {}

    @Singleton
    @Binds
    public abstract LocationService bindLocationService(AndroidLocationService locationService);

    @Singleton
    @Provides
    public static LocationManager provideLocationManager(@ApplicationContext Context context) {
        return (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
    }

    @Provides
    @LocationProvider
    @Nullable
    public static String provideLocationProvider(LocationManager locationManager, Criteria locationCriteria) {
        return locationManager.getBestProvider(locationCriteria, true);
    }

    @Singleton
    @Provides
    public static Criteria provideCriteria() {
        Criteria criteria = new Criteria();
        criteria.setPowerRequirement(Criteria.POWER_MEDIUM);
        return criteria;
    }
}
