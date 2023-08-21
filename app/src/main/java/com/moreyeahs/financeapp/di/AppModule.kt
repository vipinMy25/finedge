package com.moreyeahs.financeapp.di

import android.content.Context
import androidx.room.Room
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.moreyeahs.financeapp.R
import com.moreyeahs.financeapp.data.local.FinanceDatabase
import com.moreyeahs.financeapp.data.local.dao.FinanceDao
import com.moreyeahs.financeapp.data.remote.api.AuthUserApi
import com.moreyeahs.financeapp.data.remote.api.FinanceApi
import com.moreyeahs.financeapp.domain.repository.AuthUserRepo
import com.moreyeahs.financeapp.domain.repository.AuthUserRepoImpl
import com.moreyeahs.financeapp.domain.repository.FinanceRepo
import com.moreyeahs.financeapp.domain.repository.FinanceRepoImpl
import com.moreyeahs.financeapp.util.Constants.BASE_URL
import com.moreyeahs.financeapp.util.Constants.DATABASE_NAME
import com.moreyeahs.financeapp.util.PreferencesManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun providePreferencesManager(@ApplicationContext context: Context): PreferencesManager = PreferencesManager(context)

    @Singleton
    @Provides
    fun provideFinanceDatabase(
        @ApplicationContext context: Context
    ): FinanceDatabase = Room.databaseBuilder(
        context,
        FinanceDatabase::class.java,
        DATABASE_NAME
    ).fallbackToDestructiveMigration().build()

    @Singleton
    @Provides
    fun provideFinanceDao(
        financeDatabase: FinanceDatabase
    ): FinanceDao = financeDatabase.getFinanceDao()

    @Singleton
    @Provides
    fun provideRetrofitClient(): Retrofit {
        return Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .client(
                OkHttpClient.Builder().addInterceptor(
                    HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)
                ).build()
            )
            .baseUrl(BASE_URL)
            .build()
    }

    @Singleton
    @Provides
    fun provideAuthUserApi(
        client: Retrofit
    ): AuthUserApi {
        return client.create(AuthUserApi::class.java)
    }

    @Singleton
    @Provides
    fun provideFinanceApi(
        client: Retrofit
    ): FinanceApi {
        return client.create(FinanceApi::class.java)
    }

    @Provides
    @Singleton
    fun provideAuthUserRepo(
        @ApplicationContext context: Context,
        authUserApi: AuthUserApi,
        preferencesManager: PreferencesManager
    ): AuthUserRepo = AuthUserRepoImpl(context, authUserApi, preferencesManager)

    @Provides
    @Singleton
    fun provideFinanceRepo(
        @ApplicationContext context: Context,
        financeDatabase: FinanceDatabase,
        financeDao: FinanceDao,
        financeApi: FinanceApi,
        preferencesManager: PreferencesManager
    ): FinanceRepo = FinanceRepoImpl(context, financeDatabase, financeDao, financeApi, preferencesManager)

    @Provides
    @Singleton
    fun provideGlideInstance(
        @ApplicationContext context: Context
    ) = Glide.with(context).setDefaultRequestOptions(
        RequestOptions()
            .placeholder(R.drawable.transaction_item_icon)
            .error(R.drawable.transaction_item_icon)
    )

}